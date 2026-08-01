package com.linguaperipherals.mod.network;

import com.linguaperipherals.mod.LinguaPeripherals;
import com.linguaperipherals.mod.client.audio.CassetteAudioManager;
import com.linguaperipherals.mod.peripheral.EncodedAudio;
import com.mojang.text2speech.Narrator;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

import java.util.function.Supplier;

public class LinguaPeripheralsNetwork {
    private static final String PROTOCOL_VERSION = "1";
    public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(LinguaPeripherals.MODID, "main"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
    );

    private static int id = 0;

    public static void register() {
        CHANNEL.registerMessage(id++,
                SpeakTextPacket.class,
                SpeakTextPacket::encode,
                SpeakTextPacket::decode,
                SpeakTextPacket::handle);

        CHANNEL.registerMessage(id++,
                CassetteAudioPacket.class,
                CassetteAudioPacket::encode,
                CassetteAudioPacket::decode,
                CassetteAudioPacket::handle);

        CHANNEL.registerMessage(id++,
                CassetteVolumePacket.class,
                CassetteVolumePacket::encode,
                CassetteVolumePacket::decode,
                CassetteVolumePacket::handle);

        CHANNEL.registerMessage(id++,
                CassetteAudioStopPacket.class,
                CassetteAudioStopPacket::encode,
                CassetteAudioStopPacket::decode,
                CassetteAudioStopPacket::handle);
    }

    // ---- SpeakText Packet ----

    public record SpeakTextPacket(String text) {
        public static SpeakTextPacket of(String text) { return new SpeakTextPacket(text); }

        static void encode(SpeakTextPacket msg, FriendlyByteBuf buf) {
            buf.writeUtf(msg.text, 32767);
        }

        static SpeakTextPacket decode(FriendlyByteBuf buf) {
            return new SpeakTextPacket(buf.readUtf(32767));
        }

        static void handle(SpeakTextPacket msg, Supplier<NetworkEvent.Context> ctx) {
            ctx.get().enqueueWork(() -> {
                Narrator narrator = Narrator.getNarrator();
                narrator.clear();
                narrator.say(msg.text, true);
            });
            ctx.get().setPacketHandled(true);
        }
    }

    // ---- CassetteAudio Packet ----

    public record CassetteAudioPacket(BlockPos pos, EncodedAudio content, float volume) {
        static void encode(CassetteAudioPacket msg, FriendlyByteBuf buf) {
            buf.writeBlockPos(msg.pos);
            msg.content.write(buf);
            buf.writeFloat(msg.volume);
        }

        static CassetteAudioPacket decode(FriendlyByteBuf buf) {
            return new CassetteAudioPacket(buf.readBlockPos(), EncodedAudio.read(buf), buf.readFloat());
        }

        static void handle(CassetteAudioPacket msg, Supplier<NetworkEvent.Context> ctx) {
            ctx.get().enqueueWork(() ->
                    CassetteAudioManager.handleAudio(msg.pos, msg.volume, msg.content));
            ctx.get().setPacketHandled(true);
        }
    }

    // ---- CassetteVolume Packet ----

    public record CassetteVolumePacket(BlockPos pos, float volume) {
        static void encode(CassetteVolumePacket msg, FriendlyByteBuf buf) {
            buf.writeBlockPos(msg.pos);
            buf.writeFloat(msg.volume);
        }

        static CassetteVolumePacket decode(FriendlyByteBuf buf) {
            return new CassetteVolumePacket(buf.readBlockPos(), buf.readFloat());
        }

        static void handle(CassetteVolumePacket msg, Supplier<NetworkEvent.Context> ctx) {
            ctx.get().enqueueWork(() ->
                    CassetteAudioManager.updateVolume(msg.pos, msg.volume));
            ctx.get().setPacketHandled(true);
        }
    }

    // ---- CassetteAudioStop Packet ----

    public record CassetteAudioStopPacket(BlockPos pos) {
        static void encode(CassetteAudioStopPacket msg, FriendlyByteBuf buf) {
            buf.writeBlockPos(msg.pos);
        }

        static CassetteAudioStopPacket decode(FriendlyByteBuf buf) {
            return new CassetteAudioStopPacket(buf.readBlockPos());
        }

        static void handle(CassetteAudioStopPacket msg, Supplier<NetworkEvent.Context> ctx) {
            ctx.get().enqueueWork(() ->
                    CassetteAudioManager.stopSound(msg.pos));
            ctx.get().setPacketHandled(true);
        }
    }
}
