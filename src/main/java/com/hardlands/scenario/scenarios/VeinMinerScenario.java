package com.hardlands.scenario.scenarios;

import com.hardlands.scenario.Scenario;
import com.hardlands.util.BlockUtil;
import com.hardlands.util.BoundedCounter;
import com.hardlands.util.option.Option;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

public final class VeinMinerScenario extends Scenario {

    private final Option<Integer> oreLimit = super.option("ore_limit", 40);

    @EventHandler(priority = EventPriority.HIGHEST)
    private void onBlockBreak(BlockBreakEvent event) {
        Block block = event.getBlock();
        if (!BlockUtil.isOre(block.getType())) return;

        event.setCancelled(true);
        this.mineVein(block, event.getPlayer());
    }

    private void mineVein(Block origin, Player player) {
        Material ore = origin.getType();
        ItemStack tool = player.getInventory().getItemInMainHand();
        BoundedCounter counter = new BoundedCounter(this.oreLimit.getValue());

        BlockUtil.breakConnected(origin, block -> {
            if (block.getType() != ore || !counter.tryAdvance()) return false;
            BlockUtil.breakWithDropEvent(block, player, tool);
            return true;
        });
    }
}