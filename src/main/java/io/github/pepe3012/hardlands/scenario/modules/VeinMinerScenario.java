package io.github.pepe3012.hardlands.scenario.modules;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import io.github.pepe3012.hardlands.common.util.BlockTraversal;
import io.github.pepe3012.hardlands.common.util.data.BoundedCounter;
import io.github.pepe3012.hardlands.config.option.Option;
import io.github.pepe3012.hardlands.config.option.OptionValidators;
import io.github.pepe3012.hardlands.scenario.base.OreScenarioModule;

public final class VeinMinerScenario extends OreScenarioModule {

    private final Option<Integer> veinSizeLimitOption = super.createOption("vein-size-limit", OptionValidators.Integers.POSITIVE);

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    private void onBlockBreak(BlockBreakEvent event) {
        Block block = event.getBlock();

        if (!super.isAffectedOre(block.getType())) return;

        event.setCancelled(true);
        this.mineVein(block, event.getPlayer());
    }

    private void mineVein(Block origin, Player player) {
        Material ore = origin.getType();
        ItemStack tool = player.getInventory().getItemInMainHand();
        BoundedCounter counter = new BoundedCounter(this.veinSizeLimitOption.getValue());

        BlockTraversal.traverseConnected(origin, block -> {
            if (block.getType() != ore || !counter.tryAdvance()) return false;

            block.breakNaturally(tool);
            return true;
        });
    }
}