package com.linguaperipherals.mod.peripheral;

import com.linguaperipherals.mod.LinguaPeripherals;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Files;
import java.nio.file.Path;

public class CassetteTapeStorage {
    private final Path filePath;
    private final long sizeLimit;
    private RandomAccessFile openFile;
    private CassetteTapeFileHandle currentHandle;

    public CassetteTapeStorage(Path filePath, long sizeLimit) throws IOException {
        this.filePath = filePath;
        this.sizeLimit = sizeLimit;
        Files.createDirectories(filePath.getParent());
        if (!Files.exists(filePath)) {
            Files.createFile(filePath);
        }
    }

    public Path getFilePath() { return filePath; }
    public long sizeLimit() { return sizeLimit; }

    public long size() {
        try { return Files.size(filePath); } catch (IOException e) {
            LinguaPeripherals.LOGGER.warn("Failed to read tape file size: {}", filePath, e);
            return 0;
        }
    }

    public synchronized CassetteTapeFileHandle open(String mode) throws IOException {
        if (currentHandle != null && !currentHandle.isClosed()) {
            throw new IOException("Tape is already in use");
        }
        String normalizedMode = normalizeMode(mode);
        openFile = new RandomAccessFile(filePath.toFile(), normalizedMode);
        if (mode.startsWith("w")) {
            openFile.setLength(0);
        }
        if (mode.startsWith("a")) {
            openFile.seek(openFile.length());
        }
        currentHandle = new CassetteTapeFileHandle(this, openFile, mode, normalizedMode);
        return currentHandle;
    }

    synchronized void onHandleClosed() { currentHandle = null; openFile = null; }

    public synchronized void forceClose() {
        if (currentHandle != null) {
            try { currentHandle.forceClose(); } catch (IOException ignored) {}
        }
        currentHandle = null; openFile = null;
    }

    static String normalizeMode(String mode) {
        if (mode == null) return "r";
        return switch (mode) {
            case "r", "rb", "r+", "r+b" -> "r";
            case "w", "wb", "w+", "w+b" -> "rw";
            case "a", "ab", "a+", "a+b" -> "rw";
            default -> "r";
        };
    }
}