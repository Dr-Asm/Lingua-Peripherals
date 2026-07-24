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
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.component.DyedItemColor;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.client.event.sound.PlayStreamingSourceEvent;

@EventBusSubscriber(modid = LinguaPeripherals.MODID, value = Dist.CLIENT)
public class ClientModEvents {

    @SubscribeEvent
    static void registerMenuScreens(RegisterMenuScreensEvent event) {
        event.register(ModMenuTypes.CASSETTE_DRIVE.get(), CassetteDriveScreen::new);
    }

    @SubscribeEvent
    static void registerItemColors(RegisterColorHandlersEvent.Item event) {
        event.register((stack, tintIndex) -> {
            if (tintIndex == 1) {
                DyedItemColor color = stack.get(DataComponents.DYED_COLOR);
                int rgb = color != null ? color.rgb() : CassetteTapeItem.DEFAULT_COLOR;
                return rgb | 0xFF000000;
            }
            return -1;
        }, ModBlocks.CASSETTE_TAPE.get());
    }

    /**
     * Intercept streaming sound playback to inject the SoundEngine Channel and Executor
     * into the CassetteAudioStream, enabling real-time pumpBuffers when new audio packets arrive.
     */
    @SubscribeEvent
    static void onPlayStreaming(PlayStreamingSourceEvent event) {
        if (!(event.getSound() instanceof CassetteSound sound)) return;
        CassetteAudioManager.onPlayStreaming(
                event.getEngine(), event.getChannel(), sound.stream);
    }

    /** Register turtle upgrade models so the narrator renders correctly on turtles. */
    @SubscribeEvent
    static void registerTurtleModellers(RegisterTurtleModellersEvent event) {
        var narratorLeft  = ResourceLocation.fromNamespaceAndPath(LinguaPeripherals.MODID, "block/turtle_narrator_left");
        var narratorRight = ResourceLocation.fromNamespaceAndPath(LinguaPeripherals.MODID, "block/turtle_narrator_right");
        event.register(ModTurtleUpgrades.NARRATOR.get(),
                TurtleUpgradeModeller.sided(narratorLeft, narratorRight));

        var creativeLeft  = ResourceLocation.fromNamespaceAndPath(LinguaPeripherals.MODID, "block/turtle_creative_narrator_left");
        var creativeRight = ResourceLocation.fromNamespaceAndPath(LinguaPeripherals.MODID, "block/turtle_creative_narrator_right");
        event.register(ModTurtleUpgrades.CREATIVE_NARRATOR.get(),
                TurtleUpgradeModeller.sided(creativeLeft, creativeRight));
    }
}
