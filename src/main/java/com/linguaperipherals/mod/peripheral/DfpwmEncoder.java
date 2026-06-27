package com.linguaperipherals.mod.peripheral;

import dan200.computercraft.api.lua.LuaTable;
import dan200.computercraft.api.lua.LuaException;

import java.nio.ByteBuffer;
import java.util.Optional;

/**
 * Server-side DFPWM encoder with playback flow control.
 *
 * Encodes 8-bit PCM samples ([-128, 127]) to DFPWM format for efficient network transmission.
 * Manages client-side buffer estimation to ensure smooth streaming — sends the next chunk
 * when the client has less than 0.5 seconds of audio remaining.
 */
public class DfpwmEncoder {
    public static final int SAMPLE_RATE = 48000;
    private static final long SECOND = 1_000_000_000L; // nanos
    private static final long CLIENT_BUFFER = (long) (SECOND * 0.5);
    private static final int PREC = 10;

    private int charge;
    private int strength;
    private boolean previousBit;

    private boolean unplayed = true;
    private long clientEndTime;
    private float pendingVolume = 1.0f;
    private EncodedAudio pendingAudio;

    public DfpwmEncoder() {
        clientEndTime = System.nanoTime();
    }

    /**
     * Encode a table of PCM samples to DFPWM format.
     *
     * @param table  Lua table of samples (-128 to 127)
     * @param size   Number of samples in the table
     * @param volume Volume for this chunk
     * @return true if the buffer was accepted, false if a previous buffer is still pending
     */
    public synchronized boolean pushBuffer(LuaTable<?, ?> table, int size, double volume) throws LuaException {
        if (pendingAudio != null) return false;

        var outSize = size / 8;
        var buffer = ByteBuffer.allocate(outSize);

        var initialCharge = charge;
        var initialStrength = strength;
        var initialPreviousBit = previousBit;

        for (var i = 0; i < outSize; i++) {
            var thisByte = 0;
            for (var j = 1; j <= 8; j++) {
                var level = table.getInt(i * 8 + j);
                if (level < -128 || level > 127) {
                    throw new LuaException("table item #" + (i * 8 + j) + " must be between -128 and 127");
                }

                var currentBit = level > charge || (level == charge && charge == 127);
                encodeBit(currentBit);

                thisByte = (thisByte >> 1) + (currentBit ? 128 : 0);
            }
            buffer.put((byte) thisByte);
        }

        buffer.flip();
        pendingAudio = new EncodedAudio(initialCharge, initialStrength, initialPreviousBit, buffer);
        pendingVolume = (float) Math.max(0, Math.min(volume, 3.0));
        return true;
    }

    /**
     * Encode a single bit of PCM data into the DFPWM state.
     * Advances charge, strength, and previousBit.
     */
    private void encodeBit(boolean currentBit) {
        var target = currentBit ? 127 : -128;

        // q' <- q + (s * (t - q) + 128)/256
        var nextCharge = charge + ((strength * (target - charge) + (1 << (PREC - 1))) >> PREC);
        if (nextCharge == charge && nextCharge != target) nextCharge += currentBit ? 1 : -1;

        var z = currentBit == previousBit ? (1 << PREC) - 1 : 0;

        var nextStrength = strength;
        if (strength != z) nextStrength += currentBit == previousBit ? 1 : -1;
        if (nextStrength < 2 << (PREC - 8)) nextStrength = 2 << (PREC - 8);

        charge = nextCharge;
        strength = nextStrength;
        previousBit = currentBit;
    }

    /**
     * Check if we should send the next batch of audio to clients.
     * Returns true when clients have less than 0.5s of buffered audio remaining.
     */
    public boolean shouldSendPending(long now) {
        return pendingAudio != null && now >= clientEndTime - CLIENT_BUFFER;
    }

    /**
     * Pull the pending audio for network transmission.
     * Updates the estimated client buffer end time.
     */
    public EncodedAudio pullPending(long now) {
        var audio = pendingAudio;
        if (audio == null) throw new IllegalStateException("No pending audio to pull");
        pendingAudio = null;
        // Compute when we should next send: current end + duration of this chunk
        clientEndTime = Math.max(now, clientEndTime)
                + (audio.audio().remaining() * SECOND * 8 / SAMPLE_RATE);
        unplayed = false;
        return audio;
    }

    /**
     * @return true if audio is currently buffered on the client and still playing
     */
    public boolean isPlaying() {
        return unplayed || clientEndTime >= System.nanoTime();
    }

    public float getVolume() {
        return pendingVolume;
    }

    /**
     * Reset encoder state for a new playback session.
     */
    public synchronized void reset() {
        charge = 0;
        strength = 0;
        previousBit = false;
        unplayed = true;
        clientEndTime = System.nanoTime();
        pendingVolume = 1.0f;
        pendingAudio = null;
    }
}
