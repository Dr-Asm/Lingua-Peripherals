package com.linguaperipherals.mod.network;

import com.linguaperipherals.mod.LinguaPeripherals;
import com.linguaperipherals.mod.client.audio.CassetteAudioManager;
import com.mojang.text2speech.Narrator;
import net.minecraft.core.BlockPos;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class ClientPayloadHandler {

    public static void handleSpeakText(final SpeakTextPayload payload, final IPayloadContext context) {
        context.enqueueWork(() -> {
            try {
                Narrator narrator = Narrator.getNarrator();
                narrator.clear();
                narrator.say(payload.text(), true);
            } catch (Exception e) {
                LinguaPeripherals.LOGGER.error("Failed to play voice: {}", e.getMessage());
            }
        });
    }

    public static void handleCassetteAudio(final CassetteAudioPayload payload, final IPayloadContext context) {
        context.enqueueWork(() -> {
            BlockPos pos = BlockPos.of(payload.blockPos());
            CassetteAudioManager.handleAudio(pos, payload.volume(), payload.content());
        });
    }

    public static void handleCassetteAudioStop(final CassetteAudioStopPayload payload, final IPayloadContext context) {
        context.enqueueWork(() -> {
            CassetteAudioManager.stopSound(BlockPos.of(payload.blockPos()));
        });
    }
}
