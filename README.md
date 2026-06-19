# Lingua Peripherals

> [English](README.md) | [中文](README_CN.md)

An addon mod for [CC: Tweaked](https://github.com/cc-tweaked/CC-Tweaked) that adds language-related peripherals controllable via Computers.

**Supported Minecraft version:** 1.21.1  
**Loader:** NeoForge 21.1.228  
**License:** [CC BY-NC-SA 4.0](LICENSE)

## Blocks

| Block | Description |
|-------|-------------|
| [Narrator](doc/narrator.md) | Text-to-speech block using Minecraft's Narrator system |
| [Creative Narrator](doc/creative_narrator.md) | Indestructible version with global broadcast |

## Chinese Text Support

Use Unicode escape sequences for Chinese TTS. Online converter: [unicode-converter.soe-hentai.win](https://unicode-converter.soe-hentai.win/)

## Development

**Requirements:** Java 21, NeoForge 21.1.228, CC: Tweaked 1.119.0+

```bash
./gradlew build
```