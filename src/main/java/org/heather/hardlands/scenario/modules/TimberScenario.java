package org.heather.hardlands.scenario.modules;

import org.heather.hardlands.core.option.Option;
import org.heather.hardlands.core.option.OptionDataType;
import org.heather.hardlands.core.option.OptionValidators;
import org.heather.hardlands.common.util.BlockTraversal;
import org.heather.hardlands.common.util.BoundedCounter;
import org.heather.hardlands.scenario.ScenarioModule;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

public final class TimberScenario extends ScenarioModule {

    private final Option<Boolean> breakLeavesOption = super.createOption("break-leaves", OptionDataType.BOOLEAN);
    private final Option<Integer> leafLimitOption = super.createOption("leaf-limit", OptionValidators.Integers.NON_NEGATIVE);
    private final Option<Integer> logLimitOption = super.createOption("log-limit", OptionValidators.Integers.POSITIVE);

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    private void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        Block block = event.getBlock();

        if (!this.isHoldingAxe(player) || !this.isLog(block.getType())) return;

        event.setCancelled(true);
        this.breakTree(block, player);
    }

    private void breakTree(Block origin, Player player) {
        ItemStack tool = player.getInventory().getItemInMainHand();
        BoundedCounter logCounter = new BoundedCounter(this.logLimitOption.getValue());
        BoundedCounter leafCounter = new BoundedCounter(this.leafLimitOption.getValue());
        boolean breakLeaves = this.breakLeavesOption.getValue();

        BlockTraversal.traverseConnected(origin, block -> {
            if (!this.canBreakTreeBlock(block.getType(), logCounter, leafCounter, breakLeaves)) return false;

            block.breakNaturally(tool);
            return true;
        });
    }

    private boolean canBreakTreeBlock(Material material, BoundedCounter logCounter, BoundedCounter leafCounter, boolean breakLeaves) {
        if (this.isLog(material)) return logCounter.tryAdvance();
        return breakLeaves && this.isLeaf(material) && leafCounter.tryAdvance();
    }

    private boolean isHoldingAxe(Player player) {
        return player.getInventory().getItemInMainHand().getType().name().endsWith("_AXE");
    }

    private boolean isLog(Material material) {
        return Tag.LOGS.isTagged(material);
    }

    private boolean isLeaf(Material material) {
        return Tag.LEAVES.isTagged(material);
    }
}