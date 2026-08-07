package com.hardlands.scenario.scenarios;

import com.hardlands.scenario.Scenario;
import com.hardlands.util.BlockUtil;
import com.hardlands.util.option.Option;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockDropItemEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

public final class CutCleanScenario extends Scenario {

    private static final Map<Material, SmeltResult> SMELT_MAP = BlockUtil.withDeepslateVariants(Map.of(
            Material.IRON_ORE, smeltsTo(Material.IRON_INGOT, 0.7F),
            Material.GOLD_ORE, smeltsTo(Material.GOLD_INGOT, 1.0F),
            Material.COPPER_ORE, smeltsTo(Material.COPPER_INGOT, 0.7F),
            Material.ANCIENT_DEBRIS, smeltsTo(Material.NETHERITE_SCRAP, 2.0F),
            Material.SAND, smeltsTo(Material.GLASS, 0.0F)
    ));

    private static final Map<Material, Material> COOK_MAP = Map.of(
            Material.BEEF, Material.COOKED_BEEF,
            Material.PORKCHOP, Material.COOKED_PORKCHOP,
            Material.CHICKEN, Material.COOKED_CHICKEN,
            Material.MUTTON, Material.COOKED_MUTTON,
            Material.RABBIT, Material.COOKED_RABBIT,
            Material.SALMON, Material.COOKED_SALMON,
            Material.COD, Material.COOKED_COD
    );

    private final Option<Boolean> dropExperience = super.option("drop_experience", true);

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    private void onBlockDropItem(BlockDropItemEvent event) {
        SmeltResult result = SMELT_MAP.get(event.getBlockState().getType());
        if (result == null) return;

        int amount = 0;

        for (Item item : event.getItems()) {
            ItemStack stack = item.getItemStack();
            amount += stack.getAmount();
            item.setItemStack(new ItemStack(result.material(), stack.getAmount()));
        }

        if (Boolean.TRUE.equals(this.dropExperience.getValue())) spawnExperience(event, amount, result.experience());
    }

    @EventHandler
    private void onEntityDeath(EntityDeathEvent event) {
        if (event.getEntity() instanceof Player) return;

        event.getDrops().replaceAll(drop -> {
            Material cooked = COOK_MAP.get(drop.getType());
            return cooked == null ? drop : new ItemStack(cooked, drop.getAmount());
        });
    }

    private static void spawnExperience(BlockDropItemEvent event, int amount, float experiencePerItem) {
        if (experiencePerItem <= 0.0F) return;

        int experience = calculateExperience(amount, experiencePerItem);
        if (experience <= 0) return;

        Location location = event.getBlock().getLocation().add(0.5D, 0.5D, 0.5D);
        event.getBlock().getWorld().spawn(location, ExperienceOrb.class, orb -> orb.setExperience(experience));
    }

    private static int calculateExperience(int amount, float experiencePerItem) {
        float experience = amount * experiencePerItem;
        int guaranteed = (int) experience;
        return ThreadLocalRandom.current().nextFloat() < experience - guaranteed ? guaranteed + 1 : guaranteed;
    }

    private static SmeltResult smeltsTo(Material material, float experience) {
        return new SmeltResult(material, experience);
    }

    private record SmeltResult(Material material, float experience) {}
}