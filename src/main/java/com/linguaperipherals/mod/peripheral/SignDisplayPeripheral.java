package com.linguaperipherals.mod.peripheral;

import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.peripheral.IComputerAccess;
import dan200.computercraft.api.peripheral.IPeripheral;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.minecraft.world.level.block.entity.SignText;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class SignDisplayPeripheral implements IPeripheral {
    private static final int MAX_TEXT_LENGTH = 100;

    private final SignBlockEntity sign;
    private final List<IComputerAccess> attachedComputers = new CopyOnWriteArrayList<>();

    public SignDisplayPeripheral(SignBlockEntity sign) {
        this.sign = sign;
    }

    @Override
    public String getType() { return "sign_display"; }

    @Override public void attach(IComputerAccess c) { attachedComputers.add(c); }
    @Override public void detach(IComputerAccess c) { attachedComputers.remove(c); }

    @Override
    public boolean equals(@Nullable IPeripheral other) {
        if (this == other) return true;
        if (!(other instanceof SignDisplayPeripheral that)) return false;
        return sign.getBlockPos().equals(that.sign.getBlockPos());
    }

    @LuaFunction(mainThread = true)
    public final Object[] getSize() {
        return new Object[]{15, 4};
    }

    @LuaFunction(mainThread = true)
    public final List<String> readText() {
        SignText text = sign.getText(true);
        List<String> result = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            Component msg = text.getMessage(i, true);
            result.add(msg != null ? encodeNonAscii(msg.getString()) : "");
        }
        return result;
    }

    @LuaFunction(mainThread = true)
    public final String readLine(int line) throws LuaException {
        if (line < 1 || line > 4) throw new LuaException("line out of range (1..4)");
        Component msg = sign.getText(true).getMessage(line - 1, true);
        return msg != null ? encodeNonAscii(msg.getString()) : "";
    }

    @LuaFunction(mainThread = true)
    public final void writeLine(int line, String text) throws LuaException {
        if (line < 1 || line > 4) throw new LuaException("line out of range (1..4)");
        String decoded = validateLength(decodeEscapeSequences(text));
        SignText cur = sign.getText(true);
        sign.setText(cur.setMessage(line - 1, Component.literal(decoded)), true);
        sign.setChanged();
        sign.getLevel().sendBlockUpdated(sign.getBlockPos(), sign.getBlockState(), sign.getBlockState(), 3);
    }

    @LuaFunction(mainThread = true)
    public final void clearLine(int line) throws LuaException {
        if (line < 1 || line > 4) throw new LuaException("line out of range (1..4)");
        SignText cur = sign.getText(true);
        sign.setText(cur.setMessage(line - 1, Component.empty()), true);
        sign.setChanged();
        sign.getLevel().sendBlockUpdated(sign.getBlockPos(), sign.getBlockState(), sign.getBlockState(), 3);
    }

    @LuaFunction(mainThread = true)
    public final void clear() {
        SignText cur = sign.getText(true);
        for (int i = 0; i < 4; i++) cur = cur.setMessage(i, Component.empty());
        sign.setText(cur, true);
        sign.setChanged();
        sign.getLevel().sendBlockUpdated(sign.getBlockPos(), sign.getBlockState(), sign.getBlockState(), 3);
    }

    private String validateLength(String text) throws LuaException {
        if (text != null && text.length() > MAX_TEXT_LENGTH)
            throw new LuaException("text too long (max " + MAX_TEXT_LENGTH + " chars)");
        return text == null ? "" : text;
    }

    private String decodeEscapeSequences(String text) {
        if (text == null || text.isEmpty()) return text;
        StringBuilder result = new StringBuilder();
        int i = 0;
        while (i < text.length()) {
            if (text.charAt(i) == '\\' && i + 5 < text.length() && text.charAt(i + 1) == 'u') {
                String hex = text.substring(i + 2, i + 6);
                try { result.append((char) Integer.parseInt(hex, 16)); i += 6; continue; }
                catch (NumberFormatException ignored) {}
            }
            result.append(text.charAt(i));
            i++;
        }
        return result.toString();
    }

    private static String encodeNonAscii(String text) {
        if (text == null || text.isEmpty()) return text;
        StringBuilder sb = new StringBuilder();
        for (char c : text.toCharArray()) {
            if (c > 127) sb.append(String.format("\\u%04x", (int) c));
            else sb.append(c);
        }
        return sb.toString();
    }
}
