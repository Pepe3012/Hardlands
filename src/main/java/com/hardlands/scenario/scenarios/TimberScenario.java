package com.hardlands.scenario.scenarios;

import com.hardlands.option.Option;
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

public class TimberScenario extends Scenario {

    private final Option<Boolean> breakLeaves = super.optionContainer.create("break_leaves", false);
    private final Option<Integer> logLimit = super.optionContainer.create("log_limit", 200);
    private final Option<Integer> leaveLimit = super.optionContainer.create("leave_limit", 300);

    @EventHandler(priority = EventPriority.HIGHEST)
    private void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        Block block = event.getBlock();

        if (!hasAxe(player) || !isLog(block.getType())) return;

        this.breakTree(block, player);
    }

    private void breakTree(Block origin, Player player) {
        ItemStack tool = player.getInventory().getItemInMainHand();
        boolean shouldBreakLeaves = Boolean.TRUE.equals(this.breakLeaves.getValue());

        BoundedCounter logs = new BoundedCounter(this.logLimit.getValue());
        BoundedCounter leaves = new BoundedCounter(this.leaveLimit.getValue());

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