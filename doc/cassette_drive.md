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

### Data I/O

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

### Audio Playback

The cassette drive can play DFPWM-encoded audio stored on cassette tapes. Both CC:Tweaked-encoded files (with `DFPWM\n` 6-byte header) and raw ffmpeg DFPWM output are supported.

Playback can be triggered via CC computer or redstone. A comparator outputs strength 15 while playing, 0 otherwise.

| Method | Returns | Description |
|--------|---------|-------------|
| `playTape()` | `boolean` | Start or resume playback. Errors if tape is empty or not DFPWM format |
| `pauseTape()` | — | Pause playback, preserving position |
| `stopTape()` | — | Stop playback and reset to beginning |
| `seekTape(seconds)` | — | Jump to a specific time in seconds (`seconds >= 0`) |
| `setVolume(vol)` | — | Set volume (0.0 ~ config.maxVolume, default 3.0) |
| `getVolume()` | `number` | Get current volume |
| `isPlaying()` | `boolean` | Whether the tape is currently playing |
| `getPlayPosition()` | `number` | Current playback position in seconds |
| `getTapeDuration()` | `number` | Total audio duration in seconds. Returns 0 if empty or no tape |
| `saveAudio(audio)` | — | Encodes a PCM sample table (-128 to 127) to DFPWM and writes to tape, overwriting existing data |

### Playback Events

| Event | When |
|-------|------|
| `tape_play_start` | Playback starts |
| `tape_play_end` | Playback completes (progress auto-resets) |

```lua
local d = peripheral.find("cassette_drive")
d.playTape()
local _, side = os.pullEvent("tape_play_end")
print("Playback finished: " .. side)
```

### Redstone

- **Pulse (0→non-zero):** Start playback from beginning
- **Comparator:** 15 when playing, 0 when stopped/paused

### Volume

`volume` specifies the audible distance (≥ 0.0). Values below 1.0 reduce the sound and shrink the audible sphere. Values above 1.0 do not increase loudness but multiply the audible range (16m at 1.0). Sound fades with distance from the sphere center. Upper limit is configurable (default 3.0 in `lingua_peripherals.conf`).

### Config Options

| Option | Type | Default | Description |
|--------|------|---------|-------------|
| `cassetteTapeSizeLimit` | int | 1048576 (1MB) | Maximum tape data file size in bytes |
| `maxVolume` | double | 3.0 | Maximum playback volume |
| `cassetteBroadcastAudio` | boolean | false | `true`: send audio to all online players (ignoring distance); `false`: only send to players within audible range |

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
