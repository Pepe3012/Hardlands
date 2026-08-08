package com.hardlands.menu.screen;

import com.hardlands.menu.MenuInventory;
import org.bukkit.Material;

public final class SettingsMenu extends BaseMenu {

    public static final SettingsMenu INSTANCE = new SettingsMenu();

    private SettingsMenu() {
        super("Configuración", Material.ORANGE_STAINED_GLASS_PANE, MenuInventory.Size.SIX_ROWS, MainMenu.INSTANCE);
    }
}