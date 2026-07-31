# Flap Display — Create Integration

> ⚠ Requires [Create](https://github.com/Creators-of-Create/Create) mod.

Place a CC: Tweaked computer adjacent to any Flap Display panel to control the entire multi-panel display.

- **Peripheral type:** `flap_display`
- **Requirement:** Only available when Create mod is installed
- **Text limit:** 500 chars per write call

## Unicode Support

Read methods return raw UTF-8 bytes. Write methods accept Lua `\u{XXXX}` escapes directly.

```lua
local f = peripheral.find("flap_display")
f.writeLine(1, '\u{4F60}\u{597D}')     -- write Unicode
local text = f.getLine(1)                -- returns UTF-8 bytes
f.writeLine(2, text)                     -- round-trip works
```

## Peripheral Methods

| Method | Description |
|--------|-------------|
| `getSize()` | Returns `(maxCols, maxRows)` |
| `isRunning()` | Whether the display has kinetic power |
| `getText()` | All lines as raw UTF-8 byte strings |
| `getLine(line)` | Single line (1-indexed) as raw UTF-8 bytes |
| `write(text)` | Write at cursor position |
| `writeLine(line, text)` / `setLine(line, text)` | Write to specific line |
| `setCursorPos(col, row)` / `getCursorPos()` | Cursor control (1-indexed) |
| `clearLine(line)` / `clear()` | Clear one or all lines |
| `setColor(line, color)` | 16 dye colors: `white` `orange` `magenta` `light_blue` `yellow` `lime` `pink` `gray` `light_gray` `cyan` `purple` `blue` `brown` `green` `red` `black` |

## Example

```lua
local f = peripheral.find("flap_display")
f.clear()
f.setColor(1, "green")
f.writeLine(1, "SERVER STATUS")
f.writeLine(2, "All systems nominal")
```
