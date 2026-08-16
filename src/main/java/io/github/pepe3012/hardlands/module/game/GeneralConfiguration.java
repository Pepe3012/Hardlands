package io.github.pepe3012.hardlands.module.game;

import io.github.pepe3012.hardlands.core.config.Configuration;
import io.github.pepe3012.hardlands.core.config.Option;
import org.bukkit.Material;

import java.util.Set;

public final class GeneralConfiguration extends Configuration {

    private final Option<Set<Material>> blacklistedItems = super.registerSet("blacklisted-items", Material.class);
    private final Option<Set<Material>> ores = super.registerSet("ores", Material.class);

    public GeneralConfiguration() {
        super("general");
    }

    public boolean isOre(Material material) {
        return this.ores.getValue().contains(material);
    }

    public boolean isBlacklisted(Material material) {
        return this.blacklistedItems.getValue().contains(material);
    }
}