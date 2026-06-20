# Lectern Display

Place a CC: Tweaked computer adjacent to a lectern to read and write book content.

> 💡 The lectern uses **page-based** operations (different from line-based sign/flap display).
> Use `\\n` within text for line breaks.

- **Peripheral type:** `lectern_display`
- **Requirement:** Always available (vanilla block), requires Book & Quill on the lectern
- **Write limitation:** Book & Quill only; signed books are read-only

## Peripheral Methods

### Page Control

#### getPages()
Returns total number of pages.

#### setPage(page)
Sets the current page (1-indexed).

#### getPage()
Returns current page number.

#### getPageText()
Returns full text of the current page.

#### readPage(page)
Reads a specific page.

### Writing

#### write(text)
Writes text at cursor position on current page. Use `\\n` for line breaks.

#### clearPage()
Clears current page.

#### clear()
Resets to a single blank page.

### Cursor

#### setCursorPos(pos) / getCursorPos()
Cursor position within the page (1-indexed character offset).

## Example

```lua
local l = peripheral.find("lectern_display")
l.setPage(1)
l.write("=== Server Log ===\\n")
l.write("Status: Online\\n")
l.write("Players: 5/20")
```
