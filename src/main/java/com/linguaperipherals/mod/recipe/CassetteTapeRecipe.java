package com.linguaperipherals.mod.recipe;

import com.linguaperipherals.mod.init.ModBlocks;
import com.linguaperipherals.mod.init.ModRecipeSerializers;
import com.linguaperipherals.mod.item.CassetteTapeItem;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.DyedItemColor;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;

public class CassetteTapeRecipe extends CustomRecipe {
    public CassetteTapeRecipe(CraftingBookCategory category) {
        super(category);
    }

    @Override
    public boolean matches(CraftingInput input, Level level) {
        int itemCount = 0;
        boolean hasTape = false;
        boolean hasRedstone = false;
        boolean hasIronNugget = false;
        boolean hasDriedKelp = false;
        int dyeCount = 0;

        for (int i = 0; i < input.size(); i++) {
            ItemStack stack = input.getItem(i);
            if (stack.isEmpty()) continue;
            itemCount++;
            if (stack.getItem() instanceof CassetteTapeItem) hasTape = true;
            else if (stack.is(Items.REDSTONE)) hasRedstone = true;
            else if (stack.is(Items.IRON_NUGGET)) hasIronNugget = true;
            else if (stack.is(Items.DRIED_KELP)) hasDriedKelp = true;
            else if (stack.getItem() instanceof DyeItem) dyeCount++;
            else return false;
        }

        if (hasTape) {
            return itemCount == 2 && dyeCount == 1;
        } else {
            return itemCount == 4 && hasRedstone && hasIronNugget && hasDriedKelp && dyeCount == 1;
        }
    }

    @Override
    public ItemStack assemble(CraftingInput input, HolderLookup.Provider registries) {
        DyeColor dyeColor = DyeColor.WHITE;
        ItemStack tapeStack = ItemStack.EMPTY;

        for (int i = 0; i < input.size(); i++) {
            ItemStack stack = input.getItem(i);
            if (stack.isEmpty()) continue;
            if (stack.getItem() instanceof DyeItem dye) {
                dyeColor = dye.getDyeColor();
            } else if (stack.getItem() instanceof CassetteTapeItem) {
                tapeStack = stack.copy();
            }
        }

        ItemStack result;
        if (!tapeStack.isEmpty()) {
            result = tapeStack.copyWithCount(1);
        } else {
            result = new ItemStack(ModBlocks.CASSETTE_TAPE.get());
        }
        int colorRgb = dyeColor.getFireworkColor();
                if (dyeColor == DyeColor.BLACK) colorRgb = 1973790;
                result.set(DataComponents.DYED_COLOR,
                new DyedItemColor(colorRgb, false));
        return result;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width * height >= 2;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipeSerializers.CASSETTE_TAPE.get();
    }
}