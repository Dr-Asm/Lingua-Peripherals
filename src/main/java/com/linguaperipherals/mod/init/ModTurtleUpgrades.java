package com.linguaperipherals.mod.init;

import com.linguaperipherals.mod.LinguaPeripherals;
import com.linguaperipherals.mod.turtle.TurtleCreativeNarrator;
import com.linguaperipherals.mod.turtle.TurtleNarrator;
import dan200.computercraft.api.turtle.ITurtleUpgrade;
import dan200.computercraft.api.upgrades.UpgradeType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.bus.api.IEventBus;

import java.util.function.Supplier;

public class ModTurtleUpgrades {
    public static final DeferredRegister<UpgradeType<? extends ITurtleUpgrade>> REGISTRY =
            DeferredRegister.create(ITurtleUpgrade.typeRegistry(), LinguaPeripherals.MODID);

    public static final Supplier<UpgradeType<TurtleNarrator>> NARRATOR =
            REGISTRY.register("narrator", () -> {
                // Deferred self-reference to avoid circular init
                @SuppressWarnings("unchecked")
                UpgradeType<TurtleNarrator>[] holder = new UpgradeType[1];
                holder[0] = UpgradeType.simpleWithCustomItem(stack -> new TurtleNarrator(stack, holder[0]));
                return holder[0];
            });

    public static final Supplier<UpgradeType<TurtleCreativeNarrator>> CREATIVE_NARRATOR =
            REGISTRY.register("creative_narrator", () -> {
                @SuppressWarnings("unchecked")
                UpgradeType<TurtleCreativeNarrator>[] holder = new UpgradeType[1];
                holder[0] = UpgradeType.simpleWithCustomItem(stack -> new TurtleCreativeNarrator(stack, holder[0]));
                return holder[0];
            });

    public static void register(IEventBus modEventBus) {
        REGISTRY.register(modEventBus);
    }
}
