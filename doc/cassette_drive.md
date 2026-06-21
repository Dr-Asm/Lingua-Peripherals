# Cassette Drive

A block that accepts cassette tapes and other items. Right-click to open its single-slot GUI.
When placed next to a CC: Tweaked computer, it acts as a peripheral (type `cassette_drive`).

- **Block ID:** `linguaperipherals:cassette_drive`
- **Hardness:** 2.0 (stone pickaxe required)
- **Wrench support:** Rotate with wrench (right-click), dismantle (shift + right-click)

## Inventory

- Single item slot accessible via GUI (right-click the block)
- Accepts any item; front texture changes based on contents:
  - **Empty:** Default front texture
  - **Cassette tape:** Green accepted indicator on front
  - **Other items:** Red rejected indicator on front
- Contents are dropped when the block is broken or dismantled with a wrench

## Peripheral API

When a cassette tape is inserted, the drive provides the following Lua methods:

| Method | Returns | Description |
|--------|---------|-------------|
| `isTapePresent()` | `boolean` | Whether a cassette tape is inserted |
| `getTapeID()` | `number` or `nil` | The unique ID of the inserted tape |
| `getTapeLabel()` | `string` or `nil` | The label of the inserted tape |
| `setTapeLabel(label)` | — | Sets or clears the tape label (reflected in item name) |
| `ejectTape()` | — | Ejects the tape from the front of the drive |
| `dataSize()` | `number` | Current data file size in bytes |
| `dataSizeLimit()` | `number` | Maximum data size limit in bytes (configurable) |
| `open(mode)` | file handle | Opens the tape data file (modes: "r", "w", "a", "rb", "wb", "ab", "r+", "w+", "a+") |
| `close()` | — | Force-closes any open file handle |
| `reset()` | — | Closes handle and clears all data |

The file handle returned by `open()` supports `read()`, `write()`, `writeLine()`, `seek()`, and `close()` — similar to Lua's `io` library.

## Data Storage

- Tapes store a single binary file per ID under `computercraft/cassette_tape/<id>/data.bin`
- Each tape receives a unique ID on first insertion (stored in `computercraft/ids.json`)
- Size limit is configurable via `cassetteTapeSizeLimit` (default: 256 KB)
- Data persists across world saves

## Crafting

| A | A | A |
| A | B | A |
| A | B | A |

- A = Stone
- B = Iron Nugget