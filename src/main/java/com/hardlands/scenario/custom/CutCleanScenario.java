package com.hardlands.scenario.custom;

import com.hardlands.scenario.Scenario;
import org.bukkit.Material;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockDropItemEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;

import java.util.EnumMap;
import java.util.Map;

public class CutCleanScenario extends Scenario {
    private static final Map<Material, Material> COOK_MAP = createCookMap();
    private static final Map<Material, Material> SMELT_MAP = createSmeltMap();
    private static final Map<Material, Float> XP_MAP = createXpMap();

    @EventHandler
    private void onEntityDeath(EntityDeathEvent event) {
        if (event.getEntity() instanceof Player) return;
        event.getDrops().replaceAll(drop -> {
            Material cooked = COOK_MAP.get(drop.getType());
            return cooked != null
                    ? new ItemStack(cooked, drop.getAmount())
                    : drop;
        });
    }

    @EventHandler(priority = EventPriority.LOWEST)
    private void onBlockDrop(BlockDropItemEvent event) {
        Material smelted = SMELT_MAP.get(event.getBlockState().getType());
        if (smelted == null) return;

        float xpPerItem = XP_MAP.get(event.getBlockState().getType());
        event.getItems().forEach(item -> {
            int amount = item.getItemStack().getAmount();
            item.setItemStack(new ItemStack(smelted, amount));

            int xp = calculateXp(amount, xpPerItem);
            event.getBlock().getWorld().spawn(event.getBlock().getLocation(), ExperienceOrb.class, orb -> orb.setExperience(xp));
        });
    }

    private static int calculateXp(int amount, float xpPerItem) {
        float xp = amount * xpPerItem;
        int full = (int) xp;
        return Math.random() < (xp - full) ? full + 1 : full;
    }

    private static Map<Material, Material> createCookMap() {
        return new EnumMap<>(Map.of(
                Material.BEEF, Material.COOKED_BEEF,
                Material.PORKCHOP, Material.COOKED_PORKCHOP,
                Material.CHICKEN, Material.COOKED_CHICKEN,
                Material.MUTTON, Material.COOKED_MUTTON,
                Material.RABBIT, Material.COOKED_RABBIT,
                Material.SALMON, Material.COOKED_SALMON,
                Material.COD, Material.COOKED_COD
        ));
    }

    private static Map<Material, Material> createSmeltMap() {
        EnumMap<Material, Material> map = new EnumMap<>(Map.of(
                Material.IRON_ORE, Material.IRON_INGOT,
                Material.GOLD_ORE, Material.GOLD_INGOT,
                Material.COPPER_ORE, Material.COPPER_INGOT,
                Material.ANCIENT_DEBRIS, Material.NETHERITE_SCRAP
        ));
        map.clone().forEach((ore, result) -> {
            Material deepslateOre = Material.getMaterial("DEEPSLATE_" + ore.name());
            if (deepslateOre != null) {
                map.put(deepslateOre, result);
            }
        });
        return map;
    }

    private static Map<Material, Float> createXpMap() {
        EnumMap<Material, Float> map = new EnumMap<>(Map.of(
                Material.IRON_ORE, 0.7f,
                Material.GOLD_ORE, 1.0f,
                Material.COPPER_ORE, 0.7f,
                Material.ANCIENT_DEBRIS, 2.0f
        ));
        map.clone().forEach((ore, xp) -> {
            Material deepslateOre = Material.getMaterial("DEEPSLATE_" + ore.name());
            if (deepslateOre != null) map.put(deepslateOre, xp);
        });
        return map;
    }
}