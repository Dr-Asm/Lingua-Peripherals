package com.linguaperipherals.mod.network;

import com.linguaperipherals.mod.LinguaPeripherals;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

public class LinguaPeripheralsNetwork {

    public static void register(IEventBus modEventBus) {
        modEventBus.addListener(LinguaPeripheralsNetwork::onRegisterPayloadHandlers);
    }

    private static void onRegisterPayloadHandlers(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar(LinguaPeripherals.MODID);
        registrar.playToClient(
                SpeakTextPayload.TYPE,
                SpeakTextPayload.CODEC,
                (payload, context) -> ClientPayloadHandler.handleSpeakText(payload, context)
        );
    }
}