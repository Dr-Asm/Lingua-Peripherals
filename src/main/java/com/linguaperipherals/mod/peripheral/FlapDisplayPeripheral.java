package com.linguaperipherals.mod.peripheral;

import com.simibubi.create.content.trains.display.FlapDisplayBlockEntity;
import com.simibubi.create.content.trains.display.FlapDisplayLayout;
import com.simibubi.create.content.trains.display.FlapDisplaySection;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.peripheral.IComputerAccess;
import dan200.computercraft.api.peripheral.IPeripheral;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import com.linguaperipherals.mod.util.LinguaUtility;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;

public class FlapDisplayPeripheral implements IPeripheral {
    private static final int MAX_TEXT_LENGTH = 500;
    private static final Set<String> VALID_COLORS = Set.of(
        "white", "orange", "magenta", "light_blue", "yellow", "lime",
        "pink", "gray", "light_gray", "cyan", "purple", "blue",
        "brown", "green", "red", "black"
    );

    private final FlapDisplayBlockEntity blockEntity;
    private final List<IComputerAccess> attachedComputers = new CopyOnWriteArrayList<>();
    private int cursorX = 0;
    private int cursorY = 0;

    public FlapDisplayPeripheral(FlapDisplayBlockEntity blockEntity) {
        this.blockEntity = blockEntity;
    }

    @Override
    public String getType() { return "flap_display"; }

    @Override public void attach(IComputerAccess computer) { attachedComputers.add(computer); }
    @Override public void detach(IComputerAccess computer) { attachedComputers.remove(computer); }

    @Override
    public boolean equals(@Nullable IPeripheral other) {
        if (this == other) return true;
        if (!(other instanceof FlapDisplayPeripheral that)) return false;
        return blockEntity.getBlockPos().equals(that.blockEntity.getBlockPos());
    }

    // ==================== Info ====================

    @LuaFunction(mainThread = true)
    public final Object[] getSize() {
        FlapDisplayBlockEntity ctrl = getController();
        if (ctrl == null) return new Object[]{0, 0};
        return new Object[]{ctrl.getMaxCharCount(), ctrl.ySize * 2};
    }

    @LuaFunction(mainThread = true)
    public final boolean isRunning() {
        FlapDisplayBlockEntity ctrl = getController();
        return ctrl != null && ctrl.isSpeedRequirementFulfilled();
    }

    // ==================== Read ====================

    @LuaFunction(mainThread = true)
    public final List<byte[]> getText() {
        FlapDisplayBlockEntity ctrl = getController();
        if (ctrl == null) return List.of();
        List<FlapDisplayLayout> lines = ctrl.getLines();
        List<byte[]> result = new ArrayList<>(lines.size());
        for (FlapDisplayLayout layout : lines) {
            StringBuilder sb = new StringBuilder();
            for (FlapDisplaySection sec : layout.getSections()) {
                Component comp = sec.getText();
                if (comp != null) sb.append(comp.getString());
            }
            result.add(LinguaUtility.toLuaBytes(sb.toString()));
        }
        return result;
    }

    @LuaFunction(mainThread = true)
    public final byte[] getLine(int line) throws LuaException {
        FlapDisplayBlockEntity ctrl = getController();
        if (ctrl == null) return new byte[0];
        int index = line - 1;
        List<FlapDisplayLayout> ctrlLines = ctrl.getLines();
        if (index < 0 || index >= ctrlLines.size())
            throw new LuaException("line out of range");
        StringBuilder sb = new StringBuilder();
        for (FlapDisplaySection sec : ctrlLines.get(index).getSections()) {
            Component comp = sec.getText();
            if (comp != null) sb.append(comp.getString());
        }
        return LinguaUtility.toLuaBytes(sb.toString());
    }

    // ==================== Cursor ====================

    @LuaFunction
    public final void setCursorPos(int col, int row) throws LuaException {
        if (col < 1 || row < 1) throw new LuaException("cursor must be >= 1");
        cursorX = col - 1;
        cursorY = row - 1;
    }

    @LuaFunction
    public final Object[] getCursorPos() {
        return new Object[]{cursorX + 1, cursorY + 1};
    }

    // ==================== Write ====================

    @LuaFunction(mainThread = true)
    public final void write(String text) throws LuaException {
        String decoded = validateLength(LinguaUtility.fixLuaString(text));
        FlapDisplayBlockEntity ctrl = getController();
        if (ctrl == null) return;
        List<FlapDisplayLayout> lines = ctrl.getLines();
        if (cursorY >= lines.size()) return;

        StringBuilder current = new StringBuilder();
        for (FlapDisplaySection sec : lines.get(cursorY).getSections()) {
            Component comp = sec.getText();
            if (comp != null) current.append(comp.getString());
        }
        String cur = current.toString();
        while (cur.length() < cursorX) cur += " ";
        int end = cursorX + decoded.length();
        String prefix = cur.substring(0, Math.min(cursorX, cur.length()));
        String suffix = cur.length() > end ? cur.substring(end) : "";
        ctrl.applyTextManually(cursorY, prefix + decoded + suffix);
        ctrl.sendData();
        cursorX += decoded.length();
    }

    @LuaFunction(mainThread = true)
    public final void writeLine(int line, String text) throws LuaException {
        String decoded = validateLength(LinguaUtility.fixLuaString(text));
        FlapDisplayBlockEntity ctrl = getController();
        if (ctrl == null) return;
        int index = line - 1;
        List<FlapDisplayLayout> ctrlLines = ctrl.getLines();
        if (index < 0 || index >= ctrlLines.size())
            throw new LuaException("line out of range");
        ctrl.applyTextManually(index, decoded);
        ctrl.sendData();
    }

    @LuaFunction(mainThread = true)
    public final void setLine(int line, String text) throws LuaException {
        writeLine(line, text);
    }

    @LuaFunction(mainThread = true)
    public final void clearLine(int line) throws LuaException {
        FlapDisplayBlockEntity ctrl = getController();
        if (ctrl == null) return;
        int index = line - 1;
        List<FlapDisplayLayout> ctrlLines = ctrl.getLines();
        if (index < 0 || index >= ctrlLines.size())
            throw new LuaException("line out of range");
        ctrl.applyTextManually(index, null);
        ctrl.sendData();
    }

    @LuaFunction(mainThread = true)
    public final void clear() {
        FlapDisplayBlockEntity ctrl = getController();
        if (ctrl == null) return;
        for (int i = 0; i < ctrl.getLines().size(); i++)
            ctrl.applyTextManually(i, "");
        ctrl.sendData();
    }

    @LuaFunction(mainThread = true)
    public final void setColor(int line, String color) throws LuaException {
        FlapDisplayBlockEntity ctrl = getController();
        if (ctrl == null) return;
        String lower = color != null ? color.toLowerCase() : "white";
        if (!VALID_COLORS.contains(lower))
            throw new LuaException("invalid color: " + color);
        DyeColor dye = DyeColor.byName(lower, DyeColor.WHITE);
        int index = line - 1;
        if (index < 0 || index >= ctrl.colour.length)
            throw new LuaException("line out of range");
        ctrl.setColour(index, dye);
        ctrl.sendData();
    }

    // ==================== Helpers ====================

    private FlapDisplayBlockEntity getController() {
        Level level = blockEntity.getLevel();
        if (level == null || level.isClientSide) return null;
        return blockEntity.getController();
    }

    private String validateLength(String text) throws LuaException {
        if (text != null && text.length() > MAX_TEXT_LENGTH)
            throw new LuaException("text too long (max " + MAX_TEXT_LENGTH + " chars)");
        return text == null ? "" : text;
    }


}
