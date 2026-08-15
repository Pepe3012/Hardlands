package io.github.pepe3012.hardlands.scenario.modules;

import io.github.pepe3012.hardlands.data.option.Option;
import io.github.pepe3012.hardlands.scenario.ScenarioModule;
import io.papermc.paper.event.player.PlayerInventorySlotChangeEvent;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.ItemStack;

import java.util.Map;

public final class MagicManScenario extends ScenarioModule {

    private final Option<Map<Enchantment, Integer>> enchantmentsOption = super.createMapOption("enchantments");

    @EventHandler
    private void onInventorySlotChange(PlayerInventorySlotChangeEvent event) {
        ItemStack item = event.getNewItemStack();

        if (!this.applyConfiguredEnchantments(item)) return;

        event.getPlayer().getInventory().setItem(event.getSlot(), item);
    }

    private boolean applyConfiguredEnchantments(ItemStack item) {
        boolean changed = false;

        for (Map.Entry<Enchantment, Integer> entry : this.enchantmentsOption.getValue().entrySet()) {
            changed |= this.applyMinimumEnchantment(item, entry.getKey(), entry.getValue());
        }

        return changed;
    }

    private boolean applyMinimumEnchantment(ItemStack item, Enchantment enchantment, int level) {
        if (level <= 0
                || !enchantment.canEnchantItem(item)
                || item.getEnchantmentLevel(enchantment) >= level) return false;

        item.addUnsafeEnchantment(enchantment, level);
        return true;
    }
}