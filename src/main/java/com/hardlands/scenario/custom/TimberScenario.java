package com.hardlands.scenario.custom;

import com.hardlands.scenario.Scenario;
import com.hardlands.util.BlockUtil;
import com.hardlands.util.BoundedCounter;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

public final class TimberScenario extends Scenario {
    private static final int LOG_LIMIT = 100;
    private static final int LEAVES_LIMIT = 200;

    private final Option<Boolean> breakLeaves = super.createOption("break_leaves", false);

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    private void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        Block block = event.getBlock();

        if (!hasAxe(player) || !isLog(block.getType())) return;

        event.setCancelled(true);
        this.breakTree(block, player);
    }

    private void breakTree(Block origin, Player player) {
        ItemStack tool = player.getInventory().getItemInMainHand();
        boolean shouldBreakLeaves = Boolean.TRUE.equals(this.breakLeaves.getValue());

        BoundedCounter logs = new BoundedCounter(LOG_LIMIT);
        BoundedCounter leaves = new BoundedCounter(LEAVES_LIMIT);

        BlockUtil.breakConnected(origin, block -> {
            if (!canBreak(block.getType(), logs, leaves, shouldBreakLeaves)) return false;
            BlockUtil.breakWithDropEvent(block, player, tool);
            return true;
        });
    }

    private static boolean canBreak(Material material, BoundedCounter logs, BoundedCounter leaves, boolean shouldBreakLeaves) {
        if (isLog(material)) return logs.tryAdvance();
        if (shouldBreakLeaves && isLeaves(material)) return leaves.tryAdvance();

        return false;
    }

    private static boolean hasAxe(Player player) {
        return player.getInventory().getItemInMainHand().getType().name().endsWith("_AXE");
    }

    private static boolean isLog(Material material) {
        return Tag.LOGS.isTagged(material);
    }

    private static boolean isLeaves(Material material) {
        return Tag.LEAVES.isTagged(material);
    }
}