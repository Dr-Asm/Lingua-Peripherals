package com.linguaperipherals.mod.network;

import com.linguaperipherals.mod.LinguaPeripherals;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

/**
 * Sent from server to client to update playback volume mid-stream.
 */
public record CassetteVolumePayload(long blockPos, float volume) implements CustomPacketPayload {
    public static final Type<CassetteVolumePayload> TYPE = new Type<>(
            ResourceLocation.fromNamespaceAndPath(LinguaPeripherals.MODID, "cassette_volume"));

    public static final StreamCodec<RegistryFriendlyByteBuf, CassetteVolumePayload> CODEC = StreamCodec.ofMember(
            (payload, buf) -> {
                buf.writeLong(payload.blockPos());
                buf.writeFloat(payload.volume());
            },
            buf -> new CassetteVolumePayload(buf.readLong(), buf.readFloat())
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
