package com.linguaperipherals.mod.block.entity;

import com.linguaperipherals.mod.peripheral.DfpwmEncoder;
import com.linguaperipherals.mod.peripheral.EncodedAudio;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

/**
 * Reads raw DFPWM audio data from a cassette tape file, tracks decoder
 * state across chunk boundaries, and produces {@link EncodedAudio} packets
 * for network transmission.
 */
class DfpwmPlaybackController {
    private static final int PREC = 10;
    private static final byte[] DFPWM_MAGIC = {
        (byte) 0x44, (byte) 0x46, (byte) 0x50, (byte) 0x57, (byte) 0x4D, (byte) 0x0A
    };

    private int charge;
    private int strength;
    private boolean previousBit;

    private long fileByteOffset;
    private byte[] fileData;
    private long dataSize;
    private int headerSize;

    boolean init(Path filePath, long seekOffset) throws IOException {
        fileData = Files.readAllBytes(filePath);
        if (fileData.length == 0) return false;

        if (fileData.length >= 6 && Arrays.equals(
                Arrays.copyOf(fileData, 6), DFPWM_MAGIC)) {
            headerSize = 6;
        } else {
            headerSize = 0;
        }

        dataSize = fileData.length - headerSize;
        if (dataSize <= 0) return false;

        fileByteOffset = seekOffset / 8;
        if (fileByteOffset < 0) fileByteOffset = 0;
        if (fileByteOffset > dataSize) fileByteOffset = dataSize;

        resetState();
        for (long i = 0; i < fileByteOffset; i++) {
            advanceState(fileData[(int) (headerSize + i)]);
        }
        return true;
    }

    boolean hasMoreData() {
        return fileByteOffset < dataSize;
    }

    /**
     * Read the next chunk. Returns null when the file is exhausted.
     * Caller is responsible for sending it over the network.
     */
    EncodedAudio readNextChunk() {
        if (fileByteOffset >= dataSize) return null;

        int bytesPerChunk = (128 * 1024) / 8; // 16KB = 128K samples worth of DFPWM
        int remaining = (int) Math.min(bytesPerChunk, dataSize - fileByteOffset);

        byte[] chunk = new byte[remaining];
        System.arraycopy(fileData, (int) (headerSize + fileByteOffset), chunk, 0, remaining);

        int initialCharge = charge;
        int initialStrength = strength;
        boolean initialBit = previousBit;

        for (int i = 0; i < remaining; i++) {
            advanceState(chunk[i]);
        }
        fileByteOffset += remaining;

        var buf = ByteBuffer.wrap(chunk);
        return new EncodedAudio(initialCharge, initialStrength, initialBit, buf);
    }

    long getSampleOffset() {
        return fileByteOffset * 8;
    }

    private void advanceState(int inputByte) {
        for (int j = 0; j < 8; j++) {
            advanceBit((inputByte & 1) != 0);
            inputByte >>= 1;
        }
    }

    private void advanceBit(boolean currentBit) {
        int target = currentBit ? 127 : -128;
        int nextCharge = charge + ((strength * (target - charge) + (1 << (PREC - 1))) >> PREC);
        if (nextCharge == charge && nextCharge != target) nextCharge += currentBit ? 1 : -1;

        int z = currentBit == previousBit ? (1 << PREC) - 1 : 0;
        int nextStrength = strength;
        if (strength != z) nextStrength += currentBit == previousBit ? 1 : -1;
        if (nextStrength < 2 << (PREC - 8)) nextStrength = 2 << (PREC - 8);

        charge = nextCharge;
        strength = nextStrength;
        previousBit = currentBit;
    }

    private void resetState() {
        charge = 0;
        strength = 0;
        previousBit = false;
    }

    void reset() {
        resetState();
        fileByteOffset = 0;
        fileData = null;
        headerSize = 0;
    }
}
