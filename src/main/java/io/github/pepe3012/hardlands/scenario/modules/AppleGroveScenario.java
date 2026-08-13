package io.github.pepe3012.hardlands.scenario.modules;

import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockDropItemEvent;
import org.bukkit.event.block.BlockEvent;
import org.bukkit.event.block.LeavesDecayEvent;
import org.bukkit.inventory.ItemStack;
import io.github.pepe3012.hardlands.config.option.Option;
import io.github.pepe3012.hardlands.config.option.OptionDataType;
import io.github.pepe3012.hardlands.config.option.OptionValidators;
import io.github.pepe3012.hardlands.scenario.ScenarioModule;

import java.util.concurrent.ThreadLocalRandom;

public final class AppleGroveScenario extends ScenarioModule {

    private final Option<Boolean> allTreeTypesOption = super.createOption("all-tree-types", OptionDataType.BOOLEAN);
    private final Option<Float> appleDropRateOption = super.createOption("apple-drop-rate", OptionDataType.FLOAT, OptionValidators.Floats.PERCENTAGE);
    private final Option<Float> goldenAppleDropRateOption = super.createOption("golden-apple-drop-rate", OptionDataType.FLOAT, OptionValidators.Floats.PERCENTAGE);
    private final Option<Float> enchantedGoldenAppleDropRateOption = super.createOption("enchanted-golden-apple-drop-rate", OptionDataType.FLOAT, OptionValidators.Floats.PERCENTAGE);

    @EventHandler(ignoreCancelled = true)
    private void onLeavesDecay(LeavesDecayEvent event) {
        this.tryDropApple(event);
    }

    @EventHandler(ignoreCancelled = true)
    private void onBlockDropItem(BlockDropItemEvent event) {
        this.tryDropApple(event);
    }

    private void tryDropApple(BlockEvent event) {
        Block block = event.getBlock();

        if (!this.isEligibleLeaf(block.getType())) return;

        Material drop = this.rollAppleDrop();
        if (drop == null) return;

        block.getWorld().dropItemNaturally(block.getLocation(), new ItemStack(drop));
    }

    private Material rollAppleDrop() {
        float enchantedRate = this.enchantedGoldenAppleDropRateOption.getValue();
        float goldenRate = enchantedRate + this.goldenAppleDropRateOption.getValue();
        float appleRate = goldenRate + this.appleDropRateOption.getValue();

        float roll = ThreadLocalRandom.current().nextFloat();
        if (roll < enchantedRate) return Material.ENCHANTED_GOLDEN_APPLE;
        if (roll < goldenRate) return Material.GOLDEN_APPLE;
        if (roll < appleRate) return Material.APPLE;

        return null;
    }

    private boolean isEligibleLeaf(Material material) {
        return Tag.LEAVES.isTagged(material) && (this.allTreeTypesOption.getValue() || material == Material.OAK_LEAVES);
    }
}