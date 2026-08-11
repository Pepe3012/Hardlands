package org.heather.hardlands.inventory;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.Material;
import org.heather.hardlands.Hardlands;
import org.heather.hardlands.common.item.InventoryItem;
import org.heather.hardlands.inventory.screen.InventoryScreen;
import org.heather.hardlands.inventory.screen.MainInventoryScreen;
import org.heather.hardlands.inventory.screen.ScenariosInventoryScreen;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

@Getter
@RequiredArgsConstructor
public enum InventoryScreenType {

    MAIN("Hardlands", InventoryScreen.GridSize.SIX_ROWS, InventoryScreen.OutlineType.RED, MainInventoryScreen::new, null, null),

    SCENARIOS("Escenarios", InventoryScreen.GridSize.SIX_ROWS, InventoryScreen.OutlineType.PINK, ScenariosInventoryScreen::new, MAIN,
            InventoryItem.placedDisplay(Material.CHERRY_SAPLING, "<pink>Escenarios", "Activa, desactiva y configura los escenarios de la partida.", 3, 1)),

    ;

    private final String name;
    private final InventoryScreen.GridSize size;
    private final InventoryScreen.OutlineType outline;
    private final BiFunction<Hardlands, InventoryScreen.Definition, ? extends InventoryScreen> factory;
    private final @Nullable InventoryScreenType parent;
    private final @Nullable InventoryItem.PlacedInventoryDisplay display;

    public InventoryScreen createScreen(Hardlands plugin) {
        return this.factory.apply(plugin, new InventoryScreen.Definition(this.name, this.size, this.outline));
    }

    public List<InventoryScreenType> getChildren() {
        return Arrays.stream(values()).filter(type -> type.parent == this).toList();
    }
}