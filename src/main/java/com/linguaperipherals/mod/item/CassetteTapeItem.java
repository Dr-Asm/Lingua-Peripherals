package com.linguaperipherals.mod.item;

import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class CassetteTapeItem extends Item {
    public static final int DEFAULT_COLOR = 0xCCAA88;
    public static final int NO_ID = -1;

    private static final String NBT_ID = "CassetteId";
    private static final String NBT_LABEL = "CassetteLabel";

    public CassetteTapeItem(Properties properties) {
        super(properties);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, context, tooltip, flag);
        if (flag.isAdvanced()) {
            int id = getCassetteID(stack);
            if (id != NO_ID) {
                tooltip.add(Component.translatable("tooltip.linguaperipherals.cassette_id", id)
                        .withStyle(ChatFormatting.GRAY));
            }
        }
    }

    public static int getCassetteID(ItemStack stack) {
        CustomData data = stack.get(DataComponents.CUSTOM_DATA);
        if (data == null) return NO_ID;
        CompoundTag tag = data.copyTag();
        return tag.contains(NBT_ID) ? tag.getInt(NBT_ID) : NO_ID;
    }

    public static void setCassetteID(ItemStack stack, int id) {
        if (id < 0) return;
        stack.update(DataComponents.CUSTOM_DATA, CustomData.EMPTY, existing -> {
            CompoundTag tag = existing.copyTag();
            tag.putInt(NBT_ID, id);
            return CustomData.of(tag);
        });
    }

    @Nullable
    public static String getCassetteLabel(ItemStack stack) {
        CustomData data = stack.get(DataComponents.CUSTOM_DATA);
        if (data == null) return null;
        CompoundTag tag = data.copyTag();
        return tag.contains(NBT_LABEL) ? tag.getString(NBT_LABEL) : null;
    }

    public static void setCassetteLabel(ItemStack stack, @Nullable String label) {
        if (label != null) {
            stack.set(DataComponents.CUSTOM_NAME, Component.literal(label));
            stack.update(DataComponents.CUSTOM_DATA, CustomData.EMPTY, existing -> {
                CompoundTag tag = existing.copyTag();
                tag.putString(NBT_LABEL, label);
                return CustomData.of(tag);
            });
        } else {
            stack.remove(DataComponents.CUSTOM_NAME);
            stack.update(DataComponents.CUSTOM_DATA, CustomData.EMPTY, existing -> {
                CompoundTag tag = existing.copyTag();
                tag.remove(NBT_LABEL);
                return CustomData.of(tag);
            });
        }
    }
}