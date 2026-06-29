package com.hardlands.scenario.custom;

import com.hardlands.scenario.Scenario;
import com.hardlands.util.BlockUtil;
import com.hardlands.util.BoundedCounter;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import java.util.EnumSet;
import java.util.Set;

public final class VeinMinerScenario extends Scenario {
    private static final int ORE_LIMIT = 64;

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    private void onBlockBreak(BlockBreakEvent event) {
        Block block = event.getBlock();

        if (!BlockUtil.isOre(block.getType())) return;

        event.setCancelled(true);
        this.mineVein(block, event.getPlayer());
    }

    private void mineVein(Block origin, Player player) {
        Material veinType = origin.getType();
        ItemStack tool = player.getInventory().getItemInMainHand();
        BoundedCounter counter = new BoundedCounter(ORE_LIMIT);

        BlockUtil.breakConnected(origin, block -> {
            if (block.getType() != veinType || !counter.tryAdvance()) return false;
            BlockUtil.breakWithDropEvent(block, player, tool);
            return true;
        });
    }
}