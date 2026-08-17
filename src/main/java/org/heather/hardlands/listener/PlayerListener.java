package org.heather.hardlands.listener;

import org.heather.hardlands.util.TextComponents;
import io.papermc.paper.datacomponent.item.ResolvableProfile;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Skull;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

public final class PlayerListener implements Listener {

    private static final String DEATH_COLOR = "<#B22222>";
    private static final String KILL_MESSAGE = "<#FFFFFF>☠ <#B22222>¡Has eliminado a <#FFFFFF>%s<#B22222>! <#FFFFFF>☠";

    @EventHandler
    private void onPlayerDeath(PlayerDeathEvent event) {
        var player = event.getPlayer();
        var causingEntity = event.getDamageSource().getCausingEntity();

        updateDeathMessage(event, player, causingEntity);
        sendKillMessage(player, causingEntity);
        playDeathSounds(player);
        placeTombstone(player);
    }

    private static void updateDeathMessage(PlayerDeathEvent event, Player player, Entity causingEntity) {
        var message = event.deathMessage();
        if (message == null) return;

        var text = TextComponents.toPlainText(message);

        if (causingEntity instanceof Player killer) {
            text = replacePlayer(text, killer);
        }

        event.deathMessage(TextComponents.parse(DEATH_COLOR + replacePlayer(text, player)));
    }

    private static void sendKillMessage(Player victim, Entity causingEntity) {
        if (!(causingEntity instanceof Player killer) || killer == victim) return;

        var message = KILL_MESSAGE.formatted(TextComponents.formatPlayer(victim));
        killer.sendActionBar(TextComponents.parse(message));
    }

    private static String replacePlayer(String text, Player player) {
        return text.replace(player.getName(), TextComponents.formatPlayer(player));
    }

    private static void playDeathSounds(Player player) {
        player.getWorld().playSound(player.getLocation(), Sound.ITEM_TRIDENT_THUNDER, 0.75F, 1.75F);

        for (var onlinePlayer : Bukkit.getOnlinePlayers()) {
            onlinePlayer.playSound(onlinePlayer, Sound.ENTITY_ELDER_GUARDIAN_DEATH, 1.0F, 0.65F);
            onlinePlayer.playSound(onlinePlayer, Sound.ENTITY_GUARDIAN_DEATH, 1.0F, 0.5F);
        }
    }

    private static void placeTombstone(Player player) {
        var location = player.getLocation();

        location.getBlock().setType(Material.GOLD_BLOCK);
        location.clone().add(0, 1, 0).getBlock().setType(Material.IRON_BARS);

        var skullBlock = location.clone().add(0, 2, 0).getBlock();
        skullBlock.setType(Material.PLAYER_HEAD);

        var skull = (Skull) skullBlock.getState();
        skull.setProfile(ResolvableProfile.resolvableProfile(player.getPlayerProfile()));
        skull.update(true, false);
    }
}