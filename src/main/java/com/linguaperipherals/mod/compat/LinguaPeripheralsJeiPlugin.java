package com.linguaperipherals.mod.compat;

import com.linguaperipherals.mod.LinguaPeripherals;
import com.linguaperipherals.mod.init.ModBlocks;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.ingredients.subtypes.ISubtypeInterpreter;
import mezz.jei.api.ingredients.subtypes.UidContext;
import mezz.jei.api.registration.ISubtypeRegistration;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.DyedItemColor;
import org.jetbrains.annotations.Nullable;

@JeiPlugin
public class LinguaPeripheralsJeiPlugin implements IModPlugin {
    @Override
    public ResourceLocation getPluginUid() {
        return ResourceLocation.fromNamespaceAndPath(LinguaPeripherals.MODID, "jei_plugin");
    }

    @Override
    public void registerItemSubtypes(ISubtypeRegistration registration) {
        registration.registerSubtypeInterpreter(ModBlocks.CASSETTE_TAPE.get(),
                new ISubtypeInterpreter<ItemStack>() {
                    @Override
                    public @Nullable Object getSubtypeData(ItemStack stack, UidContext context) {
                        DyedItemColor color = stack.get(DataComponents.DYED_COLOR);
                        return color != null ? color.rgb() : 0;
                    }

                    @Override
                    public String getLegacyStringSubtypeInfo(ItemStack stack, UidContext context) {
                        DyedItemColor color = stack.get(DataComponents.DYED_COLOR);
                        return color != null ? String.valueOf(color.rgb()) : "default";
                    }
                });
    }
}