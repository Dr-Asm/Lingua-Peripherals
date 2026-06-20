package com.linguaperipherals.mod.inventory;

import com.linguaperipherals.mod.block.entity.CassetteDriveBlockEntity;
import com.linguaperipherals.mod.init.ModMenuTypes;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class CassetteDriveMenu extends AbstractContainerMenu {
    private final Container inventory;

    // Client constructor (called by MenuType on client)
    public CassetteDriveMenu(int id, Inventory playerInv) {
        super(ModMenuTypes.CASSETTE_DRIVE.get(), id);
        this.inventory = new SimpleContainer(1);
        addSlots(playerInv);
    }

    // Server constructor (called from BlockEntity.createMenu)
    public CassetteDriveMenu(int id, Inventory playerInv, CassetteDriveBlockEntity blockEntity) {
        super(ModMenuTypes.CASSETTE_DRIVE.get(), id);
        this.inventory = blockEntity.getInventory();
        this.inventory.startOpen(playerInv.player);
        addSlots(playerInv);
    }

    private void addSlots(Inventory playerInv) {
        this.addSlot(new Slot(inventory, 0, 80, 35));
        for (int row = 0; row < 3; row++)
            for (int col = 0; col < 9; col++)
                this.addSlot(new Slot(playerInv, col + row * 9 + 9, 8 + col * 18, 84 + row * 18));
        for (int col = 0; col < 9; col++)
            this.addSlot(new Slot(playerInv, col, 8 + col * 18, 142));
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        Slot slot = this.slots.get(index);
        if (slot == null || !slot.hasItem()) return ItemStack.EMPTY;
        ItemStack stack = slot.getItem();
        ItemStack copy = stack.copy();
        if (index == 0) {
            if (!this.moveItemStackTo(stack, 1, 37, true)) return ItemStack.EMPTY;
        } else {
            if (!this.moveItemStackTo(stack, 0, 1, false)) return ItemStack.EMPTY;
        }
        if (stack.isEmpty()) slot.setByPlayer(ItemStack.EMPTY);
        else slot.setChanged();
        return copy;
    }

    @Override
    public boolean stillValid(Player player) {
        return inventory.stillValid(player);
    }
}