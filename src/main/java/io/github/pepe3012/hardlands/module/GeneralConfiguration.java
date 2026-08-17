package io.github.pepe3012.hardlands.module;

import io.github.pepe3012.hardlands.core.config.Configuration;
import io.github.pepe3012.hardlands.core.config.Option;
import org.bukkit.Material;

import java.util.Set;

public final class GeneralConfiguration extends Configuration {

    private final Option<Set<Material>> blacklistedMaterials = super.registerSet("blacklisted-materials", Material.class);

    public GeneralConfiguration() {
        super("general");
    }

    public boolean isBlacklisted(Material material) {
        return this.blacklistedMaterials.getValue().contains(material);
    }
}