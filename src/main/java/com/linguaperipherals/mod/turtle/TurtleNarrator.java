package com.linguaperipherals.mod.turtle;

import com.linguaperipherals.mod.peripheral.NarratorPeripheral;
import dan200.computercraft.api.peripheral.IPeripheral;
import dan200.computercraft.api.turtle.AbstractTurtleUpgrade;
import dan200.computercraft.api.turtle.ITurtleAccess;
import dan200.computercraft.api.turtle.TurtleSide;
import dan200.computercraft.api.turtle.TurtleUpgradeType;
import dan200.computercraft.api.upgrades.UpgradeType;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

/**
 * Turtle upgrade for the Narrator block.
 */
public class TurtleNarrator extends AbstractTurtleUpgrade {
    private static class Peripheral extends NarratorPeripheral {
        final ITurtleAccess turtle;

        Peripheral(ITurtleAccess turtle) {
            super((com.linguaperipherals.mod.block.entity.NarratorBlockEntity) null);
            this.turtle = turtle;
        }

        @Override
        protected @Nullable Level getLevel() { return turtle.getLevel(); }

        @Override
        protected BlockPos getPos() { return turtle.getPosition(); }

        @Override
        public boolean equals(@Nullable IPeripheral other) {
            return this == other || (other instanceof Peripheral p && turtle == p.turtle);
        }
    }

    private final UpgradeType<TurtleNarrator> type;

    public TurtleNarrator(ItemStack item, UpgradeType<TurtleNarrator> type) {
        super(TurtleUpgradeType.PERIPHERAL, "upgrade.linguaperipherals.narrator.adjective", item);
        this.type = type;
    }

    @Override
    public UpgradeType<TurtleNarrator> getType() {
        return type;
    }

    @Override
    public IPeripheral createPeripheral(ITurtleAccess turtle, TurtleSide side) {
        return new Peripheral(turtle);
    }
}
