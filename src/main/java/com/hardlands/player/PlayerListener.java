package com.hardlands.player;

import com.hardlands.util.TextFormatter;
import io.papermc.paper.datacomponent.item.ResolvableProfile;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.*;
import org.bukkit.block.Skull;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class PlayerListener implements Listener {

    private static final String KILL_MESSAGE = "<#FFFFFF>☠ <#B22222>¡Has eliminado a <#FFFFFF>%s<#B22222>! <#FFFFFF>☠";

    @EventHandler
    private void onDeath(PlayerDeathEvent event) {
        Player player = event.getPlayer();
        Entity causingEntity = event.getDamageSource().getCausingEntity();

        Component deathMessage = event.deathMessage();
        if (deathMessage != null) {
            String plain = PlainTextComponentSerializer.plainText().serialize(deathMessage);
            String withKiller = causingEntity instanceof Player killer
                    ? plain.replace(killer.getName(), TextFormatter.getPlayerHeadAndName(killer))
                    : plain;
            String formatted = withKiller.replace(player.getName(), TextFormatter.getPlayerHeadAndName(player));
            event.deathMessage(MiniMessage.miniMessage().deserialize("<#B22222>" + formatted));
        }

        if (causingEntity instanceof Player killer && killer != player) {
            killer.sendActionBar(MiniMessage.miniMessage().deserialize(KILL_MESSAGE.formatted(TextFormatter.getPlayerHeadAndName(player))));
        }

        player.getWorld().playSound(player.getLocation(), Sound.ITEM_TRIDENT_THUNDER, 0.75F, 1.75F);
        Bukkit.getOnlinePlayers().forEach(online -> {
            online.playSound(online, Sound.ENTITY_ELDER_GUARDIAN_DEATH, 1.0F, 0.65F);
            online.playSound(online, Sound.ENTITY_GUARDIAN_DEATH, 1.0F, 0.5F);
        });

        this.placeTombstone(player);
    }

    @SuppressWarnings("UnstableApiUsage")
    private void placeTombstone(Player player) {
        Location location = player.getLocation();
        World world = player.getWorld();

        world.setBlockData(location, Material.GOLD_BLOCK.createBlockData());
        world.setBlockData(location.clone().add(0, 1, 0), Material.IRON_BARS.createBlockData());

        Location skullLocation = location.clone().add(0, 2, 0);
        world.setBlockData(skullLocation, Material.PLAYER_HEAD.createBlockData());

        Skull skull = (Skull) skullLocation.getBlock().getState();
        skull.setProfile(ResolvableProfile.resolvableProfile(player.getPlayerProfile()));
        skull.update(true, false);
    }
}