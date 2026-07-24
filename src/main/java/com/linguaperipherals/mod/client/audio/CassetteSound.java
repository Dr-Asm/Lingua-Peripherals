package com.linguaperipherals.mod.client.audio;

import net.minecraft.client.resources.sounds.AbstractSoundInstance;
import net.minecraft.client.resources.sounds.Sound;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.resources.sounds.TickableSoundInstance;
import net.minecraft.client.sounds.AudioStream;
import net.minecraft.client.sounds.SoundBufferLibrary;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

/**
 * A streaming sound instance for cassette drive audio playback.
 *
 * Overrides {@link #getStream} to return a {@link CassetteAudioStream} instead of
 * going through the normal SoundBufferLibrary, enabling real-time streaming of
 * DFPWM-encoded audio chunks received from the server.
 */
public class CassetteSound extends AbstractSoundInstance implements TickableSoundInstance {
    private boolean stopped = false;

    public CassetteAudioStream stream;

    CassetteSound(ResourceLocation sound, Vec3 position, float volume) {
        super(sound, SoundSource.RECORDS, SoundInstance.createUnseededRandom());
        // Set INHERITED x/y/z fields (do NOT declare local fields — that shadows them!)
        this.x = position.x;
        this.y = position.y;
        this.z = position.z;
        this.volume = volume;
        this.pitch = 1.0f;
        this.attenuation = Attenuation.LINEAR;
        this.stream = new CassetteAudioStream();
    }

    @Override
    public boolean isStopped() {
        return stopped;
    }

    @Override
    public void tick() {
        // No entity tracking needed for block-based speakers
    }

    @Override
    public CompletableFuture<AudioStream> getStream(SoundBufferLibrary soundBuffers, Sound sound, boolean looping) {
        return CompletableFuture.completedFuture(stream);
    }

    void stopSound() {
        stopped = true;
    }

    /** Update volume mid-playback. Called by CassetteAudioManager when new chunks arrive. */
    public void setVolume(float volume) {
        this.volume = volume;
    }
}
