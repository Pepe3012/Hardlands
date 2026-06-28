package com.hardlands.scenario.custom;

import com.hardlands.scenario.Scenario;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockDropItemEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class BonanzaScenario extends Scenario {
    private static final List<Material> DROPS = List.of(Material.COAL, Material.RAW_IRON, Material.RAW_COPPER, Material.RAW_GOLD, Material.REDSTONE, Material.EMERALD, Material.LAPIS_LAZULI, Material.DIAMOND, Material.QUARTZ, Material.GOLD_NUGGET, Material.NETHERITE_SCRAP);

    private final Option<Float> multiplier = createOption("multiplier", 2.0F);

    @EventHandler(priority = EventPriority.LOWEST)
    private void onBlockDropItem(BlockDropItemEvent event) {
        float multiplierValue = this.multiplier.getValue();
        event.getItems().forEach(item -> {
            ItemStack stack = item.getItemStack();
            if (DROPS.contains(stack.getType())) {
                item.setItemStack(new ItemStack(stack.getType(), (int) (stack.getAmount() * multiplierValue)));
            }
        });
    }
}