# 讲述者 (Narrator)

![讲述者](../doc_img/narrator.png)

使用 Minecraft 内置 Narrator（讲述人）系统进行文本转语音播报的方块，可通过 CC: Tweaked 电脑控制。

- **注册名：** `linguaperipherals:narrator`
- **外设类型：** `narrator`
- **硬度：** 2.0
- **挖掘工具：** 任意镐，掉落自身
- **抗爆性：** 6.0
- **扳手：** 右键旋转，Shift+右键拆卸
- **配方：** `computercraft:speaker` + 任意头颅（无序合成）

## 外设方法

### playVoice(text [, rad])

播报指定文本的语音，仅半径内的玩家能听到。

**参数：**
- `text` (string) — 需要朗读的文本。非 ASCII 字符使用 `\uXXXX` 转义。
- `rad` (number, 可选) — 声音可传播的最大半径（格）。默认为 16。超出配置中的 `globalMaxRange`（默认 128）会被自动限制。

**返回：** 发送成功返回 `true`

**错误：** `rad` 为负数时抛出 LuaException

**示例：**
```lua
local n = peripheral.find("narrator")
n.playVoice("Hello, world!")           -- 半径 16 格
n.playVoice("欢迎！", 64)               -- 半径 64 格
n.playVoice("\\u4f60\\u597d", 32)       -- 中文转义
```

## 事件

### voice_finished

估算播放时长后触发（最少 1.5 秒），用于按顺序播报。

```lua
local n = peripheral.find("narrator")
n.playVoice("第一条消息")
os.pullEvent("voice_finished")
n.playVoice("第二条消息")
```

## 配置

| 配置项 | 类型 | 默认值 | 说明 |
|--------|------|--------|------|
| globalMaxRange | double | 128.0 | playVoice 中 rad 参数的上限 |
| speechMaxFrequency | int | 0 | 播放最小间隔（毫秒） |