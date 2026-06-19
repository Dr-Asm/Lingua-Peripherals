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
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

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

    public NarratorPeripheral(NarratorBlockEntity blockEntity) {
        this.blockEntity = blockEntity;
    }

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
        return blockEntity.getBlockPos().equals(that.blockEntity.getBlockPos());
    }

    @LuaFunction
    public final boolean playVoice(String text, Optional<Double> rad) throws LuaException {
        double r = rad.orElse(16.0);
        if (r < 0) throw new LuaException("rad must be non-negative");

        double maxRange = LinguaPeripheralsConfig.GLOBAL_MAX_RANGE.get();
        if (r > maxRange) r = maxRange;

        String decodedText = decodeEscapeSequences(text);

        if (blockEntity.getWorld() == null || blockEntity.getWorld().isClientSide) return false;

        BlockPos pos = blockEntity.getBlockPos();
        Vec3 center = Vec3.atCenterOf(pos);

        ServerLevel serverLevel = (ServerLevel) blockEntity.getWorld();
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

    protected String decodeEscapeSequences(String text) {
        if (text == null || text.isEmpty()) return text;
        StringBuilder result = new StringBuilder();
        int i = 0;
        while (i < text.length()) {
            if (text.charAt(i) == '\\' && i + 5 < text.length() && text.charAt(i + 1) == 'u') {
                String hex = text.substring(i + 2, i + 6);
                try {
                    int codePoint = Integer.parseInt(hex, 16);
                    result.append((char) codePoint);
                    i += 6;
                    continue;
                } catch (NumberFormatException ignored) {
                }
            }
            result.append(text.charAt(i));
            i++;
        }
        return result.toString();
    }
}