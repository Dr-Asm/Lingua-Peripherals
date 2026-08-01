package com.linguaperipherals.mod.client;

import com.linguaperipherals.mod.LinguaPeripherals;
import com.linguaperipherals.mod.client.audio.CassetteAudioManager;
import com.linguaperipherals.mod.client.audio.CassetteSound;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.sound.PlayStreamingSourceEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * Client-side event handlers for the FORGE event bus (gameplay events).
 * Kept separate from ClientModEvents which handles MOD bus registration events.
 */
@Mod.EventBusSubscriber(modid = LinguaPeripherals.MODID, value = Dist.CLIENT)
public class ForgeClientEvents {

    @SubscribeEvent
    static void onPlayStreaming(PlayStreamingSourceEvent event) {
        if (!(event.getSound() instanceof CassetteSound sound)) return;
        CassetteAudioManager.onPlayStreaming(
                event.getEngine(), event.getChannel(), sound.stream);
    }
}
