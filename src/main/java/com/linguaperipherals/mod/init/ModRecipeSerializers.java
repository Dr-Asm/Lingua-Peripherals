package com.linguaperipherals.mod.init;

import com.linguaperipherals.mod.LinguaPeripherals;
import com.linguaperipherals.mod.recipe.CassetteTapeRecipe;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SimpleCraftingRecipeSerializer;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class ModRecipeSerializers {
    public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS =
            DeferredRegister.create(Registries.RECIPE_SERIALIZER, LinguaPeripherals.MODID);

    public static final RegistryObject<RecipeSerializer<CassetteTapeRecipe>> CASSETTE_TAPE =
            RECIPE_SERIALIZERS.register("crafting_special_cassettetape",
                    () -> new SimpleCraftingRecipeSerializer<>(CassetteTapeRecipe::new));
}
