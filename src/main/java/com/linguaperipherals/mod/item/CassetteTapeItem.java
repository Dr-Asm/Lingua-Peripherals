package com.linguaperipherals.mod.item;

import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class CassetteTapeItem extends Item {
    public static final int DEFAULT_COLOR = 0xCCAA88;
    public static final int NO_ID = -1;

    private static final String NBT_ID = "CassetteId";
    private static final String NBT_LABEL = "CassetteLabel";
    private static final String NBT_COLOR = "CassetteColor";

    public CassetteTapeItem(Properties properties) {
        super(properties);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);
        if (flag.isAdvanced()) {
            int id = getCassetteID(stack);
            if (id != NO_ID) {
                tooltip.add(Component.translatable("tooltip.linguaperipherals.cassette_id", id)
                        .withStyle(ChatFormatting.GRAY));
            }
        }
    }

    public static int getCassetteID(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        if (tag == null) return NO_ID;
        return tag.contains(NBT_ID) ? tag.getInt(NBT_ID) : NO_ID;
    }

    public static void setCassetteID(ItemStack stack, int id) {
        if (id < 0) return;
        stack.getOrCreateTag().putInt(NBT_ID, id);
    }

    @Nullable
    public static String getCassetteLabel(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        if (tag == null) return null;
        return tag.contains(NBT_LABEL) ? tag.getString(NBT_LABEL) : null;
    }

    public static void setCassetteLabel(ItemStack stack, @Nullable String label) {
        CompoundTag tag = stack.getOrCreateTag();
        if (label != null) {
            stack.setHoverName(Component.literal(label));
            tag.putString(NBT_LABEL, label);
        } else {
            stack.resetHoverName();
            tag.remove(NBT_LABEL);
        }
    }

    public static int getCassetteColor(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        if (tag != null && tag.contains(NBT_COLOR)) {
            return tag.getInt(NBT_COLOR);
        }
        return DEFAULT_COLOR;
    }

    public static void setCassetteColor(ItemStack stack, int color) {
        stack.getOrCreateTag().putInt(NBT_COLOR, color);
    }
}
