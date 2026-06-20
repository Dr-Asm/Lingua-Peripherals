# 翻牌显示器 (Flap Display) — Create 联动

> ⚠ 需要安装[机械动力 (Create)](https://github.com/Creators-of-Create/Create) mod。

将 CC: Tweaked 电脑紧邻翻牌显示器（任意一块面板），电脑即可识别外设并控制整个多面板显示屏。

- **外设类型：** `flap_display`
- **条件：** 仅在安装 Create mod 时可用
- **位置：** 电脑紧邻翻牌显示器面板（任意位置均可，自动定位主控方块）

## 外设方法

### getSize()
返回 `(maxCols, maxRows)` — 外接矩形尺寸（最大字符列数, 总行数）。
每格高度包含上下两行，故 `maxRows = ySize × 2`。

```lua
local cols, rows = f.getSize()
print("Size:", cols, "columns x", rows, "rows")
```

### isRunning()
返回翻牌显示器是否具有动能驱动（需要速度达标才能翻牌动画）。

```lua
if f.isRunning() then print("Flaps are powered!") end
```

### getText()
返回所有行的文本内容（table of strings）。

```lua
local lines = f.getText()
for i, line in ipairs(lines) do print(i, line) end
```

### getLine(line)
返回指定行的文本。`line` 从 1 开始。

### write(text)
在当前光标位置写入文本。支持 `\\uXXXX` Unicode 转义传入中文。

```lua
f.setCursorPos(1, 1)
f.write("\\u4f60\\u597d\\uff0c\\u4e16\\u754c")  -- "你好，世界"
```

### writeLine(line, text) / setLine(line, text)
直接设置指定行的完整文本。

```lua
f.setLine(1, "Server Status: ONLINE")
```

### setCursorPos(col, row) / getCursorPos()
设置/获取光标位置。行列均从 1 开始。

### clearLine(line)
清空指定行。

### clear()
清空所有行。

### setColor(line, color)
设置指定行的颜色。可用颜色：

`white` `orange` `magenta` `light_blue` `yellow` `lime` `pink` `gray`
`light_gray` `cyan` `purple` `blue` `brown` `green` `red` `black`

```lua
f.setColor(1, "red")
f.setLine(1, "ALERT!")
```

## 完整示例

```lua
local f = peripheral.find("flap_display")
if not f then error("Place computer next to a Flap Display") end

-- 查询尺寸
local cols, rows = f.getSize()
print(string.format("Display: %d cols x %d rows", cols, rows))

-- 写入文本
f.clear()
f.setColor(1, "green")
f.setLine(1, "SERVER STATUS")
f.setColor(2, "white")
f.setLine(2, "All systems nominal")

-- 逐字写入（光标方式）
f.setCursorPos(1, 3)
f.write("Players: ")
f.write("3/20")
```
