package com.linguaperipherals.mod.peripheral;

import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.peripheral.IComputerAccess;
import dan200.computercraft.api.peripheral.IPeripheral;
import net.minecraft.core.component.DataComponents;
import net.minecraft.server.network.Filterable;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.WritableBookContent;
import net.minecraft.world.level.block.entity.LecternBlockEntity;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class LecternDisplayPeripheral implements IPeripheral {
    private final LecternBlockEntity lectern;
    private final List<IComputerAccess> attachedComputers = new CopyOnWriteArrayList<>();
    private int currentPage = 0;
    private int cursorPos = 0;

    public LecternDisplayPeripheral(LecternBlockEntity lectern) { this.lectern = lectern; }

    @Override public String getType() { return "lectern_display"; }
    @Override public void attach(IComputerAccess c) { attachedComputers.add(c); }
    @Override public void detach(IComputerAccess c) { attachedComputers.remove(c); }

    @Override
    public boolean equals(@Nullable IPeripheral other) {
        if (this == other) return true;
        if (!(other instanceof LecternDisplayPeripheral that)) return false;
        return lectern.getBlockPos().equals(that.lectern.getBlockPos());
    }

    @LuaFunction(mainThread = true)
    public final int getPages() {
        ItemStack book = lectern.getBook();
        if (book.isEmpty()) return 0;
        WritableBookContent c = book.get(DataComponents.WRITABLE_BOOK_CONTENT);
        return c != null ? c.pages().size() : 0;
    }

    @LuaFunction
    public final void setPage(int page) throws LuaException {
        if (page < 1) throw new LuaException("page must be >= 1");
        int total = getPages();
        if (total == 0) throw new LuaException("no book or no pages");
        if (page > total) throw new LuaException("page out of range (1.." + total + ")");
        currentPage = page - 1; cursorPos = 0;
    }

    @LuaFunction
    public final int getPage() { return currentPage + 1; }

    @LuaFunction(mainThread = true)
    public final String getPageText() {
        ItemStack book = lectern.getBook();
        if (book.isEmpty()) return "";
        WritableBookContent c = book.get(DataComponents.WRITABLE_BOOK_CONTENT);
        if (c == null || currentPage >= c.pages().size()) return "";
        return c.pages().get(currentPage).get(true);
    }

    @LuaFunction(mainThread = true)
    public final String readPage(int page) throws LuaException {
        int total = getPages();
        if (total == 0) throw new LuaException("no book or no pages");
        if (page < 1 || page > total) throw new LuaException("page out of range (1.." + total + ")");
        WritableBookContent c = lectern.getBook().get(DataComponents.WRITABLE_BOOK_CONTENT);
        return c.pages().get(page - 1).get(true);
    }

    @LuaFunction(mainThread = true)
    public final void write(String text) {
        String decoded = decodeEscapeSequences(text);
        ItemStack book = lectern.getBook();
        if (book.isEmpty() || !book.is(Items.WRITABLE_BOOK)) return;
        WritableBookContent c = book.get(DataComponents.WRITABLE_BOOK_CONTENT);
        if (c == null) return;
        List<Filterable<String>> pages = new ArrayList<>(c.pages());
        while (pages.size() <= currentPage) pages.add(Filterable.passThrough(""));
        String pg = pages.get(currentPage).get(true);
        while (pg.length() < cursorPos) pg += " ";
        String pre = pg.substring(0, Math.min(cursorPos, pg.length()));
        String suf = pg.length() > cursorPos + decoded.length() ? pg.substring(cursorPos + decoded.length()) : "";
        pages.set(currentPage, Filterable.passThrough(pre + decoded + suf));
        book.set(DataComponents.WRITABLE_BOOK_CONTENT, new WritableBookContent(pages));
        lectern.setBook(book);
        cursorPos += decoded.length();
    }

    @LuaFunction(mainThread = true)
    public final void clearPage() {
        ItemStack book = lectern.getBook();
        if (book.isEmpty() || !book.is(Items.WRITABLE_BOOK)) return;
        WritableBookContent c = book.get(DataComponents.WRITABLE_BOOK_CONTENT);
        if (c == null) return;
        List<Filterable<String>> pages = new ArrayList<>(c.pages());
        if (currentPage < pages.size()) pages.set(currentPage, Filterable.passThrough(""));
        book.set(DataComponents.WRITABLE_BOOK_CONTENT, new WritableBookContent(pages));
        lectern.setBook(book);
        cursorPos = 0;
    }

    @LuaFunction(mainThread = true)
    public final void clear() {
        ItemStack book = lectern.getBook();
        if (book.isEmpty() || !book.is(Items.WRITABLE_BOOK)) return;
        List<Filterable<String>> pages = new ArrayList<>();
        pages.add(Filterable.passThrough(""));
        book.set(DataComponents.WRITABLE_BOOK_CONTENT, new WritableBookContent(pages));
        lectern.setBook(book);
        currentPage = 0; cursorPos = 0;
    }

    @LuaFunction
    public final void setCursorPos(int pos) throws LuaException {
        if (pos < 1) throw new LuaException("cursor must be >= 1");
        cursorPos = pos - 1;
    }

    @LuaFunction
    public final int getCursorPos() { return cursorPos + 1; }

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
