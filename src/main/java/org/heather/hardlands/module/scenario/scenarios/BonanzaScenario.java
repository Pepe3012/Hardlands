package org.heather.hardlands.module.scenario.scenarios;

import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockDropItemEvent;
import org.bukkit.inventory.ItemStack;
import org.heather.hardlands.config.ConfigBuilder;
import org.heather.hardlands.config.OptionDef;
import org.heather.hardlands.module.scenario.Scenario;
import org.heather.hardlands.util.BlockUtils;

@ConfigBuilder(
        superclass = Scenario.class,
        options =
                @OptionDef(
                        type = Float.class,
                        validators = "at-least:1.0",
                        name = "dropMultiplier"))
public class BonanzaScenario extends BonanzaScenarioConfiguration {

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    private void onBlockDropItem(BlockDropItemEvent event) {
        if (!BlockUtils.isOre(event.getBlock().getType())) return;

        float multiplier = super.dropMultiplier.getValue();

        for (Item item : event.getItems()) {
            ItemStack itemStack = item.getItemStack();
            int amount = Math.round(itemStack.getAmount() * multiplier);

            itemStack.setAmount(amount);
        }
    }
}
