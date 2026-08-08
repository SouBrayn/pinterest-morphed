package app.soubryan.patches.pinterest.auth

import app.morphe.patcher.patch.resourcePatch
import app.soubryan.patches.pinterest.shared.Constants.COMPATIBILITY_PINTEREST
import org.w3c.dom.Element

/**
 * Attempts to restore "Continue with Google" login on patched Pinterest
 * by making the app report Pinterest's Play-Store certificate to
 * whatever Google-Play-Services implementation handles the sign-in
 * intent.
 *
 * **Off by default — not confirmed working end-to-end.** On the one
 * device this was tested against (Xiaomi, MIUI 14 / Android 13, APatch +
 * LSPosed with XSpoofSignatures enabled and scoped to `system_server`,
 * `FAKE_PACKAGE_SIGNATURE` granted) the sign-in still failed and
 * XSpoofSignatures never logged `Spoofing signature for com.pinterest`,
 * meaning its `generatePackageInfo` hook never fired for the query stock
 * Play Services makes. Whether that is MIUI's modified PackageManager
 * defeating the hook, or Play Services reading the certificate through
 * some other path, was not determined. Path A (microG-RE without stock
 * Play Services installed) was never exercised at all.
 *
 * Enable it only if you already have a signature-spoofing setup you know
 * works — verify with
 * [sigspoof-checker](https://f-droid.org/packages/lanchon.sigspoof.checker/)
 * first. The certificate values below are correct and verified; what is
 * unproven is whether anything on a given device acts on them.
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
 * things can be behind that name and this patch targets both of them.
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
 * <meta-data android:name="fake-signature" android:value="3082024f3082..."/>
 * ```
 *
 * The value is the whole DER-encoded certificate in hex — not the SHA-1
 * digest Path A uses — because XSpoofSignatures feeds it straight into
 * `new Signature(String)`.
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
 *
 * ## Two different value formats
 *
 * The two consumers want the certificate in different shapes and getting
 * this wrong fails silently:
 *
 * - microG-RE substitutes the result of `sha1sum(sig.toByteArray())`, so
 *   its meta-data holds the **SHA-1 digest** (40 hex chars).
 * - XSpoofSignatures builds `new Signature(fakeSig)`, whose `String`
 *   constructor parses a **whole DER-encoded X.509 certificate** in hex
 *   (1190 chars here). A digest passed here would silently produce a
 *   20-byte garbage "certificate" that matches nothing.
 */
@Suppress("unused")
val spoofGoogleAuthPatch = resourcePatch(
    name = "Restore Google login (signature spoofing)",
    description = "Experimental, off by default. Adds the signature-spoof metadata read by microG-RE and by the XSpoofSignatures LSPosed module, so \"Continue with Google\" may work on devices with a working signature-spoofing setup. Not confirmed working end-to-end; no-op without such a setup.",
    default = false,
) {
    compatibleWith(COMPATIBILITY_PINTEREST)

    execute {
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

            upsertAppMetaData(application, microgSpoofMetaName, PLAY_STORE_CERTIFICATE_SHA1)
            upsertAppMetaData(application, xspoofMetaName, PLAY_STORE_CERTIFICATE_HEX)
        }
    }
}

/**
 * SHA-1 of the certificate the Play Store build is signed with, as
 * reported by `apksigner verify --print-certs` on 14.27.0's base.apk.
 */
private const val PLAY_STORE_CERTIFICATE_SHA1 = "b6a74dbcb894b0f73d8c485c72eb1247a8f027ca"

/**
 * The same certificate, DER-encoded and hex-formatted — the shape
 * `Signature.toCharsString()` returns and `Signature(String)` parses.
 *
 * Extracted from the v2 APK Signing Block of the official 14.27.0
 * base.apk (Signer #1: CN=Carl Rice, OU=Android, O=Pinterest Inc,
 * L=Palo Alto, ST=CA, C=US; valid 2012-04-24 → 2037-04-18). Its SHA-1
 * is [PLAY_STORE_CERTIFICATE_SHA1] and its SHA-256 is the value already
 * enforced as an install-time allowlist in `COMPATIBILITY_PINTEREST`,
 * so the patcher refuses to run against an APK signed with anything
 * else — which is what makes hardcoding this safe.
 */
private val PLAY_STORE_CERTIFICATE_HEX = """
    3082024f308201b8a00302010202044f96d518300d06092a864886f70d0101050500306c
    310b3009060355040613025553310b300906035504081302434131123010060355040713
    0950616c6f20416c746f31163014060355040a130d50696e74657265737420496e633110
    300e060355040b1307416e64726f696431123010060355040313094361726c2052696365
    301e170d3132303432343136333031365a170d3337303431383136333031365a306c310b
    3009060355040613025553310b3009060355040813024341311230100603550407130950
    616c6f20416c746f31163014060355040a130d50696e74657265737420496e633110300e
    060355040b1307416e64726f696431123010060355040313094361726c20526963653081
    9f300d06092a864886f70d010101050003818d0030818902818100bd8b325a2eb8ade0e1
    6e44971e75130ec98f2c37c8a477044382a1c5c18aa3078bede3c1a49776441617f3bb67
    11d1a7d764785ea20bf8c694d78fdc82d575f88f340fc87b948558385636f80dba536481
    a9c8bf03505781adbbca1ef65b2f59281ca92e352d9f685d04024c19cb3b4e3e14e6eb69
    ca113e55b55d766ea860170203010001300d06092a864886f70d0101050500038181009e
    6766c1071e383b75c520221b502e4701d7a110933a9fe7e7417679be71581ad24a09c42b
    b5190acfb7e487969f843a634eac015424adc4380cdc0eb21b47616b4459f11a018b4f51
    85bfb75764d95c1d8bd01c21932911578a3406caf8d317bc65f2d4d5caef1b59e59ed695
    e235a672460b2ccff2d0a8f3c3b2604c599714
""".filterNot(Char::isWhitespace)

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
