package com.hardlands.scenario.custom;

import com.hardlands.scenario.Scenario;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.LeavesDecayEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

public class TimberScenario extends Scenario {
    private final Option<Boolean> breakLeaves = createOption("break_leaves", false);
    private final Option<Float> radius = createOption("radius", 1.0F);
    private final Option<Float> logLimit = createOption("log_limit", 100.0F);
    private final Option<Float> leavesLimit = createOption("leaves_limit", 200.0F);

    @EventHandler
    private void onBlockBreak(BlockBreakEvent event) {
        if (!hasAxe(event.getPlayer())) return;

        Block block = event.getBlock();
        if (!isLog(block.getType())) return;
        breakConnectedTree(block, new HashSet<>(), new int[]{0, 0});
    }

    private void breakConnectedTree(Block block, Set<Location> visited, int[] counts) {
        if (!visited.add(block.getLocation())) return;
        if (!shouldBreak(block.getType(), counts)) return;

        breakBlock(block);

        int radiusValue = Math.round(this.radius.getValue());
        iterateNeighbors(block, radiusValue, neighbor -> breakConnectedTree(neighbor, visited, counts));
    }

    private boolean shouldBreak(Material type, int[] counts) {
        if (isLog(type)) {
            return counts[0]++ < this.logLimit.getValue();
        }
        if (Boolean.TRUE.equals(this.breakLeaves.getValue()) && isLeaves(type)) {
            return counts[1]++ < this.leavesLimit.getValue();
        }
        return false;
    }

    @SuppressWarnings("UnstableApiUsage")
    private static void breakBlock(Block block) {
        if (isLeaves(block.getType())) {
            Bukkit.getPluginManager().callEvent(new LeavesDecayEvent(block));
        }
        block.getWorld().dropItemNaturally(block.getLocation(), new ItemStack(block.getType()));
        block.setType(Material.AIR);
    }

    private static void iterateNeighbors(Block block, int radius, Consumer<Block> action) {
        for (int x = -radius; x <= radius; x++)
            for (int y = -radius; y <= radius; y++)
                for (int z = -radius; z <= radius; z++) {
                    if (x == 0 && y == 0 && z == 0) continue;
                    action.accept(block.getRelative(x, y, z));
                }
    }

    private static boolean hasAxe(Player player) {
        return player.getInventory().getItemInMainHand().getType().name().contains("_AXE");
    }

    private static boolean isLog(Material material) {
        String name = material.name();
        return name.contains("_LOG") || name.contains("_WOOD");
    }

    private static boolean isLeaves(Material material) {
        return material.name().contains("_LEAVES");
    }
}