package com.linguaperipherals.mod.init;

import com.linguaperipherals.mod.LinguaPeripherals;
import com.linguaperipherals.mod.block.NarratorBlock;
import com.linguaperipherals.mod.block.CreativeNarratorBlock;
import com.linguaperipherals.mod.block.CassetteDriveBlock;
import com.linguaperipherals.mod.item.CassetteTapeItem;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class ModBlocks {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(Registries.BLOCK, LinguaPeripherals.MODID);
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(Registries.ITEM, LinguaPeripherals.MODID);

    public static final RegistryObject<NarratorBlock> NARRATOR = BLOCKS.register("narrator",
            () -> new NarratorBlock(BlockBehaviour.Properties.of()
                    .strength(2.0f, 6.0f)
                    .sound(SoundType.STONE)
                    .requiresCorrectToolForDrops()));

    public static final RegistryObject<CreativeNarratorBlock> CREATIVE_NARRATOR = BLOCKS.register("creative_narrator",
            () -> new CreativeNarratorBlock(BlockBehaviour.Properties.of()
                    .strength(-1.0f, 3600000.0f)
                    .sound(SoundType.STONE)
                    .noLootTable()));

    public static final RegistryObject<CassetteDriveBlock> CASSETTE_DRIVE = BLOCKS.register("cassette_drive",
            () -> new CassetteDriveBlock(BlockBehaviour.Properties.of()
                    .strength(2.0f, 6.0f)
                    .sound(SoundType.STONE)
                    .requiresCorrectToolForDrops()));

    public static final RegistryObject<BlockItem> NARRATOR_ITEM = ITEMS.register("narrator",
            () -> new BlockItem(NARRATOR.get(), new Item.Properties()));

    public static final RegistryObject<BlockItem> CREATIVE_NARRATOR_ITEM = ITEMS.register("creative_narrator",
            () -> new BlockItem(CREATIVE_NARRATOR.get(), new Item.Properties()));

    public static final RegistryObject<BlockItem> CASSETTE_DRIVE_ITEM = ITEMS.register("cassette_drive",
            () -> new BlockItem(CASSETTE_DRIVE.get(), new Item.Properties()));

    public static final RegistryObject<CassetteTapeItem> CASSETTE_TAPE = ITEMS.register("cassette_tape",
            () -> new CassetteTapeItem(new Item.Properties().stacksTo(1)));
}
