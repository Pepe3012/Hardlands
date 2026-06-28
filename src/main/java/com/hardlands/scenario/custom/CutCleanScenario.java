package com.hardlands.scenario.custom;

import com.hardlands.scenario.Scenario;
import org.bukkit.Location;
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
    private static final Map<Material, Material> SMELT_MAP = buildWithDeepslate(Map.of(
            Material.IRON_ORE, Material.IRON_INGOT,
            Material.GOLD_ORE, Material.GOLD_INGOT,
            Material.COPPER_ORE, Material.COPPER_INGOT,
            Material.ANCIENT_DEBRIS, Material.NETHERITE_SCRAP
    ));

    private static final Map<Material, Float> XP_MAP = buildWithDeepslate(Map.of(
            Material.IRON_ORE, 0.7F,
            Material.COPPER_ORE, 0.7F,
            Material.GOLD_ORE, 1.0F,
            Material.ANCIENT_DEBRIS, 2.0F
    ));

    private static final Map<Material, Material> COOK_MAP = new EnumMap<>(Map.of(
            Material.BEEF, Material.COOKED_BEEF,
            Material.PORKCHOP, Material.COOKED_PORKCHOP,
            Material.CHICKEN, Material.COOKED_CHICKEN,
            Material.MUTTON, Material.COOKED_MUTTON,
            Material.RABBIT, Material.COOKED_RABBIT,
            Material.SALMON, Material.COOKED_SALMON,
            Material.COD, Material.COOKED_COD
    ));

    private final Option<Boolean> dropExperience = super.createOption("drop_experience", true);

    @EventHandler(priority = EventPriority.MONITOR)
    private void onBlockDropItem(BlockDropItemEvent event) {
        Material type = event.getBlockState().getType();
        Material smelted = SMELT_MAP.get(type);
        if (smelted == null) return;

        float xpPerItem = XP_MAP.get(type);
        Location dropLocation = event.getBlock().getLocation();

        event.getItems().forEach(item -> {
            int amount = item.getItemStack().getAmount();
            item.setItemStack(new ItemStack(smelted, amount));

            if (Boolean.TRUE.equals(this.dropExperience.getValue())) {
                int xp = calculateXp(amount, xpPerItem);
                dropLocation.getWorld().spawn(dropLocation, ExperienceOrb.class, orb -> orb.setExperience(xp));
            }
        });
    }

    @EventHandler
    private void onEntityDeath(EntityDeathEvent event) {
        if (event.getEntity() instanceof Player) return;
        event.getDrops().replaceAll(drop -> {
            Material cooked = COOK_MAP.get(drop.getType());
            return cooked != null ? new ItemStack(cooked, drop.getAmount()) : drop;
        });
    }

    private static int calculateXp(int amount, float xpPerItem) {
        float xp = amount * xpPerItem;
        int full = (int) xp;
        return Math.random() < (xp - full) ? full + 1 : full;
    }

    private static <V> Map<Material, V> buildWithDeepslate(Map<Material, V> base) {
        EnumMap<Material, V> map = new EnumMap<>(base);
        new EnumMap<>(map).forEach((ore, value) -> {
            Material deepslate = Material.getMaterial("DEEPSLATE_" + ore.name());
            if (deepslate != null) map.put(deepslate, value);
        });
        return map;
    }
}