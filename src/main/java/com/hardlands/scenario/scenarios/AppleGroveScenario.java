package com.hardlands.scenario.scenarios;

import com.hardlands.scenario.Scenario;
import com.hardlands.util.option.Option;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockDropItemEvent;
import org.bukkit.event.block.LeavesDecayEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.ThreadLocalRandom;

public final class AppleGroveScenario extends Scenario {

    private final Option<Boolean> allTrees = super.option("all_trees", true);
    private final Option<Float> appleRate = super.option("apple_rate", 0.1F);
    private final Option<Float> goldenAppleRate = super.option("golden_apple_rate", 0.0F);
    private final Option<Float> enchantedGoldenAppleRate = super.option("enchanted_golden_apple_rate", 0.0F);

    @EventHandler
    private void onLeavesDecay(LeavesDecayEvent event) {
        this.tryDropApple(event.getBlock());
    }

    @EventHandler(ignoreCancelled = true)
    private void onBlockDropItem(BlockDropItemEvent event) {
        this.tryDropApple(event.getBlock());
    }

    private void tryDropApple(Block block) {
        if (!this.isEligibleLeaf(block.getType())) return;

        Material apple = this.rollApple();
        if (apple != null) block.getWorld().dropItemNaturally(block.getLocation(), new ItemStack(apple));
    }

    private boolean isEligibleLeaf(Material material) {
        return Tag.LEAVES.isTagged(material) && (Boolean.TRUE.equals(this.allTrees.getValue()) || material == Material.OAK_LEAVES);
    }

    private @Nullable Material rollApple() {
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