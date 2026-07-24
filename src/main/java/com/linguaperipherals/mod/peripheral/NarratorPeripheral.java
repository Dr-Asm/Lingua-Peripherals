package com.linguaperipherals.mod.peripheral;

import com.linguaperipherals.mod.block.entity.NarratorBlockEntity;
import com.linguaperipherals.mod.config.LinguaPeripheralsConfig;
import com.linguaperipherals.mod.network.SpeakTextPayload;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.peripheral.IComputerAccess;
import dan200.computercraft.api.peripheral.IPeripheral;
import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.common.ClientboundCustomPayloadPacket;
import net.minecraft.server.TickTask;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import com.linguaperipherals.mod.util.TextUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class NarratorPeripheral implements IPeripheral {
    private static final ScheduledExecutorService FINISH_SCHEDULER = Executors.newSingleThreadScheduledExecutor(r -> {
        Thread t = new Thread(r, "Narrator-Finish");
        t.setDaemon(true);
        return t;
    });

    protected final NarratorBlockEntity blockEntity;
    protected final List<IComputerAccess> attachedComputers = new CopyOnWriteArrayList<>();

    /** Block-based constructor (existing). */
    public NarratorPeripheral(NarratorBlockEntity blockEntity) {
        this.blockEntity = blockEntity;
    }

    // ---- Overridable position hooks (for turtle upgrades) ----

    /** The level this peripheral is in. Override for turtle upgrades. */
    @Nullable
    protected Level getLevel() {
        return blockEntity != null ? blockEntity.getWorld() : null;
    }

    /** The block position this peripheral is at. Override for turtle upgrades. */
    protected BlockPos getPos() {
        return blockEntity != null ? blockEntity.getBlockPos() : BlockPos.ZERO;
    }

    /** Convenience: cast level to ServerLevel. */
    @Nullable
    protected ServerLevel getServerLevel() {
        var level = getLevel();
        return level instanceof ServerLevel sl && !level.isClientSide ? sl : null;
    }

    // ---- IPeripheral ----

    @Override
    public String getType() {
        return "narrator";
    }

    @Override
    public void attach(IComputerAccess computer) {
        attachedComputers.add(computer);
    }

    @Override
    public void detach(IComputerAccess computer) {
        attachedComputers.remove(computer);
    }

    @Override
    public boolean equals(@Nullable IPeripheral other) {
        if (this == other) return true;
        if (!(other instanceof NarratorPeripheral that)) return false;
        return getPos().equals(that.getPos());
    }

    @LuaFunction
    public final boolean playVoice(String text, Optional<Double> rad) throws LuaException {
        double r = rad.orElse(16.0);
        if (r < 0) throw new LuaException("rad must be non-negative");

        double maxRange = LinguaPeripheralsConfig.GLOBAL_MAX_RANGE.get();
        if (r > maxRange) r = maxRange;

        String decodedText = TextUtils.decodeEscapeSequences(text);

        ServerLevel serverLevel = getServerLevel();
        if (serverLevel == null) return false;

        BlockPos pos = getPos();
        Vec3 center = Vec3.atCenterOf(pos);

        SpeakTextPayload payload = SpeakTextPayload.of(decodedText);
        ClientboundCustomPayloadPacket packet = new ClientboundCustomPayloadPacket(payload);

        for (ServerPlayer player : serverLevel.getServer().getPlayerList().getPlayers()) {
            if (isPlayerInRange(player, center, serverLevel, r)) {
                player.connection.send(packet);
            }
        }

        scheduleFinishEvent(decodedText, serverLevel);
        return true;
    }

    protected void scheduleFinishEvent(String decodedText, ServerLevel serverLevel) {
        long estimatedMs = Math.max(1500, decodedText.length() * 80L);
        List<IComputerAccess> computers = new ArrayList<>(attachedComputers);

        FINISH_SCHEDULER.schedule(() -> {
            serverLevel.getServer().tell(new TickTask(0, () -> {
                for (IComputerAccess comp : computers) {
                    comp.queueEvent("voice_finished");
                }
            }));
        }, estimatedMs, TimeUnit.MILLISECONDS);
    }

    protected boolean isPlayerInRange(ServerPlayer player, Vec3 sourcePos, ServerLevel sourceLevel, double rad) {
        if (player.level() != sourceLevel) return false;
        return player.distanceToSqr(sourcePos) <= rad * rad;
    }
}
