package org.heather.hardlands.module.scenario.scenarios;

import io.papermc.paper.event.player.PlayerInventorySlotChangeEvent;
import java.util.Map;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.ItemStack;
import org.heather.hardlands.config.ConfigBuilder;
import org.heather.hardlands.config.OptionDef;
import org.heather.hardlands.module.scenario.Scenario;

@ConfigBuilder(
        superclass = Scenario.class,
        options =
                @OptionDef(
                        type = Map.class,
                        keyType = Enchantment.class,
                        valueType = Integer.class,
                        name = "enchantments"))
public class MagicManScenario extends MagicManScenarioConfiguration {

    @EventHandler
    private void onPlayerInventorySlotChange(PlayerInventorySlotChangeEvent event) {
        ItemStack item = event.getNewItemStack();

        if (this.applyEnchantments(item)) {
            event.getPlayer().getInventory().setItem(event.getSlot(), item);
        }
    }

    private boolean applyEnchantments(ItemStack item) {
        boolean changed = false;

        for (Map.Entry<Enchantment, Integer> entry : super.enchantments.getValue().entrySet()) {
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
