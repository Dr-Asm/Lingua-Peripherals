package com.linguaperipherals.mod.init;

import com.linguaperipherals.mod.LinguaPeripherals;
import com.linguaperipherals.mod.block.CassetteDriveBlock;
import com.linguaperipherals.mod.block.CreativeNarratorBlock;
import com.linguaperipherals.mod.block.NarratorBlock;
import com.linguaperipherals.mod.block.entity.CassetteDriveBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.Containers;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

@EventBusSubscriber(modid = LinguaPeripherals.MODID)
public class WrenchHandler {

    @SubscribeEvent
    public static void onBlockRightClick(PlayerInteractEvent.RightClickBlock event) {
        Level level = event.getLevel();
        BlockPos pos = event.getPos();
        BlockState state = level.getBlockState(pos);
        Block block = state.getBlock();
        Player player = event.getEntity();

        if (!(block instanceof NarratorBlock)
                && !(block instanceof CreativeNarratorBlock)
                && !(block instanceof CassetteDriveBlock)) return;
        if (block instanceof CreativeNarratorBlock && !player.isCreative()) return;

        ItemStack stack = event.getItemStack();

        if (isWrench(stack)) {
            event.setCanceled(true);
            if (level.isClientSide) return;

            if (player.isShiftKeyDown()) {
                if (block instanceof CreativeNarratorBlock) return;
                // Drop inventory contents before destroying (safety net; onRemove also handles this)
                if (block instanceof CassetteDriveBlock) {
                    BlockEntity be = level.getBlockEntity(pos);
                    if (be instanceof CassetteDriveBlockEntity drive) {
                        Containers.dropContents(level, pos, drive.getInventory());
                    }
                }
                ItemStack blockItem = new ItemStack(state.getBlock().asItem());
                if (!player.addItem(blockItem)) {
                    Block.popResource(level, player.blockPosition(), blockItem);
                }
                level.destroyBlock(pos, false);
                level.playSound(null, pos, SoundEvents.ITEM_PICKUP, SoundSource.BLOCKS, 1.0f, 1.0f);
            } else {
                Direction currentFacing = state.getValue(CassetteDriveBlock.FACING);
                Direction newFacing = currentFacing.getClockWise();
                level.setBlock(pos, state.setValue(CassetteDriveBlock.FACING, newFacing), 3);
                level.playSound(null, pos, SoundEvents.STONE_PLACE, SoundSource.BLOCKS, 1.0f, 1.0f);
            }
        }
    }

    private static boolean isWrench(ItemStack stack) {
        if (stack.is(ItemTags.create(ResourceLocation.fromNamespaceAndPath("c", "tools/wrench")))) return true;
        if (stack.is(ItemTags.create(ResourceLocation.fromNamespaceAndPath("forge", "tools/wrench")))) return true;
        return false;
    }
}