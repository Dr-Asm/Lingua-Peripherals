# 翻牌显示器 (Flap Display) — Create 联动

> ⚠ 需要安装[机械动力 (Create)](https://github.com/Creators-of-Create/Create) mod。

将 CC: Tweaked 电脑紧邻翻牌显示器（任意一块面板），电脑即可识别外设并控制整个多面板显示屏。

- **外设类型：** `flap_display`
- **条件：** 仅在安装 Create mod 时可用
- **文本上限：** 每次写入 500 字符
- **读取编码：** 非 ASCII 字符自动转为 `\uXXXX` 转义格式

## 外设方法

### getSize()
返回 `(maxCols, maxRows)` — 外接矩形尺寸（最大字符列数, 总行数）。

### isRunning()
返回翻牌显示器是否具有动能驱动。

### getText()
返回所有行的文本内容（table of strings）。非 ASCII 字符已转义编码。

### getLine(line)
返回指定行的文本（1-indexed）。非 ASCII 字符已转义编码。

### write(text)
在当前光标位置写入文本。支持 `\uXXXX` Unicode 转义。

### writeLine(line, text) / setLine(line, text)
直接设置指定行的完整文本。

### setCursorPos(col, row) / getCursorPos()
光标位置控制（均从 1 开始）。

### clearLine(line) / clear()
清空一行或全部行。

### setColor(line, color)
设置指定行的颜色。可用颜色：`white` `orange` `magenta` `light_blue` `yellow` `lime` `pink` `gray` `light_gray` `cyan` `purple` `blue` `brown` `green` `red` `black`

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
