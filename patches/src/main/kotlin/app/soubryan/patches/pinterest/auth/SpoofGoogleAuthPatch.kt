package app.soubryan.patches.pinterest.auth

import app.morphe.patcher.patch.resourcePatch
import app.soubryan.patches.pinterest.shared.Constants.COMPATIBILITY_PINTEREST
import org.w3c.dom.Element

/**
 * Restores "Continue with Google" login on patched Pinterest by making
 * the app report Pinterest's Play-Store certificate SHA-1 to whatever
 * Google-Play-Services implementation handles the sign-in intent.
 *
 * ## The problem
 *
 * Pinterest authenticates via the OAuth 2.0 Android client registered
 * in Google's developer console. That client is keyed by
 * `(packageName, signingCertificate SHA-1)`. Once the app is patched
 * the APK is resigned with a developer key that does **not** match the
 * SHA-1 Pinterest registered with Google, so the OAuth server rejects
 * the token exchange with `invalid_client` and the app silently returns
 * to the login screen after the account picker.
 *
 * On modern Pinterest builds the sign-in flow goes through
 * `androidx.credentials:credentials-play-services-auth` →
 * `androidx.credentials.playservices.HiddenActivity`, which resolves
 * the `com.google.android.gms` package on the device. Two very different
 * things can be behind that name and this patch covers both of them.
 *
 * ## Path A — microG-RE (`app.revanced.android.gms`)
 *
 * microG-RE (MorpheApp's fork of ReVanced GmsCore) implements
 * *per-caller signature spoofing*: when it signs a Google API request
 * on behalf of another app, it looks up the meta-data below on the
 * caller and, if present, tells Google that the caller has that
 * signature instead of its real one.
 *
 * ```
 * <meta-data android:name="app.revanced.android.gms.SPOOFED_PACKAGE_SIGNATURE"
 *            android:value="b6a74dbcb894b0f73d8c485c72eb1247a8f027ca"/>
 * ```
 *
 * Only relevant when microG-RE is actually the one servicing the intent
 * (i.e. no stock Google Play Services is installed, or it has been
 * disabled). See `PackageSpoofUtils.kt` in `MorpheApp/MicroG-RE`,
 * `play-services-base/core/src/main/kotlin/org/microg/gms/common`.
 *
 * ## Path B — stock Google Play Services + XSpoofSignatures
 *
 * Many devices ship with `com.google.android.gms` as a SYSTEM /
 * UPDATED_SYSTEM_APP (MIUI/HyperOS, most OEM ROMs). In that case
 * `resolve-activity` for the Google sign-in intent picks the *stock*
 * Play Services, not microG-RE — Path A does nothing and login still
 * fails. To recover, the patched app declares the standard signature
 * spoofing metadata expected by
 * [XSpoofSignatures](https://github.com/rushiiMachine/XSpoofSignatures),
 * an LSPosed module that hooks
 * `PackageManagerService.generatePackageInfo` and rewrites the reported
 * signature when the caller has `FAKE_PACKAGE_SIGNATURE` granted:
 *
 * ```
 * <uses-permission android:name="android.permission.FAKE_PACKAGE_SIGNATURE"/>
 * <meta-data android:name="fake-signature"
 *            android:value="b6a74dbcb894b0f73d8c485c72eb1247a8f027ca"/>
 * ```
 *
 * The permission itself is *not* declared here — XSpoofSignatures's own
 * APK (`dev.rushii.xspoofsignatures`) declares it with
 * `protectionLevel="dangerous"`. Trying to redeclare it from the patched
 * app fails with `INSTALL_FAILED_DUPLICATE_PERMISSION` because the
 * permission is already owned by XSpoofSignatures.
 *
 * Because the permission is `dangerous`, Android does not auto-grant
 * it at install time. The user has to grant it manually after installing
 * the patched Pinterest, e.g. via ADB:
 *
 * ```
 * adb shell pm grant com.pinterest android.permission.FAKE_PACKAGE_SIGNATURE
 * ```
 *
 * LSPosed's XSpoofSignatures then intercepts every subsequent call to
 * `getPackageInfo("com.pinterest", GET_SIGNATURES)` from the system
 * server, including the one stock Play Services makes when it validates
 * the OAuth caller, and returns Pinterest's real Play-Store SHA-1.
 *
 * Requires the user to install XSpoofSignatures inside their LSPosed
 * flavour (the JingMatrix / Vector 2.0 fork works, so does upstream
 * LSPosed) and add "System framework" (`android`) to the module scope.
 *
 * ## Why this is safe on unmodified Play Services + no LSPosed
 *
 * On a Play-certified device without XSpoofSignatures installed, the
 * permission `android.permission.FAKE_PACKAGE_SIGNATURE` is undefined,
 * so `<uses-permission>` silently no-ops (the system does not fail to
 * install unknown permissions, it just leaves them ungranted). The
 * `fake-signature` meta-data has no reader either. Play Services keeps
 * rejecting the OAuth call — exactly the same broken state as before.
 * The microG-RE meta-data is likewise a no-op there. Safe to leave on.
 *
 * ## Trade-off — `fake-signature-only`
 *
 * XSpoofSignatures defaults to *sole signer* mode (reports only the
 * fake signature, discarding the real one). This is required for stock
 * Play Services to accept the caller. Any third-party integrity checker
 * on the device that queries the Pinterest signature will therefore see
 * the Play-Store cert instead of the Morphe signing cert — that's the
 * whole point of this patch, and no realistic use case is harmed by it.
 */
