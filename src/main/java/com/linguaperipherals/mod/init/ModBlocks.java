package com.linguaperipherals.mod.init;

import com.linguaperipherals.mod.LinguaPeripherals;
import com.linguaperipherals.mod.block.NarratorBlock;
import com.linguaperipherals.mod.block.CreativeNarratorBlock;
import com.linguaperipherals.mod.block.CassetteDriveBlock;
import com.linguaperipherals.mod.item.CassetteTapeItem;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModBlocks {
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(LinguaPeripherals.MODID);
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(LinguaPeripherals.MODID);

    public static final DeferredBlock<NarratorBlock> NARRATOR = BLOCKS.register("narrator",
            () -> new NarratorBlock(BlockBehaviour.Properties.of()
                    .strength(2.0f, 6.0f)
                    .sound(SoundType.STONE)
                    .requiresCorrectToolForDrops()));

    public static final DeferredBlock<CreativeNarratorBlock> CREATIVE_NARRATOR = BLOCKS.register("creative_narrator",
            () -> new CreativeNarratorBlock(BlockBehaviour.Properties.of()
                    .strength(-1.0f, 3600000.0f)
                    .sound(SoundType.STONE)
                    .noLootTable()));

    public static final DeferredBlock<CassetteDriveBlock> CASSETTE_DRIVE = BLOCKS.register("cassette_drive",
            () -> new CassetteDriveBlock(BlockBehaviour.Properties.of()
                    .strength(2.0f, 6.0f)
                    .sound(SoundType.STONE)
                    .requiresCorrectToolForDrops()));

    public static final DeferredItem<BlockItem> NARRATOR_ITEM = ITEMS.register("narrator",
            () -> new BlockItem(NARRATOR.get(), new Item.Properties()));

    public static final DeferredItem<BlockItem> CREATIVE_NARRATOR_ITEM = ITEMS.register("creative_narrator",
            () -> new BlockItem(CREATIVE_NARRATOR.get(), new Item.Properties()));

    public static final DeferredItem<BlockItem> CASSETTE_DRIVE_ITEM = ITEMS.register("cassette_drive",
            () -> new BlockItem(CASSETTE_DRIVE.get(), new Item.Properties()));

    public static final DeferredItem<CassetteTapeItem> CASSETTE_TAPE = ITEMS.register("cassette_tape",
            () -> new CassetteTapeItem(new Item.Properties().stacksTo(1)));
}