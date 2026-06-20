package com.linguaperipherals.mod.block.entity;

import com.linguaperipherals.mod.block.CassetteDriveBlock;
import com.linguaperipherals.mod.init.ModBlockEntities;
import com.linguaperipherals.mod.inventory.CassetteDriveMenu;
import com.linguaperipherals.mod.item.CassetteTapeItem;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Container;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class CassetteDriveBlockEntity extends BlockEntity implements MenuProvider {
    private final NonNullList<ItemStack> items = NonNullList.withSize(1, ItemStack.EMPTY);

    public CassetteDriveBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.CASSETTE_DRIVE_BE.get(), pos, state);
    }

    public Container getInventory() {
        return new Container() {
            @Override public int getContainerSize() { return 1; }
            @Override public boolean isEmpty() { return items.get(0).isEmpty(); }
            @Override public ItemStack getItem(int slot) { return items.get(slot); }
            @Override public ItemStack removeItem(int slot, int amount) {
                ItemStack result = ContainerHelper.removeItem(items, slot, amount);
                if (!result.isEmpty()) updateBlockState();
                return result;
            }
            @Override public ItemStack removeItemNoUpdate(int slot) {
                ItemStack result = ContainerHelper.takeItem(items, slot);
                if (!result.isEmpty()) updateBlockState();
                return result;
            }
            @Override public void setItem(int slot, ItemStack stack) {
                items.set(slot, stack);
                updateBlockState();
            }
            @Override public void setChanged() { CassetteDriveBlockEntity.this.setChanged(); }
            @Override public boolean stillValid(Player player) {
                return Container.stillValidBlockEntity(CassetteDriveBlockEntity.this, player);
            }
            @Override public void clearContent() { items.clear(); updateBlockState(); }
        };
    }

    private void updateBlockState() {
        if (level != null && !level.isClientSide) {
            ItemStack stack = items.get(0);
            CassetteDriveBlock.CassetteState state;
            if (stack.isEmpty()) {
                state = CassetteDriveBlock.CassetteState.EMPTY;
            } else if (stack.getItem() instanceof CassetteTapeItem) {
                state = CassetteDriveBlock.CassetteState.ACCEPTED;
            } else {
                state = CassetteDriveBlock.CassetteState.REJECTED;
            }
            BlockState bs = getBlockState();
            if (bs.getBlock() instanceof CassetteDriveBlock) {
                level.setBlock(worldPosition, bs.setValue(CassetteDriveBlock.CASSETTE_STATE, state), 3);
            }
        }
    }

    public ItemStack getStoredItem() { return items.get(0); }

    public void tick() {}

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        ContainerHelper.saveAllItems(tag, items, registries);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        ContainerHelper.loadAllItems(tag, items, registries);
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("block.linguaperipherals.cassette_drive");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int id, Inventory playerInv, Player player) {
        return new CassetteDriveMenu(id, playerInv, this);
    }
}
