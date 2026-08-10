package com.hardlands.scenario.modules;

import com.hardlands.common.option.Option;
import com.hardlands.scenario.ScenarioModule;
import com.hardlands.common.util.BlockUtil;
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

//TODO Change static final maps into Options.
public final class CutCleanScenario extends ScenarioModule {

    private static final Map<Material, SmeltingResult> SMELTING_RESULTS = BlockUtil.withDeepslateVariants(Map.of(
            Material.IRON_ORE, smeltsTo(Material.IRON_INGOT, 0.7F),
            Material.GOLD_ORE, smeltsTo(Material.GOLD_INGOT, 1.0F),
            Material.COPPER_ORE, smeltsTo(Material.COPPER_INGOT, 0.7F),
            Material.ANCIENT_DEBRIS, smeltsTo(Material.NETHERITE_SCRAP, 2.0F),
            Material.SAND, smeltsTo(Material.GLASS, 0.0F)
    ));

    private static final Map<Material, Material> COOKED_DROPS = Map.of(
            Material.BEEF, Material.COOKED_BEEF,
            Material.PORKCHOP, Material.COOKED_PORKCHOP,
            Material.CHICKEN, Material.COOKED_CHICKEN,
            Material.MUTTON, Material.COOKED_MUTTON,
            Material.RABBIT, Material.COOKED_RABBIT,
            Material.SALMON, Material.COOKED_SALMON,
            Material.COD, Material.COOKED_COD
    );

    private final Option<Boolean> dropExperienceOption = createOption("drop-experience", Boolean.class);

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    private void onBlockDropItem(BlockDropItemEvent event) {
        SmeltingResult result = SMELTING_RESULTS.get(event.getBlockState().getType());
        if (result == null) return;

        int smeltedAmount = replaceSmeltedDrops(event, result.material());

        if (dropExperienceOption.getValue()) dropSmeltingExperience(event, smeltedAmount, result.experience());
    }

    @EventHandler
    private void onEntityDeath(EntityDeathEvent event) {
        if (event.getEntity() instanceof Player) return;

        event.getDrops().replaceAll(CutCleanScenario::cookDrop);
    }

    private static int replaceSmeltedDrops(BlockDropItemEvent event, Material smeltedMaterial) {
        int amount = 0;

        for (Item item : event.getItems()) {
            ItemStack itemStack = item.getItemStack();
            amount += itemStack.getAmount();
            item.setItemStack(new ItemStack(smeltedMaterial, itemStack.getAmount()));
        }

        return amount;
    }

    private static ItemStack cookDrop(ItemStack itemStack) {
        Material cookedMaterial = COOKED_DROPS.get(itemStack.getType());

        return cookedMaterial == null ? itemStack : new ItemStack(cookedMaterial, itemStack.getAmount());
    }

    private static void dropSmeltingExperience(BlockDropItemEvent event, int amount, float experiencePerItem) {
        if (experiencePerItem <= 0.0F) return;

        int experience = calculateSmeltingExperience(amount, experiencePerItem);
        if (experience == 0) return;

        Location location = event.getBlock().getLocation().add(0.5D, 0.5D, 0.5D);
        event.getBlock().getWorld().spawn(location, ExperienceOrb.class, orb -> orb.setExperience(experience));
    }

    private static int calculateSmeltingExperience(int amount, float experiencePerItem) {
        float experience = amount * experiencePerItem;
        int guaranteedExperience = (int) experience;

        return ThreadLocalRandom.current().nextFloat() < experience - guaranteedExperience ? guaranteedExperience + 1 : guaranteedExperience;
    }

    private static SmeltingResult smeltsTo(Material material, float experience) {
        return new SmeltingResult(material, experience);
    }

    private record SmeltingResult(Material material, float experience) {}
}