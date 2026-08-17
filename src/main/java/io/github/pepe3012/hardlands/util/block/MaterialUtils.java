package io.github.pepe3012.hardlands.util.block;

import org.bukkit.Material;
import org.bukkit.Tag;

public final class MaterialUtils {

    private MaterialUtils() {}

    public static boolean isOre(Material material) {
        return Tag.COAL_ORES.isTagged(material)
                || Tag.IRON_ORES.isTagged(material)
                || Tag.COPPER_ORES.isTagged(material)
                || Tag.GOLD_ORES.isTagged(material)
                || Tag.DIAMOND_ORES.isTagged(material)
                || Tag.EMERALD_ORES.isTagged(material)
                || Tag.LAPIS_ORES.isTagged(material)
                || Tag.REDSTONE_ORES.isTagged(material);
    }
}