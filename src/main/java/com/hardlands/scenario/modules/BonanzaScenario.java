package com.hardlands.scenario.modules;

import com.hardlands.common.option.Option;
import com.hardlands.common.option.OptionValidators;
import com.hardlands.scenario.ScenarioModule;
import com.hardlands.common.util.BlockUtil;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockDropItemEvent;
import org.bukkit.inventory.ItemStack;

public final class BonanzaScenario extends ScenarioModule {

    private final Option<Float> multiplierOption = createOption("multiplier", Float.class, OptionValidators.Floats.atLeast(1.0F));

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    private void onBlockDropItem(BlockDropItemEvent event) {
        if (!BlockUtil.isOre(event.getBlockState().getType())) return;

        float multiplier = multiplierOption.getValue();

        event.getItems().forEach(item -> {
            ItemStack itemStack = item.getItemStack();
            itemStack.setAmount(calculateMultipliedAmount(itemStack.getAmount(), multiplier));
            item.setItemStack(itemStack);
        });
    }

    private static int calculateMultipliedAmount(int amount, float multiplier) {
        return Math.round(amount * multiplier);
    }
}