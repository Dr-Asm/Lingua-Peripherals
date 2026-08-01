package com.linguaperipherals.mod.client;

import com.linguaperipherals.mod.LinguaPeripherals;
import com.linguaperipherals.mod.client.audio.CassetteAudioManager;
import com.linguaperipherals.mod.client.audio.CassetteSound;
import com.linguaperipherals.mod.client.screen.CassetteDriveScreen;
import com.linguaperipherals.mod.init.ModBlocks;
import com.linguaperipherals.mod.init.ModMenuTypes;
import com.linguaperipherals.mod.init.ModTurtleUpgrades;
import com.linguaperipherals.mod.item.CassetteTapeItem;
import dan200.computercraft.api.client.turtle.RegisterTurtleModellersEvent;
import dan200.computercraft.api.client.turtle.TurtleUpgradeModeller;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterColorHandlersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(modid = LinguaPeripherals.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ClientModEvents {

    @SubscribeEvent
    static void onClientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() ->
                net.minecraft.client.gui.screens.MenuScreens.register(
                        ModMenuTypes.CASSETTE_DRIVE.get(), CassetteDriveScreen::new));
    }

    @SubscribeEvent
    static void registerItemColors(RegisterColorHandlersEvent.Item event) {
        event.register((stack, tintIndex) -> {
            if (tintIndex == 1) {
                int rgb = CassetteTapeItem.getCassetteColor(stack);
                return rgb | 0xFF000000;
            }
            return -1;
        }, ModBlocks.CASSETTE_TAPE.get());
    }

    @SubscribeEvent
    static void registerTurtleModellers(RegisterTurtleModellersEvent event) {
        var narratorLeft  = new ResourceLocation(LinguaPeripherals.MODID, "block/turtle_narrator_left");
        var narratorRight = new ResourceLocation(LinguaPeripherals.MODID, "block/turtle_narrator_right");
        event.register(ModTurtleUpgrades.NARRATOR.get(),
                TurtleUpgradeModeller.sided(narratorLeft, narratorRight));

        var creativeLeft  = new ResourceLocation(LinguaPeripherals.MODID, "block/turtle_creative_narrator_left");
        var creativeRight = new ResourceLocation(LinguaPeripherals.MODID, "block/turtle_creative_narrator_right");
        event.register(ModTurtleUpgrades.CREATIVE_NARRATOR.get(),
                TurtleUpgradeModeller.sided(creativeLeft, creativeRight));
    }
}
