# 磁带 (Cassette Tape)

可染色的物品，通过染料合成着色。插入磁带机后可存储数据或播放 DFPWM 编码音频。

- **物品 ID：** `linguaperipherals:cassette_tape`
- **堆叠：** 1（不可堆叠）
- **16 色：** 每种染料对应一种颜色（白、橙、品红、淡蓝、黄、黄绿、粉、灰、淡灰、青、紫、蓝、棕、绿、红、黑）

## 颜色系统

磁带颜色存储在 `minecraft:dyed_color` 组件中。物品贴图的彩色条纹会随颜色变化。

## 数据存储

- 磁带最多可存储 1 MB 数据（可通过 `cassetteTapeSizeLimit` 配置）
- 首次插入磁带机时自动分配唯一数字 ID
- ID 可在高级提示框（F3+H）中查看
- 数据持久保存在世界存档目录下的 `computercraft/cassette_tape/` 中

## 音频播放

磁带可作为 DFPWM 编码音频的存储介质。将 DFPWM 数据写入磁带后，可在磁带机中播放。

支持的 DFPWM 格式：
- **带头文件：** CC:Tweaked `cc.audio.dfpwm.encode()` 输出（带 `DFPWM\n` 6 字节头）
- **无头文件：** ffmpeg `-f dfpwm` 输出（原始 DFPWM 数据）

使用 ffmpeg 转换音频到 DFPWM：
```bash
ffmpeg -i input.mp3 -ac 1 -ar 48000 -f dfpwm output.dfpwm
```

将 DFPWM 文件写入磁带（Lua）：
```lua
local drive = peripheral.find("cassette_drive")
local handle = drive.open("wb")
local f = fs.open("music.dfpwm", "rb")
handle.write(f.readAll())
f.close()
handle.close()
```

## 合成

**新建磁带：** 4 种原料，无序合成：
- 红石粉
- 铁粒
- 干海带
- 任意染料（决定颜色）

**重新染色：** 将已有磁带 + 任意染料放入合成格（无序），即可改变颜色。

## 配方书

每种颜色在配方书中均有独立配方条目。点击有色磁带可查看所需染料的合成方式。
