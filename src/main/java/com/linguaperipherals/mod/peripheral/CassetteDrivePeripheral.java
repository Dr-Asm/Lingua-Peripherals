package com.linguaperipherals.mod.peripheral;

import com.linguaperipherals.mod.block.entity.CassetteDriveBlockEntity;
import com.linguaperipherals.mod.item.CassetteTapeItem;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.peripheral.IComputerAccess;
import dan200.computercraft.api.peripheral.IPeripheral;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.List;

public class CassetteDrivePeripheral implements IPeripheral {
    private final CassetteDriveBlockEntity blockEntity;
    final List<IComputerAccess> attachedComputers = new CopyOnWriteArrayList<>();

    public CassetteDrivePeripheral(CassetteDriveBlockEntity blockEntity) {
        this.blockEntity = blockEntity;
    }

    @Override
    public String getType() { return "cassette_drive"; }

    @Override
    public void attach(IComputerAccess computer) { attachedComputers.add(computer); }

    @Override
    public void detach(IComputerAccess computer) {
        attachedComputers.remove(computer);
        blockEntity.onComputerDetach(computer);
    }

    @Override
    public boolean equals(@Nullable IPeripheral other) {
        if (this == other) return true;
        if (!(other instanceof CassetteDrivePeripheral that)) return false;
        return blockEntity.getBlockPos().equals(that.blockEntity.getBlockPos());
    }

    @LuaFunction
    public final boolean isTapePresent() {
        ItemStack stack = blockEntity.getStoredItem();
        return !stack.isEmpty() && stack.getItem() instanceof CassetteTapeItem;
    }

    @LuaFunction
    public final @Nullable Object @Nullable [] getTapeID() {
        ItemStack stack = blockEntity.getStoredItem();
        if (!(stack.getItem() instanceof CassetteTapeItem)) return null;
        int id = CassetteTapeItem.getCassetteID(stack);
        return id != CassetteTapeItem.NO_ID ? new Object[]{id} : null;
    }

    @LuaFunction
    public final @Nullable Object @Nullable [] getTapeLabel() {
        ItemStack stack = blockEntity.getStoredItem();
        if (!(stack.getItem() instanceof CassetteTapeItem)) return null;
        String label = CassetteTapeItem.getCassetteLabel(stack);
        return label != null ? new Object[]{label} : null;
    }

    @LuaFunction(mainThread = true)
    public final void setTapeLabel(Optional<String> label) throws LuaException {
        ItemStack stack = blockEntity.getStoredItem();
        if (!(stack.getItem() instanceof CassetteTapeItem))
            throw new LuaException("No cassette tape in drive");
        CassetteTapeItem.setCassetteLabel(stack, label.orElse(null));
        blockEntity.setChanged();
    }

    @LuaFunction
    public final void ejectTape() { blockEntity.requestEject(); }

    @LuaFunction
    public final long dataSize() throws LuaException {
        CassetteTapeStorage storage = blockEntity.getTapeStorage();
        if (storage == null) throw new LuaException("No cassette tape in drive");
        return storage.size();
    }

    @LuaFunction
    public final long dataSizeLimit() throws LuaException {
        CassetteTapeStorage storage = blockEntity.getTapeStorage();
        if (storage == null) throw new LuaException("No cassette tape in drive");
        return storage.sizeLimit();
    }

    @LuaFunction
    public final CassetteTapeFileHandle open(IComputerAccess computer, String mode) throws LuaException {
        CassetteTapeStorage storage = blockEntity.getTapeStorage();
        if (storage == null) throw new LuaException("No cassette tape in drive");
        if (mode == null || mode.isEmpty())
            throw new LuaException("bad argument #1 to 'open' (string expected, got nil)");
        String m = mode.trim();
        if (!m.matches("^[rwa][b+]?[b+]?$"))
            throw new LuaException("unsupported mode '" + mode + "'");
        try {
            blockEntity.closeHandle(computer);
            CassetteTapeFileHandle handle = storage.open(m);
            blockEntity.trackHandle(computer, handle);
            return handle;
        } catch (IOException e) { throw new LuaException("Failed to open tape: " + e.getMessage()); }
    }

    @LuaFunction
    public final void close() throws LuaException {
        CassetteTapeStorage storage = blockEntity.getTapeStorage();
        if (storage == null) throw new LuaException("No cassette tape in drive");
        storage.forceClose();
    }

    @LuaFunction(mainThread = true)
    public final void reset() throws LuaException {
        CassetteTapeStorage storage = blockEntity.getTapeStorage();
        if (storage == null) throw new LuaException("No cassette tape in drive");
        storage.forceClose();
        try {
            java.nio.file.Files.write(storage.getFilePath(), new byte[0]);
        } catch (IOException e) {
            throw new LuaException("Failed to reset tape: " + e.getMessage());
        }
    }
}