# Narrator

![Narrator](../doc_img/narrator.png)

A block that plays text-to-speech using Minecraft's built-in Narrator system, controllable via CC: Tweaked computers. Also supports all standard CC Speaker methods (playNote, playSound, playAudio, stop).

- **Registry name:** `linguaperipherals:narrator`
- **Peripheral type:** `narrator`
- **Hardness:** 2.0
- **Tool:** Pickaxe (any tier), drops itself
- **Blast resistance:** 6.0
- **Wrench:** Right-click to rotate, Shift+right-click to dismantle
- **Recipe:** `computercraft:speaker` + any skull (shapeless)
- **Turtle upgrade:** Yes — equip on left/right side

## Peripheral Methods

### Narrator Methods

#### playVoice(text [, rad])

Plays the specified text as speech. Only players within the radius will hear it.

**Parameters:**
- `text` (string) — The text to speak. Use `\uXXXX` escapes for non-ASCII characters.
- `rad` (number, optional) — Maximum audible radius in blocks. Default: 16. Clamped to `globalMaxRange` (config, default 128).

**Returns:** `true` if the voice was sent successfully.

**Throws:** LuaException if `rad` is negative.

**Example:**
```lua
local n = peripheral.find("narrator")
n.playVoice("Hello, world!")       -- radius 16
n.playVoice("Welcome!", 64)         -- radius 64
n.playVoice("\\u4f60\\u597d", 32)   -- Chinese text via escapes
```

### Speaker Methods (inherited from CC Speaker)

#### playNote(instrument [, volume [, pitch]])

Plays a note block note through the narrator.

**Parameters:**
- `instrument` (string) — The instrument name (e.g. `"harp"`, `"basedrum"`, `"flute"`).
- `volume` (number, optional) — Volume from 0.0 to 3.0. Default: 1.0.
- `pitch` (number, optional) — Pitch in semitones from 0 to 24. Default: 12.

**Returns:** `false` if the per-tick note limit (8) was reached.

#### playSound(name [, volume [, pitch]])

Plays a Minecraft sound through the narrator.

**Parameters:**
- `name` (string) — The sound identifier (e.g. `"minecraft:entity.creeper.primed"`).
- `volume` (number, optional) — Volume from 0.0 to 3.0. Default: 1.0.
- `pitch` (number, optional) — Pitch from 0.5 to 2.0. Default: 1.0.

**Returns:** `false` if another sound or audio is already playing.

#### playAudio(audio [, volume])

Streams custom audio data to the narrator. Accepts a table of 8-bit PCM amplitudes (-128 to 127) at 48 kHz.

**Parameters:**
- `audio` (table) — Table of PCM samples.
- `volume` (number, optional) — Volume from 0.0 to 3.0.

**Returns:** `false` if the internal buffer is full. Wait for a `speaker_audio_empty` event before retrying.

**Example:**
```lua
local dfpwm = require("cc.audio.dfpwm")
local n = peripheral.find("narrator")
local decoder = dfpwm.make_decoder()
for chunk in io.lines("data/example.dfpwm", 16 * 1024) do
    local buffer = decoder(chunk)
    while not n.playAudio(buffer) do
        os.pullEvent("speaker_audio_empty")
    end
end
```

#### stop()

Stops any currently playing audio and clears the internal buffer.

## Events

### voice_finished

Fired after an estimated playback delay (minimum 1.5 seconds). Useful for sequential TTS playback.

```lua
local n = peripheral.find("narrator")
n.playVoice("First message")
os.pullEvent("voice_finished")
n.playVoice("Second message")
```

### speaker_audio_empty

Fired when the internal audio buffer has space for more data. Used with `playAudio`.

## Turtle Integration

The Narrator block can be equipped as a Turtle upgrade (left or right side). When equipped, the Turtle gains all Narrator and Speaker functionality. Use `peripheral.find("narrator")` from the Turtle's computer.

## Configuration

| Option | Type | Default | Description |
|--------|------|---------|-------------|
| globalMaxRange | double | 128.0 | Maximum limit for playVoice rad parameter |
| speechMaxFrequency | int | 0 | Min interval between plays (ms) |
