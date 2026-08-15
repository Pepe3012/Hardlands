package io.github.pepe3012.hardlands.scenario.modules;

import io.github.pepe3012.hardlands.config.option.Option;
import io.github.pepe3012.hardlands.scenario.ScenarioModule;
import org.bukkit.Material;

import java.util.Set;

public abstract class AbstractOreScenario extends ScenarioModule {

    protected final Option<Set<Material>> affectedOresOption = super.createSetOption("affected-ores");

    protected final boolean isAffectedOre(Material material) {
        return this.affectedOresOption.getValue().contains(material);
    }
}