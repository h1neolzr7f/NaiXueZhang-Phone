# Public source validation — 2026-09-01

Reviewed source commit: `594bca66b07d68201aa923fead2a9915d9796cfe` plus the files described below.

## Why the previous CI failed

The latest public workflow stopped at `node tests/standalone_core_test.js` because `web/m/standalone-core.js` was absent. The matching implementation was recovered from the maintainer's phone packaging workspace. Only this JavaScript module was copied; its SHA-256 in the reviewed copy is `61ae58855ce1e0762b9430d96077400f657da61bbcd455176b7eabc1732874af`. A scan found no credential values, account identifiers, user paths or hard-coded HTTP endpoints in the file.

The workflow's Python command also treated `tests/` as an importable package even though it has no `__init__.py`. The workflow now executes the test file directly.

## Commands and results

Windows / Node.js 20 / Python 3.13:

```powershell
node tests/standalone_core_test.js
py -3.13 tests/test_mobile_standalone.py
```

Results: `standalone-core ok`; 6 Python tests ran successfully, with 2 explicit skips.

The skipped checks require distribution-only files that are absent from this public source profile:

- `web/m/m.js`, used by the complete mobile web shell;
- `scripts/phone_preview_server.py` and `data/phone_char_index.txt`, used by the packaged preview/search workflow.

The Android launcher artwork and `censor.onnx` are also not included. The test still checks the Android project, local loopback service declarations and public source contracts that are present.

## Limits

This was not an Android Gradle build, emulator/device test, APK installation test, OCR/censorship-model test or paid generation test. Because the public branch is phone.16 and the distributed APK is phone.23, this repository does not currently provide a reproducible build of that APK. No screenshot is published for this source profile: the complete UI bundle is not present, and using the private packaging copy would misrepresent what a public checkout can run.
