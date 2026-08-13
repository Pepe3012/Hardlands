package io.github.pepe3012.hardlands.common.item.inventory;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import io.github.pepe3012.hardlands.common.item.ItemBuilder;

public record InventoryDisplay(Material material, String description) {

    private static final String GRAY = "<gray>";

    public ItemStack build(String name) {
        return new ItemBuilder(this.material)
                .name(name)
                .lore(GRAY + this.description)
                .build();
    }
}