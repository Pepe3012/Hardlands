package com.hardlands.scenario.custom;

import com.hardlands.scenario.Scenario;
import com.hardlands.util.BlockUtil;
import org.bukkit.Material;
import org.bukkit.block.BlockState;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockDropItemEvent;
import org.bukkit.inventory.ItemStack;

import java.util.EnumSet;
import java.util.Set;

public final class BonanzaScenario extends Scenario {
    private static final Set<Material> ORES = BlockUtil.withDeepslateVariants(EnumSet.of(
            Material.COAL_ORE,
            Material.IRON_ORE,
            Material.COPPER_ORE,
            Material.GOLD_ORE,
            Material.REDSTONE_ORE,
            Material.EMERALD_ORE,
            Material.LAPIS_ORE,
            Material.DIAMOND_ORE,
            Material.NETHER_QUARTZ_ORE,
            Material.NETHER_GOLD_ORE,
            Material.ANCIENT_DEBRIS
    ));

    private final Option<Float> multiplier = super.createOption("multiplier", 2.0F);

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    private void onBlockDropItem(BlockDropItemEvent event) {
        BlockState state = event.getBlockState();

        if (!ORES.contains(state.getType())) return;

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