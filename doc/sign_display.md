# Sign Display

Place a CC: Tweaked computer adjacent to a vanilla sign to read and write sign text.

- **Peripheral type:** `sign_display`
- **Requirement:** Always available (vanilla block)
- **Size:** Fixed 15 characters x 4 lines

## Unicode Support

All read methods return raw UTF-8 bytes as Lua strings (not `\\uXXXX` escapes). Write methods accept Lua native `\u{XXXX}` escapes directly.

```lua
local s = peripheral.find("sign_display")
s.writeLine(1, '\u{4F60}\u{597D}')     -- write Unicode
local text = s.readLine(1)               -- returns UTF-8 bytes
print(#text)                             -- 6 (3 bytes/character)
s.writeLine(2, text)                     -- round-trip works
```

## Peripheral Methods

### getSize()
Returns `(15, 4)`.

### readText()
Returns all 4 lines. Each line is a Lua string of raw UTF-8 bytes.

### readLine(line)
Returns line text (1-4) as raw UTF-8 bytes.

### writeLine(line, text)
Sets a specific line (1-4). Supports Lua `\u{XXXX}` Unicode escapes.
- Max 100 chars per write; excess will be truncated

### clearLine(line) / clear()
Clears a line or all lines.

## Example

```lua
local s = peripheral.find("sign_display")
s.clear()
s.writeLine(1, "Shop")
s.writeLine(2, "Buy: 1 Diamond")
s.writeLine(3, '\u{51FA}\u{552E}: 64 \u{77F3}\u{5934}')  -- "出售: 64 石头"
```
