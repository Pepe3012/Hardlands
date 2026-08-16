package io.github.pepe3012.hardlands.module.scenario.scenarios;

import io.github.pepe3012.hardlands.core.config.Option;
import io.github.pepe3012.hardlands.core.config.OptionValidators;
import io.github.pepe3012.hardlands.module.scenario.Scenario;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockDropItemEvent;
import org.bukkit.event.block.BlockEvent;
import org.bukkit.event.block.LeavesDecayEvent;
import org.bukkit.inventory.ItemStack;

import java.util.concurrent.ThreadLocalRandom;

public final class AppleGroveScenario extends Scenario {

    private final Option<Boolean> allTreeTypes = super.registerOption("all-tree-types", Boolean.class);
    private final Option<Float> appleDropRate = super.registerOption("apple-drop-rate", Float.class, OptionValidators.Floats.UNIT_INTERVAL);
    private final Option<Float> goldenAppleDropRate = super.registerOption("golden-apple-drop-rate", Float.class, OptionValidators.Floats.UNIT_INTERVAL);
    private final Option<Float> enchantedGoldenAppleDropRate = super.registerOption("enchanted-golden-apple-drop-rate", Float.class, OptionValidators.Floats.UNIT_INTERVAL);

    @EventHandler(ignoreCancelled = true)
    private void onLeavesDecay(LeavesDecayEvent event) {
        this.tryDropApple(event);
    }

    @EventHandler(ignoreCancelled = true)
    private void onBlockDropItem(BlockDropItemEvent event) {
        this.tryDropApple(event);
    }

    private void tryDropApple(BlockEvent event) {
        var block = event.getBlock();

        if (!this.isEligibleLeaf(block.getType())) return;

        var drop = this.rollAppleDrop();
        if (drop == null) return;

        block.getWorld().dropItemNaturally(block.getLocation(), new ItemStack(drop));
    }

    private Material rollAppleDrop() {
        var enchantedRate = this.enchantedGoldenAppleDropRate.getValue();
        var goldenRate = enchantedRate + this.goldenAppleDropRate.getValue();
        var appleRate = goldenRate + this.appleDropRate.getValue();

        var roll = ThreadLocalRandom.current().nextFloat();

        if (roll < enchantedRate) return Material.ENCHANTED_GOLDEN_APPLE;
        if (roll < goldenRate) return Material.GOLDEN_APPLE;
        if (roll < appleRate) return Material.APPLE;

        return null;
    }

    private boolean isEligibleLeaf(Material material) {
        return Tag.LEAVES.isTagged(material)
                && (this.allTreeTypes.getValue() || material == Material.OAK_LEAVES);
    }
}