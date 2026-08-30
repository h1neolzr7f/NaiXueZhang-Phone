# Nai学长工作室 · 安卓独立版

公开源码仓。装在手机上就能搜图、收藏、换角、出图。**不连电脑，不遥控 Windows 工作室**。非官方。

当前发布：**1.5.2-phone.23**（`versionCode` 174）  
电脑版基准：v1.5.2  
包名：`com.naixuezhang.studio.mobile`  
只要 **64 位 Android**（`arm64-v8a`）。debug 签名。

本仓库与 pixiv、NovelAI（Anlatan）、DeepSeek、AITag **没有隶属或合作关系**。使用前请阅读 [LICENSE](LICENSE)、[DISCLAIMER.md](DISCLAIMER.md)、[RESPONSIBLE_USE.md](RESPONSIBLE_USE.md)。

## 直接下载 APK

请到专用下载仓，不用登录 GitHub：

**[打开 NaiXueZhang-Phone-APK](https://github.com/h1neolzr7f/NaiXueZhang-Phone-APK)**

直链（约 72 小时有效）：https://litter.catbox.moe/mukoxq.apk

|项目|值|
|---|---|
|文件|`NaiXueZhang-Phone-1.5.2.apk`|
|大小|约 51MB|
|版本|1.5.2-phone.23 / versionCode 174|
|SHA256|`213ddd93005a70284c05d3e978734d13cd086d492d6f3fa6ae9ed2f5b19e9095`|
|包名|`com.naixuezhang.studio.mobile`|

装法：先卸旧版，手机浏览器打开直链，允许「安装未知应用」。

## 这版要记住的

- **换角页能选模型和参数。** 跟电脑工作台一样：V4.5/V4/V3、尺寸、步数、CFG、采样器、免费档。默认跟随原图。
- **本地库真正写进手机。** 本地搜和在线搜分开，断网也能打开、改名、删除。
- **在线库打不开，不会再把你锁进内置样例。** 样例可选。
- **打开更快。**
- **接口被 Cloudflare 拦时仍能入库：** 点「网页打开在线库」在官网里搜、收藏；也可粘贴 `https://aitag.win/i/作品号`。
- 设置里可选自动 / 官网 / 自己的 https 镜像。
- `aitag.win` 今天仍可能要过人机，图床可能 521。不是安装包坏了。

## 小白三步

1. 设置里填 NovelAI Token 和 DeepSeek Key。Token 每行一个，几个就能并发几路。
2. 「发现」里搜图并点☆收藏。接口不行就用网页库或粘贴链接。
3. 本地库选人、看画风，需要时改模型和参数，点「整系列换角并入队」。

不要只开全局 VPN / TUN。Clash 填 HTTP，例如 `http://127.0.0.1:7890`。出图默认直连。

## 仓库关系

|仓库|用途|
|---|---|
|[NaiXueZhang-Phone](https://github.com/h1neolzr7f/NaiXueZhang-Phone)|本仓库：安卓独立版公开说明|
|[NaiXueZhang-Phone-APK](https://github.com/h1neolzr7f/NaiXueZhang-Phone-APK)|安装包直链|
|[NaiXueZhang-Studio-Upgrade](https://github.com/h1neolzr7f/NaiXueZhang-Studio-Upgrade)|电脑主线|

私人开发仓 `NaiXueZhang-Studio-Phone` 的代码已升到 phone.23；`cursor[bot]` 不能直接推公开源码仓的 main，完整源码同步需你本机 `git push`。

## 许可证

源码 [MIT License](LICENSE)。Token 只写进手机，不要把真实 Token 交进 Git。
