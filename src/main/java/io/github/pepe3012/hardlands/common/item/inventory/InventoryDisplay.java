package io.github.pepe3012.hardlands.common.item.inventory;

import io.github.pepe3012.hardlands.common.item.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public record InventoryDisplay(Material material, String description) {

    public ItemStack build(String name) {
        return new ItemBuilder(this.material)
                .name(name)
                .lore("<gray>" + this.description)
                .build();
    }
}