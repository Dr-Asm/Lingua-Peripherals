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

## 数据存储

- 磁带以单文件形式存储于 `computercraft/cassette_tape/<id>/data.bin`
- 首次插入时自动分配唯一 ID（记录在 `computercraft/ids.json` 中）
- 大小上限可通过 `cassetteTapeSizeLimit` 配置（默认 256 KB）
- 数据随世界存档持久保存

## 合成表

| A | A | A |
| A | B | A |
| A | B | A |

- A = 石头
- B = 铁粒