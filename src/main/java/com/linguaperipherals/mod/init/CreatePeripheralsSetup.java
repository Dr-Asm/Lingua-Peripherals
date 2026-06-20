package com.linguaperipherals.mod.init;

import com.linguaperipherals.mod.peripheral.FlapDisplayPeripheral;
import com.simibubi.create.AllBlockEntityTypes;
import com.simibubi.create.content.trains.display.FlapDisplayBlockEntity;
import dan200.computercraft.api.peripheral.PeripheralCapability;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;

/**
 * Register CC peripheral capabilities on Create mod blocks.
 * This class is only loaded when Create mod is present.
 */
public class CreatePeripheralsSetup {
    public static void register(IEventBus modEventBus) {
        modEventBus.addListener(CreatePeripheralsSetup::onRegisterCapabilities);
    }

    private static void onRegisterCapabilities(RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(
            PeripheralCapability.get(),
            AllBlockEntityTypes.FLAP_DISPLAY.get(),
            (be, dir) -> new FlapDisplayPeripheral((FlapDisplayBlockEntity) be)
        );
    }
}
