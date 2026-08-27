# Nai学长工作室 · 安卓独立版

公开源码仓库。这是装在手机上就能用的独立 App，**不连电脑，不遥控 Windows 工作室**。

当前发布：**1.5.2-phone.16**（`versionCode` 167）  
电脑版基准：v1.5.2  
包名：`com.naixuezhang.studio.mobile`  
只要 **64 位 Android**（`arm64-v8a`）。这是 debug 签名包。

本仓库与 pixiv、NovelAI（Anlatan）、DeepSeek **没有隶属或合作关系**。名称只用来说明兼容对象。使用前请阅读 [LICENSE](LICENSE)、[DISCLAIMER.md](DISCLAIMER.md)、[RESPONSIBLE_USE.md](RESPONSIBLE_USE.md)。

## 直接下载 APK

手机浏览器打开下面链接，等下载完成再安装：

- [下载 NaiXueZhang-Phone-1.5.2.apk](https://github.com/h1neolzr7f/NaiXueZhang-Studio-Upgrade/raw/cursor/android-phone-standalone-9162/android/download/NaiXueZhang-Phone-1.5.2.apk)

|项目|值|
|---|---|
|文件|`NaiXueZhang-Phone-1.5.2.apk`|
|大小|约 51MB|
|版本|1.5.2-phone.16 / versionCode 167|
|SHA256|`2a8c1adbe6b4b8c60bf711c6344779f68c76e33c638cd877c8bb6a3cbdcdc8ca`|
|包名|`com.naixuezhang.studio.mobile`|

装法：

1. 先卸载旧的「Nai学长工作室」，再用手机浏览器打开上面的链接。
2. 打开下载文件。若系统拦截，到设置里允许「安装未知应用」。
3. 打开应用，点右上角「设置」，填 **NovelAI Token** 和 **DeepSeek Key**。Token 每行一个，几个就能并发几路。
4. 「发现」里搜图并点☆收藏。收藏后走本机：本地库、图库、排队、换角断网也能看。

不要只开全局 VPN / TUN。Clash 填 HTTP，例如 `http://127.0.0.1:7890`。出图默认直连。

## 这个版本做什么

应用在手机里起一个只监听 `127.0.0.1:18797` 的小服务。发现、换角草稿、出图都在这台手机完成。

- 搜 AITag 在线库并收藏入库；在线挂了也能用内置样例
- 收藏先收屏幕上的图和咒语，不用等原图下完
- 收藏后咒语到了就能换角、换画风和生成
- 本机打进电脑同款全量 D 站角色包（约 35 万角色）+ 明日方舟库 + OC（整段咒语，支持 `{{` 权重 `}}`）
- 换角方式摊开：本页换角、按槽位换、全部页换、整系列换角并入队
- 多个 NovelAI Token 可并发出图；失败可手动重试或删除，5xx / `unknown` 不自动重试
- 图库按生成任务分组；出图先入库，再按需超分 / 清元数据
- 打码默认关。可选像素 / 模糊 / 线条 / 纯色 / 黑条 / 表情。部位、灵敏度、强度、外扩按理塘 / ANR 对齐
- 打码模型已打进 APK：`censor.onnx` + ONNX Runtime（arm64）

不做：

- 不遥控电脑、不配对电脑、不填电脑服务地址
- 不移植 Pixiv 发布、Live2D、管家 / 导演台、完整 Pixiv SQLite
- 电脑整套 ANR / YOLO / Windows 运行时不打进包

## 从源码构建

目录必须保持这样，Gradle 才会找到网页和角色索引：

```text
NaiXueZhang-Phone/
  android/          # Gradle 工程
  web/m/            # 手机壳
  web/shared/
  data/             # 角色 / 画风索引
```

环境：JDK 17、Android SDK 34。在 `android/` 里：

```bash
echo "sdk.dir=/path/to/Android/sdk" > local.properties
./gradlew assembleDebug
```

`assembleDebug` 会先跑 `syncPhoneAssets`：从 `web/` 和 `data/` 拷进 `android/app/src/main/assets/`。生成出来的 `www/` 和 `assets/data/` 不要提交。

电脑上预览手机壳：

```bash
python3 scripts/phone_preview_server.py
# http://127.0.0.1:18797/m?standalone=1
```

```bash
node tests/standalone_core_test.js
python3 -m unittest tests.test_mobile_standalone
```

## 仓库关系

|仓库|用途|
|---|---|
|[NaiXueZhang-Phone](https://github.com/h1neolzr7f/NaiXueZhang-Phone)|本仓库：安卓独立版开源文档与源码|
|[NaiXueZhang-Studio-Upgrade](https://github.com/h1neolzr7f/NaiXueZhang-Studio-Upgrade)|电脑升级版主干；当前 APK 也在这里发布|
|[NaiXueZhang-Studio](https://github.com/h1neolzr7f/NaiXueZhang-Studio)|电脑稳定版 / 一键包|

私人占位仓 `NaiXueZhang-Studio-Phone` 不是正式发布仓。

## 许可证与第三方

- 本仓库源码：[MIT License](LICENSE)
- ONNX Runtime（Microsoft）：见 `android/app/src/main/assets/onnxruntime-LICENSE.txt`
- `censor.onnx` 与理塘百宝箱手机版同款，仅用于本机可选打码
- NovelAI、DeepSeek、AITag 的账号、额度、内容规则由各平台自行决定

Token 和 Key 只写进应用私有存储，日志里不会打印。不要把真实 Token 提交进 Git。
