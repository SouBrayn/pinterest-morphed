package app.soubryan.patches.pinterest.shared

import app.morphe.patcher.patch.ApkFileType
import app.morphe.patcher.patch.AppTarget
import app.morphe.patcher.patch.Compatibility

internal object Constants {
    /**
     * Pinterest app compatibility.
     *
     * The Pinterest APK is distributed by Google Play as an app bundle
     * (`base.apk + split_config.*`), so the preferred file type is APKM
     * (Morphe Manager will guide users to fetch a bundle from APKMirror or
     * export it from an installed copy with SAI / APKExport).
     *
     * Every target from 14.20.0 through 14.30.0 has been verified end-to-end:
     * all 13 patches apply cleanly (13/13) on the exact version code listed
     * below. The fingerprints are anchored on Gson `@SerializedName` values,
     * Pinterest-owned class names and stable Android SDK strings — they have
     * survived every Pinterest release in this range without any code change.
     *
     * Targets are ordered from newest to oldest. **14.25.0 is the recommended
     * daily-driver build** — it is the most-tested version end-to-end on the
     * developer's device (ad blocking, share-sanitiser, copy-link resolver).
     * The other releases apply cleanly but have received less runtime testing.
     */
    val COMPATIBILITY_PINTEREST = Compatibility(
        name = "Pinterest",
        packageName = "com.pinterest",
        apkFileType = ApkFileType.APKM,
        // Pinterest brand red (Play Store / launcher icon background).
        appIconColor = 0xE60023,
        // SHA-256 fingerprint of the certificate the Pinterest Play Store build
        // is signed with (Signer #1: CN=Carl Rice, O=Pinterest Inc, Palo Alto,
        // CA). The patcher refuses to touch APKs whose signature does not match
        // — this is a supply-chain safeguard: the user cannot accidentally (or
        // maliciously) apply these patches to a repackaged "pinterest.apk"
        // downloaded from a third-party site that could contain malware.
        //
        // Only the Pinterest team can produce an APK with this signature.
        // Verified with `apksigner verify --print-certs base.apk` on every
        // release listed below (14.20.0 → 14.30.0).
        signatures = setOf(
            "341d6881b1ecf38361fbf8c8fbae0aa516b45375c39ef5e78b161869acc1bcfa",
        ),
        targets = listOf(
            AppTarget(
                version = "14.30.0",
                versionCode = 14308020,
                minSdk = 29,
                description = "Latest release (2026-08-06).",
            ),
            AppTarget(
                version = "14.29.0",
                versionCode = 14298020,
                minSdk = 29,
            ),
            AppTarget(
                version = "14.28.0",
                versionCode = 14288010,
                minSdk = 29,
            ),
            AppTarget(
                version = "14.27.0",
                versionCode = 14278010,
                minSdk = 29,
            ),
            AppTarget(
                version = "14.26.0",
                versionCode = 14268010,
                minSdk = 29,
            ),
            AppTarget(
                version = "14.25.0",
                versionCode = 14258020,
                minSdk = 28,
                description = "Recommended — verified daily-driver build.",
            ),
            AppTarget(
                version = "14.24.0",
                versionCode = 14248020,
                minSdk = 28,
            ),
            AppTarget(
                version = "14.23.0",
                versionCode = 14238020,
                minSdk = 28,
            ),
            AppTarget(
                version = "14.22.0",
                versionCode = 14228020,
                minSdk = 28,
            ),
            AppTarget(
                version = "14.21.0",
                versionCode = 14218010,
                minSdk = 28,
            ),
            AppTarget(
                version = "14.20.0",
                versionCode = 14208010,
                minSdk = 28,
            ),
        ),
    )
}
