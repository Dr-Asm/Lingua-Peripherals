package com.linguaperipherals.mod.init;

import com.linguaperipherals.mod.LinguaPeripherals;
import com.linguaperipherals.mod.turtle.TurtleCreativeNarrator;
import com.linguaperipherals.mod.turtle.TurtleNarrator;
import dan200.computercraft.api.turtle.TurtleUpgradeSerialiser;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class ModTurtleUpgrades {
    private static final ResourceKey<net.minecraft.core.Registry<TurtleUpgradeSerialiser<?>>> REGISTRY_KEY =
            ResourceKey.createRegistryKey(new ResourceLocation("computercraft", "turtle_upgrade_serialiser"));

    public static final DeferredRegister<TurtleUpgradeSerialiser<?>> REGISTRY =
            DeferredRegister.create(REGISTRY_KEY, LinguaPeripherals.MODID);

    public static final RegistryObject<TurtleUpgradeSerialiser<TurtleNarrator>> NARRATOR =
            REGISTRY.register("narrator",
                    () -> TurtleUpgradeSerialiser.simpleWithCustomItem(TurtleNarrator::new));

    public static final RegistryObject<TurtleUpgradeSerialiser<TurtleCreativeNarrator>> CREATIVE_NARRATOR =
            REGISTRY.register("creative_narrator",
                    () -> TurtleUpgradeSerialiser.simpleWithCustomItem(TurtleCreativeNarrator::new));

    public static void register(IEventBus modEventBus) {
        REGISTRY.register(modEventBus);
    }
}
