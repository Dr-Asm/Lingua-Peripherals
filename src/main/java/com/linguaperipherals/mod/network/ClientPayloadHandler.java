package com.linguaperipherals.mod.network;

import com.mojang.text2speech.Narrator;
import com.linguaperipherals.mod.LinguaPeripherals;
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
}