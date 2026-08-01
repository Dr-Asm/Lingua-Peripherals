package com.linguaperipherals.mod.init;

import com.linguaperipherals.mod.LinguaPeripherals;
import com.linguaperipherals.mod.block.entity.NarratorBlockEntity;
import com.linguaperipherals.mod.block.entity.CreativeNarratorBlockEntity;
import com.linguaperipherals.mod.block.entity.CassetteDriveBlockEntity;
import dan200.computercraft.api.ForgeComputerCraftAPI;
import dan200.computercraft.api.peripheral.IPeripheral;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.util.LazyOptional;

public class PeripheralRegistration {

    public static void register() {
        ForgeComputerCraftAPI.registerPeripheralProvider((world, pos, side) -> {
            BlockEntity be = world.getBlockEntity(pos);
            if (be instanceof NarratorBlockEntity nbe)
                return LazyOptional.of(nbe::getPeripheral);
            if (be instanceof CreativeNarratorBlockEntity cnbe)
                return LazyOptional.of(cnbe::getPeripheral);
            if (be instanceof CassetteDriveBlockEntity cdbe)
                return LazyOptional.of(cdbe::getPeripheral);
            return LazyOptional.empty();
        });
    }
}
