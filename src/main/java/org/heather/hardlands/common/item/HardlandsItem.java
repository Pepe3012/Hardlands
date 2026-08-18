package org.heather.hardlands.common.item;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.Optional;

public enum HardlandsItem {

    ENDER_BAG(new ItemBuilder(Material.CARROT_ON_A_STICK).name("<white>Ender Bag").lore("<gray>Abre tu <white>cofre de Ender<gray> al hacer <white>clic derecho<gray>.")),
    VOID_BAG(new ItemBuilder(Material.CARROT_ON_A_STICK).name("<white>Void Bag").lore("<gray>Abre el <white>inventario compartido<gray> de tu equipo al hacer <white>clic derecho<gray>.")),
    GOLDEN_HEAD(new ItemBuilder(Material.PLAYER_HEAD).skullOwner("MHF_Apple").name("<white>Golden Head").lore("<gray>Se consume rápidamente y te otorga los efectos de una <white>manzana dorada<gray>, amplificados en <white>un nivel<gray>.")),

    ;

    private final ItemBuilder builder;

    HardlandsItem(ItemBuilder builder) {
        this.builder = builder;
    }

    public static Optional<HardlandsItem> find(String identifier) {
        return Optional.of(valueOf(identifier.toUpperCase()));
    }

    public ItemStack build() {
        return this.builder.build().clone();
    }

    public String getIdentifier() {
        return this.name().toLowerCase();
    }
}