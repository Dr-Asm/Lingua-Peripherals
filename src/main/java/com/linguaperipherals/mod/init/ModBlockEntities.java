package com.linguaperipherals.mod.init;

import com.linguaperipherals.mod.LinguaPeripherals;
import com.linguaperipherals.mod.block.entity.NarratorBlockEntity;
import com.linguaperipherals.mod.block.entity.CreativeNarratorBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(net.minecraft.core.registries.Registries.BLOCK_ENTITY_TYPE, LinguaPeripherals.MODID);

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<NarratorBlockEntity>> NARRATOR_BE =
            BLOCK_ENTITIES.register("narrator",
                    () -> BlockEntityType.Builder.of(NarratorBlockEntity::new, ModBlocks.NARRATOR.get()).build(null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<CreativeNarratorBlockEntity>> CREATIVE_NARRATOR_BE =
            BLOCK_ENTITIES.register("creative_narrator",
                    () -> BlockEntityType.Builder.of(CreativeNarratorBlockEntity::new, ModBlocks.CREATIVE_NARRATOR.get()).build(null));
}