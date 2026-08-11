package org.heather.hardlands.scenario.modules;

import org.heather.hardlands.core.option.Option;
import org.heather.hardlands.core.option.OptionDataType;
import org.heather.hardlands.core.option.OptionValidators;
import org.heather.hardlands.scenario.base.OreScenarioModule;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockDropItemEvent;

public final class BonanzaScenario extends OreScenarioModule {

    private final Option<Float> dropMultiplierOption = super.createOption("drop-multiplier", OptionDataType.FLOAT, OptionValidators.Floats.atLeast(1.0F));

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    private void onBlockDropItem(BlockDropItemEvent event) {
        if (!super.isAffectedOre(event.getBlockState().getType())) return;

        float multiplier = this.dropMultiplierOption.getValue();

        event.getItems().forEach(item -> {
            int amount = Math.round(item.getItemStack().getAmount() * multiplier);
            item.getItemStack().setAmount(amount);
        });
    }
}