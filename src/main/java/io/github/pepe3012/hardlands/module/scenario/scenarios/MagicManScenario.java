package io.github.pepe3012.hardlands.module.scenario.scenarios;

import io.github.pepe3012.hardlands.core.config.Option;
import io.github.pepe3012.hardlands.module.scenario.Scenario;
import io.papermc.paper.event.player.PlayerInventorySlotChangeEvent;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.ItemStack;

import java.util.Map;

public final class MagicManScenario extends Scenario {

    private final Option<Map<Enchantment, Integer>> enchantments = super.registerMap("enchantments", Enchantment.class, Integer.class);

    @EventHandler
    private void onInventorySlotChange(PlayerInventorySlotChangeEvent event) {
        var item = event.getNewItemStack();

        if (!this.applyEnchantments(item)) return;

        event.getPlayer().getInventory().setItem(event.getSlot(), item);
    }

    private boolean applyEnchantments(ItemStack item) {
        var changed = false;

        for (var entry : this.enchantments.getValue().entrySet()) {
            changed |= applyEnchantment(item, entry.getKey(), entry.getValue());
        }

        return changed;
    }

    private static boolean applyEnchantment(ItemStack item, Enchantment enchantment, int level) {
        if (level <= 0
                || !enchantment.canEnchantItem(item)
                || item.getEnchantmentLevel(enchantment) >= level) {
            return false;
        }

        item.addUnsafeEnchantment(enchantment, level);
        return true;
    }
}