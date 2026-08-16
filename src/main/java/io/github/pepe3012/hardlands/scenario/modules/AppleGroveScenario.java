package io.github.pepe3012.hardlands.scenario.modules;

import io.github.pepe3012.hardlands.data.option.Option;
import io.github.pepe3012.hardlands.data.option.OptionDataType;
import io.github.pepe3012.hardlands.data.option.OptionValidators;
import io.github.pepe3012.hardlands.scenario.ScenarioModule;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockDropItemEvent;
import org.bukkit.event.block.BlockEvent;
import org.bukkit.event.block.LeavesDecayEvent;
import org.bukkit.inventory.ItemStack;

import java.util.concurrent.ThreadLocalRandom;

public final class AppleGroveScenario extends ScenarioModule {

    private final Option<Boolean> allTreeTypes = super.registerOption("all-tree-types", OptionDataType.BOOLEAN);
    private final Option<Float> appleDropRate = super.registerOption("apple-drop-rate", OptionDataType.FLOAT, OptionValidators.Floats.PERCENTAGE);
    private final Option<Float> goldenAppleDropRate = super.registerOption("golden-apple-drop-rate", OptionDataType.FLOAT, OptionValidators.Floats.PERCENTAGE);
    private final Option<Float> enchantedGoldenAppleDropRate = super.registerOption("enchanted-golden-apple-drop-rate", OptionDataType.FLOAT, OptionValidators.Floats.PERCENTAGE);

    public AppleGroveScenario() {
        super("apple-grove");
    }

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
        float enchantedRate = this.enchantedGoldenAppleDropRate.getValue();
        float goldenRate = enchantedRate + this.goldenAppleDropRate.getValue();
        float appleRate = goldenRate + this.appleDropRate.getValue();

        float roll = ThreadLocalRandom.current().nextFloat();
        if (roll < enchantedRate) return Material.ENCHANTED_GOLDEN_APPLE;
        if (roll < goldenRate) return Material.GOLDEN_APPLE;
        if (roll < appleRate) return Material.APPLE;

        return null;
    }

    private boolean isEligibleLeaf(Material material) {
        return Tag.LEAVES.isTagged(material) && (this.allTreeTypes.getValue() || material == Material.OAK_LEAVES);
    }
}