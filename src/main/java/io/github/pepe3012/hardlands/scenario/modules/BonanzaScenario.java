package io.github.pepe3012.hardlands.scenario.modules;

import io.github.pepe3012.hardlands.config.option.Option;
import io.github.pepe3012.hardlands.config.option.OptionDataType;
import io.github.pepe3012.hardlands.config.option.OptionValidators;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockDropItemEvent;

public final class BonanzaScenario extends AbstractOreScenario {

    private final Option<Float> dropMultiplier = super.createOption("drop-multiplier", OptionDataType.FLOAT, OptionValidators.Floats.atLeast(1.0F));

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    private void onBlockDropItem(BlockDropItemEvent event) {
        if (!super.isAffectedOre(event.getBlockState().getType())) return;

        float multiplier = this.dropMultiplier.getValue();

        event.getItems().forEach(item -> {
            int amount = Math.round(item.getItemStack().getAmount() * multiplier);
            item.getItemStack().setAmount(amount);
        });
    }
}