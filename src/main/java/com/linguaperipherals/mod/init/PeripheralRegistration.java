package com.linguaperipherals.mod.init;

import com.linguaperipherals.mod.LinguaPeripherals;
import com.linguaperipherals.mod.block.entity.NarratorBlockEntity;
import com.linguaperipherals.mod.block.entity.CreativeNarratorBlockEntity;
import dan200.computercraft.api.peripheral.PeripheralCapability;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;

public class PeripheralRegistration {

    public static void register(IEventBus modEventBus) {
        modEventBus.addListener(PeripheralRegistration::onRegisterCapabilities);
    }

    private static void onRegisterCapabilities(RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(
                PeripheralCapability.get(),
                ModBlockEntities.NARRATOR_BE.get(),
                (be, dir) -> be.getPeripheral()
        );
        event.registerBlockEntity(
                PeripheralCapability.get(),
                ModBlockEntities.CREATIVE_NARRATOR_BE.get(),
                (be, dir) -> be.getPeripheral()
        );
    }
}