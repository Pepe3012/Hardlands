package com.hardlands.common.menu.screen;

import com.hardlands.common.menu.MenuInventory;
import org.bukkit.Material;

public final class VanillaChangesMenu extends BaseMenu {

    public static final VanillaChangesMenu INSTANCE = new VanillaChangesMenu();

    private VanillaChangesMenu() {
        super("Cambios de Vanilla", Material.BROWN_STAINED_GLASS_PANE, MenuInventory.Size.SIX_ROWS, MainMenu.INSTANCE);
    }
}