package io.github.pepe3012.hardlands.module.scenario.scenarios;

import io.github.pepe3012.hardlands.Hardlands;
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

import java.util.Optional;

public class AppleGroveScenario extends Scenario {

    private final Option<Boolean> allTreeTypes = super.registerOption("all-tree-types", Boolean.class);
    private final Option<Float> appleDropRate = super.registerOption("apple-drop-rate", Float.class, OptionValidators.Floats.UNIT_INTERVAL);
    private final Option<Float> goldenAppleDropRate = super.registerOption("golden-apple-drop-rate", Float.class, OptionValidators.Floats.UNIT_INTERVAL);
    private final Option<Float> enchantedGoldenAppleDropRate = super.registerOption("enchanted-golden-apple-drop-rate", Float.class, OptionValidators.Floats.UNIT_INTERVAL);

    @EventHandler(ignoreCancelled = true)
    private void onLeavesDecay(LeavesDecayEvent event) {
        tryDropApple(event);
    }

    @EventHandler(ignoreCancelled = true)
    private void onBlockDropItem(BlockDropItemEvent event) {
        tryDropApple(event);
    }

    private void tryDropApple(BlockEvent event) {
        var block = event.getBlock();

        if (!isEligibleLeaf(block.getType())) return;

        findAppleDrop().ifPresent(drop ->
                block.getWorld().dropItemNaturally(block.getLocation(), new ItemStack(drop)));
    }

    private Optional<Material> findAppleDrop() {
        var roll = Hardlands.RANDOMIZER.nextFloat();

        var enchantedRate = enchantedGoldenAppleDropRate.getValue();
        var goldenRate = enchantedRate + goldenAppleDropRate.getValue();
        var appleRate = goldenRate + appleDropRate.getValue();

        if (roll < enchantedRate) return Optional.of(Material.ENCHANTED_GOLDEN_APPLE);
        if (roll < goldenRate) return Optional.of(Material.GOLDEN_APPLE);
        if (roll < appleRate) return Optional.of(Material.APPLE);

        return Optional.empty();
    }

    private boolean isEligibleLeaf(Material material) {
        return Tag.LEAVES.isTagged(material)
                && (allTreeTypes.getValue() || material == Material.OAK_LEAVES);
    }
}