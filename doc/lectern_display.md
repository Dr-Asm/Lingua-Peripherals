# Lectern Display

Place a CC: Tweaked computer adjacent to a lectern to read and write book content.

> 💡 The lectern uses **page-based** operations. Use `\n` within text for line breaks.

- **Peripheral type:** `lectern_display`
- **Requirement:** Always available (vanilla block), requires Book & Quill on the lectern
- **Write limitation:** Book & Quill only; signed books are read-only
- **Text limit:** 2000 chars per write call

## Peripheral Methods

### getItem()
Returns the item ID on the lectern (e.g. `"minecraft:writable_book"`, `"minecraft:air"`).

### getPages()
Returns total number of pages.

### getPage() / setPage(page)
Get or set the current page (1-indexed). Can set any page number; pages are created on write.

### readPage(page)
Reads a specific page. Non-ASCII characters are encoded as `\uXXXX`.

### writePage(page, text)
Writes text to a specific page (replaces existing content). Use `\n` for line breaks. Supports `\uXXXX` escapes. Creates intermediate pages if needed.

### clearPage(page)
Clears a specific page.

### clear()
Resets to a single blank page.

## Example

```lua
local l = peripheral.find("lectern_display")
l.writePage(1, "=== Server Log ===\\nStatus: Online\\nPlayers: 5/20")
l.writePage(2, "=== Player List ===\\n1. Steve\\n2. Alex")
print(l.readPage(1))
```
