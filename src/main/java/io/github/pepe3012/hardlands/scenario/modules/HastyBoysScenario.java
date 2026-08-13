package io.github.pepe3012.hardlands.scenario.modules;

import io.papermc.paper.event.player.PlayerInventorySlotChangeEvent;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.ItemStack;
import io.github.pepe3012.hardlands.config.option.Option;
import io.github.pepe3012.hardlands.config.option.OptionValidators;
import io.github.pepe3012.hardlands.scenario.ScenarioModule;

import java.util.Set;

public final class HastyBoysScenario extends ScenarioModule {

    private final Option<Set<Material>> affectedToolsOption = super.createSetOption("affected-tools");
    private final Option<Integer> efficiencyLevelOption = super.createOption("efficiency-level", OptionValidators.Integers.NON_NEGATIVE);
    private final Option<Integer> unbreakingLevelOption = super.createOption("unbreaking-level", OptionValidators.Integers.NON_NEGATIVE);

    @EventHandler
    private void onInventorySlotChange(PlayerInventorySlotChangeEvent event) {
        ItemStack item = event.getNewItemStack();

        if (!this.isAffectedTool(item) || !this.applyConfiguredEnchantments(item)) return;

        event.getPlayer().getInventory().setItem(event.getSlot(), item);
    }

    private boolean isAffectedTool(ItemStack item) {
        return this.affectedToolsOption.getValue().contains(item.getType());
    }

    private boolean applyConfiguredEnchantments(ItemStack item) {
        boolean changed = false;

        changed |= this.applyMinimumEnchantment(item, Enchantment.EFFICIENCY, this.efficiencyLevelOption.getValue());
        changed |= this.applyMinimumEnchantment(item, Enchantment.UNBREAKING, this.unbreakingLevelOption.getValue());

        return changed;
    }

    private boolean applyMinimumEnchantment(ItemStack item, Enchantment enchantment, int level) {
        if (item.getEnchantmentLevel(enchantment) >= level) return false;

        item.addUnsafeEnchantment(enchantment, level);
        return true;
    }
}