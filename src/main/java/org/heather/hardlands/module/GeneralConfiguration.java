package org.heather.hardlands.module;

import org.heather.hardlands.core.config.Configuration;
import org.heather.hardlands.core.config.Option;
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