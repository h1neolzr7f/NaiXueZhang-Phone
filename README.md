# Nai 学长工作室 · Android 独立版

[English](README_EN.md) · [APK 下载说明](https://github.com/h1neolzr7f/NaiXueZhang-Phone-APK) · [Windows 主线](https://github.com/h1neolzr7f/NaiXueZhang-Studio-Upgrade)

这是手机端的公开源码快照。应用在 Android 设备本地运行，通过内置 loopback 服务提供界面，不连接或遥控 Windows 工作室。

> **版本说明**：公开源码默认分支的 Android 配置是 `1.5.2-phone.16`（`versionCode 167`）；当前分发 APK 是 `1.5.2-phone.23`（`versionCode 174`）。两者不能当作同一个可复现构建。后续同步完成前，请以源码中的 Gradle 配置判断代码版本，以 APK 仓库的 SHA-256 判断安装包版本。

## 能做什么

- 在线发现与本地收藏分开，已收藏内容可在离线时查看和管理；
- 从 NovelAI 元数据中整理 Prompt 与角色槽，建立换角草稿；
- 本地生成队列保留免费档约束、失败状态和并发设置；
- 可选的超分、元数据处理和打码流程；
- Android Keystore 保存使用者在设备上填写的凭据。

第三方在线服务可能要求人机验证或临时不可用。应用不会因此获得绕过访问控制的权利，使用者仍需遵守服务条款和第三方权利要求。

## 源码结构

```text
android/   Android WebView 容器、本地 HTTP 服务、任务与凭据存储
web/m/     手机界面与纯 JavaScript Prompt/换角逻辑
data/      可公开的角色和标签索引子集
tests/     JavaScript 与 Android 源码契约
```

源码不包含签名文件、真实凭据或用户数据。大型运行资源通过下方固定版本清单恢复，可构建修复后的 phone.16 debug 包；不能据此声称可逐字节复现 phone.23 APK。

## 验证

CI 恢复并校验构建资源后运行：

```bash
node tests/standalone_core_test.js
python3 tests/test_mobile_standalone.py
python3 scripts/test_phone_retry.py
# Android 构建另执行 assembleDebug
```

不需要真实 NovelAI Token，也不会调用付费生成。界面、索引、模型与 Android 构建不再跳过；缺少桌面预览服务时仅该服务的测试明确跳过。发布前仍需实机和安装升级验证。

## 安装包

分发说明与校验值在 [NaiXueZhang-Phone-APK](https://github.com/h1neolzr7f/NaiXueZhang-Phone-APK)。当前 phone.23 APK：

| 项目 | 值 |
|---|---|
| 包名 | `com.naixuezhang.studio.mobile` |
| ABI | `arm64-v8a` |
| 文件大小 | 52,819,220 bytes |
| SHA-256 | `213ddd93005a70284c05d3e978734d13cd086d492d6f3fa6ae9ed2f5b19e9095` |
| 签名 | debug；升级通常需要先卸载旧版 |

临时镜像会过期。长期分发应迁移到带固定资产和校验值的 GitHub Release。

## 安全与许可

不要在 Issue、日志或截图中提交 NovelAI Token、第三方 API Key、Cookie、签名文件或私人图库。详见 [SECURITY.md](SECURITY.md)、[RESPONSIBLE_USE.md](RESPONSIBLE_USE.md) 和 [DISCLAIMER.md](DISCLAIMER.md)。

代码采用 [MIT License](LICENSE)。项目与 pixiv Inc.、NovelAI/Anlatan、AITag、DeepSeek 或其他第三方服务没有隶属、授权或合作关系。


## 源码修复与构建（2026-09-05）

本修复分支以 `1.5.2-phone.16` 源码为基线，不代表已有 `phone.23` 安装包包含这些修复。
`web/m/m.js` 已从同一开发线的固定提交 `3d076cc0cbdef6d95972a2d18166da548526550c` 恢复。
缺失的公开词库、启动图标、Gradle wrapper 和 ONNX 模型可按固定清单恢复，每个文件均校验 Git blob SHA 与大小。

需要 Python 3、Java 17 JDK 和 Android SDK 34：

```sh
python scripts/restore_phone_assets.py --model
node tests/standalone_core_test.js
python tests/test_mobile_standalone.py
python scripts/test_phone_retry.py
cd android
# Windows 使用 gradlew.bat assembleDebug
bash gradlew --no-daemon assembleDebug
```

恢复的依赖来自上述公开提交，不包含账号、Token 或个人图库。大文件不会重复提交到本仓库。
构建产物是源码验证用的 debug APK；未使用正式发布签名，也不替代已发布的 phone.23。

队列的“补做未完成”保留每页每份的原始 seed 偏移，跳过已保存的图片；
重复点击同一父任务只返回已建立的补做任务。后处理失败不会再次请求已生成的原图。
结果不明的请求需核对平台记录并在界面明确确认，API 也要求 `retry_unknown: true`。
取消后要等待已发出的请求结束；仍在等待 Token 的请求会在发送前检查取消标记。

CI 现在包含资源恢复、实际 Java 队列故障场景和 Android 构建。
桌面预览服务不属于 Android 运行资源，相应测试在该服务未提供时仍明确跳过。
