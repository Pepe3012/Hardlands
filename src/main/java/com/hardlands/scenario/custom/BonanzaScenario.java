package com.hardlands.scenario.custom;

import com.hardlands.scenario.Scenario;
import com.hardlands.util.BlockUtil;
import org.bukkit.block.BlockState;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockDropItemEvent;
import org.bukkit.inventory.ItemStack;

public final class BonanzaScenario extends Scenario {
    private final Option<Float> multiplier = super.createOption("multiplier", 2.0F);

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    private void onBlockDropItem(BlockDropItemEvent event) {
        BlockState state = event.getBlockState();

        if (!BlockUtil.isOre(state.getType())) return;

        float multiplierValue = Math.max(1.0F, this.multiplier.getValue());

        event.getItems().forEach(item -> {
            ItemStack stack = item.getItemStack();
            stack.setAmount(calculateAmount(stack.getAmount(), multiplierValue));
            item.setItemStack(stack);
        });
    }

    private static int calculateAmount(int amount, float multiplier) {
        return Math.max(1, Math.round(amount * multiplier));
    }
}