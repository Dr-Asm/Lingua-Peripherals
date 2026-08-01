package com.linguaperipherals.mod.peripheral;

import com.linguaperipherals.mod.util.LinguaUtility;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.peripheral.IComputerAccess;
import dan200.computercraft.api.peripheral.IPeripheral;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.entity.LecternBlockEntity;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class LecternDisplayPeripheral implements IPeripheral {
    private static final int MAX_TEXT_LENGTH = 2000;
    private static final String TAG_PAGES = "pages";

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
        return getPageList(book).size();
    }

    @LuaFunction
    public final int getPage() { return currentPage + 1; }

    @LuaFunction
    public final void setPage(int page) {
        if (page < 1) page = 1;
        currentPage = page - 1;
    }

    @LuaFunction(mainThread = true)
    public final byte[] readPage(int page) throws LuaException {
        if (page < 1) throw new LuaException("page must be >= 1");
        ItemStack book = lectern.getBook();
        if (book.isEmpty()) return new byte[0];
        ListTag pages = getPageList(book);
        int idx = page - 1;
        if (idx >= pages.size()) return new byte[0];
        return LinguaUtility.toLuaBytes(pages.getString(idx));
    }

    @LuaFunction(mainThread = true)
    public final void writePage(int page, String text) throws LuaException {
        if (page < 1) throw new LuaException("page must be >= 1");
        String decoded = validateLength(LinguaUtility.fixLuaString(text));
        ItemStack book = lectern.getBook();
        if (book.isEmpty()) return;
        if (!book.is(Items.WRITABLE_BOOK)) return;
        ListTag pages = getPageList(book);
        while (pages.size() < page) pages.add(StringTag.valueOf(""));
        pages.set(page - 1, StringTag.valueOf(decoded));
        book.getOrCreateTag().put(TAG_PAGES, pages);
        lectern.setBook(book);
    }

    @LuaFunction(mainThread = true)
    public final void clearPage(int page) throws LuaException {
        if (page < 1) throw new LuaException("page must be >= 1");
        ItemStack book = lectern.getBook();
        if (book.isEmpty()) return;
        if (!book.is(Items.WRITABLE_BOOK)) return;
        ListTag pages = getPageList(book);
        if (page <= pages.size()) pages.set(page - 1, StringTag.valueOf(""));
        book.getOrCreateTag().put(TAG_PAGES, pages);
        lectern.setBook(book);
    }

    @LuaFunction(mainThread = true)
    public final void delPage(int page) throws LuaException {
        if (page < 1) throw new LuaException("page must be >= 1");
        ItemStack book = lectern.getBook();
        if (book.isEmpty()) return;
        if (!book.is(Items.WRITABLE_BOOK)) return;
        ListTag pages = getPageList(book);
        int idx = page - 1;
        if (idx >= pages.size()) return;
        pages.remove(idx);
        if (pages.isEmpty()) pages.add(StringTag.valueOf(""));
        book.getOrCreateTag().put(TAG_PAGES, pages);
        lectern.setBook(book);
        if (currentPage >= pages.size()) currentPage = Math.max(0, pages.size() - 1);
    }

    @LuaFunction(mainThread = true)
    public final void clear() {
        ItemStack book = lectern.getBook();
        if (book.isEmpty()) return;
        if (!book.is(Items.WRITABLE_BOOK)) return;
        ListTag pages = new ListTag();
        pages.add(StringTag.valueOf(""));
        book.getOrCreateTag().put(TAG_PAGES, pages);
        lectern.setBook(book);
        currentPage = 0;
    }

    private static ListTag getPageList(ItemStack book) {
        if (book.getTag() == null) return new ListTag();
        ListTag pages = book.getTag().getList(TAG_PAGES, Tag.TAG_STRING);
        return pages != null ? pages : new ListTag();
    }

    private static String validateLength(String text) throws LuaException {
        if (text != null && text.length() > MAX_TEXT_LENGTH)
            throw new LuaException("text too long (max " + MAX_TEXT_LENGTH + " chars)");
        return text == null ? "" : text;
    }
}
