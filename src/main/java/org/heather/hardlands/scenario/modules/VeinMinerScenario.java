package org.heather.hardlands.scenario.modules;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.heather.hardlands.util.BlockTraversal;
import org.heather.hardlands.util.data.BoundedCounter;
import org.heather.hardlands.util.option.Option;
import org.heather.hardlands.util.option.OptionValidators;
import org.heather.hardlands.scenario.base.OreScenarioModule;

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