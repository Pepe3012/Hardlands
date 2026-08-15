package io.github.pepe3012.hardlands.common.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import io.github.pepe3012.hardlands.config.inventory.InventoryDefinition;
import org.bukkit.entity.Player;

@CommandAlias("hardlands|hl")
@CommandPermission("hardlands.admin")
public final class HardlandsCommand extends BaseCommand {

    @Default
    private void onDefault(Player player) {
        InventoryDefinition.MAIN.openInventory(player);
    }
}