package com.hardlands.menu.screen;

import com.hardlands.HardlandsPlugin;
import com.hardlands.item.InventoryItem;
import com.hardlands.item.ItemBuilder;
import com.hardlands.menu.Menu;
import com.hardlands.menu.MenuAction;
import com.hardlands.menu.MenuInventory;
import com.hardlands.scenario.Scenario;
import com.hardlands.scenario.ScenarioManager;
import com.hardlands.scenario.ScenarioType;
import com.hardlands.uhc.PreparationManager;
import com.hardlands.uhc.UHC;
import com.hardlands.util.ChatMessenger;
import lombok.RequiredArgsConstructor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

@RequiredArgsConstructor
public enum HardlandsMenu implements Menu {

    MAIN("Hardlands", null, Material.RED_STAINED_GLASS_PANE, MenuInventory.Size.SIX_ROWS) {
        @Override
        public void build(MenuInventory menu, Player player) {
            menu.item(SCENARIOS_SLOT, InventoryItem.SCENARIOS.getItem(), MenuAction.click("administrar", p -> SCENARIOS.open(p, menu)));
            menu.item(TEAMS_SLOT, InventoryItem.TEAMS.getItem(), MenuAction.click("administrar", p -> TEAMS.open(p, menu)));
            menu.item(WORLD_BORDER_SLOT, InventoryItem.WORLD_BORDER.getItem(), MenuAction.click(CONFIGURE, p -> WORLD_BORDER.open(p, menu)));
            menu.item(SETTINGS_SLOT, InventoryItem.SETTINGS.getItem(), MenuAction.click(CONFIGURE, p -> SETTINGS.open(p, menu)));
            refreshPreparationItem(menu);
        }
    },

    SCENARIOS("Escenarios", MAIN, Material.PINK_STAINED_GLASS_PANE, MenuInventory.Size.SIX_ROWS) {
        @Override
        public void build(MenuInventory menu, Player player) {
            buildScenarioPage(menu, 0);
        }
    },

    TEAMS("Equipos", MAIN, Material.YELLOW_STAINED_GLASS_PANE, MenuInventory.Size.SIX_ROWS),
    WORLD_BORDER("Borde del mundo", MAIN, Material.LIGHT_BLUE_STAINED_GLASS_PANE, MenuInventory.Size.SIX_ROWS),
    SETTINGS("Configuración", MAIN, Material.ORANGE_STAINED_GLASS_PANE, MenuInventory.Size.SIX_ROWS);

    private static final String CONFIGURE = "configurar";
    private static final String RED = "<red>";

    private static final MenuInventory.Size MAIN_SIZE = MenuInventory.Size.SIX_ROWS;

    private static final int SCENARIOS_SLOT = MAIN_SIZE.slot(4, 1);
    private static final int TEAMS_SLOT = MAIN_SIZE.slot(4, 3);
    private static final int WORLD_BORDER_SLOT = MAIN_SIZE.slot(4, 5);
    private static final int SETTINGS_SLOT = MAIN_SIZE.slot(4, 7);

    public static final int PREPARATION_SLOT = MAIN_SIZE.slot(6, 4);

    private static final int SCENARIOS_PER_PAGE = 28;
    private static final int NEXT_SLOT = 53;

    private final String name;
    @Nullable private final HardlandsMenu parent;
    private final Material outline;
    private final MenuInventory.Size size;

    @Override
    public String displayName() {
        return this.name;
    }

    @Override
    public @Nullable Menu parent() {
        return this.parent;
    }

    @Override
    public Material outline() {
        return this.outline;
    }

    @Override
    public MenuInventory.Size size() {
        return this.size;
    }

    public static @Nullable PreparationManager getPreparationManager() {
        UHC uhc = HardlandsPlugin.getInstance().getUhc();
        return uhc == null ? null : uhc.getPreparationManager();
    }

    public static void refreshPreparationItem(MenuInventory menu) {
        PreparationManager preparation = getPreparationManager();
        menu.item(PREPARATION_SLOT, InventoryItem.PREPARATION.getItem(preparation), preparationAction(menu, preparation));
    }

    private static @Nullable MenuAction preparationAction(MenuInventory menu, @Nullable PreparationManager preparation) {
        if (preparation == null) return null;

        return switch (preparation.getState()) {
            case NOT_STARTED -> MenuAction.left("iniciar la preparación", player -> handlePreparationStart(player, menu, preparation));
            case IN_PROGRESS -> MenuAction.right("cancelar la preparación", player -> handlePreparationCancel(player, menu, preparation));
            case COMPLETED -> null;
        };
    }

