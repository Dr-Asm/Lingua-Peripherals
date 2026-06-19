package com.linguaperipherals.mod.init;

import com.linguaperipherals.mod.LinguaPeripherals;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModCreativeTabs {
    public static final DeferredRegister<CreativeModeTab> TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, LinguaPeripherals.MODID);

    public static final Supplier<CreativeModeTab> LINGUA_PERIPHERALS_TAB = TABS.register("lingua_peripherals",
            () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup." + LinguaPeripherals.MODID))
                    .icon(() -> new ItemStack(ModBlocks.NARRATOR_ITEM.get()))
                    .displayItems((params, output) -> {
                        output.accept(ModBlocks.NARRATOR_ITEM.get());
                        output.accept(ModBlocks.CREATIVE_NARRATOR_ITEM.get());
                    })
                    .build());
}