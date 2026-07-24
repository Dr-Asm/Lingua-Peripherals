# Lingua Peripherals —— 语言外设

> [English](README.md) | [中文](README_CN.md)

[CC: Tweaked](https://github.com/cc-tweaked/CC-Tweaked) 的附属模组，添加可通过电脑控制的语言相关外设。

**Minecraft 版本：** 1.21.1  
**加载器：** NeoForge 21.1.228  
**许可证：** [CC BY-NC-SA 4.0](LICENSE)

## 方块

| 方块 | 说明 |
|------|------|
| [讲述者 (Narrator)](doc_cn/narrator.md) | 使用 Minecraft Narrator 的文本转语音方块，完整支持 CC Speaker 方法 (playNote/playSound/playAudio/stop) |
| [创造讲述者 (Creative Narrator)](doc_cn/creative_narrator.md) | 无法破坏的版本，含全局广播和完整 Speaker 功能 |
| [磁带机 (Cassette Drive)](doc_cn/cassette_drive.md) | 单槽驱动器，支持数据读写、DFPWM 音频播放，自带 `cassette` 内置命令 |

## 物品

| 物品 | 说明 |
|------|------|
| [磁带 (Cassette Tape)](doc_cn/cassette_tape.md) | 可染色磁带（16 色），可存储最多 1 MB 数据或 DFPWM 音频 |

## Turtle 升级

将讲述者或创造讲述者装备到 Turtle 的左手/右手栏位，即可作为 Turtle 升级使用。

| 升级 | 外设类型 | 说明 |
|------|---------|------|
| 讲述者 | `narrator` | Turtle 上使用完整讲述者 + Speaker 功能 |
| 创造讲述者 | `creative_narrator` | Turtle 上使用完整创造讲述者 + globalVoice |

## 显示外设

将电脑紧邻以下已有方块放置即可作为外设使用。

| 外设 | 对应方块 | 说明 |
|------|---------|------|
| [翻牌显示器](doc_cn/flap_display.md) `flap_display` | Create 翻牌显示器 | 控制多面板文字显示屏（需安装[Create](https://github.com/Creators-of-Create/Create)） |
| [告示牌](doc_cn/sign_display.md) `sign_display` | 原版告示牌 | 读写告示牌文本 |
| [讲台](doc_cn/lectern_display.md) `lectern_display` | 原版讲台 | 按页控制书本内容（需书与笔） |

## 中文支持

使用 Unicode 转义序列输入中文。在线转换工具：[unicode-converter.soe-hentai.win](https://unicode-converter.soe-hentai.win/)

## 开发

**环境要求：** Java 21, NeoForge 21.1.228, CC: Tweaked 1.119.0+

```bash
./gradlew build
```
