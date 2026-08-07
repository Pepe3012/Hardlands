package com.hardlands.scenario.scenarios;

import com.hardlands.scenario.Scenario;
import com.hardlands.util.BlockUtil;
import com.hardlands.util.option.Option;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockDropItemEvent;
import org.bukkit.inventory.ItemStack;

public final class BonanzaScenario extends Scenario {

    private final Option<Float> multiplier = super.option("multiplier", 2.0F);

    @EventHandler(priority = EventPriority.LOWEST)
    private void onBlockDropItem(BlockDropItemEvent event) {
        if (!BlockUtil.isOre(event.getBlockState().getType())) return;

        float value = Math.max(1.0F, this.multiplier.getValue());

        event.getItems().forEach(item -> {
            ItemStack stack = item.getItemStack();
            stack.setAmount(calculateAmount(stack.getAmount(), value));
            item.setItemStack(stack);
        });
    }

    private static int calculateAmount(int amount, float multiplier) {
        return Math.max(1, Math.round(amount * multiplier));
    }
}