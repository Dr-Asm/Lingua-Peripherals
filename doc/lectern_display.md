# Lectern Display

Place a CC: Tweaked computer adjacent to a lectern to read and write book content.

> 💡 The lectern uses **page-based** operations. Use `\n` within text for line breaks.

- **Peripheral type:** `lectern_display`
- **Requirement:** Always available (vanilla block), requires Book & Quill on the lectern
- **Write limitation:** Book & Quill only; signed books are read-only
- **Text limit:** 2000 chars per write call

## Unicode Support

```lua
local l = peripheral.find("lectern_display")
l.writePage(1, '\u{4F60}\u{597D}')           -- write Unicode
local text = l.readPage(1)                     -- returns UTF-8 bytes
l.writePage(2, text)                           -- round-trip works
```

## Peripheral Methods

### getItem()
Returns the item ID on the lectern (e.g. `"minecraft:writable_book"`, `"minecraft:air"`).

### getPages()
Returns total number of pages.

### getPage() / setPage(page)
Get or set the current page (1-indexed). Can set any page number; pages are created on write.

### readPage(page)
Reads a specific page. Returns raw UTF-8 bytes as a Lua string.

### writePage(page, text)
Writes text to a specific page (replaces existing content). Use `\n` for line breaks. Supports Lua `\u{XXXX}` escapes. Creates intermediate pages if needed.

### clearPage(page) / delPage(page) / clear()
See Chinese docs for details.

## Example

```lua
local l = peripheral.find("lectern_display")
l.writePage(1, "=== Server Log ===\nStatus: Online\nPlayers: 5/20")
l.writePage(2, "=== Player List ===\n1. Steve\n2. Alex")
print(l.readPage(1))
```
