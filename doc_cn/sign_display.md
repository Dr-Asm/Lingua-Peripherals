# 告示牌 (Sign Display)

将 CC: Tweaked 电脑紧邻原版告示牌放置，电脑即可读写告示牌文本。

- **外设类型：** `sign_display`
- **条件：** 始终可用（原版方块）
- **尺寸：** 固定 15 字符 × 4 行

## 外设方法

### getSize()
返回 `(15, 4)` — 固定尺寸。

### readText()
返回 4 行文本（table of strings）。非 ASCII 字符自动转为 `\uXXXX` 转义格式。

### readLine(line)
返回第 `line` 行文本（1-4）。非 ASCII 字符自动转为 `\uXXXX` 转义格式。

### writeLine(line, text)
直接设置第 `line` 行（1-4）。支持 `\uXXXX` Unicode 转义。
- 文本上限 100 字符

### clearLine(line)
清空第 `line` 行。

### clear()
清空全部 4 行。

## 示例

```lua
local s = peripheral.find("sign_display")
s.clear()
s.writeLine(1, "Shop")
s.writeLine(2, "Buy: 1 Diamond")
s.writeLine(3, "\\u51fa\\u552e: 64 \\u77f3\\u5934")  -- "出售: 64 石头"
s.writeLine(4, "Open 24/7")
```
