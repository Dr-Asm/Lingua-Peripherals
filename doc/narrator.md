# Narrator

![Narrator](../doc_img/narrator.png)

A block that plays text-to-speech using Minecraft's built-in Narrator system, controllable via CC: Tweaked computers.

- **Registry name:** `linguaperipherals:narrator`
- **Peripheral type:** `narrator`
- **Hardness:** 2.0
- **Tool:** Pickaxe (any tier), drops itself
- **Blast resistance:** 6.0
- **Wrench:** Right-click to rotate, Shift+right-click to dismantle
- **Recipe:** `computercraft:speaker` + any skull (shapeless)

## Peripheral Methods

### playVoice(text [, rad])

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

## Events

### voice_finished

Fired after an estimated playback delay (minimum 1.5 seconds). Useful for sequential playback.

```lua
local n = peripheral.find("narrator")
n.playVoice("First message")
os.pullEvent("voice_finished")
n.playVoice("Second message")
```

## Configuration

| Option | Type | Default | Description |
|--------|------|---------|-------------|
| globalMaxRange | double | 128.0 | Maximum limit for playVoice rad parameter |
| speechMaxFrequency | int | 0 | Min interval between plays (ms) |