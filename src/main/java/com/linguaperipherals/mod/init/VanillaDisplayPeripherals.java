package com.linguaperipherals.mod.init;

import com.linguaperipherals.mod.peripheral.SignDisplayPeripheral;
import com.linguaperipherals.mod.peripheral.LecternDisplayPeripheral;
import dan200.computercraft.api.ForgeComputerCraftAPI;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.minecraft.world.level.block.entity.LecternBlockEntity;
import net.minecraftforge.common.util.LazyOptional;

public class VanillaDisplayPeripherals {
    public static void register() {
        ForgeComputerCraftAPI.registerPeripheralProvider((world, pos, side) -> {
            var be = world.getBlockEntity(pos);
            if (be instanceof SignBlockEntity sbe)
                return LazyOptional.of(() -> new SignDisplayPeripheral(sbe));
            if (be instanceof LecternBlockEntity lbe)
                return LazyOptional.of(() -> new LecternDisplayPeripheral(lbe));
            return LazyOptional.empty();
        });
    }
}
