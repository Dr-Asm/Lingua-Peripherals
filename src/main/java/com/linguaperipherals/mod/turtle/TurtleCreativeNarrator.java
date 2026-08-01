package com.linguaperipherals.mod.turtle;

import com.linguaperipherals.mod.peripheral.CreativeNarratorPeripheral;
import dan200.computercraft.api.peripheral.IPeripheral;
import dan200.computercraft.api.turtle.AbstractTurtleUpgrade;
import dan200.computercraft.api.turtle.ITurtleAccess;
import dan200.computercraft.api.turtle.TurtleSide;
import dan200.computercraft.api.turtle.TurtleUpgradeType;
import dan200.computercraft.shared.peripheral.speaker.SpeakerPosition;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
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
        public SpeakerPosition getPosition() {
            return SpeakerPosition.of(turtle.getLevel(), Vec3.atCenterOf(turtle.getPosition()));
        }

        @Override
        protected BlockPos getPos() { return turtle.getPosition(); }

        @Override
        public boolean equals(@Nullable IPeripheral other) {
            return this == other || (other instanceof Peripheral p && turtle == p.turtle);
        }
    }

    public TurtleCreativeNarrator(ResourceLocation id, ItemStack stack) {
        super(id, TurtleUpgradeType.PERIPHERAL, stack);
    }

    @Override
    public IPeripheral createPeripheral(ITurtleAccess turtle, TurtleSide side) {
        return new Peripheral(turtle);
    }
}
