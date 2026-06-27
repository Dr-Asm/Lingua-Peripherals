package com.linguaperipherals.mod.client.audio;

import com.mojang.blaze3d.audio.Channel;
import net.minecraft.client.sounds.SoundEngine;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.concurrent.Executor;

/**
 * Reflection-based access to private Minecraft fields needed for streaming audio.
 */
final class AudioReflection {
    private static Method channelPumpBuffers;
    private static Field soundEngineExecutor;

    static {
        try {
            channelPumpBuffers = Channel.class.getDeclaredMethod("pumpBuffers", int.class);
            channelPumpBuffers.setAccessible(true);

            soundEngineExecutor = SoundEngine.class.getDeclaredField("executor");
            soundEngineExecutor.setAccessible(true);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException("Failed to initialize audio reflection", e);
        }
    }

    static void pumpBuffers(Channel channel, int count) {
        try {
            channelPumpBuffers.invoke(channel, count);
        } catch (ReflectiveOperationException ignored) {}
    }

    static Executor getExecutor(SoundEngine engine) {
        try {
            return (Executor) soundEngineExecutor.get(engine);
        } catch (ReflectiveOperationException e) {
            return Runnable::run;
        }
    }
}
