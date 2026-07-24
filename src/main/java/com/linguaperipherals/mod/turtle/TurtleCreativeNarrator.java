package com.linguaperipherals.mod.turtle;

import com.linguaperipherals.mod.peripheral.CreativeNarratorPeripheral;
import dan200.computercraft.api.peripheral.IPeripheral;
import dan200.computercraft.api.turtle.AbstractTurtleUpgrade;
import dan200.computercraft.api.turtle.ITurtleAccess;
import dan200.computercraft.api.turtle.TurtleSide;
import dan200.computercraft.api.turtle.TurtleUpgradeType;
import dan200.computercraft.api.upgrades.UpgradeType;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

/**
 * Turtle upgrade for the Creative Narrator. Provides {@code globalVoice(text)}.
 */
public class TurtleCreativeNarrator extends AbstractTurtleUpgrade {
    private static class Peripheral extends CreativeNarratorPeripheral {
        final ITurtleAccess turtle;

        Peripheral(ITurtleAccess turtle) {
            super((com.linguaperipherals.mod.block.entity.CreativeNarratorBlockEntity) null);
            this.turtle = turtle;
        }

        @Override
        protected ServerLevel getLevel() {
            return turtle.getLevel() instanceof ServerLevel sl ? sl : null;
        }

        @Override
        protected BlockPos getPos() { return turtle.getPosition(); }

        @Override
        public boolean equals(@Nullable IPeripheral other) {
            return this == other || (other instanceof Peripheral p && turtle == p.turtle);
        }
    }

    private final UpgradeType<TurtleCreativeNarrator> type;

    public TurtleCreativeNarrator(ItemStack item, UpgradeType<TurtleCreativeNarrator> type) {
        super(TurtleUpgradeType.PERIPHERAL, "upgrade.linguaperipherals.creative_narrator.adjective", item);
        this.type = type;
    }

    @Override
    public UpgradeType<TurtleCreativeNarrator> getType() {
        return type;
    }

    @Override
    public IPeripheral createPeripheral(ITurtleAccess turtle, TurtleSide side) {
        return new Peripheral(turtle);
    }
}
