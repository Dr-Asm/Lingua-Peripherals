# Cassette Tape

A colorable item crafted with dyes. Stores data or DFPWM-encoded audio when inserted into a Cassette Drive.

- **Item ID:** `linguaperipherals:cassette_tape`
- **Stack size:** 1 (non-stackable)
- **16 colors:** One per dye type (white, orange, magenta, light_blue, yellow, lime, pink, gray, light_gray, cyan, purple, blue, brown, green, red, black)

## Color System

The tape's color is stored in its `minecraft:dyed_color` component. The colored strip on the item texture changes to match.

## Data Storage

- Tapes store up to 256 KB of data (configurable via `cassetteTapeSizeLimit`)
- Each tape gets a unique numeric ID on first insertion into a cassette drive
- The ID appears in the advanced tooltip (F3+H)
- Data persists in the world save directory under `computercraft/cassette_tape/`

## Audio Playback

Cassette tapes can store DFPWM-encoded audio for playback in a Cassette Drive.

Supported DFPWM formats:
- **With header:** CC:Tweaked `cc.audio.dfpwm.encode()` output (with `DFPWM\n` 6-byte header)
- **Without header:** ffmpeg `-f dfpwm` output (raw DFPWM data)

Convert audio to DFPWM with ffmpeg:
```bash
ffmpeg -i input.mp3 -ac 1 -ar 48000 -f dfpwm output.dfpwm
```

Write DFPWM file to tape (Lua):
```lua
local drive = peripheral.find("cassette_drive")
local handle = drive.open("wb")
local f = fs.open("music.dfpwm", "rb")
handle.write(f.readAll())
f.close()
handle.close()
```

## Crafting

**New tape:** 4 ingredients, shapeless:
- Redstone
- Iron Nugget
- Dried Kelp
- Any dye (determines the color)

**Re-dye:** Place an existing tape + any dye in the crafting grid (shapeless) to change its color.

## Recipe Book

Each color has its own recipe entry visible in the recipe book. Look up a colored tape to see which dye produces it.
