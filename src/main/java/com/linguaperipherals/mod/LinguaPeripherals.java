package com.linguaperipherals.mod;

import com.linguaperipherals.mod.config.LinguaPeripheralsConfig;
import com.linguaperipherals.mod.init.ModBlockEntities;
import com.linguaperipherals.mod.init.ModBlocks;
import com.linguaperipherals.mod.init.ModCreativeTabs;
import com.linguaperipherals.mod.init.ModMenuTypes;
import com.linguaperipherals.mod.init.ModRecipeSerializers;
import com.linguaperipherals.mod.init.ModTurtleUpgrades;
import com.linguaperipherals.mod.init.PeripheralRegistration;
import com.linguaperipherals.mod.init.VanillaDisplayPeripherals;
import com.linguaperipherals.mod.network.LinguaPeripheralsNetwork;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Mod(LinguaPeripherals.MODID)
public class LinguaPeripherals {
    public static final String MODID = "linguaperipherals";
    public static final Logger LOGGER = LoggerFactory.getLogger(LinguaPeripherals.class);

    public LinguaPeripherals() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        ModBlocks.BLOCKS.register(modEventBus);
        ModBlocks.ITEMS.register(modEventBus);
        ModBlockEntities.BLOCK_ENTITIES.register(modEventBus);
        ModCreativeTabs.TABS.register(modEventBus);
        ModMenuTypes.MENU_TYPES.register(modEventBus);
        ModRecipeSerializers.RECIPE_SERIALIZERS.register(modEventBus);
        ModTurtleUpgrades.register(modEventBus);

        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, LinguaPeripheralsConfig.SPEC);

        LinguaPeripheralsNetwork.register();

        // Defer CC peripheral registration to common setup
        modEventBus.addListener(this::onCommonSetup);
    }

    private void onCommonSetup(FMLCommonSetupEvent event) {
        PeripheralRegistration.register();
        VanillaDisplayPeripherals.register();

        if (ModList.get().isLoaded("create")) {
            try {
                Class.forName("com.linguaperipherals.mod.init.CreatePeripheralsSetup")
                    .getMethod("register")
                    .invoke(null);
            } catch (ReflectiveOperationException e) {
                LOGGER.error("Failed to register Create display peripherals", e);
            }
        }
    }
}
