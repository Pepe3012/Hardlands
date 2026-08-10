package com.hardlands.common.menu.screen;

import com.hardlands.common.menu.MenuInventory;
import org.bukkit.Material;

public final class PlayersMenu extends BaseMenu {

    public static final PlayersMenu INSTANCE = new PlayersMenu();

    private PlayersMenu() {
        super("Jugadores", Material.YELLOW_STAINED_GLASS_PANE, MenuInventory.Size.SIX_ROWS, MainMenu.INSTANCE);
    }
}