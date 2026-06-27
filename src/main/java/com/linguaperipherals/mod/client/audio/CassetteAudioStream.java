package com.linguaperipherals.mod.client.audio;

import com.linguaperipherals.mod.peripheral.DfpwmEncoder;
import com.linguaperipherals.mod.peripheral.EncodedAudio;
import com.mojang.blaze3d.audio.Channel;
import net.minecraft.client.sounds.AudioStream;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.BufferUtils;

import javax.sound.sampled.AudioFormat;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayDeque;
import java.util.Queue;
import java.util.concurrent.Executor;

/**
 * An {@link AudioStream} which decodes DFPWM streams to PCM for playback
 * through Minecraft's SoundEngine.
 */
public class CassetteAudioStream implements AudioStream {
    private static final int PREC = 10;
    private static final int LPF_STRENGTH = 140;

    private static final AudioFormat MONO_8 = new AudioFormat(
            DfpwmEncoder.SAMPLE_RATE, 8, 1, true, false);

    private final Queue<ByteBuffer> buffers = new ArrayDeque<>(2);

    @Nullable
    public Channel channel;

    @Nullable
    public Executor executor;

    private int lowPassCharge;

    /**
     * Push a new chunk of encoded DFPWM audio into the stream.
     * Decodes it to PCM and queues it for reading by the SoundEngine.
     */
    public void push(EncodedAudio audio) {
        var charge = audio.charge();
        var strength = audio.strength();
        var previousBit = audio.previousBit();
        var input = audio.audio();

        var readable = input.remaining();
        var output = ByteBuffer.allocate(readable * 8).order(ByteOrder.nativeOrder());

        for (var i = 0; i < readable; i++) {
            var inputByte = input.get();
            for (var j = 0; j < 8; j++) {
                var currentBit = (inputByte & 1) != 0;
                var target = currentBit ? 127 : -128;

                // DFPWM decode
                var nextCharge = charge + ((strength * (target - charge) + (1 << (PREC - 1))) >> PREC);
                if (nextCharge == charge && nextCharge != target) nextCharge += currentBit ? 1 : -1;

                var z = currentBit == previousBit ? (1 << PREC) - 1 : 0;

                var nextStrength = strength;
                if (strength != z) nextStrength += currentBit == previousBit ? 1 : -1;
                if (nextStrength < 2 << (PREC - 8)) nextStrength = 2 << (PREC - 8);

                // Antijerk
                var chargeWithAntijerk = currentBit == previousBit
                        ? nextCharge
                        : nextCharge + charge + 1 >> 1;

                // Low pass filter
                lowPassCharge += ((chargeWithAntijerk - lowPassCharge) * LPF_STRENGTH + 0x80) >> 8;

                charge = nextCharge;
                strength = nextStrength;
                previousBit = currentBit;

                // Convert signed to unsigned for OpenAL
                output.put((byte) ((lowPassCharge & 0xFF) ^ 0x80));

                inputByte >>= 1;
            }
        }

        output.flip();
        synchronized (this) {
            buffers.add(output);
        }
    }

    @Override
    public AudioFormat getFormat() {
        return MONO_8;
    }

    @Nullable
    @Override
    public synchronized ByteBuffer read(int capacity) {
        var result = BufferUtils.createByteBuffer(capacity);
        while (result.hasRemaining()) {
            var head = buffers.peek();
            if (head == null) break;

            var toRead = Math.min(head.remaining(), result.remaining());
            result.put(result.position(), head, head.position(), toRead);
            result.position(result.position() + toRead);
            head.position(head.position() + toRead);

            if (head.hasRemaining()) break;
            buffers.remove();
        }

        result.flip();
        return result.remaining() == 0 ? null : result;
    }

    @Override
    public void close() {
        buffers.clear();
    }

    public boolean isEmpty() {
        return buffers.isEmpty();
    }
}
