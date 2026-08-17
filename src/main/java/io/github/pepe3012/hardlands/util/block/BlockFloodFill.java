package io.github.pepe3012.hardlands.util.block;

import org.bukkit.Location;
import org.bukkit.block.Block;

import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.Queue;
import java.util.function.Predicate;

public final class BlockFloodFill {

    private BlockFloodFill() {}

    public static void traverse(Block origin, Predicate<Block> action) {
        var pending = new ArrayDeque<Block>();
        var visited = new HashSet<Location>();

        pending.add(origin);

        while (!pending.isEmpty()) {
            Block block = pending.remove();

            if (!visited.add(block.getLocation()) || !action.test(block)) continue;

            addNeighbors(block, pending);
        }
    }

    private static void addNeighbors(Block block, Queue<Block> pending) {
        for (int x = -1; x <= 1; x++) {
            for (int y = -1; y <= 1; y++) {
                for (int z = -1; z <= 1; z++) {
                    if (x != 0 || y != 0 || z != 0) {
                        pending.add(block.getRelative(x, y, z));
                    }
                }
            }
        }
    }
}