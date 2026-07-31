# Lingua Peripherals

> [English](README.md) | [中文](README_CN.md)

An addon mod for [CC: Tweaked](https://github.com/cc-tweaked/CC-Tweaked) that adds language-related peripherals controllable via Computers.

**Supported Minecraft version:** 1.21.1  
**Loader:** NeoForge 21.1.228  
**License:** [CC BY-NC-SA 4.0](LICENSE)

## Blocks

| Block | Description |
|-------|-------------|
| [Narrator](doc/narrator.md) | Text-to-speech block using Minecraft Narrator system. Full CC Speaker support (playNote/playSound/playAudio/stop) |
| [Creative Narrator](doc/creative_narrator.md) | Indestructible version with global broadcast and full Speaker support |
| [Cassette Drive](doc/cassette_drive.md) | Single-slot drive with data read/write, DFPWM audio playback, and built-in `cassette` command |

## Items

| Item | Description |
|------|-------------|
| [Cassette Tape](doc/cassette_tape.md) | Colorable tape item (16 colors), stores up to 1 MB of data or DFPWM audio |

## Turtle Upgrades

Equip a Narrator or Creative Narrator onto a Turtle to use it as an upgrade.

| Upgrade | Peripheral Type | Description |
|---------|----------------|-------------|
| Narrator | `narrator` | Full Narrator + Speaker functionality on a Turtle |
| Creative Narrator | `creative_narrator` | Full Creative Narrator + globalVoice on a Turtle |

## Display Peripherals

Place a computer adjacent to these existing blocks to use them as peripherals.

| Peripheral | Block | Description |
|-----------|-------|-------------|
| [Flap Display](doc/flap_display.md) `flap_display` | Create Flap Display | Control multi-panel text displays (requires [Create](https://github.com/Creators-of-Create/Create)) |
| [Sign Display](doc/sign_display.md) `sign_display` | Vanilla Sign | Read and write sign text |
| [Lectern Display](doc/lectern_display.md) `lectern_display` | Vanilla Lectern | Page-based book control (requires Book and Quill) |

## Unicode / Chinese Text Support

All peripherals natively support Unicode. Use Lua's `\u{XXXX}` escape sequences directly in your code:

```lua
local n = peripheral.find("narrator")
n.playVoice('\u{4F60}\u{597D}')  -- 你好 (no double-backslash needed)
n.playVoice('\u{4E16}\u{754C}!')  -- 世界!

local s = peripheral.find("sign_display")
s.writeLine(1, '\u{4F60}\u{597D}')

local text = s.readLine(1)  -- returns raw UTF-8 bytes as Lua string
print(#text)                 -- 6 (3 bytes per Unicode character)
s.writeLine(2, text)         -- round-trip: read → write works correctly
```

The mod transparently corrects CC:Tweaked's lossy byte↔string conversion at both boundaries.

## Development

**Requirements:** Java 21, NeoForge 21.1.228, CC: Tweaked 1.119.0+

```bash
./gradlew build
```