@Suppress("unused")
val spoofGoogleAuthPatch = resourcePatch(
    name = "Restore Google login (signature spoofing)",
    description = "Adds the signature-spoof metadata used by microG-RE (per-caller) and XSpoofSignatures (system-wide, via LSPosed) so \"Continue with Google\" works on both microG-RE and stock Google Play Services. No-op if neither is present.",
    default = true,
) {
    compatibleWith(COMPATIBILITY_PINTEREST)

    execute {
        // SHA-1 of Signer #1 in the official Play Store build.
        // Verified with `apksigner verify --print-certs base.apk` on 14.27.0.
        // The corresponding SHA-256 (already enforced as an install-time
        // allowlist in Constants.COMPATIBILITY_PINTEREST) is
        // 341d6881b1ecf38361fbf8c8fbae0aa516b45375c39ef5e78b161869acc1bcfa.
        val pinterestOfficialSignatureSha1 =
            "b6a74dbcb894b0f73d8c485c72eb1247a8f027ca"

        // Path A: microG-RE per-caller spoofing metadata. Key derived at
        // build time in microG-RE from `BASE_PACKAGE_NAME = "app.revanced"`.
        val microgSpoofMetaName =
            "app.revanced.android.gms.SPOOFED_PACKAGE_SIGNATURE"

        // Path B: XSpoofSignatures metadata key (fixed name from its README).
        val xspoofMetaName = "fake-signature"
        val fakeSigPermission = "android.permission.FAKE_PACKAGE_SIGNATURE"

        document("AndroidManifest.xml").use { document ->
            val manifest = document.documentElement
            val application = document
                .getElementsByTagName("application")
                .item(0) as Element

            // <uses-permission> lives directly under <manifest>, not <application>.
            // The permission itself is defined by XSpoofSignatures' APK.
            upsertManifestChildTag(
                manifest = manifest,
                tag = "uses-permission",
                nameAttrValue = fakeSigPermission,
                attributes = mapOf("android:name" to fakeSigPermission),
            )

            upsertAppMetaData(application, microgSpoofMetaName, pinterestOfficialSignatureSha1)
            upsertAppMetaData(application, xspoofMetaName, pinterestOfficialSignatureSha1)
        }
    }
}

/**
 * Adds `<meta-data android:name=... android:value=...>` under
 * `application`, updating value in place if the tag already exists so
 * re-patching stays idempotent.
 */
private fun upsertAppMetaData(application: Element, name: String, value: String) {
    val existing = application.getElementsByTagName("meta-data")
    for (i in 0 until existing.length) {
        val meta = existing.item(i) as Element
        if (meta.getAttribute("android:name") == name) {
            meta.setAttribute("android:value", value)
            return
        }
    }
    val meta = application.ownerDocument.createElement("meta-data")
    meta.setAttribute("android:name", name)
    meta.setAttribute("android:value", value)
    application.appendChild(meta)
}

/**
 * Adds a direct child of `<manifest>` (e.g. `<permission>` or
 * `<uses-permission>`) with the given `android:name`, replacing its
 * attributes in place if a matching tag already exists.
 */
private fun upsertManifestChildTag(
    manifest: Element,
    tag: String,
    nameAttrValue: String,
    attributes: Map<String, String>,
) {
    val existing = manifest.getElementsByTagName(tag)
    for (i in 0 until existing.length) {
        val node = existing.item(i) as Element
        // Only reuse direct children — nested elements can carry the same tag.
        if (node.parentNode !== manifest) continue
        if (node.getAttribute("android:name") == nameAttrValue) {
            for ((k, v) in attributes) node.setAttribute(k, v)
            return
        }
    }
    val el = manifest.ownerDocument.createElement(tag)
    for ((k, v) in attributes) el.setAttribute(k, v)
    manifest.appendChild(el)
}
