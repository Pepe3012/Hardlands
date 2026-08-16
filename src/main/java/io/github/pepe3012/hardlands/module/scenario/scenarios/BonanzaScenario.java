package io.github.pepe3012.hardlands.module.scenario.scenarios;

import io.github.pepe3012.hardlands.Hardlands;
import io.github.pepe3012.hardlands.core.config.Option;
import io.github.pepe3012.hardlands.core.config.OptionValidators;
import io.github.pepe3012.hardlands.module.scenario.Scenario;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockDropItemEvent;

public final class BonanzaScenario extends Scenario {

    private final Option<Float> dropMultiplier = super.registerOption("drop-multiplier", Float.class, OptionValidators.Floats.atLeast(1.0F));

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    private void onBlockDropItem(BlockDropItemEvent event) {
        if (!super.getPlugin().getGeneralConfiguration().isOre(event.getBlockState().getType())) return;

        var multiplier = this.dropMultiplier.getValue();

        for (var item : event.getItems()) {
            var itemStack = item.getItemStack();
            var amount = Math.round(itemStack.getAmount() * multiplier);

            itemStack.setAmount(amount);
        }
    }
}