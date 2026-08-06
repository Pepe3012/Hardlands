package com.hardlands.inventory;

import com.hardlands.HardlandsPlugin;
import com.hardlands.item.InventoryItem;
import com.hardlands.uhc.PreparationManager;
import com.hardlands.util.ChatMessenger;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.Nullable;

import java.util.function.IntFunction;

public enum HardlandsMenu {

    MAIN("", Style.MAIN, Navigation.MAIN),
    SCENARIOS("Escenarios", Style.SCENARIOS, Navigation.NONE),
    TEAMS("Equipos", Style.TEAMS, Navigation.NONE),
    WORLD_BORDER("Borde del mundo", Style.WORLD_BORDER, Navigation.NONE),
    SETTINGS("Configuración", Style.SETTINGS, Navigation.NONE);

    private static final String TITLE = "Hardlands";
    private static final int PREVIOUS_SLOT = 45;
    private static final int PREPARATION_SLOT = 49;
    private static final int SCENARIOS_SLOT = 28;
    private static final int TEAMS_SLOT = 30;
    private static final int WORLD_BORDER_SLOT = 32;
    private static final int SETTINGS_SLOT = 34;

    private final String name;
    private final Style style;
    private final Navigation navigation;

    HardlandsMenu(String name, Style style, Navigation navigation) {
        this.name = name;
        this.style = style;
        this.navigation = navigation;
    }

    public void open(Player player) {
        this.open(player, null);
    }

    public void handleClick(Player player, MenuInventory<?> inventory, int slot, ClickType click) {
        if (this == MAIN && slot == PREPARATION_SLOT) {
            this.handlePreparationClick(player, inventory, click);
            return;
        }

        if (!click.isLeftClick()) return;

        if (slot == PREVIOUS_SLOT) {
            this.openPrevious(player, inventory);
            return;
        }

        HardlandsMenu target = this.navigation.getTarget(slot);
        if (target != null) target.open(player, inventory);
    }

    private void open(Player player, @Nullable MenuInventory<?> previous) {
        MenuInventory<HardlandsMenu> inventory = this.createInventory(previous);
        inventory.openInventory(player);
        if (this == MAIN) this.startPreparationUpdater(player, inventory);
    }

    private MenuInventory<HardlandsMenu> createInventory(@Nullable MenuInventory<?> previous) {
        MenuInventory<HardlandsMenu> inventory = new MenuInventory<>(this, MenuInventory.Size.SIX_ROWS, this.createTitle(), this.style.getStyle(), previous, 0);

        this.renderContent(inventory);
        this.renderNavigation(inventory);

        return inventory;
    }

    private void renderContent(MenuInventory<HardlandsMenu> inventory) {
        if (this != MAIN) return;

        inventory.setItem(SCENARIOS_SLOT, InventoryItem.SCENARIOS.getItem());
        inventory.setItem(TEAMS_SLOT, InventoryItem.TEAMS.getItem());
        inventory.setItem(WORLD_BORDER_SLOT, InventoryItem.WORLD_BORDER.getItem());
        inventory.setItem(SETTINGS_SLOT, InventoryItem.SETTINGS.getItem());
        inventory.setItem(PREPARATION_SLOT, InventoryItem.PREPARATION.getItem(getPreparationManager()));
    }

    private void renderNavigation(MenuInventory<HardlandsMenu> inventory) {
        if (this != MAIN || inventory.getPrevious() != null) {
            inventory.setItem(PREVIOUS_SLOT, InventoryItem.PREVIOUS.getItem());
        }
    }

    private void openPrevious(Player player, MenuInventory<?> current) {
        MenuInventory<?> previous = current.getPrevious();

        if (previous != null && previous.getMenu() instanceof HardlandsMenu menu) {
            menu.open(player, previous.getPrevious());
        } else if (this != MAIN) {
            MAIN.open(player);
        }
    }

