package com.hardlands.menu.screen.scenario;

import com.hardlands.menu.MenuInventory;
import com.hardlands.menu.screen.BaseMenu;
import com.hardlands.menu.screen.MainMenu;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public final class ScenarioPageMenu extends BaseMenu {

    private final int page;

    public ScenarioPageMenu(int page) {
        super("Escenarios", Material.PINK_STAINED_GLASS_PANE, MenuInventory.Size.SIX_ROWS, MainMenu.INSTANCE);
        this.page = page;
    }

    @Override
    public void build(MenuInventory menu, Player player) {
        ScenarioMenuContent.build(menu, this.page);
    }
}