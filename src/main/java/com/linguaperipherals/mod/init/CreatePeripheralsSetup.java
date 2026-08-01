package com.linguaperipherals.mod.init;

import com.linguaperipherals.mod.peripheral.FlapDisplayPeripheral;
import com.simibubi.create.content.trains.display.FlapDisplayBlockEntity;
import dan200.computercraft.api.ForgeComputerCraftAPI;
import net.minecraftforge.common.util.LazyOptional;

/**
 * Register CC peripheral capabilities on Create mod blocks.
 * This class is only loaded when Create mod is present.
 */
public class CreatePeripheralsSetup {
    public static void register() {
        ForgeComputerCraftAPI.registerPeripheralProvider((world, pos, side) -> {
            var be = world.getBlockEntity(pos);
            if (be instanceof FlapDisplayBlockEntity fdbe)
                return LazyOptional.of(() -> new FlapDisplayPeripheral(fdbe));
            return LazyOptional.empty();
        });
    }
}
