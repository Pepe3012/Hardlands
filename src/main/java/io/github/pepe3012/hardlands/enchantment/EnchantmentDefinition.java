package io.github.pepe3012.hardlands.enchantment;

import io.github.pepe3012.hardlands.common.item.ItemBuilder;
import io.github.pepe3012.hardlands.common.util.formatter.RomanNumber;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.inventory.ItemStack;

import java.util.Locale;

public enum EnchantmentDefinition {

    DEAD_EYE(
            "Dead Eye",
            "Aumenta ligeramente el daño de cada golpe consecutivo realizado en combo.",
            3,
            Tag.ITEMS_ENCHANTABLE_SHARP_WEAPON
    ),

    WISDOM(
            "Wisdom",
            "Incrementa en un 25% por nivel la experiencia obtenida al extraer bloques.",
            5,
            Tag.ITEMS_ENCHANTABLE_MINING
    ),

    MOLTEN_TOUCH(
            "Molten Touch",
            "Funde automáticamente cualquier drop que tenga una receta válida de horno.",
            1,
            Tag.ITEMS_ENCHANTABLE_MINING
    ),

    TIMBER(
            "Timber",
            "Al romper un tronco, rompe automáticamente todos los troncos conectados que pertenezcan al mismo árbol.",
            1,
            Tag.ITEMS_ENCHANTABLE_MINING
    ),

    SPELUNKER(
            "Spelunker",
            "Al romper una mena, rompe automáticamente todas las menas conectadas que pertenezcan a la misma veta.",
            1,
            Tag.ITEMS_ENCHANTABLE_MINING
    ),

    ;

    private final String displayName;
    private final String description;
    private final int maxLevel;
    private final Tag<Material> enchantable;

    EnchantmentDefinition(String displayName, String description, int maxLevel, Tag<Material> enchantable) {
        this.displayName = displayName;
        this.description = description;
        this.maxLevel = maxLevel;
        this.enchantable = enchantable;
    }

    public String getIdentifier() {
        return this.name().toLowerCase(Locale.ROOT);
    }

    public boolean canEnchant(Material material) {
        return this.enchantable.isTagged(material);
    }

    public ItemStack createBook(int level) {
        if (level < 1 || level > this.maxLevel) {
            throw new IllegalArgumentException("Invalid level " + level + " for " + this.displayName);
        }

        return new ItemBuilder(Material.ENCHANTED_BOOK).lore(
                "<gray>" + this.displayName + " " + RomanNumber.format(level),
                "<dark_gray>" + this.description
        ).build();
    }
}