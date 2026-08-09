# 📌 Pinterest Morphed

<p align="center">

![GitHub Release](https://img.shields.io/github/v/release/SouBryan/pinterest-morphed?style=for-the-badge&logo=github&color=E60023)
![GitHub Pre-Release](https://img.shields.io/github/v/release/SouBryan/pinterest-morphed?include_prereleases&label=pre-release&style=for-the-badge&color=BD081C)
![License](https://img.shields.io/github/license/SouBryan/pinterest-morphed?style=for-the-badge&color=E60023)
![GitHub Stars](https://img.shields.io/github/stars/SouBryan/pinterest-morphed?style=for-the-badge&color=E60023)
![GitHub Issues](https://img.shields.io/github/issues/SouBryan/pinterest-morphed?style=for-the-badge&color=BD081C)

</p>

<br/>

> [!NOTE]
>
> Focused, Pinterest-only patch set for use with **[Morphe](https://morphe.software)**.
> No ads, no trackers, no third-party analytics — just the boards, pins and the feed.
>
> Patch requests are welcome, but this repo intentionally stays scoped to the Pinterest Android app only.

<br/>

## Contents

- [About](#-about)
- [How to use these patches](#how-to-use-these-patches)
- [Supported Pinterest versions](#-supported-pinterest-versions)
- [Patches list](#-patches-list)
- [Reporting bugs](#reporting-bugs)
- [Building from source](#%EF%B8%8F-building)
- [FAQ](#faq)
- [Disclaimer](#-disclaimer)

## ❓ About

Pinterest is a great pin/moodboard app, but the client keeps piling on tracking
SDKs and Promoted Pins that get in the way. This project ships a curated set
of Morphe patches that strip every third-party ad, analytics and telemetry
SDK, plus every "Promoted"/"Sponsored" pin, without touching the parts of the
app you actually use.

The patches target the **Pinterest Android client** distributed on the Play
Store and APKMirror. Everything is delivered as a standard Morphe patch bundle
(`.mpp`) that plugs into **Morphe Manager** or **Morphe CLI** — no side-loaded
APK from strangers, you always build your own patched APK from a clean copy of
Pinterest.

## How to use these patches

Click here to add the patch source to Morphe:
<https://morphe.software/add-source?github=SouBryan/pinterest-morphed>

Or, in Morphe Manager, open **Settings → Sources → Add source** and paste:

```
https://github.com/SouBryan/pinterest-morphed
```

Then:

1. Grab a **clean Pinterest APK bundle** (`.apkm`, `.apks`, `.xapk`) from
   [APKMirror](https://www.apkmirror.com/apk/pinterest/pinterest/) or export it
   from your installed copy.
2. In Morphe Manager, pick **Pinterest** and apply the patches — all are enabled
   by default and safe to combine.
3. Install the patched APK. On the first launch you'll need to sign in again
   (Pinterest treats the re-signed patched build as a different app).

## 🎯 Supported Pinterest versions

| Version | Channel         | Notes                                                                    |
| ------- | --------------- | ------------------------------------------------------------------------ |
| 14.30.0 | Stable          | Latest release (2026-08-06)                                              |
| 14.29.0 | Stable          |                                                                          |
| 14.28.0 | Stable          |                                                                          |
| 14.27.0 | Stable          |                                                                          |
| 14.26.0 | Stable          |                                                                          |
| 14.25.0 | **Recommended** | Most-tested build on real hardware (ad-blocking, sanitisers, resolver)   |
| 14.24.0 | Stable          |                                                                          |
| 14.23.0 | Stable          |                                                                          |
| 14.22.0 | Stable          |                                                                          |
| 14.21.0 | Stable          |                                                                          |
| 14.20.0 | Stable          | Oldest supported release                                                 |

Every listed version has been verified end-to-end — **all 13 patches apply
cleanly on the exact `versionCode` declared in
[`Constants.kt`](patches/src/main/kotlin/app/soubryan/patches/pinterest/shared/Constants.kt).**
Fingerprints are anchored on Gson `@SerializedName` values, Pinterest-owned
class names and stable Android SDK strings, so they survive every 14.2x /
14.3x release without any code change.

Morphe Manager fetches the exact APKMirror download link for each version
automatically — no need to hunt for it manually.

## 🩹 Patches list

<!-- PATCHES_START EXPANDED -->
> **[v1.7.0](https://github.com/SouBryan/pinterest-morphed/releases/tag/v1.7.0)**&nbsp;&nbsp;•&nbsp;&nbsp;`main`&nbsp;&nbsp;•&nbsp;&nbsp;14 patches total
<details open>
<summary>📦 Pinterest&nbsp;&nbsp;•&nbsp;&nbsp;14 patches</summary>
<br>

**🎯 Supported versions:**

| 14.30.0 | 14.29.0 | 14.28.0 | 14.27.0 | 14.26.0 | 14.25.0 | 14.24.0 | 14.23.0 | 14.22.0 | 14.21.0 | 14.20.0 |
| :---: | :---: | :---: | :---: | :---: | :---: | :---: | :---: | :---: | :---: | :---: |

| 💊&nbsp;Patch | 📜&nbsp;Description | ⚙️&nbsp;Options |
|----------|----------------|-----------|
| [Disable Android Privacy Sandbox Ad Services](#disable-android-privacy-sandbox-ad-services) | Removes the AD_SERVICES_CONFIG property so Android Privacy Sandbox ad APIs (Topics, Attribution Reporting, Custom Audiences) are not opted into. |  |
| [Disable AppsFlyer tracking](#disable-appsflyer-tracking) | Neutralises the AppsFlyer attribution / tracking SDK. init() becomes a no-op and isStopped() always returns true, so no events, installs, uninstalls or attribution data are transmitted. |  |
| [Disable Bugsnag crash tracking](#disable-bugsnag-crash-tracking) | Removes the Bugsnag API key metadata so the crash reporting SDK cannot initialize or upload telemetry. |  |
| [Disable Firebase services](#disable-firebase-services) | Adds the officially documented Firebase off-switches (`firebase_analytics_collection_deactivated`, `firebase_crashlytics_collection_enabled=false`, `firebase_performance_collection_deactivated`, `google_analytics_adid_collection_enabled=false`) so the Firebase Analytics, Crashlytics and Performance SDKs go dormant. |  |
| [Disable Google Ads SDK](#disable-google-ads-sdk) | Removes the Google Mobile Ads (AdMob) initialization metadata so the SDK never starts. |  |
| [Disable Google Engage integration](#disable-google-engage-integration) | Removes the Google Engage broadcast receiver so Pinterest cannot publish content recommendations back to Google (Discover, Assistant, Play Store, etc.). |  |
| [Disable Google Engage worker](#disable-google-engage-worker) | Rewrites GoogleEngageWorker.createWork() to return null so WorkManager fails the periodic job and no content recommendations are ever published to Google. |  |
| [Hide ad views](#hide-ad-views) | Collapses Pinterest's ad-specific views (TextAdView, promoted closeup action bars, board sponsor headers, …) to zero-size on construction, so ad chrome never draws even if the feed adapter tries to render one. |  |
| [Hide promoted pins](#hide-promoted-pins) | Neutralises every ad-indicator field on the Pinterest pin/story models (is_promoted, promoted_is_*, is_native, ad_data, ...) so Promoted Pins, shopping-carousel ads, native-content ads and click-out CTAs are never rendered or fired. |  |
| [Opt out of Google Analytics](#opt-out-of-google-analytics) | Sets the default Google Analytics consent flags to false so the Firebase Measurement SDK does not collect analytics, ad data or personalization signals. |  |
| [Remove Advertising ID permission](#remove-advertising-id-permission) | Strips the com.google.android.gms.permission.AD_ID permission so any residual SDK cannot read the device's Google Advertising ID. |  |
| [Restore Google login (signature spoofing)](#restore-google-login-signature-spoofing) | Experimental, off by default. Adds the signature-spoof metadata read by microG-RE and by the XSpoofSignatures LSPosed module, so "Continue with Google" may work on devices with a working signature-spoofing setup. Not confirmed working end-to-end; no-op without such a setup. |  |
| [Sanitize copied links](#sanitize-copied-links) | Resolves Pinterest short URLs (pin.it/…, pinterest.com/url_shortener/…) to their canonical pin URL before they are placed on the system clipboard, so "Copy link" no longer produces a fingerprinted short link. |  |
| [Sanitize sharing links](#sanitize-sharing-links) | Strips UTM and click-ID tracking parameters from the URL the app puts on the Android share sheet, so friends receive clean pin links. |  |

</details>

<!-- PATCHES_END -->

## Reporting bugs

Please include:

- Pinterest version (Settings → About) — e.g. `14.25.0`
- Pinterest Morphed release used to patch — e.g. `stable v1.0.0` or `dev v1.1.0-dev.3`
- Where you got the APK bundle from (APKMirror, Play Store export, …)
- Relevant logs. Capture with:
  ```bash
  adb logcat -c
  adb logcat | grep -i "pinterest\|AndroidRuntime\|FATAL" > logs.txt
  ```
- Steps to reproduce and — if visual — a screenshot or screen recording.

Open a bug via the [issue template](https://github.com/SouBryan/pinterest-morphed/issues/new?template=bug_report.yml).

## 🛠️ Building

Requirements:

- **JDK 21**
- **Android SDK** (any recent build-tools)
- A **GitHub Personal Access Token** with `read:packages` scope — the Morphe
  patcher is published to GitHub Packages, not Maven Central. Save it in
  `~/.gradle/gradle.properties`:
  ```properties
  gpr.user=<your github username>
  gpr.key=<your PAT>
  ```
  Or export `GITHUB_ACTOR` / `GITHUB_TOKEN`.

Then:

```bash
./gradlew :patches:buildAndroid
# → patches/build/libs/patches-<version>.mpp
```

Apply locally with the [Morphe CLI](https://github.com/MorpheApp/morphe-cli):

```bash
morphe patch \
  --patches patches/build/libs/patches-*.mpp \
  com.pinterest_14.25.0.apkm
```

## FAQ

### How do I actually use this?
Install [Morphe Manager](https://morphe.software), add this repo as a patch
source, then pick Pinterest to patch.

### The patched app makes me log in again — is that normal?
Yes. All patched apps are re-signed, so Android sees them as a different app
from the Play Store version and won't share credentials.

### "Continue with Google" — does it work?
**Short answer: no, use email/password.**

Google Sign-In verifies the app's signing certificate server-side. A
patched APK is resigned with a different key, so the OAuth call comes
back `invalid_client` and the app silently returns to the login screen
after the account picker. Nothing inside the APK can change that on its
own — the fix has to come from the system reporting a different
certificate for `com.pinterest`.

There is an **experimental, off-by-default** patch for that,
*"Restore Google login (signature spoofing)"*. It writes Pinterest's
Play-Store certificate into two manifest keys:

| Consumer | Key | Value |
| --- | --- | --- |
| [microG-RE](https://github.com/MorpheApp/MicroG-RE) | `app.revanced.android.gms.SPOOFED_PACKAGE_SIGNATURE` | SHA-1 digest |
| [XSpoofSignatures](https://github.com/rushiiMachine/XSpoofSignatures) (LSPosed) | `fake-signature` | full DER certificate in hex |

plus `<uses-permission android:name="android.permission.FAKE_PACKAGE_SIGNATURE"/>`.
The values are verified correct — the certificate reproduces both the
SHA-1 and the SHA-256 that `COMPATIBILITY_PINTEREST` already enforces —
but **the patch is not confirmed to actually restore login.** On the one
device it was tried (Xiaomi, MIUI 14 / Android 13, APatch + LSPosed with
XSpoofSignatures enabled, scoped to `system_server`, permission granted)
sign-in still failed and the module never logged
`Spoofing signature for com.pinterest`, so its hook never fired for the
query stock Play Services makes.

Only enable it if you already run a signature-spoofing setup you have
verified independently — check with
[sigspoof-checker](https://f-droid.org/packages/lanchon.sigspoof.checker/)
first. If you get it working, please open an issue describing your setup.

Notes if you do try it: XSpoofSignatures defines the
`FAKE_PACKAGE_SIGNATURE` permission itself (the patch only requests it),
and declares it `dangerous`, so it is not auto-granted:

```bash
adb shell pm grant com.pinterest android.permission.FAKE_PACKAGE_SIGNATURE
```

Every other server-side-verified sign-in (Meta Login, banking apps, …)
has the same limitation and is out of scope.

Any other server-side-verified sign-in (Meta Login, Google Drive scopes
outside standard sign-in, banking apps, …) has the same limitation and
is not covered by this patch.

### Which APK/bundle should I download?
See the [Supported Pinterest versions](#-supported-pinterest-versions) table
above for direct APKMirror links. Prefer the **Bundle** (`.apkm`) variant when
listed — it's the same multi-split file Google Play delivers, so the download
matches your device configuration exactly. If the bundle installer refuses
your device, fall back to the **Universal APK**. Both produce identical
patched builds. Avoid already-modded or repacked APKs from other sources.

### Will you support version X.Y.Z?
Pinterest **14.20.0 through 14.30.0** are already verified (13/13 patches on
every release in that range). If a future Pinterest release still applies all
13 patches, it works with no code change on my side — the fingerprints are
anchored on Gson `@SerializedName` values and Pinterest-owned class names,
which R8 preserves. If a patch does stop applying, open an issue with your
Pinterest version and the CLI output so I can re-anchor the fingerprint.

### Can you make patches for other apps?
No — this repo is Pinterest-only by design. There are other Morphe patch
sources that cover many apps (see
[rushiranpise/morphe-patches](https://github.com/rushiranpise/morphe-patches),
the official [MorpheApp/morphe-patches](https://github.com/MorpheApp/morphe-patches), etc.).

## 📜 Disclaimer

> **⚠️ Legal Notice**
>
> This project exists for **educational, research and personal privacy purposes only**.
> It modifies a third-party application (Pinterest) and may violate that app's terms of service.
>
> - This project is **not affiliated** with, endorsed by, or sponsored by Pinterest, Inc., the Morphe open source project, or any of the SDK vendors mentioned in the patches list.
> - "Morphe" is referenced solely for descriptive compatibility purposes (see [NOTICE](NOTICE) for the GPLv3 §7(b)/§7(c) terms that apply).
> - Use these patches **at your own risk**. The author is not responsible for account bans, data loss, or any other consequence of using patched software.
> - If you are Pinterest, Inc. or another rights-holder and believe this project infringes on your rights, please open a private issue or contact the repository owner and the relevant patches will be reviewed and, if appropriate, removed.

## 📄 License

Released under the [GNU General Public License v3.0](LICENSE) with the
Additional Terms in [NOTICE](NOTICE).

## ❤️ Credits

- The [Morphe](https://github.com/MorpheApp) team — patcher, CLI, documentation, patches template.
- [rushiranpise/morphe-patches](https://github.com/rushiranpise/morphe-patches) — layout of this README is inspired by their work.
- Everyone who tests patches on their phone and files a good bug report.
