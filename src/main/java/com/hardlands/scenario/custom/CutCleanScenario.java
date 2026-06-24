package com.hardlands.scenario.custom;

import com.hardlands.scenario.Scenario;
import org.bukkit.Material;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockDropItemEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;

import java.util.EnumMap;
import java.util.Map;

public class CutCleanScenario extends Scenario {
    private static final Map<Class<? extends Entity>, MobDrop> COOK_MAP = createCookMap();
    private static final Map<Material, Material> SMELT_MAP = createSmeltMap();

    @Override
    protected void onInitialize() {
    }

    @Override
    protected void onTerminate() {
    }

    @EventHandler
    public void onBlockDrop(BlockDropItemEvent event) {
        Material smelted = SMELT_MAP.get(event.getBlockState().getType());
        if (smelted != null) {
            event.getItems().forEach(item -> item.setItemStack(new ItemStack(smelted, item.getItemStack().getAmount())));
        }
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        MobDrop mobDrop = COOK_MAP.get(event.getEntity().getClass());
        if (mobDrop != null) {
            event.getDrops().replaceAll(drop -> drop.getType() == mobDrop.raw() ? new ItemStack(mobDrop.cooked(), drop.getAmount()) : drop);
        }
    }

    private record MobDrop(Material raw, Material cooked) {}

    private static Map<Class<? extends Entity>, MobDrop> createCookMap() {
        return Map.ofEntries(
                Map.entry(Cow.class, new MobDrop(Material.BEEF, Material.COOKED_BEEF)),
                Map.entry(MushroomCow.class, new MobDrop(Material.BEEF, Material.COOKED_BEEF)),
                Map.entry(Pig.class, new MobDrop(Material.PORKCHOP, Material.COOKED_PORKCHOP)),
                Map.entry(Hoglin.class, new MobDrop(Material.PORKCHOP, Material.COOKED_PORKCHOP)),
                Map.entry(Zoglin.class, new MobDrop(Material.PORKCHOP, Material.COOKED_PORKCHOP)),
                Map.entry(Chicken.class, new MobDrop(Material.CHICKEN, Material.COOKED_CHICKEN)),
                Map.entry(Sheep.class, new MobDrop(Material.MUTTON, Material.COOKED_MUTTON)),
                Map.entry(Rabbit.class, new MobDrop(Material.RABBIT, Material.COOKED_RABBIT)),
                Map.entry(Salmon.class, new MobDrop(Material.SALMON, Material.COOKED_SALMON)),
                Map.entry(Cod.class, new MobDrop(Material.COD, Material.COOKED_COD)),
                Map.entry(Dolphin.class, new MobDrop(Material.COD, Material.COOKED_COD)),
                Map.entry(PolarBear.class, new MobDrop(Material.COD, Material.COOKED_COD)),
                Map.entry(Guardian.class, new MobDrop(Material.COD, Material.COOKED_COD)),
                Map.entry(ElderGuardian.class, new MobDrop(Material.COD, Material.COOKED_COD))
        );
    }

    private static Map<Material, Material> createSmeltMap() {
        EnumMap<Material, Material> map = new EnumMap<>(Material.class);

        map.put(Material.IRON_ORE, Material.IRON_INGOT);
        map.put(Material.GOLD_ORE, Material.GOLD_INGOT);
        map.put(Material.COPPER_ORE, Material.COPPER_INGOT);
        map.put(Material.COAL_ORE, Material.COAL);
        map.put(Material.DIAMOND_ORE, Material.DIAMOND);
        map.put(Material.EMERALD_ORE, Material.EMERALD);
        map.put(Material.LAPIS_ORE, Material.LAPIS_LAZULI);
        map.put(Material.REDSTONE_ORE, Material.REDSTONE);
        map.put(Material.NETHER_GOLD_ORE, Material.GOLD_INGOT);
        map.put(Material.NETHER_QUARTZ_ORE, Material.QUARTZ);
        map.put(Material.ANCIENT_DEBRIS, Material.NETHERITE_SCRAP);

        map.forEach((ore, result) -> {
            Material deepslate = Material.getMaterial("DEEPSLATE_" + ore.name());
            if (deepslate != null) map.put(deepslate, result);
        });

        return map;
    }
}