package io.github.pepe3012.hardlands.scenario.modules;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockDropItemEvent;
import io.github.pepe3012.hardlands.config.option.Option;
import io.github.pepe3012.hardlands.config.option.OptionDataType;
import io.github.pepe3012.hardlands.config.option.OptionValidators;
import io.github.pepe3012.hardlands.scenario.base.OreScenarioModule;

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