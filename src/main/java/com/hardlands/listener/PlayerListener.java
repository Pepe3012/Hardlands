package com.hardlands.listener;

import io.papermc.paper.datacomponent.item.ResolvableProfile;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.*;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Skull;
import org.bukkit.block.data.Directional;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

public final class PlayerListener implements Listener {
    public static final PlayerListener INSTANCE = new PlayerListener();
    private static final String KILL_ACTION_BAR = "<#FFFFFF>☠ <#B22222>¡Has eliminado a <#FFFFFF>%s<#B22222>! <#FFFFFF>☠";

    private PlayerListener() {}

    @EventHandler
    private void onDeath(PlayerDeathEvent event) {
        Player player = event.getPlayer();

        Component deathMessage = event.deathMessage();
        if (deathMessage != null) {
            String plainDeathMessage = PlainTextComponentSerializer.plainText().serialize(deathMessage);
            event.deathMessage(MiniMessage.miniMessage().deserialize("<#B22222>" + plainDeathMessage));
        }

        Entity causingEntity = event.getDamageSource().getCausingEntity();
        if (causingEntity instanceof Player killer && killer != player) {
            String killFeedback = KILL_ACTION_BAR.formatted(player.getName());
            killer.sendActionBar(MiniMessage.miniMessage().deserialize(killFeedback));
        }

        player.getWorld().playSound(player.getLocation(), Sound.ITEM_TRIDENT_THUNDER, 0.75F, 1.75F);
        Bukkit.getOnlinePlayers().forEach(onlinePlayers -> {
            onlinePlayers.playSound(onlinePlayers, Sound.ENTITY_ELDER_GUARDIAN_DEATH, 1.0F, 0.65F);
            onlinePlayers.playSound(onlinePlayers, Sound.ENTITY_GUARDIAN_DEATH, 1.0F, 0.5F);
        });

        this.placeTombstone(player);
    }

    @SuppressWarnings("UnstableApiUsage")
    private void placeTombstone(Player player) {
        Location location = player.getLocation();
        World world = player.getWorld();

        world.setBlockData(location, Material.BEDROCK.createBlockData());

        Directional endRod = (Directional) Material.END_ROD.createBlockData();
        endRod.setFacing(BlockFace.DOWN);
        world.setBlockData(location.clone().add(0, 1, 0), endRod);

        Location skullLocation = location.clone().add(0, 2, 0);
        world.setBlockData(skullLocation, Material.PLAYER_HEAD.createBlockData());

        Skull skull = (Skull) skullLocation.getBlock().getState();
        skull.setProfile(ResolvableProfile.resolvableProfile(player.getPlayerProfile()));
        skull.update(true, false);
    }
}