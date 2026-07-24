# 磁带机 (Cassette Drive)

可放置的方块，接受磁带和其他物品。右键打开单槽 GUI。
紧邻 CC: Tweaked 电脑放置时可作为外设使用（类型 `cassette_drive`）。

- **方块 ID：** `linguaperipherals:cassette_drive`
- **硬度：** 2.0（需石镐挖掘）
- **扳手支持：** 右键旋转，Shift+右键拆解

## 物品栏

- 右键打开单槽 GUI
- 接受任意物品；正面贴图随内容变化：
  - **空：** 默认正面贴图
  - **磁带：** 正面显示绿色接受标识
  - **其他物品：** 正面显示红色拒绝标识
- 方块被破坏或扳手拆解时内容物掉落

## 外设 API

插入磁带后，磁带机提供以下 Lua 方法：

### 数据读写

| 方法 | 返回值 | 说明 |
|------|--------|------|
| `isTapePresent()` | `boolean` | 是否有磁带插入 |
| `getTapeID()` | `number` 或 `nil` | 磁带的唯一 ID |
| `getTapeLabel()` | `string` 或 `nil` | 磁带的标签 |
| `setTapeLabel(label)` | — | 设置或清除标签（同步更新物品显示名） |
| `ejectTape()` | — | 从驱动器正面弹出磁带 |
| `dataSize()` | `number` | 当前数据文件大小（字节） |
| `dataSizeLimit()` | `number` | 数据文件大小上限（字节，可配置） |
| `open(mode)` | 文件句柄 | 打开磁带数据文件（模式："r", "w", "a", "rb", "wb", "ab", "r+", "w+", "a+"） |
| `close()` | — | 强制关闭所有文件句柄 |
| `reset()` | — | 关闭句柄并清空全部数据 |

`open()` 返回的文件句柄支持 `read()`, `write()`, `writeLine()`, `seek()`, `close()` 等方法，与 Lua `io` 库接口类似。

### 音频播放

磁带机支持播放储存在磁带中的 DFPWM 编码音频文件。支持 CC:Tweaked 编码的带有 `DFPWM\n`（6 字节）头的文件，也支持 ffmpeg 输出的原始 DFPWM 数据。

可通过 CC 电脑控制或红石信号触发播放。播放状态下比较器输出强度 15，否则为 0。

| 方法 | 返回值 | 说明 |
|------|--------|------|
| `playTape()` | `boolean` | 开始或恢复播放。磁带为空或非 DFPWM 格式时会报错 |
| `pauseTape()` | — | 暂停播放，保留播放位置 |
| `stopTape()` | — | 停止播放并重置到开头 |
| `seekTape(seconds)` | — | 跳到指定秒数（`seconds >= 0`） |
| `setVolume(vol)` | — | 设置音量（0.0 ~ config.maxVolume，默认 3.0） |
| `getVolume()` | `number` | 获取当前音量 |
| `isPlaying()` | `boolean` | 是否正在播放 |
| `getPlayPosition()` | `number` | 当前播放位置（秒） |
| `getTapeDuration()` | `number` | 音频总时长（秒）。文件为空或无磁带时返回 0 |
| `saveAudio(audio)` | — | 将 PCM 采样表（-128~127）编码为 DFPWM 并写入磁带，覆盖原有数据 |

### 播放事件

| 事件 | 触发时机 |
|------|---------|
| `tape_play_start` | 开始播放时 |
| `tape_play_end` | 播放完成时（自动重置进度） |

```lua
local d = peripheral.find("cassette_drive")
d.playTape()
local _, side = os.pullEvent("tape_play_end")
print("播放完成: " .. side)
```

### 红石控制

- **脉冲信号（0→非0）：** 从头开始播放
- **比较器输出：** 播放中 = 15，停止/暂停 = 0

### 音量说明

`volume` 指定声音能被听见的距离（≥ 0.0）。对小于 1.0 的值，声音会相对减轻，可闻范围缩小。对大于 1.0 的值，声音不增大但可闻范围（1.0 时 16 米）与音量相乘。声音基于球体中心距离逐渐衰减。上限可在 `lingua_peripherals.conf` 中配置（默认 3.0）。

### 配置项

| 配置项 | 类型 | 默认值 | 说明 |
|--------|------|--------|------|
| `cassetteTapeSizeLimit` | int | 1048576 (1MB) | 磁带数据文件大小上限（字节） |
| `maxVolume` | double | 3.0 | 播放音量上限 |
| `cassetteBroadcastAudio` | boolean | false | `true`：音频发送给全体在线玩家（无视距离）；`false`：仅发送给可闻范围内的玩家 |

## 数据存储

- 磁带以单文件形式存储于 `computercraft/cassette_tape/<id>/data.bin`
- 首次插入时自动分配唯一 ID（记录在 `computercraft/ids.json` 中）
- 大小上限可通过 `cassetteTapeSizeLimit` 配置（默认 1 MB）
- 数据随世界存档持久保存

## 内置程序

模组自带 `cassette` 命令，在**所有** CC 电脑上均可直接使用（不需要磁带机——无磁碟机时会优雅报错退出）：

| 命令 | 说明 |
|------|------|
| `cassette play` | 开始/恢复播放 |
| `cassette pause` | 暂停播放 |
| `cassette stop` | 停止播放并重置位置 |
| `cassette label` | 显示当前磁带标签 |
| `cassette label <文字>` | 设置或清除磁带标签 |
| `cassette volume` | 显示当前音量 |
| `cassette volume <0-3>` | 设置音量 |
| `cassette write <文件>` | 将 CC 电脑中的文件写入磁带 |
| `cassette wget <url>` | 从 URL 下载文件到磁带（先通过 HTTP HEAD 检查大小） |

### 示例

```
cassette play
cassette volume 2.0
cassette label My Mixtape
cassette write disk/audio.dfpwm
cassette wget https://example.com/sound.dfpwm
```

## 合成表

| A | A | A |
| A | B | A |
| A | B | A |

- A = 石头
- B = 铁粒
