# 告示牌 (Sign Display)

将 CC: Tweaked 电脑紧邻原版告示牌放置，电脑即可读写告示牌文本。

- **外设类型：** `sign_display`
- **条件：** 始终可用（原版方块）
- **尺寸：** 固定 15 字符 × 4 行

## Unicode 支持

所有读取方法返回原始 UTF-8 字节的 Lua 字符串（非 `\\uXXXX` 转义）。写入方法直接支持 Lua `\u{XXXX}` 转义。

```lua
local s = peripheral.find("sign_display")
s.writeLine(1, '\u{4F60}\u{597D}')     -- 写入中文
local text = s.readLine(1)               -- 返回 UTF-8 字节
print(#text)                             -- 6 (每字 3 字节)
s.writeLine(2, text)                     -- 读写闭环
```

## 外设方法

### getSize()
返回 `(15, 4)` — 固定尺寸。

### readText()
返回 4 行文本。每行均为原始 UTF-8 字节的 Lua 字符串。

### readLine(line)
返回第 `line` 行文本（1-4），原始 UTF-8 字节。

### writeLine(line, text)
直接设置第 `line` 行（1-4）。支持 Lua 原生 `\u{XXXX}` 转义。
- 文本上限 100 字符，超出部分自动截断

### clearLine(line) / clear()
清空一行或全部行。

## 示例

```lua
local s = peripheral.find("sign_display")
s.clear()
s.writeLine(1, "Shop")
s.writeLine(2, "Buy: 1 Diamond")
s.writeLine(3, '\u{51FA}\u{552E}: 64 \u{77F3}\u{5934}')  -- "出售: 64 石头"
s.writeLine(4, "Open 24/7")
```
