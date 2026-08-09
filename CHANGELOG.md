## [1.7.0](https://github.com/SouBryan/pinterest-morphed/compare/v1.6.0...v1.7.0) (2026-08-09)

### 🐛 Bug Fixes

* **auth:** disable Google login patch by default — unproven end-to-end ([cf31d99](https://github.com/SouBryan/pinterest-morphed/commit/cf31d996eccc79073217a4b2afcec9c020701e22))
* **auth:** don't redeclare FAKE_PACKAGE_SIGNATURE — XSpoofSignatures owns it ([ffea710](https://github.com/SouBryan/pinterest-morphed/commit/ffea71043f1576c7117db603de01a8d2cb478fe0))
* **auth:** fake-signature needs the whole certificate, not its SHA-1 ([3f240f3](https://github.com/SouBryan/pinterest-morphed/commit/3f240f3d6f5d3da4b4bcc7a8a5cb89af4ab246a1))

### ✨ New Features

* **auth:** SpoofGoogleAuthPatch — restore Google Sign-In on microG-RE ([8d4fc62](https://github.com/SouBryan/pinterest-morphed/commit/8d4fc6281c68e13b22f6f949a5fcb6067e185ce2))
* **auth:** support stock Play Services via XSpoofSignatures LSPosed module ([652d65e](https://github.com/SouBryan/pinterest-morphed/commit/652d65eff4c7e3222312b321d0ab6f22d14ed47c))
* **compat:** extend Pinterest support to 14.28.0 - 14.30.0 ([bb7a5f0](https://github.com/SouBryan/pinterest-morphed/commit/bb7a5f045c7299d13437db84ac5a328761c3ce29)), closes [#1](https://github.com/SouBryan/pinterest-morphed/issues/1)

## [1.7.0-dev.6](https://github.com/SouBryan/pinterest-morphed/compare/v1.7.0-dev.5...v1.7.0-dev.6) (2026-08-09)

### ✨ New Features

* **compat:** extend Pinterest support to 14.28.0 - 14.30.0 ([bb7a5f0](https://github.com/SouBryan/pinterest-morphed/commit/bb7a5f045c7299d13437db84ac5a328761c3ce29)), closes [#1](https://github.com/SouBryan/pinterest-morphed/issues/1)

## [1.7.0-dev.5](https://github.com/SouBryan/pinterest-morphed/compare/v1.7.0-dev.4...v1.7.0-dev.5) (2026-08-08)

### 🐛 Bug Fixes

* **auth:** disable Google login patch by default — unproven end-to-end ([cf31d99](https://github.com/SouBryan/pinterest-morphed/commit/cf31d996eccc79073217a4b2afcec9c020701e22))

## [1.7.0-dev.4](https://github.com/SouBryan/pinterest-morphed/compare/v1.7.0-dev.3...v1.7.0-dev.4) (2026-08-08)

### 🐛 Bug Fixes

* **auth:** fake-signature needs the whole certificate, not its SHA-1 ([3f240f3](https://github.com/SouBryan/pinterest-morphed/commit/3f240f3d6f5d3da4b4bcc7a8a5cb89af4ab246a1))

## [1.7.0-dev.3](https://github.com/SouBryan/pinterest-morphed/compare/v1.7.0-dev.2...v1.7.0-dev.3) (2026-08-08)

### 🐛 Bug Fixes

* **auth:** don't redeclare FAKE_PACKAGE_SIGNATURE — XSpoofSignatures owns it ([ffea710](https://github.com/SouBryan/pinterest-morphed/commit/ffea71043f1576c7117db603de01a8d2cb478fe0))

## [1.7.0-dev.2](https://github.com/SouBryan/pinterest-morphed/compare/v1.7.0-dev.1...v1.7.0-dev.2) (2026-08-08)

### ✨ New Features

* **auth:** support stock Play Services via XSpoofSignatures LSPosed module ([652d65e](https://github.com/SouBryan/pinterest-morphed/commit/652d65eff4c7e3222312b321d0ab6f22d14ed47c))

## [1.7.0-dev.1](https://github.com/SouBryan/pinterest-morphed/compare/v1.6.0...v1.7.0-dev.1) (2026-07-17)

### ✨ New Features

* **auth:** SpoofGoogleAuthPatch — restore Google Sign-In on microG-RE ([8d4fc62](https://github.com/SouBryan/pinterest-morphed/commit/8d4fc6281c68e13b22f6f949a5fcb6067e185ce2))

## [1.6.0](https://github.com/SouBryan/pinterest-morphed/compare/v1.5.0...v1.6.0) (2026-07-15)

### 🐛 Bug Fixes

* **ads:** cover third-party, sponsored and promoted-variant flags on first-boot feed ([89e0dec](https://github.com/SouBryan/pinterest-morphed/commit/89e0decc797b53b216f176829d162b48da1b7c7f))

### ✨ New Features

* **tracking:** add DisableFirebaseServicesPatch to flip 5 Firebase auto-init flags ([e536843](https://github.com/SouBryan/pinterest-morphed/commit/e5368437da1accfe06ea8aae4e0b616387f56c29))

## [1.5.0](https://github.com/SouBryan/pinterest-morphed/compare/v1.4.0...v1.5.0) (2026-07-14)

### ✨ New Features

* **safety:** pin Pinterest signing certificate SHA-256 ([0a3fff7](https://github.com/SouBryan/pinterest-morphed/commit/0a3fff7e3ff7b1040e902d333ae27168649bb931))

## [1.4.0](https://github.com/SouBryan/pinterest-morphed/compare/v1.3.0...v1.4.0) (2026-07-14)

### ✨ New Features

* **compat:** support Pinterest 14.20.0-14.27.0 as stable ([0e10291](https://github.com/SouBryan/pinterest-morphed/commit/0e1029111ef8530982d215bd1af05694767de7ae))

## [1.3.0](https://github.com/SouBryan/pinterest-morphed/compare/v1.2.1...v1.3.0) (2026-07-14)

### ✨ New Features

* add HideAdViewsPatch — collapse ad-specific views to GONE ([064d99c](https://github.com/SouBryan/pinterest-morphed/commit/064d99ccb5cbcd7b70955f7b6de0b2d298b83163))

## [1.2.1](https://github.com/SouBryan/pinterest-morphed/compare/v1.2.0...v1.2.1) (2026-07-14)

### 🐛 Bug Fixes

* cover shopping ads and ad_data in HidePromotedPinsPatch ([2ac9672](https://github.com/SouBryan/pinterest-morphed/commit/2ac967234e54f113e693454fd6066cc5ba13e312))

## [1.2.0](https://github.com/SouBryan/pinterest-morphed/compare/v1.1.0...v1.2.0) (2026-07-14)

### ✨ New Features

* resolve pin.it and pinterest.com/url_shortener short links to canonical pin URLs, and sanitize clipboard ([1e6b6e4](https://github.com/SouBryan/pinterest-morphed/commit/1e6b6e461300c3ae21000a427ba26f28086e62fa))

## [1.1.0](https://github.com/SouBryan/pinterest-morphed/compare/v1.0.0...v1.1.0) (2026-07-07)

### ✨ New Features

* add Sanitize sharing links + make cross-version patches resilient ([0989429](https://github.com/SouBryan/pinterest-morphed/commit/0989429e1e85dc021c8985cf2affe934fe283207))

## 1.0.0 (2026-07-07)

### ✨ New Features

* add Disable Google Engage and Privacy Sandbox Ad Services patches ([db79293](https://github.com/SouBryan/pinterest-morphed/commit/db79293e26486f1d8e5e34c002ac5a688efda59e))
* hide promoted pins app-wide via isPromoted getter shotgun ([1811d7a](https://github.com/SouBryan/pinterest-morphed/commit/1811d7a344be1600cdc57f1c8f009221b2422581))
* hook GoogleEngageWorker.createWork() to no-op success ([ae30cc2](https://github.com/SouBryan/pinterest-morphed/commit/ae30cc25a07da6796d1b8b05661a90469948f899))
* scaffold pinterest-morphed and add initial tracking / ads patches ([4fecc98](https://github.com/SouBryan/pinterest-morphed/commit/4fecc98f62ebe92759c6d3bf8853e98807de4f25))
