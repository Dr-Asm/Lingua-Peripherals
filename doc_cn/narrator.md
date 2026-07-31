# 讲述者 (Narrator)

![讲述者](../doc_img/narrator.png)

使用 Minecraft 内置 Narrator（讲述人）系统进行文本转语音播报的方块，可通过 CC: Tweaked 电脑控制。同时完整支持 CC Speaker 的全部方法 (playNote/playSound/playAudio/stop)。

- **注册名：** `linguaperipherals:narrator`
- **外设类型：** `narrator`
- **硬度：** 2.0
- **挖掘工具：** 任意镐，掉落自身
- **抗爆性：** 6.0
- **扳手：** 右键旋转，Shift+右键拆卸
- **配方：** `computercraft:speaker` + 任意头颅（无序合成）
- **Turtle 升级：** 支持 — 可装备到 Turtle 左手/右手

## 外设方法

### 讲述者方法

#### playVoice(text [, rad])

播报指定文本的语音，仅半径内的玩家能听到。

**参数：**
- `text` (string) — 需要朗读的文本。使用 Lua `\u{XXXX}` 转义。
- `rad` (number, 可选) — 声音可传播的最大半径（格）。默认为 16。超出配置中的 `globalMaxRange`（默认 128）会被自动限制。

**返回：** 发送成功返回 `true`

**错误：** `rad` 为负数时抛出 LuaException

**示例：**
```lua
local n = peripheral.find("narrator")
n.playVoice("Hello, world!")           -- 半径 16 格
n.playVoice("欢迎！", 64)               -- 半径 64 格
n.playVoice('\u{4F60}\u{597D}', 32)     -- 中文文本
```

### Speaker 方法（继承自 CC Speaker）

#### playNote(instrument [, volume [, pitch]])

通过讲述者播放音符盒音效。

**参数：**
- `instrument` (string) — 乐器名称（如 `"harp"`、`"basedrum"`、`"flute"`）。
- `volume` (number, 可选) — 音量 0.0~3.0。默认：1.0。
- `pitch` (number, 可选) — 半音音高 0~24。默认：12。

**返回：** 达到每 tick 8 个音符的限制时返回 `false`。

#### playSound(name [, volume [, pitch]])

通过讲述者播放 Minecraft 游戏音效。

**参数：**
- `name` (string) — 音效 ID（如 `"minecraft:entity.creeper.primed"`）。
- `volume` (number, 可选) — 音量 0.0~3.0。默认：1.0。
- `pitch` (number, 可选) — 播放速度 0.5~2.0。默认：1.0。

**返回：** 已有音频播放中时返回 `false`。

#### playAudio(audio [, volume])

向讲述者流式传输自定义音频。接收 8-bit PCM 采样表（-128~127，48kHz）。

**参数：**
- `audio` (table) — PCM 采样表。
- `volume` (number, 可选) — 音量 0.0~3.0。

**返回：** 内部缓冲区满时返回 `false`。等待 `speaker_audio_empty` 事件后重试。

**示例：**
```lua
local dfpwm = require("cc.audio.dfpwm")
local n = peripheral.find("narrator")
local decoder = dfpwm.make_decoder()
for chunk in io.lines("data/example.dfpwm", 16 * 1024) do
    local buffer = decoder(chunk)
    while not n.playAudio(buffer) do
        os.pullEvent("speaker_audio_empty")
    end
end
```

#### stop()

停止当前播放的所有音频并清空缓冲区。

## 事件

### voice_finished

估算播放时长后触发（最少 1.5 秒），用于按顺序播报。

```lua
local n = peripheral.find("narrator")
n.playVoice("第一条消息")
os.pullEvent("voice_finished")
n.playVoice("第二条消息")
```

### speaker_audio_empty

内部音频缓冲区有空间时触发，配合 `playAudio` 使用。

## Turtle 集成

讲述者方块可装备到 Turtle 的左手或右手栏位。装备后 Turtle 获得完整讲述者和 Speaker 功能。从 Turtle 的电脑中使用 `peripheral.find("narrator")` 即可。

## 配置

| 配置项 | 类型 | 默认值 | 说明 |
|--------|------|--------|------|
| globalMaxRange | double | 128.0 | playVoice 中 rad 参数的上限 |
| speechMaxFrequency | int | 0 | 播放最小间隔（毫秒） |
