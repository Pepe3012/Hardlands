package io.github.pepe3012.hardlands.common.item.inventory;

import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.function.Supplier;

record InventoryItemConfiguration(
        Supplier<ItemStack> factory,
        List<InventoryItemClick> clicks
) {}