package com.hardlands.common.menu.screen;

import com.hardlands.common.menu.MenuInventory;
import org.bukkit.Material;

public final class TemplatesMenu extends BaseMenu {

    public static final TemplatesMenu INSTANCE = new TemplatesMenu();

    private TemplatesMenu() {
        super("Plantillas", Material.PURPLE_STAINED_GLASS_PANE, MenuInventory.Size.SIX_ROWS, MainMenu.INSTANCE);
    }
}