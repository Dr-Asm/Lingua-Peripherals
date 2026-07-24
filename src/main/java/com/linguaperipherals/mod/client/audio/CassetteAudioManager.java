package com.linguaperipherals.mod.client.audio;

import com.linguaperipherals.mod.LinguaPeripherals;
import com.linguaperipherals.mod.peripheral.EncodedAudio;
import com.mojang.blaze3d.audio.Channel;
import net.minecraft.client.Minecraft;
import net.minecraft.client.sounds.AudioStream;
import net.minecraft.client.sounds.SoundEngine;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Client-side manager for cassette drive audio playback.
 *
 * Critical ordering (matching CC:Tweaked's SpeakerInstance):
 * 1. Create CassetteSound (with empty stream)
 * 2. Push first audio chunk into the stream
 * 3. Call soundManager.play() — SoundEngine now finds data on its first read()
 * 4. Subsequent chunks: push + pumpBuffers if channel is ready
 */
public class CassetteAudioManager {
    public static final ResourceLocation CASSETTE_STREAM = ResourceLocation.fromNamespaceAndPath(
            LinguaPeripherals.MODID, "cassette_drive.stream");

    private static final Map<Long, CassetteSound> sounds = new ConcurrentHashMap<>();

    public static void onPlayStreaming(SoundEngine engine, Channel channel, AudioStream stream) {
        if (!(stream instanceof CassetteAudioStream cStream)) return;
        cStream.channel = channel;
        cStream.executor = AudioReflection.getExecutor(engine);
    }

    public static void handleAudio(BlockPos pos, float volume, EncodedAudio audio) {
        long key = pos.asLong();
        CassetteSound sound = sounds.get(key);
        var soundManager = Minecraft.getInstance().getSoundManager();

        // If the sound exists but was stopped by the SoundEngine (buffer exhausted
        // from a previous playback), remove it so we create a fresh one.
        if (sound != null && !soundManager.isActive(sound)) {
            sounds.remove(key);
            sound = null;
        }

        if (sound == null) {
            // First chunk: push data BEFORE calling play(), so SoundEngine
            // finds audio in the buffer on its first read().
            sound = new CassetteSound(CASSETTE_STREAM, Vec3.atCenterOf(pos), volume);
            sound.stream.push(audio);
            sounds.put(key, sound);
            Minecraft.getInstance().getSoundManager().play(sound);
        } else {
            // Subsequent chunks: push and wake SoundEngine if needed.
            // Also sync volume so mid-playback changes take effect.
            sound.setVolume(volume);
            var stream = sound.stream;
            boolean wasEmpty = stream.isEmpty();
            stream.push(audio);
            if (wasEmpty && stream.channel != null
                    && stream.executor != null
                    && !stream.channel.stopped()) {
                stream.executor.execute(
                        () -> AudioReflection.pumpBuffers(stream.channel, 1));
            }
        }
    }

    public static void stopSound(BlockPos pos) {
        long key = pos.asLong();
        var sound = sounds.remove(key);
        if (sound != null) {
            sound.stopSound();
            Minecraft.getInstance().getSoundManager().stop(sound);
        }
    }

    public static void reset() {
        var soundManager = Minecraft.getInstance().getSoundManager();
        for (var sound : sounds.values()) {
            sound.stopSound();
            soundManager.stop(sound);
        }
        sounds.clear();
    }

    @Nullable
    public static CassetteSound getSound(BlockPos pos) {
        return sounds.get(pos.asLong());
    }
}
