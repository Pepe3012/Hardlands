package io.github.pepe3012.hardlands.common.item;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

public enum HardlandsItems {

    ENDER_BAG(new ItemBuilder(Material.CARROT_ON_A_STICK).name("<white>Ender Bag").lore("<gray>Abre tu <white>cofre de Ender<gray> al hacer <white>clic derecho<gray>.")),
    VOID_BAG(new ItemBuilder(Material.CARROT_ON_A_STICK).name("<white>Void Bag").lore("<gray>Abre el <white>inventario compartido<gray> de tu equipo al hacer <white>clic derecho<gray>.")),
    PLAYER_RADAR(new ItemBuilder(Material.COMPASS).name("<white>Player Radar").lore("<gray>Detecta a los <white>jugadores cercanos<gray> y te indica su presencia y distancia.")),
    RADIANT_APPLE(new ItemBuilder(Material.GOLDEN_APPLE).name("<white>Radiant Apple").lore("<gray>Se consume rápidamente y te otorga los efectos de una <white>manzana dorada<gray>, amplificados en <white>un nivel<gray>.")),
    ENCHANTED_RADIANT_APPLE(new ItemBuilder(Material.ENCHANTED_GOLDEN_APPLE).name("<white>Enchanted Radiant Apple").lore("<gray>Se consume rápidamente y te otorga los efectos de una <white>manzana dorada encantada<gray>, amplificados en <white>un nivel<gray>.")),

    DEAD_CROWN(new ItemBuilder(Material.DIAMOND_HELMET)
            .name("<white>Dead Crown")
            .lore("<gray>Una corona creada para quien domina la muerte. Mientras la lleves puesta, <white>brillarás<gray> y recibirás <white>Fuerza I<gray> y <white>Velocidad I<gray> de forma permanente.")
            .enchant(Enchantment.PROTECTION, 3)
            .unbreakable()),

    ;

    private final ItemBuilder builder;

    HardlandsItems(ItemBuilder builder) {
        this.builder = builder;
    }

    public static final List<String> IDENTIFIERS = Arrays.stream(values()).map(HardlandsItems::getIdentifier).toList();

    public ItemStack build() {
        return this.builder.build().clone();
    }

    public String getIdentifier() {
        return this.name().toLowerCase();
    }

    public static Optional<HardlandsItems> find(String identifier) {
        return Optional.of(valueOf(identifier.toUpperCase()));
    }
}