package org.heather.hardlands.module.general;

import java.util.Set;
import org.bukkit.Material;
import org.heather.hardlands.config.ConfigBuilder;
import org.heather.hardlands.config.OptionDef;

@ConfigBuilder(
        identifier = "general",
        options = {
                @OptionDef(type = Set.class, elementType = Material.class, name = "blacklistedMaterials")
        })
public final class GeneralConfiguration extends GeneralConfigurationConfiguration {

    public boolean isBlacklisted(Material material) {
        return super.blacklistedMaterials.getValue().contains(material);
    }
}