    private void handlePreparationClick(Player player, MenuInventory<?> inventory, ClickType click) {
        PreparationManager preparation = getPreparationManager();

        if (preparation == null) {
            ChatMessenger.sendMessage(player, "<red>No existe una sesión de UHC.");
            return;
        }

        try {
            if (click.isLeftClick()) {
                if (!startPreparation(player, preparation)) return;
            } else if (click.isRightClick()) {
                if (!cancelPreparation(player, preparation)) return;
            } else {
                return;
            }

            inventory.setItem(PREPARATION_SLOT, InventoryItem.PREPARATION.getItem(preparation));
        } catch (IllegalStateException exception) {
            ChatMessenger.sendMessage(player, "<red>" + exception.getMessage());
        }
    }

    private static boolean startPreparation(Player player, PreparationManager preparation) {
        if (preparation.isInProgress()) {
            ChatMessenger.sendMessage(player, "<yellow>La pregeneración ya está en curso.");
            return false;
        }

        if (preparation.isCompleted()) {
            ChatMessenger.sendMessage(player, "<green>El mundo ya está preparado.");
            return false;
        }

        preparation.startPreparation();
        ChatMessenger.sendMessage(player, "<green>Se inició la pregeneración del mundo.");
        return true;
    }

    private static boolean cancelPreparation(Player player, PreparationManager preparation) {
        if (!preparation.isInProgress()) {
            ChatMessenger.sendMessage(player, "<red>No hay una pregeneración en curso.");
            return false;
        }

        preparation.cancelPreparation();
        ChatMessenger.sendMessage(player, "<yellow>Se canceló la pregeneración del mundo.");
        return true;
    }

    private void startPreparationUpdater(Player player, MenuInventory<HardlandsMenu> inventory) {
        new BukkitRunnable() {

            @Override
            public void run() {
                if (player.getOpenInventory().getTopInventory() != inventory.getInventory()) {
                    this.cancel();
                    return;
                }

                inventory.setItem(PREPARATION_SLOT, InventoryItem.PREPARATION.getItem(getPreparationManager()));
            }
        }.runTaskTimer(HardlandsPlugin.getInstance(), 20L, 20L);
    }

    private Component createTitle() {
        return Component.text(this == MAIN ? TITLE : TITLE + " » " + this.name);
    }

    private static @Nullable PreparationManager getPreparationManager() {
        var uhc = HardlandsPlugin.getInstance().getUhc();
        return uhc == null ? null : uhc.getPreparationManager();
    }

    private enum Style {

        MAIN(Material.RED_STAINED_GLASS_PANE, Sound.BLOCK_COPPER_CHEST_OPEN),
        SCENARIOS(Material.PINK_STAINED_GLASS_PANE),
        TEAMS(Material.YELLOW_STAINED_GLASS_PANE),
        WORLD_BORDER(Material.LIGHT_BLUE_STAINED_GLASS_PANE),
        SETTINGS(Material.ORANGE_STAINED_GLASS_PANE);

        private final MenuInventory.Style style;

        Style(Material background) {
            this(background, null);
        }

        Style(Material background, @Nullable Sound openingSound) {
            this.style = new MenuInventory.Style(background, openingSound);
        }

        private MenuInventory.Style getStyle() {
            return this.style;
        }
    }

    private enum Navigation {

        NONE(),
        MAIN(slot -> switch (slot) {
            case SCENARIOS_SLOT -> SCENARIOS;
            case TEAMS_SLOT -> TEAMS;
            case WORLD_BORDER_SLOT -> WORLD_BORDER;
            case SETTINGS_SLOT -> SETTINGS;
            default -> null;
        });

        private final IntFunction<HardlandsMenu> resolver;

        Navigation(IntFunction<HardlandsMenu> resolver) {
            this.resolver = resolver;
        }

        Navigation() {
            this.resolver = _ -> null;
        }

        private @Nullable HardlandsMenu getTarget(int slot) {
            return this.resolver.apply(slot);
        }
    }
}