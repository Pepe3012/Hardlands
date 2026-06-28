package com.hardlands.scenario.custom;

import com.hardlands.scenario.Scenario;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDropItemEvent;
import org.bukkit.event.block.LeavesDecayEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

public class VeinMinerScenario extends Scenario {
    private static final List<Material> ORES = List.of(
            Material.COAL_ORE, Material.DEEPSLATE_COAL_ORE,
            Material.IRON_ORE, Material.DEEPSLATE_IRON_ORE,
            Material.COPPER_ORE, Material.DEEPSLATE_COPPER_ORE,
            Material.GOLD_ORE, Material.DEEPSLATE_GOLD_ORE,
            Material.REDSTONE_ORE, Material.DEEPSLATE_REDSTONE_ORE,
            Material.EMERALD_ORE, Material.DEEPSLATE_EMERALD_ORE,
            Material.LAPIS_ORE, Material.DEEPSLATE_LAPIS_ORE,
            Material.DIAMOND_ORE, Material.DEEPSLATE_DIAMOND_ORE,
            Material.NETHER_QUARTZ_ORE, Material.NETHER_GOLD_ORE,
            Material.ANCIENT_DEBRIS
    );

    private final Option<Float> radius = createOption("radius", 1.0F);
    private final Option<Float> oreLimit = createOption("ore_limit", 64.0F);

    @EventHandler(priority = EventPriority.LOWEST)
    private void onBlockBreak(BlockBreakEvent event) {
        Block block = event.getBlock();
        if (!ORES.contains(block.getType())) return;
        this.mineVein(block, event.getPlayer(), new HashSet<>(), new int[]{0});
    }

    private void mineVein(Block block, Player player, Set<Location> visited, int[] count) {
        if (!visited.add(block.getLocation()) || !ORES.contains(block.getType())) return;
        if (count[0]++ >= this.oreLimit.getValue()) return;

        ItemStack tool = player.getInventory().getItemInMainHand();
        List<Item> drops = block.getDrops(tool).stream()
                .map(stack -> block.getWorld().dropItem(block.getLocation(), stack, item -> item.setPickupDelay(Integer.MAX_VALUE)))
                .toList();

        BlockDropItemEvent dropEvent = new BlockDropItemEvent(block, block.getState(), player, new ArrayList<>(drops));
        Bukkit.getPluginManager().callEvent(dropEvent);

        drops.forEach(Item::remove);
        dropEvent.getItems().forEach(item -> block.getWorld().dropItemNaturally(block.getLocation(), item.getItemStack()));
        block.setType(Material.AIR);

        int r = Math.round(this.radius.getValue());
        iterateNeighbors(block, r, neighbor -> mineVein(neighbor, player, visited, count));
    }

    private static void iterateNeighbors(Block block, int radius, Consumer<Block> action) {
        for (int x = -radius; x <= radius; x++)
            for (int y = -radius; y <= radius; y++)
                for (int z = -radius; z <= radius; z++) {
                    if (x == 0 && y == 0 && z == 0) continue;
                    action.accept(block.getRelative(x, y, z));
                }
    }
}