package com.linguaperipherals.mod.block.entity;

import com.linguaperipherals.mod.LinguaPeripherals;
import com.linguaperipherals.mod.block.CassetteDriveBlock;
import com.linguaperipherals.mod.config.LinguaPeripheralsConfig;
import com.linguaperipherals.mod.init.ModBlockEntities;
import com.linguaperipherals.mod.inventory.CassetteDriveMenu;
import com.linguaperipherals.mod.item.CassetteTapeItem;
import com.linguaperipherals.mod.peripheral.CassetteDrivePeripheral;
import com.linguaperipherals.mod.peripheral.CassetteTapeFileHandle;
import com.linguaperipherals.mod.peripheral.CassetteTapeStorage;
import dan200.computercraft.api.ComputerCraftAPI;
import dan200.computercraft.api.peripheral.IComputerAccess;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.LevelEvent;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.LevelResource;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.nio.file.Path;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

public class CassetteDriveBlockEntity extends BlockEntity implements MenuProvider {
    private static final LevelResource CC_FOLDER = new LevelResource("computercraft");

    private final NonNullList<ItemStack> items = NonNullList.withSize(1, ItemStack.EMPTY);
    private final Container inventory = new Container() {
        @Override public int getContainerSize() { return 1; }
        @Override public boolean isEmpty() { return items.get(0).isEmpty(); }
        @Override public ItemStack getItem(int slot) { return items.get(slot); }
        @Override public ItemStack removeItem(int slot, int amount) {
            ItemStack result = ContainerHelper.removeItem(items, slot, amount);
            if (!result.isEmpty()) onItemChanged();
            return result;
        }
        @Override public ItemStack removeItemNoUpdate(int slot) {
            ItemStack result = ContainerHelper.takeItem(items, slot);
            if (!result.isEmpty()) onItemChanged();
            return result;
        }
        @Override public void setItem(int slot, ItemStack stack) {
            items.set(slot, stack);
            onItemChanged();
        }
        @Override public void setChanged() { CassetteDriveBlockEntity.this.setChanged(); }
        @Override public boolean stillValid(Player player) {
            return Container.stillValidBlockEntity(CassetteDriveBlockEntity.this, player);
        }
        @Override public void clearContent() { items.clear(); onItemChanged(); }
    };

    private final CassetteDrivePeripheral peripheral = new CassetteDrivePeripheral(this);
    private @Nullable CassetteTapeStorage tapeStorage;
    private final Map<IComputerAccess, CassetteTapeFileHandle> computerHandles = new ConcurrentHashMap<>();
    private final AtomicBoolean ejectQueued = new AtomicBoolean(false);

