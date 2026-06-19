package com.linguaperipherals.mod.network;

import com.linguaperipherals.mod.LinguaPeripherals;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record SpeakTextPayload(String text) implements CustomPacketPayload {
    public static final Type<SpeakTextPayload> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(LinguaPeripherals.MODID, "speak_text"));

    public static final StreamCodec<FriendlyByteBuf, SpeakTextPayload> CODEC = StreamCodec.ofMember(
            (payload, buf) -> buf.writeUtf(payload.text(), 32767),
            buf -> new SpeakTextPayload(buf.readUtf(32767))
    );

    public static SpeakTextPayload of(String text) {
        return new SpeakTextPayload(text);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}