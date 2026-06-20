# Sign Display

Place a CC: Tweaked computer adjacent to a vanilla sign to read and write sign text.

- **Peripheral type:** `sign_display`
- **Requirement:** Always available (vanilla block)
- **Size:** Fixed 15 characters x 4 lines

## Peripheral Methods

### getSize()
Returns `(15, 4)`.

### getText()
Returns all 4 lines as a table of strings.

### getLine(line)
Returns line text (1-4).

### write(text)
Writes at cursor position. Supports `\\uXXXX` Unicode escapes.

### writeLine(line, text) / setLine(line, text)
Sets a specific line (1-4).

### setCursorPos(col, row) / getCursorPos()
Cursor control.

### clearLine(line) / clear()
Clears a line or all lines.

## Example

```lua
local s = peripheral.find("sign_display")
s.clear()
s.setLine(1, "Shop")
s.setLine(2, "Buy: 1 Diamond")
s.setLine(3, "Sell: 64 Stone")
```
