package io.github.pepe3012.hardlands.scenario.base;

import org.bukkit.Material;
import io.github.pepe3012.hardlands.config.option.Option;
import io.github.pepe3012.hardlands.scenario.ScenarioModule;

import java.util.Set;

public abstract class OreScenarioModule extends ScenarioModule {

    protected final Option<Set<Material>> affectedOresOption = super.createSetOption("affected-ores");

    protected final boolean isAffectedOre(Material material) {
        return this.affectedOresOption.getValue().contains(material);
    }
}