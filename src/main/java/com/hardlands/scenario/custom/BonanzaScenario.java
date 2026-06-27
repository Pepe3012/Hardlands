package com.hardlands.scenario.custom;

import com.hardlands.scenario.Scenario;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockDropItemEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class BonanzaScenario extends Scenario {
    private static final List<Material> ORE_MAP = createOreMap();

    private final Option<Float> multiplierOption = super.registerOption(Option.create("multiplier", 2.0F));

    @EventHandler
    private void onBlockDrop(BlockDropItemEvent event) {
        if (!ORE_MAP.contains(event.getBlockState().getType())) return;

        float multiplier = this.multiplierOption.getValue();
        event.getItems().forEach(item -> {
            ItemStack stack = item.getItemStack();
            item.setItemStack(new ItemStack(stack.getType(), (int) (stack.getAmount() * multiplier)));
        });
    }

    private static List<Material> createOreMap() {
        return List.of(
                Material.COAL_ORE, Material.DEEPSLATE_COAL_ORE,
                Material.IRON_ORE, Material.DEEPSLATE_IRON_ORE,
                Material.COPPER_ORE, Material.DEEPSLATE_COPPER_ORE,
                Material.GOLD_ORE, Material.DEEPSLATE_GOLD_ORE,
                Material.REDSTONE_ORE, Material.DEEPSLATE_REDSTONE_ORE,
                Material.EMERALD_ORE, Material.DEEPSLATE_EMERALD_ORE,
                Material.LAPIS_ORE, Material.DEEPSLATE_LAPIS_ORE,
                Material.DIAMOND_ORE, Material.DEEPSLATE_DIAMOND_ORE,
                Material.NETHER_QUARTZ_ORE,
                Material.NETHER_GOLD_ORE,
                Material.ANCIENT_DEBRIS
        );
    }
}