package com.hardlands.scenario.custom;

import com.hardlands.scenario.Scenario;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockDropItemEvent;
import org.bukkit.event.block.LeavesDecayEvent;
import org.bukkit.inventory.ItemStack;

public class AppleGroveScenario extends Scenario {
    private final Option<Float> appleRate = createOption("apple_rate", 0.1F);
    private final Option<Float> goldenAppleRate = createOption("golden_apple_rate", 0.0F);
    private final Option<Float> enchantedGoldenAppleRate = createOption("enchanted_golden_apple_rate", 0.0F);
    private final Option<Boolean> allTrees = createOption("all_trees", true);

    @EventHandler
    private void onLeavesDecay(LeavesDecayEvent event) {
        if (isValidLeaves(event.getBlock().getType())) {
            tryDropApple(event.getBlock());
        }
    }

    @EventHandler
    private void onBlockDrop(BlockDropItemEvent event) {
        if (isValidLeaves(event.getBlockState().getType())) {
            tryDropApple(event.getBlock());
        }
    }

    private boolean isValidLeaves(Material material) {
        String name = material.name();
        return allTrees.getValue() ? name.contains("_LEAVES") : material == Material.OAK_LEAVES;
    }

    private void tryDropApple(Block block) {
        Material apple = rollApple();
        if (apple != null) {
            block.getWorld().dropItemNaturally(block.getLocation(), new ItemStack(apple));
        }
    }

    private Material rollApple() {
        double roll = Math.random();
        if (roll < enchantedGoldenAppleRate.getValue()) return Material.ENCHANTED_GOLDEN_APPLE;
        if (roll < goldenAppleRate.getValue()) return Material.GOLDEN_APPLE;
        if (roll < appleRate.getValue()) return Material.APPLE;
        return null;
    }
}