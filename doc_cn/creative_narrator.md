# 创造讲述者 (Creative Narrator)

![创造讲述者](../doc_img/creative_narrator.png)

讲述者的创造模式版本，无法破坏，额外提供全局广播功能，并继承完整的 Speaker 功能。

- **注册名：** `linguaperipherals:creative_narrator`
- **外设类型：** `creative_narrator`
- **硬度：** -1.0（生存模式无法破坏）
- **扳手：** 仅创造模式可用，右键旋转
- **配方：** 无法合成
- **Turtle 升级：** 支持 — 可装备到 Turtle 左手/右手

## 外设方法

创造讲述者继承 [讲述者](narrator.md) 的所有方法（包括 `playVoice`、`playNote`、`playSound`、`playAudio`、`stop`），并额外包含：

### globalVoice(text)

向**所有维度、所有位置**的玩家播报语音，无视距离。

**参数：**
- `text` (string) — 需要朗读的文本。

**返回：** 发送成功返回 `true`

**示例：**
```lua
local n = peripheral.find("creative_narrator")
n.globalVoice("服务器将在 5 分钟后关闭！")
```

## Turtle 集成

创造讲述者可装备到 Turtle 的左手或右手栏位。装备后 Turtle 获得完整创造讲述者功能，包括 `globalVoice`。
