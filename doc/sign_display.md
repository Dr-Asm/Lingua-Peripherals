# Sign Display

Place a CC: Tweaked computer adjacent to a vanilla sign to read and write sign text.

- **Peripheral type:** `sign_display`
- **Requirement:** Always available (vanilla block)
- **Size:** Fixed 15 characters x 4 lines

## Peripheral Methods

### getSize()
Returns `(15, 4)`.

### readText()
Returns all 4 lines (table of strings). Non-ASCII characters are encoded as `\uXXXX`.

### readLine(line)
Returns line text (1-4). Non-ASCII characters are encoded as `\uXXXX`.

### writeLine(line, text)
Sets a specific line (1-4). Supports `\uXXXX` Unicode escapes.
- Max 100 chars per write; excess will be truncated

### clearLine(line) / clear()
Clears a line or all lines.

## Example

```lua
local s = peripheral.find("sign_display")
s.clear()
s.writeLine(1, "Shop")
s.writeLine(2, "Buy: 1 Diamond")
s.writeLine(3, "Sell: 64 Stone")
```
