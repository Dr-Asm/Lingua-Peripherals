package com.linguaperipherals.mod.peripheral;

import com.linguaperipherals.mod.block.entity.CassetteDriveBlockEntity;
import com.linguaperipherals.mod.item.CassetteTapeItem;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.lua.LuaTable;
import dan200.computercraft.api.peripheral.IComputerAccess;
import dan200.computercraft.api.peripheral.IPeripheral;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;

public class CassetteDrivePeripheral implements IPeripheral {
    private final CassetteDriveBlockEntity blockEntity;
    final List<IComputerAccess> attachedComputers = new CopyOnWriteArrayList<>();

    public CassetteDrivePeripheral(CassetteDriveBlockEntity blockEntity) {
        this.blockEntity = blockEntity;
    }

    @Override
    public String getType() { return "cassette_drive"; }

    @Override
    public void attach(IComputerAccess computer) { attachedComputers.add(computer); }

    @Override
    public void detach(IComputerAccess computer) {
        attachedComputers.remove(computer);
        blockEntity.onComputerDetach(computer);
    }

    @Override
    public boolean equals(@Nullable IPeripheral other) {
        if (this == other) return true;
        if (!(other instanceof CassetteDrivePeripheral that)) return false;
        return blockEntity.getBlockPos().equals(that.blockEntity.getBlockPos());
    }

    // ==================== Inventory / Tape Info ====================

    @LuaFunction
    public final boolean isTapePresent() {
        ItemStack stack = blockEntity.getStoredItem();
        return !stack.isEmpty() && stack.getItem() instanceof CassetteTapeItem;
    }

    @LuaFunction
    public final @Nullable Object @Nullable [] getTapeID() {
        ItemStack stack = blockEntity.getStoredItem();
        if (!(stack.getItem() instanceof CassetteTapeItem)) return null;
        int id = CassetteTapeItem.getCassetteID(stack);
        return id != CassetteTapeItem.NO_ID ? new Object[]{id} : null;
    }

    @LuaFunction
    public final @Nullable Object @Nullable [] getTapeLabel() {
        ItemStack stack = blockEntity.getStoredItem();
        if (!(stack.getItem() instanceof CassetteTapeItem)) return null;
        String label = CassetteTapeItem.getCassetteLabel(stack);
        return label != null ? new Object[]{label} : null;
    }

    @LuaFunction(mainThread = true)
    public final void setTapeLabel(Optional<String> label) throws LuaException {
        ItemStack stack = blockEntity.getStoredItem();
        if (!(stack.getItem() instanceof CassetteTapeItem))
            throw new LuaException("No cassette tape in drive");
        CassetteTapeItem.setCassetteLabel(stack, label.orElse(null));
        blockEntity.setChanged();
    }

    @LuaFunction
    public final void ejectTape() { blockEntity.requestEject(); }

    @LuaFunction
    public final long dataSize() throws LuaException {
        CassetteTapeStorage storage = blockEntity.getTapeStorage();
        if (storage == null) throw new LuaException("No cassette tape in drive");
        return storage.size();
    }

    @LuaFunction
    public final long dataSizeLimit() throws LuaException {
        CassetteTapeStorage storage = blockEntity.getTapeStorage();
        if (storage == null) throw new LuaException("No cassette tape in drive");
        return storage.sizeLimit();
    }

    @LuaFunction
    public final CassetteTapeFileHandle open(IComputerAccess computer, String mode) throws LuaException {
        CassetteTapeStorage storage = blockEntity.getTapeStorage();
        if (storage == null) throw new LuaException("No cassette tape in drive");
        if (mode == null || mode.isEmpty())
            throw new LuaException("bad argument #1 to 'open' (string expected, got nil)");
        String m = mode.trim();
        if (!m.matches("^[rwa][b+]?[b+]?$"))
            throw new LuaException("unsupported mode '" + mode + "'");
        try {
            blockEntity.closeHandle(computer);
            CassetteTapeFileHandle handle = storage.open(m);
            blockEntity.trackHandle(computer, handle);
            return handle;
        } catch (IOException e) { throw new LuaException("Failed to open tape: " + e.getMessage()); }
    }

    @LuaFunction
    public final void close() throws LuaException {
        CassetteTapeStorage storage = blockEntity.getTapeStorage();
        if (storage == null) throw new LuaException("No cassette tape in drive");
        storage.forceClose();
    }

    @LuaFunction(mainThread = true)
    public final void reset() throws LuaException {
        CassetteTapeStorage storage = blockEntity.getTapeStorage();
        if (storage == null) throw new LuaException("No cassette tape in drive");
        storage.forceClose();
        try {
            java.nio.file.Files.write(storage.getFilePath(), new byte[0]);
        } catch (IOException e) {
            throw new LuaException("Failed to reset tape: " + e.getMessage());
        }
    }

    // ==================== Playback API ====================

