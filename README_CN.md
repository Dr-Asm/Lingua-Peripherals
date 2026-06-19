# Lingua Peripherals - 语言外设

## 概述

Lingua Peripherals 是 [CC: Tweaked](https://github.com/cc-tweaked/CC-Tweaked) 的一个附属模组，添加了可通过电脑控制的语言相关外设。目前包含讲述者（Narrator）方块，使用 Minecraft 内置的 Narrator 系统将文本转换为语音并播放。

**支持的 Minecraft 版本：** 1.21.1  
**加载器：** NeoForge 21.1.228  
**前置模组：** CC: Tweaked

## 方块

### 讲述者 (Narrator)

- **注册名：** linguaperipherals:narrator
- 可被任意等级的镐挖掘并掉落自身。
- 可用扳手（任意含有扳手功能的模组）右键旋转方向，Shift+右键进行拆卸。
- 合成配方：computercraft:speaker + 任意头颅（minecraft:skulls），无序合成。

### 创造讲述者 (Creative Narrator)

- **注册名：** linguaperipherals:creative_narrator
- 无法合成，仅创造模式可获得。
- 生存模式下无法破坏（硬度为 -1），创造模式下可用扳手旋转。

## 使用方法

### 外设方法

将讲述者放置在 CC: Tweaked 电脑的相邻面后，电脑即可识别为一个外设（peripheral.find("narrator")）。

#### playVoice(text [, rad])

播放指定文本的语音。基于距离过滤玩家，仅范围内的玩家能听到。

**参数：**
- text (string) - 需要朗读的文本。
- rad (number, 可选) - 声音可传播的最大半径（格）。默认为 16。该值超出配置中的 globalMaxRange 上限时会被自动限制。

**行为：**
- rad 须为非负数，否则报错。
- 超出 rad 范围的玩家将听不到声音。
- 服务器端按距离过滤后再向客户端播报。

**示例：**
`lua
local speaker = peripheral.find("narrator")
speaker.playVoice("Hello, world!")         -- 默认半径 16 格
speaker.playVoice("Welcome to my base", 32) -- 半径 32 格
`

### 创造讲述者专属方法

#### globalVoice(text)

无视距离向所有维度、所有位置的玩家播放语音。

**参数：**
- text (string) - 需要朗读的文本。

**示例：**
`lua
local speaker = peripheral.find("creative_narrator")
speaker.globalVoice("Attention all players!")
`

### 播放完成事件

语音播报是异步进行的（在客户端播放），playVoice 和 globalVoice 会立即返回。如需按顺序播报多段文本，可以使用 voice_finished 事件等待前一段播完。

**示例（顺序播报）：**
`lua
local speaker = peripheral.find("narrator")

speaker.playVoice("First message")
os.pullEvent("voice_finished")

speaker.playVoice("Second message")
os.pullEvent("voice_finished")

speaker.playVoice("Third message")
`

**注意：** 延时为估算值（至少 1.5 秒），并非精确的播放结束时间。

### 中文语音支持

由于 CC: Tweaked 不支持中文字符串的直接输入，播放中文语音时需要使用 Unicode 转义序列：

`lua
local speaker = peripheral.find("narrator")
speaker.playVoice("\\u4f60\\u597d\\uff0c\\u4e16\\u754c") -- "你好，世界"
`

## 配置

配置文件 lingua_peripherals.conf 在游戏首次运行后自动生成于 config/ 目录下：

| 配置项 | 类型 | 默认值 | 说明 |
|--------|------|--------|------|
| globalMaxRange | double | 128.0 | playVoice 方法 rad 参数的最大限制值（格） |
| speechMaxFrequency | int | 0 | 语音播放最小间隔（毫秒） |

## 开发

### 构建要求

- Java 21
- NeoForge 21.1.228
- CC: Tweaked 1.119.0+

### 构建

`ash
./gradlew build
`

构建产物位于 build/libs/ 目录。