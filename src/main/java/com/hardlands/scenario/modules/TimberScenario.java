package com.hardlands.scenario.modules;

import com.hardlands.common.option.Option;
import com.hardlands.common.option.OptionValidators;
import com.hardlands.scenario.ScenarioModule;
import com.hardlands.common.util.BlockUtil;
import com.hardlands.common.util.BoundedCounter;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

public final class TimberScenario extends ScenarioModule {

    private final Option<Boolean> breakLeavesOption = createOption("break-leaves", Boolean.class);
    private final Option<Integer> leafLimitOption = createOption("leaf-limit", Integer.class, OptionValidators.Integers.NON_NEGATIVE);
    private final Option<Integer> logLimitOption = createOption("log-limit", Integer.class, OptionValidators.Integers.POSITIVE);

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    private void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        Block block = event.getBlock();

        if (!isHoldingAxe(player) || !isLog(block.getType())) return;

        breakTree(block, player);
    }

    private void breakTree(Block origin, Player player) {
        ItemStack tool = player.getInventory().getItemInMainHand();
        BoundedCounter logCounter = new BoundedCounter(logLimitOption.getValue());
        BoundedCounter leafCounter = new BoundedCounter(leafLimitOption.getValue());
        boolean breakLeaves = breakLeavesOption.getValue();

        BlockUtil.breakConnected(origin, block -> {
            if (!canBreakTreeBlock(block.getType(), logCounter, leafCounter, breakLeaves)) return false;

            BlockUtil.breakWithDropEvent(block, player, tool);
            return true;
        });
    }

    private static boolean canBreakTreeBlock(Material material, BoundedCounter logCounter, BoundedCounter leafCounter, boolean breakLeaves) {
        if (isLog(material)) return logCounter.tryAdvance();

        return breakLeaves && isLeaf(material) && leafCounter.tryAdvance();
    }

    private static boolean isHoldingAxe(Player player) {
        return player.getInventory().getItemInMainHand().getType().name().endsWith("_AXE");
    }

    private static boolean isLog(Material material) {
        return Tag.LOGS.isTagged(material);
    }

    private static boolean isLeaf(Material material) {
        return Tag.LEAVES.isTagged(material);
    }
}