# Cassette Tape

A colorable item crafted with dyes. Stores a color value used for future cassette drive operations.

> Coming soon: CC: Tweaked peripheral integration for cassette tape operations.

- **Item ID:** `linguaperipherals:cassette_tape`
- **Stack size:** 1 (non-stackable)
- **16 colors:** One per dye type (white, orange, magenta, light_blue, yellow, lime, pink, gray, light_gray, cyan, purple, blue, brown, green, red, black)

## Color System

The tape's color is stored in its `minecraft:dyed_color` component. The colored strip on the item texture changes to match.

## Crafting

**New tape:** 4 ingredients, shapeless:
- Redstone
- Iron Nugget
- Dried Kelp
- Any dye (determines the color)

**Re-dye:** Place an existing tape + any dye in the crafting grid (shapeless) to change its color.

## Recipe Book

Each color has its own recipe entry visible in the recipe book. Look up a colored tape to see which dye produces it.