    public CassetteDriveBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.CASSETTE_DRIVE_BE.get(), pos, state);
    }

    public CassetteDrivePeripheral getPeripheral() { return peripheral; }
    public Container getInventory() { return inventory; }
    public ItemStack getStoredItem() { return items.get(0); }
    public @Nullable CassetteTapeStorage getTapeStorage() { return tapeStorage; }

    public void closeHandle(IComputerAccess computer) {
        CassetteTapeFileHandle old = computerHandles.remove(computer);
        if (old != null) {
            try { old.forceClose(); } catch (IOException ignored) {}
        }
    }

    public void trackHandle(IComputerAccess computer, CassetteTapeFileHandle handle) {
        computerHandles.put(computer, handle);
    }

    @Override
    public void clearRemoved() {
        super.clearRemoved();
        if (level != null && !level.isClientSide) refreshStorage();
    }

    @Override
    public void setRemoved() {
        super.setRemoved();
        closeAllHandles();
        tapeStorage = null;
    }

    public void onComputerDetach(IComputerAccess computer) {
        closeHandle(computer);
    }

    public void requestEject() { ejectQueued.set(true); }

    // ==================== Internal ====================

    private void onItemChanged() {
        if (level != null && !level.isClientSide) {
            closeAllHandles();
            updateBlockState(getStoredItem());
            refreshStorage();
        }
    }

    private void refreshStorage() {
        ItemStack stack = getStoredItem();
        if (stack.getItem() instanceof CassetteTapeItem && level instanceof ServerLevel sl) {
            int id = CassetteTapeItem.getCassetteID(stack);
            if (id == CassetteTapeItem.NO_ID) {
                try {
                    id = ComputerCraftAPI.createUniqueNumberedSaveDir(sl.getServer(), "cassette_tape");
                    CassetteTapeItem.setCassetteID(stack, id);
                    setChanged();
                } catch (Exception e) {
                    LinguaPeripherals.LOGGER.error("Failed to assign cassette tape ID", e);
                    tapeStorage = null;
                    return;
                }
            }
            try {
                Path ccDir = sl.getServer().getWorldPath(CC_FOLDER);
                Path tapeDir = ccDir.resolve("cassette_tape").resolve(String.valueOf(id));
                long limit = LinguaPeripheralsConfig.CASSETTE_TAPE_SIZE_LIMIT.get();
                tapeStorage = new CassetteTapeStorage(tapeDir.resolve("data.bin"), limit);
            } catch (IOException e) {
                LinguaPeripherals.LOGGER.error("Failed to create tape storage for id {}", id, e);
                tapeStorage = null;
            }
        } else {
            tapeStorage = null;
        }
    }

    private void updateBlockState(ItemStack stack) {
        if (level != null && !level.isClientSide) {
            CassetteDriveBlock.CassetteState state;
            if (stack.isEmpty()) state = CassetteDriveBlock.CassetteState.EMPTY;
            else if (stack.getItem() instanceof CassetteTapeItem) state = CassetteDriveBlock.CassetteState.ACCEPTED;
            else state = CassetteDriveBlock.CassetteState.REJECTED;
            BlockState bs = getBlockState();
            if (bs.getBlock() instanceof CassetteDriveBlock) {
                level.setBlock(worldPosition, bs.setValue(CassetteDriveBlock.CASSETTE_STATE, state), 2);
            }
        }
    }

    private void doEject() {
        if (level == null || level.isClientSide) return;
        ItemStack stack = getStoredItem();
        if (stack.isEmpty()) return;
        closeAllHandles();
        tapeStorage = null;
        items.set(0, ItemStack.EMPTY);
        updateBlockState(ItemStack.EMPTY);

        Direction facing = getBlockState().getValue(CassetteDriveBlock.FACING);
        double xDir = facing.getStepX();
        double zDir = facing.getStepZ();
        double x = worldPosition.getX() + 0.5 + xDir * 0.7;
        double y = worldPosition.getY() + 0.5;
        double z = worldPosition.getZ() + 0.5 + zDir * 0.7;
        ItemEntity entity = new ItemEntity(level, x, y, z, stack.copy());
        double baseSpeed = level.random.nextDouble() * 0.1 + 0.2;
        double dropSpeed = 0.0172275 * 6;
        entity.setDeltaMovement(
            level.random.triangle(xDir * baseSpeed, dropSpeed),
            level.random.triangle(0, dropSpeed),
            level.random.triangle(zDir * baseSpeed, dropSpeed)
        );
        entity.setDefaultPickUpDelay();
        level.addFreshEntity(entity);
        level.levelEvent(LevelEvent.SOUND_DISPENSER_DISPENSE, worldPosition, 0);
        setChanged();
    }

    private void closeAllHandles() {
        for (CassetteTapeFileHandle h : computerHandles.values()) {
            try { h.forceClose(); } catch (IOException ignored) {}
        }
        computerHandles.clear();
        if (tapeStorage != null) tapeStorage.forceClose();
    }

    public void tick() {
        if (level == null || level.isClientSide) return;
        if (ejectQueued.getAndSet(false)) doEject();
    }

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

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int id, Inventory playerInv, Player player) {
        return new CassetteDriveMenu(id, playerInv, this);
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("block.linguaperipherals.cassette_drive");
    }
}