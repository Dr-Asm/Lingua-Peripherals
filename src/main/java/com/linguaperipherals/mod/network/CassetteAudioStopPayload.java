package com.linguaperipherals.mod.network;

import com.linguaperipherals.mod.LinguaPeripherals;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

/**
 * Tells the client to stop audio playback for a specific cassette drive.
 */
public record CassetteAudioStopPayload(long blockPos) implements CustomPacketPayload {
    public static final Type<CassetteAudioStopPayload> TYPE = new Type<>(
            ResourceLocation.fromNamespaceAndPath(LinguaPeripherals.MODID, "cassette_audio_stop"));

    public static final StreamCodec<RegistryFriendlyByteBuf, CassetteAudioStopPayload> CODEC =
            StreamCodec.ofMember(
                    (payload, buf) -> buf.writeLong(payload.blockPos()),
                    buf -> new CassetteAudioStopPayload(buf.readLong())
            );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
