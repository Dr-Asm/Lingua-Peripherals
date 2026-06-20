# 讲台 (Lectern Display)

将 CC: Tweaked 电脑紧邻讲台放置，电脑即可读写讲台上书与笔的内容。

> 💡 讲台使用**页**为单位进行操作，不同于告示牌和翻牌显示器的按行操作。
> 文本中使用 `\\n` 进行换行。

- **外设类型：** `lectern_display`
- **条件：** 始终可用（原版方块），需讲台上放置书与笔
- **写入限制：** 仅书与笔（可写书），已签名的成书只能读取

## 外设方法

### 页控制

#### getPages()
返回书本当前总页数。

```lua
local pages = l.getPages()
print("Total pages:", pages)
```

#### setPage(page)
设置当前操作的页码（从 1 开始）。

```lua
l.setPage(3)  -- 切换到第 3 页
```

#### getPage()
返回当前页码。

#### getPageText()
返回当前页的全部文本。

#### readPage(page)
读取指定页的全部文本。

```lua
local text = l.readPage(1)
print("Page 1:", text)
```

### 写入

#### write(text)
在当前光标位置写入文本。文本中的 `\\n` 将作为换行符处理。

```lua
l.setPage(1)
l.write("Line one\\nLine two\\nLine three")
```

#### clearPage()
清空当前页。

#### clear()
清空所有页（重置为空白第一页）。

### 光标

#### setCursorPos(pos) / getCursorPos()
设置/获取当前页内的光标位置（从 1 开始的字符偏移）。

## 完整示例

```lua
local l = peripheral.find("lectern_display")
if not l then error("Place computer next to a Lectern with Book & Quill") end

-- 在第 1 页写入
l.setPage(1)
l.clear()
l.write("=== Server Log ===\\n")
l.write("Date: 2026-06-19\\n")
l.write("Status: Online\\n")
l.write("Players: 5/20")

-- 在第 2 页写入
l.setPage(2)
l.write("=== Player List ===\\n")
l.write("1. Steve\\n")
l.write("2. Alex\\n")

-- 读取
l.setPage(1)
print(l.getPageText())
```
