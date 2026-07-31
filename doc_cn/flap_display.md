# 翻牌显示器 (Flap Display) — Create 联动

> ⚠ 需要安装[机械动力 (Create)](https://github.com/Creators-of-Create/Create) mod。

将 CC: Tweaked 电脑紧邻翻牌显示器（任意一块面板），电脑即可识别外设并控制整个多面板显示屏。

- **外设类型：** `flap_display`
- **条件：** 仅在安装 Create mod 时可用
- **文本上限：** 每次写入 500 字符

## Unicode 支持

读取方法返回原始 UTF-8 字节的 Lua 字符串。写入方法直接支持 Lua `\u{XXXX}` 转义。

```lua
local f = peripheral.find("flap_display")
f.writeLine(1, '\u{4F60}\u{597D}')     -- 写入中文
local text = f.getLine(1)                -- 返回 UTF-8 字节
f.writeLine(2, text)                     -- 读写闭环
```

## 外设方法

| 方法 | 说明 |
|------|------|
| `getSize()` | 返回 `(maxCols, maxRows)` — 最大字符列数, 总行数 |
| `isRunning()` | 是否有动能驱动 |
| `getText()` | 全部行，返回原始 UTF-8 字节字符串 |
| `getLine(line)` | 指定行（1-indexed），原始 UTF-8 字节 |
| `write(text)` | 在光标位置写入文本 |
| `writeLine(line, text)` / `setLine(line, text)` | 直接设置指定行 |
| `setCursorPos(col, row)` / `getCursorPos()` | 光标位置（均从 1 开始） |
| `clearLine(line)` / `clear()` | 清空一行/全部 |
| `setColor(line, color)` | 16 色: `white` `orange` `magenta` `light_blue` `yellow` `lime` `pink` `gray` `light_gray` `cyan` `purple` `blue` `brown` `green` `red` `black` |

## 示例

```lua
local f = peripheral.find("flap_display")
local cols, rows = f.getSize()
f.clear()
f.setColor(1, "green")
f.writeLine(1, "SERVER STATUS")
f.writeLine(2, "All systems nominal")
f.setCursorPos(1, 3); f.write("Online")
```
