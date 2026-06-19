package com.linguaperipherals.mod.block.entity;

import com.linguaperipherals.mod.init.ModBlockEntities;
import com.linguaperipherals.mod.peripheral.CreativeNarratorPeripheral;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class CreativeNarratorBlockEntity extends NarratorBlockEntity {
    private final CreativeNarratorPeripheral creativePeripheral;

    public CreativeNarratorBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.CREATIVE_NARRATOR_BE.get(), pos, state);
        this.creativePeripheral = new CreativeNarratorPeripheral(this);
    }

    public CreativeNarratorPeripheral getPeripheral() {
        return creativePeripheral;
    }
}