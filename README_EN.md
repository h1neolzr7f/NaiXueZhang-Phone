# NaiXueZhang Studio for Android

[中文](README.md) · [APK distribution notes](https://github.com/h1neolzr7f/NaiXueZhang-Phone-APK) · [Windows line](https://github.com/h1neolzr7f/NaiXueZhang-Studio-Upgrade)

This repository is the public Android source snapshot. The app runs a loopback service on the device and does not connect to or remote-control the Windows Studio.

> **Version boundary:** the public branch currently declares `1.5.2-phone.16` (`versionCode 167`), while the distributed APK is `1.5.2-phone.23` (`versionCode 174`). They are not the same reproducible build. Use Gradle metadata for the source version and the APK repository's SHA-256 for the binary version.

## Included work

- Separate online discovery and device-local favorites
- NovelAI metadata parsing, prompt organization, and character-swap drafts
- A persistent local queue with free-tier constraints and explicit failure states
- Optional post-processing and censorship stages
- Android Keystore storage for credentials entered on the device

The public snapshot excludes signing material, real credentials, user data, and some large distribution-only assets. It supports code review and public contract tests; it does not currently reproduce the phone.23 APK byte for byte.

```bash
node tests/standalone_core_test.js
python3 tests/test_mobile_standalone.py
```

These tests make no paid provider calls. Checks that require distribution-only indexes, models, or preview tools are reported as skipped when those assets are absent.

The phone.23 APK is an arm64 debug-signed build, 52,819,220 bytes, SHA-256 `213ddd93005a70284c05d3e978734d13cd086d492d6f3fa6ae9ed2f5b19e9095`. See the [APK repository](https://github.com/h1neolzr7f/NaiXueZhang-Phone-APK) for the current mirror and installation notes.

Do not post provider tokens, API keys, cookies, signing files, or private galleries in issues or screenshots. This unofficial project has no affiliation with pixiv Inc., NovelAI/Anlatan, AITag, DeepSeek, or other third-party services. Code is available under the [MIT License](LICENSE).
