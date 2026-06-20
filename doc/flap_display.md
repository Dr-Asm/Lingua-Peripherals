# Flap Display — Create Integration

> ⚠ Requires [Create](https://github.com/Creators-of-Create/Create) mod.

Place a CC: Tweaked computer adjacent to any Flap Display panel. The computer will detect the peripheral and control the entire multi-panel display.

- **Peripheral type:** `flap_display`
- **Requirement:** Only available when Create mod is installed
- **Placement:** Computer adjacent to any flap display panel (controller auto-detected)

## Peripheral Methods

### getSize()
Returns `(maxCols, maxRows)` — bounding rectangle dimensions.

```lua
local cols, rows = f.getSize()
print("Size:", cols, "columns x", rows, "rows")
```

### isRunning()
Returns whether the flap display has sufficient kinetic power for animation.

### getText()
Returns all lines as a table of strings.

### getLine(line)
Returns the text of the specified line (1-indexed).

### write(text)
Writes text at the current cursor position. Supports `\\uXXXX` Unicode escapes for CJK text.

### writeLine(line, text) / setLine(line, text)
Directly sets the complete text of a specific line.

### setCursorPos(col, row) / getCursorPos()
Cursor position control (1-indexed).

### clearLine(line)
Clears a specific line.

### clear()
Clears all lines.

### setColor(line, color)
Sets the text color for a line. Available colors:
`white`, `orange`, `magenta`, `light_blue`, `yellow`, `lime`, `pink`, `gray`,
`light_gray`, `cyan`, `purple`, `blue`, `brown`, `green`, `red`, `black`

## Example

```lua
local f = peripheral.find("flap_display")
f.clear()
f.setColor(1, "green")
f.setLine(1, "SERVER STATUS")
f.setLine(2, "All systems nominal")
```
