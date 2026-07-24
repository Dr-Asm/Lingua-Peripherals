package com.linguaperipherals.mod.peripheral;

import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.LuaFunction;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

@SuppressWarnings("unused")
public class CassetteTapeFileHandle {
    private final CassetteTapeStorage storage;
    private final RandomAccessFile raf;
    private final String mode;
    private final boolean readable;
    private final boolean writable;
    private volatile boolean closed;

    CassetteTapeFileHandle(CassetteTapeStorage storage, RandomAccessFile raf, String mode, String normalizedMode) {
        this.storage = storage;
        this.raf = raf;
        this.mode = mode;
        this.readable = true;
        this.writable = normalizedMode.equals("rw");
    }

    @LuaFunction
    public final void close() throws LuaException {
        if (closed) return;
        try { raf.close(); closed = true; storage.onHandleClosed(); }
        catch (IOException e) { throw new LuaException("Failed to close file: " + e.getMessage()); }
    }

    @LuaFunction
    public final Object read(Optional<String> format) throws LuaException {
        checkClosed();
        if (!readable) throw new LuaException("File not open for reading");
        try {
            String fmt = format.orElse("*l");
            return switch (fmt) {
                case "*l" -> readLineInternal(false);
                case "*L" -> readLineInternal(true);
                case "*a", "*A" -> readAll();
                case "*n", "*N" -> readNumber();
                default -> {
                    try { int n = Integer.parseInt(fmt); yield readChars(n); }
                    catch (NumberFormatException e) { throw new LuaException("bad argument to 'read' (invalid format)"); }
                }
            };
        } catch (IOException e) { throw new LuaException("Read error: " + e.getMessage()); }
    }

    @LuaFunction
    public final void write(String data) throws LuaException {
        checkClosed();
        if (!writable) throw new LuaException("File not open for writing");
        try {
            byte[] bytes = data != null ? data.getBytes(StandardCharsets.ISO_8859_1) : new byte[0];
            checkSizeLimit(bytes.length);
            raf.write(bytes);
        } catch (IOException e) { throw new LuaException("Write error: " + e.getMessage()); }
    }

    @LuaFunction
    public final void writeLine(String line) throws LuaException {
        write(line + "\n");
    }

    @LuaFunction
    public final long seek(String whence, long offset) throws LuaException {
        checkClosed();
        try {
            long newPos = switch (whence) {
                case "set" -> offset;
                case "cur" -> raf.getFilePointer() + offset;
                case "end" -> raf.length() + offset;
                default -> throw new LuaException("bad argument #1 to 'seek' (invalid option)");
            };
            if (newPos < 0) newPos = 0;
            raf.seek(newPos);
            return newPos;
        } catch (IOException e) { throw new LuaException("Seek error: " + e.getMessage()); }
    }

    public boolean isClosed() { return closed; }
    public void forceClose() throws IOException { closed = true; raf.close(); storage.onHandleClosed(); }

    private void checkClosed() throws LuaException { if (closed) throw new LuaException("File handle is closed"); }

    private void checkSizeLimit(int bytesToWrite) throws LuaException {
        try {
            long currentSize = raf.length();
            long newSize;
            if (mode.startsWith("a")) newSize = currentSize + bytesToWrite;
            else { long pos = raf.getFilePointer(); newSize = Math.max(currentSize, pos + bytesToWrite); }
            if (newSize > storage.sizeLimit())
                throw new LuaException("Cassette tape is full (limit: " + storage.sizeLimit() + " bytes)");
        } catch (IOException e) { throw new LuaException("Failed to check file size: " + e.getMessage()); }
    }

    @Nullable
    private String readLineInternal(boolean keepNewline) throws IOException {
        StringBuilder sb = new StringBuilder();
        while (true) {
            int ch = raf.read();
            if (ch == -1) return sb.isEmpty() ? null : sb.toString();
            if (ch == '\n') { if (keepNewline) sb.append('\n'); break; }
            if (ch != '\r') sb.append((char) ch);
        }
        return sb.toString();
    }

    @Nullable
    private String readAll() throws IOException {
        long pos = raf.getFilePointer();
        long remaining = raf.length() - pos;
        if (remaining <= 0) return "";
        if (remaining > Integer.MAX_VALUE) remaining = Integer.MAX_VALUE;
        byte[] buf = new byte[(int) remaining];
        int read = raf.read(buf);
        return read > 0 ? new String(buf, 0, read, StandardCharsets.ISO_8859_1) : "";
    }

    private String readChars(int n) throws IOException {
        n = Math.min(n, (int) storage.sizeLimit());
        byte[] buf = new byte[n];
        int total = 0;
        while (total < n) { int r = raf.read(buf, total, n - total); if (r == -1) break; total += r; }
        return total > 0 ? new String(buf, 0, total, StandardCharsets.ISO_8859_1) : null;
    }

    @Nullable
    private Double readNumber() throws IOException {
        StringBuilder sb = new StringBuilder();
        while (true) {
            int ch = raf.read();
            if (ch == -1) break;
            char c = (char) ch;
            if (Character.isWhitespace(c)) { if (!sb.isEmpty()) break; continue; }
            if (c == '-' && sb.isEmpty()) { sb.append(c); continue; }
            if (c == '.' && sb.indexOf(".") == -1) { sb.append(c); continue; }
            if (Character.isDigit(c)) { sb.append(c); continue; }
            raf.seek(raf.getFilePointer() - 1); break;
        }
        if (sb.isEmpty()) return null;
        try { return Double.parseDouble(sb.toString()); } catch (NumberFormatException e) { return null; }
    }
}