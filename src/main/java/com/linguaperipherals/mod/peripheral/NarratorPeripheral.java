package com.linguaperipherals.mod.peripheral;

import com.linguaperipherals.mod.block.entity.NarratorBlockEntity;
import com.linguaperipherals.mod.config.LinguaPeripheralsConfig;
import com.linguaperipherals.mod.network.LinguaPeripheralsNetwork;
import com.linguaperipherals.mod.util.LinguaUtility;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.peripheral.IComputerAccess;
import dan200.computercraft.api.peripheral.IPeripheral;
import dan200.computercraft.shared.peripheral.speaker.SpeakerPeripheral;
import dan200.computercraft.shared.peripheral.speaker.SpeakerPosition;
import net.minecraft.core.BlockPos;
import net.minecraftforge.network.PacketDistributor;
import net.minecraft.server.TickTask;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Narrator peripheral — TTS speech via Minecraft Narrator system, plus all
 * standard CC Speaker functionality (playNote, playSound, playAudio, stop).
 */
public class NarratorPeripheral extends SpeakerPeripheral {
    private static final ScheduledExecutorService FINISH_SCHEDULER = Executors.newSingleThreadScheduledExecutor(r -> {
        Thread t = new Thread(r, "Narrator-Finish");
        t.setDaemon(true);
        return t;
    });

    protected final NarratorBlockEntity blockEntity;
    protected final List<IComputerAccess> attachedComputers = new CopyOnWriteArrayList<>();

    /** Block-based constructor. */
    public NarratorPeripheral(NarratorBlockEntity blockEntity) {
        this.blockEntity = blockEntity;
    }

    // ---- SpeakerPeripheral abstract method (only getPosition in CC 1.20.1) ----

    @Override
    public SpeakerPosition getPosition() {
        Level level = blockEntity != null ? blockEntity.getWorld() : null;
        return SpeakerPosition.of(level, Vec3.atCenterOf(getPos()));
    }

    // ---- Helper to extract ServerLevel (for playVoice / turtle overrides) ----

    @Nullable
    protected ServerLevel getLevel() {
        Level level = getPosition().level();
        return level instanceof ServerLevel sl ? sl : null;
    }

    // ---- IPeripheral ----

    @Override
    public String getType() {
        return "narrator";
    }

    @Override
    public void attach(IComputerAccess computer) {
        super.attach(computer);
        attachedComputers.add(computer);
    }

    @Override
    public void detach(IComputerAccess computer) {
        super.detach(computer);
        attachedComputers.remove(computer);
    }

    @Override
    public boolean equals(@Nullable IPeripheral other) {
        if (this == other) return true;
        if (!(other instanceof NarratorPeripheral that)) return false;
        return getPos().equals(that.getPos());
    }

    // ---- Position helpers (for turtle overrides) ----

    protected BlockPos getPos() {
        return blockEntity != null ? blockEntity.getBlockPos() : BlockPos.ZERO;
    }

    // ---- Narrator-specific method ----

    @LuaFunction
    public final boolean playVoice(String text, Optional<Double> rad) throws LuaException {
        double r = rad.orElse(16.0);
        if (r < 0) throw new LuaException("rad must be non-negative");

        double maxRange = LinguaPeripheralsConfig.GLOBAL_MAX_RANGE.get();
        if (r > maxRange) r = maxRange;

        String decodedText = LinguaUtility.fixLuaString(text);

        ServerLevel serverLevel = getLevel();
        if (serverLevel == null) return false;

        BlockPos pos = getPos();
        Vec3 center = Vec3.atCenterOf(pos);

        var payload = new LinguaPeripheralsNetwork.SpeakTextPacket(decodedText);

        for (ServerPlayer player : serverLevel.getServer().getPlayerList().getPlayers()) {
            if (isPlayerInRange(player, center, serverLevel, r)) {
                LinguaPeripheralsNetwork.CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), payload);
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
