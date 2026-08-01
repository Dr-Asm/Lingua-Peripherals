package com.linguaperipherals.mod.recipe;

import com.linguaperipherals.mod.init.ModRecipeSerializers;
import com.linguaperipherals.mod.item.CassetteTapeItem;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;

public class CassetteTapeRecipe extends CustomRecipe {

    public CassetteTapeRecipe(ResourceLocation id, CraftingBookCategory category) {
        super(id, category);
    }

    @Override
    public boolean matches(CraftingContainer container, Level level) {
        ItemStack tape = ItemStack.EMPTY;
        ItemStack dye = ItemStack.EMPTY;
        int count = 0;

        for (int i = 0; i < container.getContainerSize(); i++) {
            ItemStack stack = container.getItem(i);
            if (stack.isEmpty()) continue;
            count++;
            if (stack.getItem() instanceof CassetteTapeItem) {
                if (!tape.isEmpty()) return false;
                tape = stack;
            } else if (stack.getItem() instanceof DyeItem) {
                if (!dye.isEmpty()) return false;
                dye = stack;
            } else {
                return false;
            }
        }

        return count == 2 && !tape.isEmpty() && !dye.isEmpty();
    }

    @Override
    public ItemStack assemble(CraftingContainer container, RegistryAccess registryAccess) {
        ItemStack tape = ItemStack.EMPTY;
        DyeItem dyeItem = null;

        for (int i = 0; i < container.getContainerSize(); i++) {
            ItemStack stack = container.getItem(i);
            if (stack.getItem() instanceof CassetteTapeItem) {
                tape = stack;
            } else if (stack.getItem() instanceof DyeItem di) {
                dyeItem = di;
            }
        }

        if (tape.isEmpty() || dyeItem == null) return ItemStack.EMPTY;

        ItemStack result = tape.copy();
        result.setCount(1);

        int colorRgb = dyeItem.getDyeColor().getFireworkColor();
        CassetteTapeItem.setCassetteColor(result, colorRgb);

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
