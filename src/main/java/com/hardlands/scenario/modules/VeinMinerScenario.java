package com.hardlands.scenario.modules;

import com.hardlands.common.option.Option;
import com.hardlands.common.option.OptionValidators;
import com.hardlands.scenario.ScenarioModule;
import com.hardlands.common.util.BlockUtil;
import com.hardlands.common.util.BoundedCounter;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

public final class VeinMinerScenario extends ScenarioModule {

    private final Option<Integer> oreLimitOption = createOption("ore-limit", Integer.class, OptionValidators.Integers.POSITIVE);

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    private void onBlockBreak(BlockBreakEvent event) {
        Block block = event.getBlock();
        if (!BlockUtil.isOre(block.getType())) return;

        event.setCancelled(true);
        mineOreVein(block, event.getPlayer());
    }

    private void mineOreVein(Block origin, Player player) {
        Material oreType = origin.getType();
        ItemStack tool = player.getInventory().getItemInMainHand();
        BoundedCounter oreCounter = new BoundedCounter(oreLimitOption.getValue());

        BlockUtil.breakConnected(origin, block -> {
            if (block.getType() != oreType || !oreCounter.tryAdvance()) return false;

            BlockUtil.breakWithDropEvent(block, player, tool);
            return true;
        });
    }
}