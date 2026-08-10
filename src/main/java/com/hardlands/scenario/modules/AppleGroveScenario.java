package com.hardlands.scenario.modules;

import com.hardlands.common.option.Option;
import com.hardlands.common.option.OptionValidators;
import com.hardlands.scenario.ScenarioModule;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockDropItemEvent;
import org.bukkit.event.block.BlockEvent;
import org.bukkit.event.block.LeavesDecayEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

public final class AppleGroveScenario extends ScenarioModule {

    private final Option<Boolean> allTreesOption = createOption("all-trees", Boolean.class);
    private final Option<Float> appleRateOption = createOption("apple-rate", Float.class, OptionValidators.Floats.PERCENTAGE);
    private final Option<Float> goldenAppleRateOption = createOption("golden-apple-rate", Float.class, OptionValidators.Floats.PERCENTAGE);
    private final Option<Float> enchantedGoldenAppleRateOption = createOption("enchanted-golden-apple-rate", Float.class, OptionValidators.Floats.PERCENTAGE);

    @EventHandler(ignoreCancelled = true)
    private void onLeavesDecay(LeavesDecayEvent event) {
        handleBlockDrop(event);
    }

    @EventHandler(ignoreCancelled = true)
    private void onBlockDropItem(BlockDropItemEvent event) {
        handleBlockDrop(event);
    }

    private void handleBlockDrop(BlockEvent event) {
        Block block = event.getBlock();

        if (!isEligibleLeaf(block.getType())) return;

        determineAppleDrop().ifPresent(apple ->
                block.getWorld().dropItemNaturally(block.getLocation(), new ItemStack(apple)));
    }

    private Optional<Material> determineAppleDrop() {
        float enchantedRate = enchantedGoldenAppleRateOption.getValue();
        float goldenRate = enchantedRate + goldenAppleRateOption.getValue();
        float appleRate = goldenRate + appleRateOption.getValue();

        float roll = ThreadLocalRandom.current().nextFloat();
        if (roll < enchantedRate) return Optional.of(Material.ENCHANTED_GOLDEN_APPLE);
        if (roll < goldenRate) return Optional.of(Material.GOLDEN_APPLE);
        if (roll < appleRate) return Optional.of(Material.APPLE);

        return Optional.empty();
    }

    private boolean isEligibleLeaf(Material material) {
        return Tag.LEAVES.isTagged(material) && (allTreesOption.getValue() || material == Material.OAK_LEAVES);
    }
}