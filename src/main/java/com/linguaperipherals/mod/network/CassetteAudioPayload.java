package com.linguaperipherals.mod.network;

import com.linguaperipherals.mod.LinguaPeripherals;
import com.linguaperipherals.mod.peripheral.EncodedAudio;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

/**
 * Sends a chunk of encoded DFPWM audio from the server to the client for playback.
 */
public record CassetteAudioPayload(long blockPos, EncodedAudio content, float volume) implements CustomPacketPayload {
    public static final Type<CassetteAudioPayload> TYPE = new Type<>(
            ResourceLocation.fromNamespaceAndPath(LinguaPeripherals.MODID, "cassette_audio"));

    public static final StreamCodec<RegistryFriendlyByteBuf, CassetteAudioPayload> CODEC = StreamCodec.ofMember(
            (payload, buf) -> {
                buf.writeLong(payload.blockPos());
                payload.content().write(buf);
                buf.writeFloat(payload.volume());
            },
            buf -> new CassetteAudioPayload(
                    buf.readLong(),
                    EncodedAudio.read(buf),
                    buf.readFloat()
            )
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
