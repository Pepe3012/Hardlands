package com.hardlands.common.menu.screen;

import com.hardlands.common.menu.MenuInventory;
import org.bukkit.Material;

public final class WorldBorderMenu extends BaseMenu {

    public static final WorldBorderMenu INSTANCE = new WorldBorderMenu();

    private WorldBorderMenu() {
        super("Borde del mundo", Material.LIGHT_BLUE_STAINED_GLASS_PANE, MenuInventory.Size.SIX_ROWS, MainMenu.INSTANCE);
    }
}