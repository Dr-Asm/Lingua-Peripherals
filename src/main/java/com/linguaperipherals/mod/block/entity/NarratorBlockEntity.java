package com.linguaperipherals.mod.block.entity;

import com.linguaperipherals.mod.init.ModBlockEntities;
import com.linguaperipherals.mod.peripheral.NarratorPeripheral;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public class NarratorBlockEntity extends BlockEntity {
    private final NarratorPeripheral peripheral;

    public NarratorBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.NARRATOR_BE.get(), pos, state);
        this.peripheral = new NarratorPeripheral(this);
    }

    protected NarratorBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
        this.peripheral = new NarratorPeripheral(this);
    }

    public NarratorPeripheral getPeripheral() {
        return peripheral;
    }

    public Vec3 getPosition() {
        return Vec3.atCenterOf(worldPosition);
    }

    public Level getWorld() {
        return level;
    }

    public void tick() {
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
    }
}