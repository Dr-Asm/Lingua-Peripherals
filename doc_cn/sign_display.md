# 告示牌 (Sign Display)

将 CC: Tweaked 电脑紧邻原版告示牌放置，电脑即可读写告示牌文本。

- **外设类型：** `sign_display`
- **条件：** 始终可用（原版方块）
- **尺寸：** 固定 15 字符 × 4 行

## 外设方法

### getSize()
返回 `(15, 4)` — 固定尺寸。

### getText()
返回 4 行文本（table of strings）。

### getLine(line)
返回第 `line` 行文本（1-4）。

### write(text)
在当前光标位置写入文本。支持 `\\uXXXX` Unicode 转义。

### writeLine(line, text) / setLine(line, text)
直接设置第 `line` 行（1-4）。

### setCursorPos(col, row) / getCursorPos()
设置/获取光标位置。

### clearLine(line)
清空第 `line` 行。

### clear()
清空全部 4 行。

## 示例

```lua
local s = peripheral.find("sign_display")
s.clear()
s.setLine(1, "Shop")
s.setLine(2, "Buy: 1 Diamond")
s.setLine(3, "Sell: 64 Stone")
s.setLine(4, "Open 24/7")
```
