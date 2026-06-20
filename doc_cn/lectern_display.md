# 讲台 (Lectern Display)

将 CC: Tweaked 电脑紧邻讲台放置，电脑即可读写讲台上书与笔的内容。

> 💡 讲台使用**页**为单位进行操作。文本中使用 `\n` 进行换行。

- **外设类型：** `lectern_display`
- **条件：** 始终可用（原版方块），需讲台上放置书与笔
- **写入限制：** 仅书与笔（可写书），已签名的成书只能读取
- **文本上限：** 每次写入 2000 字符

## 外设方法

### getItem()
返回当前讲台上的物品 ID（如 `"minecraft:writable_book"`、`"minecraft:air"`）。

### getPages()
返回书本当前总页数。

### getPage()
返回当前页码。

### setPage(page)
设置当前页码（从 1 开始）。可设置任意页码，实际页面在 writePage 时创建。

### readPage(page)
读取指定页的全部文本。非 ASCII 字符自动转为 `\uXXXX` 转义格式。

```lua
local text = l.readPage(1)
print(text)  -- "Line one\nLine two"
```

### writePage(page, text)
在指定页写入文本（清空原有文本）。文本中的 `\n` 将作为换行符处理。支持 `\uXXXX` Unicode 转义。
若页码超出当前范围，自动创建中间空白页。

```lua
l.writePage(1, "=== Server Log ===\\nDate: 2026-06-19\\nStatus: Online")
```

### clearPage(page)
清空指定页。

### clear()
清空所有页（重置为空白第一页）。

## 完整示例

```lua
local l = peripheral.find("lectern_display")
if not l then error("Place computer next to a Lectern with Book & Quill") end

-- 写入第 1 页
l.writePage(1, "=== Server Log ===\\nDate: 2026-06-19\\nStatus: Online\\nPlayers: 5/20")

-- 写入第 2 页
l.writePage(2, "=== Player List ===\\n1. Steve\\n2. Alex")

-- 读取
local text = l.readPage(1)
print(text)
```
