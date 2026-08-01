package com.linguaperipherals.mod.block.entity;

import com.linguaperipherals.mod.LinguaPeripherals;
import com.linguaperipherals.mod.block.CassetteDriveBlock;
import com.linguaperipherals.mod.config.LinguaPeripheralsConfig;
import com.linguaperipherals.mod.init.ModBlockEntities;
import com.linguaperipherals.mod.inventory.CassetteDriveMenu;
import com.linguaperipherals.mod.item.CassetteTapeItem;
import com.linguaperipherals.mod.network.LinguaPeripheralsNetwork;
import com.linguaperipherals.mod.peripheral.CassetteDrivePeripheral;
import com.linguaperipherals.mod.peripheral.CassetteTapeFileHandle;
import com.linguaperipherals.mod.peripheral.CassetteTapeStorage;
import com.linguaperipherals.mod.peripheral.DfpwmEncoder;
import dan200.computercraft.api.ComputerCraftAPI;
import dan200.computercraft.api.peripheral.IComputerAccess;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraftforge.network.PacketDistributor;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.LevelEvent;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.LevelResource;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

public class CassetteDriveBlockEntity extends BlockEntity implements MenuProvider {
    private static final LevelResource CC_FOLDER = new LevelResource("computercraft");
    private static final byte[] DFPWM_MAGIC = {
        (byte) 0x44, (byte) 0x46, (byte) 0x50, (byte) 0x57, (byte) 0x4D, (byte) 0x0A
    };

    /**
     * Get the byte offset where actual DFPWM audio data starts.
     * If the file has a 6-byte "DFPWM\\n" header, data starts at byte 6.
     * Otherwise, data starts at byte 0 (raw DFPWM, e.g. from ffmpeg).
     */
    public int getDfpwmDataOffset() {
        if (tapeStorage == null) return 0;
        try {
            Path path = tapeStorage.getFilePath();
            byte[] bytes = Files.readAllBytes(path);
            if (bytes.length >= 6 && Arrays.equals(Arrays.copyOf(bytes, 6), DFPWM_MAGIC)) {
                return 6;
            }
        } catch (IOException e) {
            LinguaPeripherals.LOGGER.warn("Failed to read DFPWM header", e);
        }
        return 0;
    }

    public long getDfpwmDataSize() {
        if (tapeStorage == null) return 0;
        long size = tapeStorage.size();
        long offset = getDfpwmDataOffset();
        return Math.max(0, size - offset);
    }

    public enum PlayState { STOPPED, PLAYING, PAUSED }

    // ---- NBT-persisted playback state ----
    private long audioOffset;
    private float audioVolume = 1.0f;

    // ---- Transient playback state ----
    private PlayState playState = PlayState.STOPPED;
    private DfpwmPlaybackController playbackController;
    private long playResumeTime;   // System.nanoTime() when last started/resumed
    private long playFinishTime;   // Estimated System.nanoTime() when client finishes playing
    private long totalSamples;     // Total samples in the file (for duration calculation)
    private boolean pendingPlayStartEvent;
    private boolean pendingPlayEndEvent;

    // ---- Inventory ----
    private final NonNullList<ItemStack> items = NonNullList.withSize(1, ItemStack.EMPTY);
    private final Container inventory = new Container() {
        @Override public int getContainerSize() { return 1; }
        @Override public boolean isEmpty() { return items.get(0).isEmpty(); }
        @Override public ItemStack getItem(int slot) { return items.get(slot); }
        @Override public ItemStack removeItem(int slot, int amount) {
            ItemStack result = ContainerHelper.removeItem(items, slot, amount);
            if (!result.isEmpty()) onItemChanged();
            return result;
        }
        @Override public ItemStack removeItemNoUpdate(int slot) {
            ItemStack result = ContainerHelper.takeItem(items, slot);
            if (!result.isEmpty()) onItemChanged();
            return result;
        }
        @Override public void setItem(int slot, ItemStack stack) {
            items.set(slot, stack);
            onItemChanged();
        }
        @Override public void setChanged() { CassetteDriveBlockEntity.this.setChanged(); }
        @Override public boolean stillValid(Player player) {
            return Container.stillValidBlockEntity(CassetteDriveBlockEntity.this, player);
        }
        @Override public void clearContent() { items.clear(); onItemChanged(); }
    };

    // ---- Peripheral / Handles ----
    private final CassetteDrivePeripheral peripheral = new CassetteDrivePeripheral(this);
    private @Nullable CassetteTapeStorage tapeStorage;
    private final Map<IComputerAccess, CassetteTapeFileHandle> computerHandles = new ConcurrentHashMap<>();
    private final AtomicBoolean ejectQueued = new AtomicBoolean(false);

