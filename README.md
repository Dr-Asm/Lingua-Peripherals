# Lingua Peripherals

> [English](README.md) | [中文](README_CN.md)

An addon mod for [CC: Tweaked](https://github.com/cc-tweaked/CC-Tweaked) that adds language-related peripherals controllable via Computers.

**Supported Minecraft version:** 1.21.1  
**Loader:** NeoForge 21.1.228  
**License:** [CC BY-NC-SA 4.0](LICENSE)

## Blocks

| Block | Description |
|-------|-------------|
| [Narrator](doc/narrator.md) | Text-to-speech block using Minecraft Narrator system |
| [Creative Narrator](doc/creative_narrator.md) | Indestructible version with global broadcast |
| [Cassette Drive](doc/cassette_drive.md) | Single-slot drive accepting cassette tapes with data read/write support |

## Items

| Item | Description |
|------|-------------|
| [Cassette Tape](doc/cassette_tape.md) | Colorable tape item (16 colors), stores up to 256 KB of data |

## Display Peripherals

Place a computer adjacent to these existing blocks to use them as peripherals.

| Peripheral | Block | Description |
|-----------|-------|-------------|
| [Flap Display](doc/flap_display.md) `flap_display` | Create Flap Display | Control multi-panel text displays (requires [Create](https://github.com/Creators-of-Create/Create)) |
| [Sign Display](doc/sign_display.md) `sign_display` | Vanilla Sign | Read and write sign text |
| [Lectern Display](doc/lectern_display.md) `lectern_display` | Vanilla Lectern | Page-based book control (requires Book and Quill) |

## Chinese Text Support

Use Unicode escape sequences for Chinese text input. Online converter: [unicode-converter.soe-hentai.win](https://unicode-converter.soe-hentai.win/)

## Development

**Requirements:** Java 21, NeoForge 21.1.228, CC: Tweaked 1.119.0+

```bash
./gradlew build
```
