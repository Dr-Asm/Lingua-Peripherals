# Cassette Drive

A block that accepts cassette tapes and other items. Right-click to open its single-slot GUI.

> Coming soon: CC: Tweaked peripheral integration for cassette tape operations.

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

## Crafting

| A | A | A |
| A | B | A |
| A | B | A |

- A = Stone
- B = Iron Nugget