    private static void handlePreparationStart(Player player, MenuInventory menu, PreparationManager preparation) {
        try {
            preparation.startPreparation();
            ChatMessenger.sendMessage(player, "<green>Se inició la pregeneración del mundo.");
            refreshPreparationItem(menu);
        } catch (IllegalStateException exception) {
            ChatMessenger.sendMessage(player, RED + exception.getMessage());
        }
    }

    private static void handlePreparationCancel(Player player, MenuInventory menu, PreparationManager preparation) {
        try {
            preparation.cancelPreparation();
            ChatMessenger.sendMessage(player, "<yellow>Se canceló la pregeneración del mundo.");
            refreshPreparationItem(menu);
        } catch (IllegalStateException exception) {
            ChatMessenger.sendMessage(player, RED + exception.getMessage());
        }
    }

    private static void buildScenarioPage(MenuInventory menu, int page) {
        ScenarioManager manager = HardlandsPlugin.getInstance().getScenarioManager();
        var types = manager.getRegisteredScenarioTypes();

        int start = page * SCENARIOS_PER_PAGE;
        int end = Math.min(start + SCENARIOS_PER_PAGE, types.size());

        for (int index = start; index < end; index++) {
            ScenarioType type = types.get(index);
            int pageIndex = index - start;
            int slot = MAIN_SIZE.slot(2 + pageIndex / 7, 1 + pageIndex % 7);

            menu.item(slot, createScenarioItem(type, manager), createScenarioAction(menu, type, page, manager));
        }

        if (end < types.size()) menu.item(NEXT_SLOT, InventoryItem.NEXT.getItem(), MenuAction.click("continuar", player -> new ScenarioPageMenu(page + 1).open(player, menu)));
    }

    private static ItemStack createScenarioItem(ScenarioType type, ScenarioManager manager) {
        boolean active = manager.isActive(type);

        return new ItemBuilder(type.getMaterial())
                .name((active ? "<green>" : RED) + type.getDisplayName())
                .lore("<gray>" + type.getDescription(), "", "<gray>Estado: " + (active ? "<green>Activo" : RED + "Inactivo"))
                .glint(active)
                .build();
    }

    private static MenuAction createScenarioAction(MenuInventory menu, ScenarioType type, int page, ScenarioManager manager) {
        boolean active = manager.isActive(type);
        String utility = active ? "desactivar" : "activar";
        Scenario scenario = manager.get(type);

        if (scenario.getContainer().getOptions().isEmpty()) {
            return MenuAction.left(utility, player -> toggleScenario(player, menu, type, page));
        }

        return MenuAction.leftRight(utility, player -> toggleScenario(player, menu, type, page), CONFIGURE, player -> new ScenarioOptionsMenu(type).open(player, menu));
    }

    private static void toggleScenario(Player player, MenuInventory menu, ScenarioType type, int page) {
        if (!canModifyScenarios(player)) return;

        ScenarioManager manager = HardlandsPlugin.getInstance().getScenarioManager();
        boolean enabled = manager.toggle(type);

        ChatMessenger.sendMessage(player, enabled
                ? "<green>Escenario '" + type.getDisplayName() + "' activado."
                : "<yellow>Escenario '" + type.getDisplayName() + "' desactivado.");

        if (page == 0) SCENARIOS.open(player, menu.getPrevious());
        else new ScenarioPageMenu(page).open(player, menu.getPrevious());
    }

    private static boolean canModifyScenarios(Player player) {
        UHC uhc = HardlandsPlugin.getInstance().getUhc();
        if (uhc == null || !uhc.isRunning()) return true;

        ChatMessenger.sendMessage(player, RED + "No puedes modificar escenarios mientras la UHC está en curso.");
        return false;
    }

    private record ScenarioPageMenu(int page) implements Menu {

        @Override
        public String displayName() {
            return "Escenarios";
        }

        @Override
        public Material outline() {
            return Material.PINK_STAINED_GLASS_PANE;
        }

        @Override
        public MenuInventory.Size size() {
            return MAIN_SIZE;
        }

        @Override
        public Menu parent() {
            return MAIN;
        }

        @Override
        public void build(MenuInventory menu, Player player) {
            buildScenarioPage(menu, this.page);
        }
    }
}