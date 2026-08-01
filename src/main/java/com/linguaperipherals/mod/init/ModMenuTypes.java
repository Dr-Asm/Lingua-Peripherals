package com.linguaperipherals.mod.init;

import com.linguaperipherals.mod.LinguaPeripherals;
import com.linguaperipherals.mod.inventory.CassetteDriveMenu;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class ModMenuTypes {
    public static final DeferredRegister<MenuType<?>> MENU_TYPES =
            DeferredRegister.create(Registries.MENU, LinguaPeripherals.MODID);

    public static final RegistryObject<MenuType<CassetteDriveMenu>> CASSETTE_DRIVE =
            MENU_TYPES.register("cassette_drive",
                    () -> new MenuType<>(CassetteDriveMenu::new, FeatureFlags.DEFAULT_FLAGS));
}
