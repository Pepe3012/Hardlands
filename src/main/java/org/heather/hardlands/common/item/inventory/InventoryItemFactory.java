package org.heather.hardlands.common.item.inventory;

import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.heather.hardlands.common.item.ItemBuilder;
import org.heather.hardlands.config.inventory.InventoryDefinition;

final class InventoryItemFactory {

    private static final MiniMessage MINI_MESSAGE = MiniMessage.miniMessage();
    private static final String GRAY = "<gray>";

    private InventoryItemFactory() {}

    static ItemStack menu(
            InventoryDefinition definition,
            ItemBuilder builder,
            String description
    ) {
        return builder
                .name(MINI_MESSAGE.deserialize(definition.getTitle()))
                .lore(GRAY + description)
                .build();
    }

    static ItemStack head(String owner, String name, String description) {
        return new ItemBuilder(Material.PLAYER_HEAD)
                .skullOwner(owner)
                .name(name)
                .lore(GRAY + description)
                .build();
    }
}