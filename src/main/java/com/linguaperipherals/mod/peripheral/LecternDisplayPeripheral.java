package com.linguaperipherals.mod.peripheral;

import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.peripheral.IComputerAccess;
import dan200.computercraft.api.peripheral.IPeripheral;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
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
    private static final int MAX_TEXT_LENGTH = 2000;

    private final LecternBlockEntity lectern;
    private final List<IComputerAccess> attachedComputers = new CopyOnWriteArrayList<>();
    private int currentPage = 0;

    public LecternDisplayPeripheral(LecternBlockEntity lectern) {
        this.lectern = lectern;
    }

    @Override
    public String getType() { return "lectern_display"; }

    @Override public void attach(IComputerAccess c) { attachedComputers.add(c); }
    @Override public void detach(IComputerAccess c) { attachedComputers.remove(c); }

    @Override
    public boolean equals(@Nullable IPeripheral other) {
        if (this == other) return true;
        if (!(other instanceof LecternDisplayPeripheral that)) return false;
        return lectern.getBlockPos().equals(that.lectern.getBlockPos());
    }

    @LuaFunction(mainThread = true)
    public final String getItem() {
        ItemStack book = lectern.getBook();
        if (book.isEmpty()) return "minecraft:air";
        return BuiltInRegistries.ITEM.getKey(book.getItem()).toString();
    }

    @LuaFunction(mainThread = true)
    public final int getPages() {
        ItemStack book = lectern.getBook();
        if (book.isEmpty()) return 0;
        WritableBookContent c = book.get(DataComponents.WRITABLE_BOOK_CONTENT);
        return c != null ? c.pages().size() : 0;
    }

    @LuaFunction
    public final int getPage() {
        return currentPage + 1;
    }

    @LuaFunction
    public final void setPage(int page) {
        if (page < 1) page = 1;
        currentPage = page - 1;
    }

    @LuaFunction(mainThread = true)
    public final String readPage(int page) throws LuaException {
        if (page < 1) throw new LuaException("page must be >= 1");
        ItemStack book = lectern.getBook();
        if (book.isEmpty()) return "";
        WritableBookContent c = book.get(DataComponents.WRITABLE_BOOK_CONTENT);
        if (c == null) return "";
        int idx = page - 1;
        if (idx >= c.pages().size()) return "";
        return encodeNonAscii(c.pages().get(idx).get(true));
    }

    @LuaFunction(mainThread = true)
    public final void writePage(int page, String text) throws LuaException {
        if (page < 1) throw new LuaException("page must be >= 1");
        String decoded = validateLength(decodeEscapeSequences(text));
        ItemStack book = lectern.getBook();
        if (book.isEmpty()) return;
        if (!book.is(Items.WRITABLE_BOOK)) return;

        WritableBookContent c = book.get(DataComponents.WRITABLE_BOOK_CONTENT);
        if (c == null) return;
        List<Filterable<String>> pages = new ArrayList<>(c.pages());

        while (pages.size() < page)
            pages.add(Filterable.passThrough(""));

        pages.set(page - 1, Filterable.passThrough(decoded));

        book.set(DataComponents.WRITABLE_BOOK_CONTENT, new WritableBookContent(pages));
        lectern.setBook(book);
    }

    @LuaFunction(mainThread = true)
    public final void clearPage(int page) throws LuaException {
        if (page < 1) throw new LuaException("page must be >= 1");
        ItemStack book = lectern.getBook();
        if (book.isEmpty()) return;
        if (!book.is(Items.WRITABLE_BOOK)) return;

        WritableBookContent c = book.get(DataComponents.WRITABLE_BOOK_CONTENT);
        if (c == null) return;
        List<Filterable<String>> pages = new ArrayList<>(c.pages());

        if (page <= pages.size())
            pages.set(page - 1, Filterable.passThrough(""));

        book.set(DataComponents.WRITABLE_BOOK_CONTENT, new WritableBookContent(pages));
        lectern.setBook(book);
    }

    @LuaFunction(mainThread = true)
    public final void clear() {
        ItemStack book = lectern.getBook();
        if (book.isEmpty()) return;
        if (!book.is(Items.WRITABLE_BOOK)) return;
        List<Filterable<String>> pages = new ArrayList<>();
        pages.add(Filterable.passThrough(""));
        book.set(DataComponents.WRITABLE_BOOK_CONTENT, new WritableBookContent(pages));
        lectern.setBook(book);
        currentPage = 0;
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
