package com.hardlands.util;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockDropItemEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;

public final class BlockUtil {
    private static final String DEEPSLATE_PREFIX = "DEEPSLATE_";
    private static final int RADIUS = 1;

    private BlockUtil() {}

    public static void breakWithDropEvent(@NotNull Block block, @NotNull Player player, @NotNull ItemStack tool) {
        BlockState state = block.getState();
        List<Item> drops = createTemporaryDrops(block, tool);

        BlockDropItemEvent event = new BlockDropItemEvent(block, state, player, drops);
        Bukkit.getPluginManager().callEvent(event);

        drops.forEach(Item::remove);
        block.setType(Material.AIR);

        if (event.isCancelled()) return;

        event.getItems().forEach(item -> block.getWorld().dropItemNaturally(block.getLocation(), item.getItemStack()));
    }

    public static void breakConnected(@NotNull Block origin, @NotNull Predicate<Block> action) {
        Deque<Block> pending = new ArrayDeque<>();
        Set<Location> visited = new HashSet<>();

        pending.add(origin);

        while (!pending.isEmpty()) {
            Block block = pending.removeFirst();
            if (visited.add(block.getLocation()) && action.test(block)) {
                addNearby(block, pending);
            }
        }
    }

    public static <V> Map<Material, V> withDeepslateVariants(@NotNull Map<Material, V> base) {
        EnumMap<Material, V> materials = new EnumMap<>(Material.class);

        base.forEach((material, value) -> {
            materials.put(material, value);

            Material deepslate = getDeepslateVariant(material);
            if (deepslate != null) materials.put(deepslate, value);
        });

        return materials;
    }

    public static Set<Material> withDeepslateVariants(@NotNull Set<Material> base) {
        EnumSet<Material> materials = EnumSet.noneOf(Material.class);

        for (Material material : base) {
            materials.add(material);

            Material deepslate = getDeepslateVariant(material);
            if (deepslate != null) materials.add(deepslate);
        }

        return materials;
    }

    private static List<Item> createTemporaryDrops(Block block, ItemStack tool) {
        List<Item> drops = new ArrayList<>();

        for (ItemStack drop : block.getDrops(tool)) {
            Item item = block.getWorld().dropItem(block.getLocation(), drop);
            item.setPickupDelay(Integer.MAX_VALUE);
            drops.add(item);
        }

        return drops;
    }

    private static void addNearby(Block block, Deque<Block> pending) {
        for (int x = -BlockUtil.RADIUS; x <= BlockUtil.RADIUS; x++) {
            for (int y = -BlockUtil.RADIUS; y <= BlockUtil.RADIUS; y++) {
                for (int z = -BlockUtil.RADIUS; z <= BlockUtil.RADIUS; z++) {
                    if (x != 0 || y != 0 || z != 0) {
                        pending.add(block.getRelative(x, y, z));
                    }
                }
            }
        }
    }

    private static Material getDeepslateVariant(Material material) {
        return Material.getMaterial(DEEPSLATE_PREFIX + material.name());
    }
}