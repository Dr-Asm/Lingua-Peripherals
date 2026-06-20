package com.linguaperipherals.mod.init;

import com.linguaperipherals.mod.peripheral.SignDisplayPeripheral;
import com.linguaperipherals.mod.peripheral.LecternDisplayPeripheral;
import dan200.computercraft.api.peripheral.PeripheralCapability;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.minecraft.world.level.block.entity.LecternBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;

public class VanillaDisplayPeripherals {
    public static void register(IEventBus modEventBus) {
        modEventBus.addListener(VanillaDisplayPeripherals::onRegisterCapabilities);
    }

    private static void onRegisterCapabilities(RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(
            PeripheralCapability.get(),
            BlockEntityType.SIGN,
            (be, dir) -> new SignDisplayPeripheral((SignBlockEntity) be)
        );
        event.registerBlockEntity(
            PeripheralCapability.get(),
            BlockEntityType.LECTERN,
            (be, dir) -> new LecternDisplayPeripheral((LecternBlockEntity) be)
        );
    }
}
