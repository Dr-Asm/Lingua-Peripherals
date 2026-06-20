package com.linguaperipherals.mod.init;

import com.linguaperipherals.mod.LinguaPeripherals;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.DyedItemColor;
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
                        output.accept(ModBlocks.CASSETTE_DRIVE_ITEM.get());
                        // Colored cassette tapes (one per dye color)
                        for (DyeColor dye : DyeColor.values()) {
                            ItemStack tape = new ItemStack(ModBlocks.CASSETTE_TAPE.get());
                            int colorRgb = dye.getFireworkColor();
                            // Workaround: Minecraft recipe book fails to match DyedItemColor with rgb=1908001
                            if (dye == DyeColor.BLACK) colorRgb = 1973790;
                            if (dye == DyeColor.GRAY) colorRgb = 6710886;
                            tape.set(DataComponents.DYED_COLOR,
                                    new DyedItemColor(colorRgb, false));
                            output.accept(tape);
                        }
                    })
                    .build());
}