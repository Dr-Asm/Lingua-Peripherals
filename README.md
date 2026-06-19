# Lingua Peripherals

## Overview

Lingua Peripherals is an addon mod for [CC: Tweaked](https://github.com/cc-tweaked/CC-Tweaked) that adds language-related peripherals controllable via Computers. Currently includes the Narrator block, which uses Minecraft's built-in Narrator system for text-to-speech playback.

**Supported Minecraft version:** 1.21.1  
**Loader:** NeoForge 21.1.228  
**Dependencies:** CC: Tweaked

## Blocks

### Narrator

- **Registry name:** linguaperipherals:narrator
- Mineable with any pickaxe and drops itself.
- Can be rotated with a wrench (from any mod) by right-click, and shift+right-click to dismantle.
- Recipe: computercraft:speaker + any skull (minecraft:skulls), shapeless crafting.

### Creative Narrator

- **Registry name:** linguaperipherals:creative_narrator
- Not craftable, only obtainable in Creative mode.
- Indestructible in Survival mode (hardness -1). Wrench interaction allowed in Creative mode.

## Usage

### Peripheral Methods

When a Narrator is placed adjacent to a CC: Tweaked computer, it is recognized as a peripheral via peripheral.find("narrator").

#### playVoice(text [, rad])

Plays the specified text as speech. Players are filtered by distance.

**Parameters:**
- text (string) - The text to speak.
- rad (number, optional) - Maximum audible radius in blocks. Default: 16. Values exceeding the globalMaxRange config limit will be clamped.

**Example:**
`lua
local speaker = peripheral.find("narrator")
speaker.playVoice("Hello, world!")          -- default radius 16
speaker.playVoice("Welcome to my base", 32) -- radius 32
`

### Creative Narrator Method

#### globalVoice(text)

Plays the voice message to all players across all dimensions and positions, ignoring distance.

**Example:**
`lua
local speaker = peripheral.find("creative_narrator")
speaker.globalVoice("Attention all players!")
`

### Playback Complete Event

Voice playback is asynchronous. Use the voice_finished event for sequential playback:

`lua
local speaker = peripheral.find("narrator")

speaker.playVoice("First message")
os.pullEvent("voice_finished")

speaker.playVoice("Second message")
`

### Chinese Text Support

Use Unicode escape sequences for Chinese TTS:

`lua
speaker.playVoice("\\u4f60\\u597d\\uff0c\\u4e16\\u754c")
`

## Configuration

The config file lingua_peripherals.conf is in config/:

| Option | Type | Default | Description |
|--------|------|---------|-------------|
| globalMaxRange | double | 128.0 | Maximum limit for playVoice rad parameter |
| speechMaxFrequency | int | 0 | Minimum interval between speech plays (ms) |

## Development

### Requirements

- Java 21, NeoForge 21.1.228, CC: Tweaked 1.119.0+

### Building

`ash
./gradlew build
`

The built jar will be in build/libs/.