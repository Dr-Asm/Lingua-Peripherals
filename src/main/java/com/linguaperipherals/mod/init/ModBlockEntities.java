package com.linguaperipherals.mod.init;

import com.linguaperipherals.mod.LinguaPeripherals;
import com.linguaperipherals.mod.block.entity.NarratorBlockEntity;
import com.linguaperipherals.mod.block.entity.CreativeNarratorBlockEntity;
import com.linguaperipherals.mod.block.entity.CassetteDriveBlockEntity;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class ModBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, LinguaPeripherals.MODID);

    public static final RegistryObject<BlockEntityType<NarratorBlockEntity>> NARRATOR_BE =
            BLOCK_ENTITIES.register("narrator",
                    () -> BlockEntityType.Builder.of(NarratorBlockEntity::new, ModBlocks.NARRATOR.get()).build(null));

    public static final RegistryObject<BlockEntityType<CreativeNarratorBlockEntity>> CREATIVE_NARRATOR_BE =
            BLOCK_ENTITIES.register("creative_narrator",
                    () -> BlockEntityType.Builder.of(CreativeNarratorBlockEntity::new, ModBlocks.CREATIVE_NARRATOR.get()).build(null));

    public static final RegistryObject<BlockEntityType<CassetteDriveBlockEntity>> CASSETTE_DRIVE_BE =
            BLOCK_ENTITIES.register("cassette_drive",
                    () -> BlockEntityType.Builder.of(CassetteDriveBlockEntity::new, ModBlocks.CASSETTE_DRIVE.get()).build(null));
}
