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
    private final SignBlockEntity sign;
    private final List<IComputerAccess> attachedComputers = new CopyOnWriteArrayList<>();
    private int cursorX = 0;
    private int cursorY = 0;

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
    public final Object[] getSize() { return new Object[]{15, 4}; }

    @LuaFunction(mainThread = true)
    public final List<String> getText() {
        SignText text = sign.getText(true);
        List<String> result = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            Component msg = text.getMessage(i, true);
            result.add(msg != null ? msg.getString() : "");
        }
        return result;
    }

    @LuaFunction(mainThread = true)
    public final String getLine(int line) throws LuaException {
        if (line < 1 || line > 4) throw new LuaException("line out of range (1..4)");
        Component msg = sign.getText(true).getMessage(line - 1, true);
        return msg != null ? msg.getString() : "";
    }

    @LuaFunction
    public final void setCursorPos(int col, int row) throws LuaException {
        if (col < 1 || row < 1) throw new LuaException("cursor must be >= 1");
        cursorX = col - 1; cursorY = row - 1;
    }

    @LuaFunction
    public final Object[] getCursorPos() { return new Object[]{cursorX + 1, cursorY + 1}; }

    @LuaFunction(mainThread = true)
    public final void write(String text) {
        String decoded = decodeEscapeSequences(text);
        if (cursorY < 0 || cursorY >= 4) return;
        SignText cur = sign.getText(true);
        String curStr = cur.getMessage(cursorY, true).getString();
        while (curStr.length() < cursorX) curStr += " ";
        int end = cursorX + decoded.length();
        String pre = curStr.substring(0, Math.min(cursorX, curStr.length()));
        String suf = curStr.length() > end ? curStr.substring(end) : "";
        sign.setText(cur.setMessage(cursorY, Component.literal(pre + decoded + suf)), true);
        sign.setChanged();
        sign.getLevel().sendBlockUpdated(sign.getBlockPos(), sign.getBlockState(), sign.getBlockState(), 3);
        cursorX += decoded.length();
    }

    @LuaFunction(mainThread = true)
    public final void writeLine(int line, String text) throws LuaException {
        String decoded = decodeEscapeSequences(text);
        if (line < 1 || line > 4) throw new LuaException("line out of range (1..4)");
        SignText cur = sign.getText(true);
        sign.setText(cur.setMessage(line - 1, Component.literal(decoded)), true);
        sign.setChanged();
        sign.getLevel().sendBlockUpdated(sign.getBlockPos(), sign.getBlockState(), sign.getBlockState(), 3);
    }

    @LuaFunction(mainThread = true)
    public final void setLine(int line, String text) throws LuaException { writeLine(line, text); }

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

    @LuaFunction(mainThread = true)
    public final void setColor(int line, String color) throws LuaException {
        throw new LuaException("signs do not support color");
    }

    @SuppressWarnings("unused")
    private String decodeEscapeSequences(String text) {
        if (text == null || text.isEmpty()) return text;
        StringBuilder sb = new StringBuilder();
        int i = 0;
        while (i < text.length()) {
            if (text.charAt(i) == '\\' && i + 5 < text.length() && text.charAt(i + 1) == 'u') {
                try { sb.append((char) Integer.parseInt(text.substring(i + 2, i + 6), 16)); i += 6; continue; }
                catch (NumberFormatException ignored) {}
            }
            sb.append(text.charAt(i)); i++;
        }
        return sb.toString();
    }
}
