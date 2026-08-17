package org.heather.hardlands.module.scenario.scenarios;

import org.heather.hardlands.core.config.Option;
import org.heather.hardlands.core.config.OptionValidators;
import org.heather.hardlands.module.scenario.Scenario;
import org.heather.hardlands.util.block.MaterialUtils;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockDropItemEvent;

public class BonanzaScenario extends Scenario {

    private final Option<Float> dropMultiplier = super.registerOption("drop-multiplier", Float.class, OptionValidators.Floats.atLeast(1.0F));

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    private void onBlockDropItem(BlockDropItemEvent event) {
        if (!MaterialUtils.isOre(event.getBlock().getType())) return;

        var multiplier = this.dropMultiplier.getValue();

        for (var item : event.getItems()) {
            var itemStack = item.getItemStack();
            var amount = Math.round(itemStack.getAmount() * multiplier);

            itemStack.setAmount(amount);
        }
    }
}