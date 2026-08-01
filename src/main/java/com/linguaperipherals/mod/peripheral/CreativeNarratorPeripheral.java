package com.linguaperipherals.mod.peripheral;

import com.linguaperipherals.mod.block.entity.CreativeNarratorBlockEntity;
import com.linguaperipherals.mod.network.LinguaPeripheralsNetwork;
import com.linguaperipherals.mod.util.LinguaUtility;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.LuaFunction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.PacketDistributor;

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
        String decodedText = LinguaUtility.fixLuaString(text);

        ServerLevel serverLevel = getLevel();
        if (serverLevel == null) return false;

        var payload = new LinguaPeripheralsNetwork.SpeakTextPacket(decodedText);

        for (ServerPlayer player : serverLevel.getServer().getPlayerList().getPlayers()) {
            LinguaPeripheralsNetwork.CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), payload);
        }

        scheduleFinishEvent(decodedText, serverLevel);
        return true;
    }
}
