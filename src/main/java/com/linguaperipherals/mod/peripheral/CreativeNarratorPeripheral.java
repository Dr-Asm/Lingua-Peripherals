package com.linguaperipherals.mod.peripheral;

import com.linguaperipherals.mod.block.entity.CreativeNarratorBlockEntity;
import com.linguaperipherals.mod.network.SpeakTextPayload;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.LuaFunction;
import net.minecraft.network.protocol.common.ClientboundCustomPayloadPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import com.linguaperipherals.mod.util.TextUtils;

public class CreativeNarratorPeripheral extends NarratorPeripheral {

    public CreativeNarratorPeripheral(CreativeNarratorBlockEntity blockEntity) {
        super(blockEntity);
    }

    @Override
    public String getType() {
        return "creative_narrator";
    }

    @LuaFunction
    public final boolean globalVoice(String text) throws LuaException {
        String decodedText = TextUtils.decodeEscapeSequences(text);

        if (blockEntity.getWorld() == null || blockEntity.getWorld().isClientSide) return false;

        ServerLevel serverLevel = (ServerLevel) blockEntity.getWorld();
        SpeakTextPayload payload = SpeakTextPayload.of(decodedText);
        ClientboundCustomPayloadPacket packet = new ClientboundCustomPayloadPacket(payload);

        for (ServerPlayer player : serverLevel.getServer().getPlayerList().getPlayers()) {
            player.connection.send(packet);
        }

        scheduleFinishEvent(decodedText, serverLevel);
        return true;
    }
}