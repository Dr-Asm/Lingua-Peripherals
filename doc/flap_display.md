# Flap Display — Create Integration

> ⚠ Requires [Create](https://github.com/Creators-of-Create/Create) mod.

Place a CC: Tweaked computer adjacent to any Flap Display panel to control the entire multi-panel display.

- **Peripheral type:** `flap_display`
- **Requirement:** Only available when Create mod is installed
- **Text limit:** 500 chars per write call
- **Read encoding:** Non-ASCII characters returned as `\uXXXX` escapes

## Peripheral Methods

### getSize(), isRunning(), getText(), getLine()
See [Chinese docs](doc_cn/flap_display.md) or use `peripheral.find` in-game.

### write(text), writeLine(line, text), setLine(line, text)
Write text. Supports `\uXXXX` Unicode escapes.

### setCursorPos(col, row) / getCursorPos()
Cursor control (1-indexed).

### clearLine(line) / clear()
Clear one or all lines.

### setColor(line, color)
16 dye colors supported.

## Example

```lua
local f = peripheral.find("flap_display")
f.clear()
f.setColor(1, "green")
f.writeLine(1, "SERVER STATUS")
f.writeLine(2, "All systems nominal")
```
