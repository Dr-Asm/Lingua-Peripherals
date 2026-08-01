package com.linguaperipherals.mod.client.audio;

import com.mojang.blaze3d.audio.Channel;
import net.minecraft.client.sounds.SoundEngine;

import java.util.concurrent.Executor;

/**
 * Direct access to Channel.pumpBuffers(int) and SoundEngine.executor.
 * Made public by Access Transformer (see META-INF/accesstransformer.cfg).
 */
final class AudioReflection {
    private AudioReflection() {}

    static void pumpBuffers(Channel channel) {
        channel.pumpBuffers(1);
    }

    static Executor getExecutor(SoundEngine engine) {
        return engine.executor;
    }
}
