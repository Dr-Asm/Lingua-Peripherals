# Lingua Peripherals — 语言外设

> [English](README.md) | [中文](README_CN.md)

[CC: Tweaked](https://github.com/cc-tweaked/CC-Tweaked) 的附属模组，添加可通过电脑控制的语言相关外设。

**Minecraft 版本：** 1.21.1  
**加载器：** NeoForge 21.1.228  
**许可证：** [CC BY-NC-SA 4.0](LICENSE)

## 方块

| 方块 | 说明 |
|------|------|
| [讲述者 (Narrator)](doc_cn/narrator.md) | 使用 Minecraft Narrator 的文本转语音方块 |
| [创造讲述者 (Creative Narrator)](doc_cn/creative_narrator.md) | 无法破坏的版本，含全局广播功能 |

## 中文语音支持

使用 Unicode 转义序列播报中文。在线转换工具：[unicode-converter.soe-hentai.win](https://unicode-converter.soe-hentai.win/)

## 开发

**环境要求：** Java 21, NeoForge 21.1.228, CC: Tweaked 1.119.0+

```bash
./gradlew build
```