    /**
     * Start or resume tape playback. Throws an error if the tape is missing
     * or the data is not in DFPWM format.
     */
    @LuaFunction(mainThread = true)
    public final boolean playTape() throws LuaException {
        if (!isTapePresent()) throw new LuaException("No cassette tape in drive");
        if (blockEntity.getTapeStorage() == null)
            throw new LuaException("Tape storage not initialized");
        if (!blockEntity.isDfpwmFormat())
            throw new LuaException("Tape has no audio data (file is empty)");
        return blockEntity.startPlayback();
    }

    /**
     * Pause tape playback. The audio offset is preserved for later resumption.
     */
    @LuaFunction(mainThread = true)
    public final void pauseTape() throws LuaException {
        if (!isTapePresent()) throw new LuaException("No cassette tape in drive");
        blockEntity.pausePlayback();
    }

    /**
     * Stop tape playback and reset the play position to the beginning.
     */
    @LuaFunction(mainThread = true)
    public final void stopTape() throws LuaException {
        if (!isTapePresent()) throw new LuaException("No cassette tape in drive");
        blockEntity.stopPlayback();
    }

    /**
     * Seek to a specific position in seconds.
     */
    @LuaFunction(mainThread = true)
    public final void seekTape(double seconds) throws LuaException {
        if (!isTapePresent()) throw new LuaException("No cassette tape in drive");
        if (seconds < 0) throw new LuaException("seconds must be non-negative");
        blockEntity.seekTape(seconds);
    }

    /**
     * Set playback volume. Clamped to [0, config.maxVolume].
     */
    @LuaFunction(mainThread = true)
    public final void setVolume(double vol) throws LuaException {
        blockEntity.setVolume((float) vol);
    }

    /**
     * Get the current playback volume.
     */
    @LuaFunction(mainThread = true)
    public final double getVolume() {
        return blockEntity.getAudioVolume();
    }

    /**
     * Check if the tape is currently playing.
     */
    @LuaFunction(mainThread = true)
    public final boolean isPlaying() {
        return blockEntity.isPlaying();
    }

    /**
     * Get the current playback position in seconds.
     */
    @LuaFunction(mainThread = true)
    public final double getPlayPosition() {
        return blockEntity.getPlayPosition();
    }

    /**
     * Get the total audio duration in seconds.
     */
    @LuaFunction(mainThread = true)
    public final double getTapeDuration() {
        return blockEntity.getTapeDuration();
    }

    // ==================== Event Helpers ====================

    public void queueEvent(String eventName) {
        for (IComputerAccess comp : attachedComputers) {
            comp.queueEvent(eventName, comp.getAttachmentName());
        }
    }

    // ==================== saveAudio ====================

    /**
     * Encode a table of PCM audio samples to DFPWM format and write to the cassette tape.
     * Overwrites any existing data on the tape. The samples must be integers
     * between -128 and 127.
     *
     * @param audio Lua table of PCM samples (1-indexed)
     * @param size  Number of samples (must be a multiple of 8)
     * @cc.tparam {number...} audio A table of PCM amplitudes (-128 to 127)
     * @cc.usage Encode and save audio to a tape
     * <pre>{@code
     * local drive = peripheral.find("cassette_drive")
     * -- Generate a simple 440Hz sine wave tone
     * local samples = {}
     * for i = 1, 48000 do
     *     samples[i] = math.floor(math.sin(i * 440 * 2 * math.pi / 48000) * 127)
     * end
     * drive.saveAudio(samples)
     * }</pre>
     */
    @LuaFunction(mainThread = true, unsafe = true)
    public final void saveAudio(IComputerAccess computer, LuaTable<?, ?> audio) throws LuaException {
        CassetteTapeStorage storage = blockEntity.getTapeStorage();
        if (storage == null) throw new LuaException("No cassette tape in drive");

        int length = audio.length();
        if (length <= 0) throw new LuaException("Cannot save empty audio");
        if (length % 8 != 0) throw new LuaException("Audio length must be a multiple of 8");
        if (length > 128 * 1024 * 128) throw new LuaException("Audio data is too large");

        // Stop any current playback and close all file handles
        blockEntity.stopPlayback();
        blockEntity.closeAllHandles();

        // Encode PCM to DFPWM
        DfpwmEncoder encoder = new DfpwmEncoder();
        byte[] dfpwmData = encoder.encodeAll(audio, length);

        // Write DFPWM header + data to tape, overwriting existing content
        try {
            java.nio.file.Path path = storage.getFilePath();
            java.nio.file.Files.createDirectories(path.getParent());
            java.io.OutputStream out = java.nio.file.Files.newOutputStream(path);
            try (out) {
                // Write "DFPWM\n" header
                out.write(new byte[]{0x44, 0x46, 0x50, 0x57, 0x4D, 0x0A});
                // Write encoded data
                out.write(dfpwmData);
            }
            // Re-initialize storage to pick up the new file
            blockEntity.refreshStorage();
        } catch (IOException e) {
            throw new LuaException("Failed to save audio: " + e.getMessage());
        }
    }
}
