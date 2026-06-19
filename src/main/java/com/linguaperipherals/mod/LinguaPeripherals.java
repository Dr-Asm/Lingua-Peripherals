package com.linguaperipherals.mod;

import com.linguaperipherals.mod.init.ModBlocks;
import com.linguaperipherals.mod.init.ModBlockEntities;
import com.linguaperipherals.mod.init.ModCreativeTabs;
import com.linguaperipherals.mod.init.PeripheralRegistration;
import com.linguaperipherals.mod.network.LinguaPeripheralsNetwork;
import com.linguaperipherals.mod.config.LinguaPeripheralsConfig;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Mod(LinguaPeripherals.MODID)
public class LinguaPeripherals {
    public static final String MODID = "linguaperipherals";
    public static final Logger LOGGER = LoggerFactory.getLogger(LinguaPeripherals.class);

    public LinguaPeripherals(IEventBus modEventBus, ModContainer modContainer) {
        ModBlocks.BLOCKS.register(modEventBus);
        ModBlocks.ITEMS.register(modEventBus);
        ModBlockEntities.BLOCK_ENTITIES.register(modEventBus);
        ModCreativeTabs.TABS.register(modEventBus);

        modContainer.registerConfig(ModConfig.Type.COMMON, LinguaPeripheralsConfig.SPEC, "lingua_peripherals.conf");

        PeripheralRegistration.register(modEventBus);
        LinguaPeripheralsNetwork.register(modEventBus);
    }
}