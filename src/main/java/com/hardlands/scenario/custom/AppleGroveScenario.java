package com.hardlands.scenario.custom;

import com.hardlands.scenario.Scenario;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockDropItemEvent;
import org.bukkit.event.block.LeavesDecayEvent;
import org.bukkit.inventory.ItemStack;

import java.util.concurrent.ThreadLocalRandom;

public final class AppleGroveScenario extends Scenario {
    private final Option<Float> appleRate = super.createOption("apple_rate", 0.1F);
    private final Option<Float> goldenAppleRate = super.createOption("golden_apple_rate", 0.0F);
    private final Option<Float> enchantedGoldenAppleRate = super.createOption("enchanted_golden_apple_rate", 0.0F);
    private final Option<Boolean> allTrees = super.createOption("all_trees", true);

    @EventHandler(ignoreCancelled = true)
    private void onLeavesDecay(LeavesDecayEvent event) {
        this.tryDropApple(event.getBlock());
    }

    @EventHandler(ignoreCancelled = true)
    private void onBlockDropItem(BlockDropItemEvent event) {
        if (this.isValidLeaves(event.getBlockState().getType())) {
            this.tryDropApple(event.getBlock());
        }
    }

    private void tryDropApple(Block block) {
        if (!isValidLeaves(block.getType())) return;
        Material apple = this.rollApple();
        if (apple == null) return;
        block.getWorld().dropItemNaturally(block.getLocation(), new ItemStack(apple));
    }

    private boolean isValidLeaves(Material material) {
        if (!Tag.LEAVES.isTagged(material)) return false;
        return Boolean.TRUE.equals(this.allTrees.getValue()) || material == Material.OAK_LEAVES;
    }

    private Material rollApple() {
        float roll = ThreadLocalRandom.current().nextFloat();

        float enchantedRate = clampRate(this.enchantedGoldenAppleRate.getValue());
        float goldenRate = enchantedRate + clampRate(this.goldenAppleRate.getValue());
        float normalRate = goldenRate + clampRate(this.appleRate.getValue());

        if (roll < enchantedRate) return Material.ENCHANTED_GOLDEN_APPLE;
        if (roll < goldenRate) return Material.GOLDEN_APPLE;
        if (roll < normalRate) return Material.APPLE;

        return null;
    }

    private static float clampRate(float rate) {
        return Math.clamp(rate, 0.0F, 1.0F);
    }
}