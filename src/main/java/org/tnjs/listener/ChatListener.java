package org.tnjs.listener;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.*;
import org.tnjs.functions.pingPerm;
import org.bukkit.event.player.PlayerChatEvent;
import java.io.File;
import java.util.List;
import java.util.Objects;
import org.tnjs.AaDCF.Main;


public class ChatListener implements Listener {
    private FileConfiguration config;
    private List<String> wordsList;

    public ChatListener(Main plugin) {
        this.config = plugin.getConfig(); // Initialize config from the plugin instance
        this.wordsList = config.getStringList("words");

    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerChat(PlayerChatEvent event) {
        String message = event.getMessage().toLowerCase();

        boolean containsBannedWord = false;

        for (String bannedWord : wordsList) {
            if (message.contains(bannedWord.toLowerCase())) {
                containsBannedWord = true;
                String reason = bannedWord;
                break;
            }
        }

        if (containsBannedWord) {
            // Cancel the event to prevent the message from being sent
            event.setCancelled(true);

            event.getPlayer().sendMessage(Objects.requireNonNull(config.getString("message")));
            Bukkit.getConsoleSender().sendMessage(event.getPlayer().getName()+" was prevented from sending ' "+ ChatColor.RED +message+ ChatColor.WHITE+" '");

            event.getPlayer().playSound(event.getPlayer().getLocation(), Sound.ITEM_TOTEM_USE, 10, .1F);
            pingPerm.sendMessageToPlayersWithPermission("aadcf.notify", "§c<"+event.getPlayer().getName()+"> §m"+event.getMessage());
        };
        if (event.getPlayer().hasPermission("aadcf.gay")) {
            event.setMessage(event.getMessage()+" §c💋§f");
        };
    }


}

