package org.heather.hardlands.scenario.base;

import org.bukkit.Material;
import org.heather.hardlands.util.option.Option;
import org.heather.hardlands.scenario.ScenarioModule;

import java.util.Set;

public abstract class OreScenarioModule extends ScenarioModule {

    protected final Option<Set<Material>> affectedOresOption = super.createSetOption("affected-ores");

    protected final boolean isAffectedOre(Material material) {
        return this.affectedOresOption.getValue().contains(material);
    }
}