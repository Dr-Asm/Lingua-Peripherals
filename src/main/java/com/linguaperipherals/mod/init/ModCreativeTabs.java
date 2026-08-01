package com.linguaperipherals.mod.init;

import com.linguaperipherals.mod.LinguaPeripherals;
import com.linguaperipherals.mod.item.CassetteTapeItem;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class ModCreativeTabs {
    public static final DeferredRegister<CreativeModeTab> TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, LinguaPeripherals.MODID);

    public static final RegistryObject<CreativeModeTab> LINGUA_PERIPHERALS_TAB = TABS.register("lingua_peripherals",
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
                            DyeItem dyeItem = DyeItem.byColor(dye);
                            int colorRgb = dyeItem.getDyeColor().getFireworkColor();
                            CassetteTapeItem.setCassetteColor(tape, colorRgb);
                            output.accept(tape);
                        }
                    })
                    .build());
}
