package org.tnjs.listener;

import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.*;
import org.tnjs.functions.pingPerm;
import org.bukkit.event.player.PlayerChatEvent;
import java.io.File;
import java.util.Objects;
import org.tnjs.AaDCF.Main;


public class ChatListener implements Listener {
    private final FileConfiguration config;
    this.config = plugin.getConfig();
    private static final String FILTER_PHRASE = "poop";
    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerChat(PlayerChatEvent event) {
        String message = event.getMessage();

        if (message.toLowerCase().contains(FILTER_PHRASE)) {
            // Cancel the event to prevent the message from being sent
            event.setCancelled(true);

            // Optionally, send a message to the player informing them
            event.getPlayer().sendMessage(Objects.requireNonNull(Config.getString("message")));
            event.getPlayer().playSound(event.getPlayer().getLocation(), Sound.ITEM_TOTEM_USE, 10, .1F);
            pingPerm.sendMessageToPlayersWithPermission("aadcf.notify", "§c<"+event.getPlayer().getName()+"> "+event.getMessage());
        };
        if (event.getPlayer().hasPermission("aadcf.gay")) {
            event.setMessage(event.getMessage()+" §c💋§f");
        };
    }


}