    public CassetteDriveBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.CASSETTE_DRIVE_BE.get(), pos, state);
    }

    // ==================== Accessors ====================

    public CassetteDrivePeripheral getPeripheral() { return peripheral; }
    public Container getInventory() { return inventory; }
    public ItemStack getStoredItem() { return items.get(0); }
    public @Nullable CassetteTapeStorage getTapeStorage() { return tapeStorage; }

    public PlayState getPlayState() { return playState; }
    public boolean isPlaying() { return playState == PlayState.PLAYING; }
    public boolean isPaused() { return playState == PlayState.PAUSED; }
    public long getAudioOffset() { return audioOffset; }
    public float getAudioVolume() { return audioVolume; }

    // ==================== Handle management ====================

    public void closeHandle(IComputerAccess computer) {
        CassetteTapeFileHandle old = computerHandles.remove(computer);
        if (old != null) { try { old.forceClose(); } catch (IOException ignored) {} }
    }

    public void trackHandle(IComputerAccess computer, CassetteTapeFileHandle handle) {
        computerHandles.put(computer, handle);
    }

    public void closeAllHandles() {
        for (CassetteTapeFileHandle h : computerHandles.values()) {
            try { h.forceClose(); } catch (IOException ignored) {}
        }
        computerHandles.clear();
        if (tapeStorage != null) tapeStorage.forceClose();
    }

    public void onComputerDetach(IComputerAccess computer) { closeHandle(computer); }
    public void requestEject() { ejectQueued.set(true); }

    // ==================== Playback Control ====================

    public double getPlayPosition() {
        if (playState == PlayState.STOPPED)
            return audioOffset / (double) DfpwmEncoder.SAMPLE_RATE;
        long offset = audioOffset;
        if (playState == PlayState.PLAYING && playResumeTime > 0) {
            offset += (System.nanoTime() - playResumeTime) * DfpwmEncoder.SAMPLE_RATE / 1_000_000_000L;
        }
        // Cap at total duration
        if (totalSamples > 0 && offset > totalSamples) offset = totalSamples;
        return offset / (double) DfpwmEncoder.SAMPLE_RATE;
    }

    public double getTapeDuration() {
        if (tapeStorage == null) return 0.0;
        long totalSamples = getDfpwmDataSize() * 8L;
        return totalSamples / (double) DfpwmEncoder.SAMPLE_RATE;
    }

    public void setVolume(float vol) {
        audioVolume = Math.max(0.0f, Math.min(vol, LinguaPeripheralsConfig.MAX_VOLUME.get().floatValue()));
        // If currently playing, push volume update to all clients
        if (playState == PlayState.PLAYING && level instanceof ServerLevel sl) {
            var payload = new LinguaPeripheralsNetwork.CassetteVolumePacket(worldPosition, audioVolume);
            for (var player : sl.getServer().getPlayerList().getPlayers()) {
                LinguaPeripheralsNetwork.CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), payload);
            }
        }
        setChanged();
    }

    public void seekTape(double seconds) {
        if (playbackController != null) {
            // Re-initialize with new offset
            if (tapeStorage != null) {
                try {
                    playbackController.init(tapeStorage.getFilePath(), (long)(seconds * DfpwmEncoder.SAMPLE_RATE));
                } catch (IOException ignored) {}
            }
        }
        audioOffset = (long)(seconds * DfpwmEncoder.SAMPLE_RATE);
        if (playState == PlayState.PLAYING) playResumeTime = System.nanoTime();
        setChanged();
    }

    public boolean startPlayback() {
        if (playState == PlayState.PLAYING) return false;
        if (tapeStorage == null) {
            LinguaPeripherals.LOGGER.warn("startPlayback: tapeStorage is null");
            return false;
        }
        if (!isDfpwmFormat()) {
            LinguaPeripherals.LOGGER.warn("startPlayback: tape file has no DFPWM audio data (empty or missing). Path: {}",
                    tapeStorage.getFilePath());
            return false;
        }

        // Resume from pause: recreate controller and restart streaming
        if (playState == PlayState.PAUSED) {
            try {
                playbackController = new DfpwmPlaybackController();
                playbackController.init(tapeStorage.getFilePath(), audioOffset);
                totalSamples = Math.max(0, tapeStorage.size() - getDfpwmDataOffset()) * 8L;
                playResumeTime = System.nanoTime();
                playState = PlayState.PLAYING;
                if (level != null && !level.isClientSide) {
                    level.updateNeighbourForOutputSignal(worldPosition, getBlockState().getBlock());
                }
                return true;
            } catch (IOException e) {
                LinguaPeripherals.LOGGER.error("Failed to resume playback", e);
                return false;
            }
        }

        // Fresh start or restart
        try {
            playbackController = new DfpwmPlaybackController();
            playbackController.init(tapeStorage.getFilePath(), audioOffset);
            totalSamples = Math.max(0, tapeStorage.size() - getDfpwmDataOffset()) * 8L;
            playResumeTime = System.nanoTime();
            playFinishTime = 0;
            playState = PlayState.PLAYING;
            pendingPlayStartEvent = true;
            // Notify redstone neighbors
            if (level != null && !level.isClientSide) {
                level.updateNeighbourForOutputSignal(worldPosition, getBlockState().getBlock());
            }
            return true;
        } catch (IOException e) {
            LinguaPeripherals.LOGGER.error("Failed to start playback", e);
            return false;
        }
    }

    public void pausePlayback() {
        if (playState != PlayState.PLAYING) return;
        long elapsed = (System.nanoTime() - playResumeTime) * DfpwmEncoder.SAMPLE_RATE / 1_000_000_000L;
        audioOffset += elapsed;
        playState = PlayState.PAUSED;
        playbackController = null;
        if (level instanceof ServerLevel sl) sendStopPayload(sl);
        if (level != null && !level.isClientSide) {
            level.updateNeighbourForOutputSignal(worldPosition, getBlockState().getBlock());
        }
        setChanged();
    }

    public void stopPlayback() {
        stopPlaybackInternal();
        setChanged();
    }

    private void stopPlaybackInternal() {
        playState = PlayState.STOPPED;
        audioOffset = 0;
        playResumeTime = 0;
        playFinishTime = 0;
        playbackController = null;
        if (level instanceof ServerLevel sl) sendStopPayload(sl);
        if (level != null && !level.isClientSide) {
            level.updateNeighbourForOutputSignal(worldPosition, getBlockState().getBlock());
        }
    }

    // ==================== DFPWM format detection ====================

    /**
     * Check if the tape contains playable DFPWM audio data.
     * Accepts both raw DFPWM (e.g. ffmpeg output) and files with a "DFPWM\\n" header.
     */
    public boolean isDfpwmFormat() {
        return getDfpwmDataSize() > 0;
    }

    // ==================== Redstone ====================

    public int getRedstoneSignal() {
        return (playState == PlayState.PLAYING) ? 15 : 0;
    }

    // ==================== Internal ====================

    private void onItemChanged() {
        if (level != null && !level.isClientSide) {
            stopPlaybackInternal();
            closeAllHandles();
            updateBlockState(getStoredItem());
            refreshStorage();
        }
    }

    private void sendStopPayload(ServerLevel sl) {
        var payload = new LinguaPeripheralsNetwork.CassetteAudioStopPacket(worldPosition);
        for (var player : sl.getServer().getPlayerList().getPlayers()) {
            LinguaPeripheralsNetwork.CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), payload);
        }
    }

    public void refreshStorage() {
        ItemStack stack = getStoredItem();
        if (stack.getItem() instanceof CassetteTapeItem && level instanceof ServerLevel sl) {
            int id = CassetteTapeItem.getCassetteID(stack);
            if (id == CassetteTapeItem.NO_ID) {
                try {
                    id = ComputerCraftAPI.createUniqueNumberedSaveDir(sl.getServer(), "cassette_tape");
                    CassetteTapeItem.setCassetteID(stack, id);
                    setChanged();
                } catch (Exception e) {
                    LinguaPeripherals.LOGGER.error("Failed to assign cassette tape ID", e);
                    tapeStorage = null;
                    return;
                }
            }
            try {
                Path ccDir = sl.getServer().getWorldPath(CC_FOLDER);
                Path tapeDir = ccDir.resolve("cassette_tape").resolve(String.valueOf(id));
                long limit = LinguaPeripheralsConfig.CASSETTE_TAPE_SIZE_LIMIT.get();
                tapeStorage = new CassetteTapeStorage(tapeDir.resolve("data.bin"), limit);
            } catch (IOException e) {
                LinguaPeripherals.LOGGER.error("Failed to create tape storage for id {}", id, e);
                tapeStorage = null;
            }
        } else {
            tapeStorage = null;
        }
    }

    private void updateBlockState(ItemStack stack) {
        if (level != null && !level.isClientSide) {
            CassetteDriveBlock.CassetteState state;
            if (stack.isEmpty()) state = CassetteDriveBlock.CassetteState.EMPTY;
            else if (stack.getItem() instanceof CassetteTapeItem) state = CassetteDriveBlock.CassetteState.ACCEPTED;
            else state = CassetteDriveBlock.CassetteState.REJECTED;
            BlockState bs = getBlockState();
            if (bs.getBlock() instanceof CassetteDriveBlock) {
                level.setBlock(worldPosition, bs.setValue(CassetteDriveBlock.CASSETTE_STATE, state), 2);
            }
        }
    }

    private void doEject() {
        if (level == null || level.isClientSide) return;
        ItemStack stack = getStoredItem();
        if (stack.isEmpty()) return;
        stopPlaybackInternal();
        closeAllHandles();
        tapeStorage = null;
        items.set(0, ItemStack.EMPTY);
        updateBlockState(ItemStack.EMPTY);

        Direction facing = getBlockState().getValue(CassetteDriveBlock.FACING);
        double xDir = facing.getStepX();
        double zDir = facing.getStepZ();
        double x = worldPosition.getX() + 0.5 + xDir * 0.7;
        double y = worldPosition.getY() + 0.5;
        double z = worldPosition.getZ() + 0.5 + zDir * 0.7;
        ItemEntity entity = new ItemEntity(level, x, y, z, stack.copy());
        double baseSpeed = level.random.nextDouble() * 0.1 + 0.2;
        double dropSpeed = 0.0172275 * 6;
        entity.setDeltaMovement(
            level.random.triangle(xDir * baseSpeed, dropSpeed),
            level.random.triangle(0, dropSpeed),
            level.random.triangle(zDir * baseSpeed, dropSpeed)
        );
        entity.setDefaultPickUpDelay();
        level.addFreshEntity(entity);
        level.levelEvent(LevelEvent.SOUND_DISPENSER_DISPENSE, worldPosition, 0);
        setChanged();
    }

    // ==================== Tick ====================

    @Override
    public void clearRemoved() {
        super.clearRemoved();
        if (level != null && !level.isClientSide) refreshStorage();
    }

    @Override
    public void setRemoved() {
        super.setRemoved();
        stopPlaybackInternal();
        closeAllHandles();
        tapeStorage = null;
    }

    public void tick() {
        if (level == null || level.isClientSide) return;

        if (ejectQueued.getAndSet(false)) doEject();

        if (playState == PlayState.PLAYING && playbackController != null) {
            tickPlayback();
        }
    }

    private void tickPlayback() {
        ServerLevel sl = (ServerLevel) level;
        long now = System.nanoTime();

        if (pendingPlayStartEvent) {
            pendingPlayStartEvent = false;
            peripheral.queueEvent("tape_play_start");
        }

        // If all data has been sent, wait for the client to finish playing
        if (!playbackController.hasMoreData()) {
            if (playFinishTime == 0) {
                // Just finished sending — record when playback will actually end
                playFinishTime = playResumeTime + totalSamples * 1_000_000_000L / DfpwmEncoder.SAMPLE_RATE;
            }
            if (now >= playFinishTime) {
                // Client has finished playing
                playState = PlayState.STOPPED;
                audioOffset = 0;
                playResumeTime = 0;
                playFinishTime = 0;
                playbackController = null;
                peripheral.queueEvent("tape_play_end");
                level.updateNeighbourForOutputSignal(worldPosition, getBlockState().getBlock());
                setChanged();
            }
            return;
        }

        // Still have data to send: read one chunk per tick and broadcast
        var audio = playbackController.readNextChunk();
        if (audio != null) {
            var payload = new LinguaPeripheralsNetwork.CassetteAudioPacket(worldPosition, audio, audioVolume);
            if (LinguaPeripheralsConfig.CASSETTE_BROADCAST_AUDIO.get()) {
                // Broadcast to all online players regardless of distance
                for (var player : sl.getServer().getPlayerList().getPlayers()) {
                    LinguaPeripheralsNetwork.CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), payload);
                }
            } else {
                // Send only to players within audible range
                double range = Math.max(audioVolume, 1.0f) * 16.0;
                for (var player : sl.getServer().getPlayerList().getPlayers()) {
                    if (player.distanceToSqr(
                            worldPosition.getX() + 0.5, worldPosition.getY() + 0.5,
                            worldPosition.getZ() + 0.5) <= range * range) {
                        LinguaPeripheralsNetwork.CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), payload);
                    }
                }
            }
            setChanged();
        }
    }

    // ==================== NBT ====================

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        ContainerHelper.saveAllItems(tag, items);
        tag.putLong("AudioOffset", audioOffset);
        tag.putFloat("AudioVolume", audioVolume);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        ContainerHelper.loadAllItems(tag, items);
        audioOffset = tag.getLong("AudioOffset");
        audioVolume = tag.getFloat("AudioVolume");
        playState = PlayState.STOPPED;
    }

    // ==================== Menu ====================

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int id, Inventory playerInv, Player player) {
        return new CassetteDriveMenu(id, playerInv, this);
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("block.linguaperipherals.cassette_drive");
    }
}
