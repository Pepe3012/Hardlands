package org.heather.hardlands.scenario.modules;

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
import org.heather.hardlands.util.option.Option;
import org.heather.hardlands.util.option.OptionDataType;
import org.heather.hardlands.scenario.ScenarioModule;

import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

public final class CutCleanScenario extends ScenarioModule {

    private final Option<Map<Material, SmeltingResult>> smeltingResultsOption = super.createMapOption("smelting-results");
    private final Option<Map<Material, Material>> cookingResultsOption = super.createMapOption("cooking-results");
    private final Option<Boolean> smeltingExperienceOption = super.createOption("smelting-experience", OptionDataType.BOOLEAN);

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    private void onBlockDropItem(BlockDropItemEvent event) {
        SmeltingResult result = this.smeltingResultsOption.getValue().get(event.getBlockState().getType());
        if (result == null) return;

        int amount = this.replaceBlockDrops(event, result.material());

        if (this.smeltingExperienceOption.getValue()) {
            this.dropSmeltingExperience(event, amount, result.experience());
        }
    }

    @EventHandler(ignoreCancelled = true)
    private void onEntityDeath(EntityDeathEvent event) {
        if (event.getEntity() instanceof Player) return;

        event.getDrops().replaceAll(this::cookFood);
    }

    private ItemStack cookFood(ItemStack item) {
        Material cookedMaterial = this.cookingResultsOption.getValue().get(item.getType());
        return cookedMaterial == null ? item : new ItemStack(cookedMaterial, item.getAmount());
    }

    private int replaceBlockDrops(BlockDropItemEvent event, Material material) {
        int amount = 0;

        for (Item item : event.getItems()) {
            ItemStack stack = item.getItemStack();

            amount += stack.getAmount();
            item.setItemStack(new ItemStack(material, stack.getAmount()));
        }

        return amount;
    }

    private void dropSmeltingExperience(BlockDropItemEvent event, int amount, float experiencePerItem) {
        if (experiencePerItem <= 0.0F) return;

        int experience = this.calculateSmeltingExperience(amount, experiencePerItem);
        if (experience == 0) return;

        Location location = event.getBlock().getLocation().add(0.5D, 0.5D, 0.5D);
        event.getBlock().getWorld().spawn(location, ExperienceOrb.class, orb -> orb.setExperience(experience));
    }

    private int calculateSmeltingExperience(int amount, float experiencePerItem) {
        float experience = amount * experiencePerItem;
        int guaranteedExperience = (int) experience;

        return ThreadLocalRandom.current().nextFloat() < experience - guaranteedExperience
                ? guaranteedExperience + 1
                : guaranteedExperience;
    }

    public record SmeltingResult(Material material, float experience) {}
}