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

当前公开快照不包含签名文件、真实凭据、用户数据，也不包含发行包中的全部大型运行资源。它适合代码审阅和契约测试；不能据此声称可逐字节复现 phone.23 APK。

## 验证

CI 的公开源码档位运行：

```bash
node tests/standalone_core_test.js
python3 tests/test_mobile_standalone.py
```

不需要真实 NovelAI Token，也不会调用付费生成。只在发行工作区存在的大型索引、模型和预览工具会被明确跳过；APK 发布前还需在 Android 构建环境做完整验证。

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
