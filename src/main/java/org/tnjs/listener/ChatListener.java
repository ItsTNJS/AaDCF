package org.tnjs.listener;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.*;
import org.bukkit.event.player.PlayerChatEvent;
import org.tnjs.AaDCF.Main;
import org.tnjs.functions.pingPerm;

import java.util.List;
import java.util.Objects;

public class ChatListener implements Listener {
    private Main plugin;
    private FileConfiguration config;
    private List<String> wordsList;

    public ChatListener(Main plugin) {
        this.plugin = plugin;
        reloadConfig(); // Initialize config and words list
    }

    public void reloadConfig() {
        this.config = plugin.getConfig(); // Update config from the plugin instance
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
            net.md_5.bungee.api.chat.TextComponent actionBar = new net.md_5.bungee.api.chat.TextComponent(message);

            Bukkit.getConsoleSender().sendMessage(event.getPlayer().getName() + " was prevented from sending ' " + ChatColor.RED + message + ChatColor.WHITE + " '");

            event.getPlayer().playSound(event.getPlayer().getLocation(), Sound.ENTITY_VILLAGER_NO, 10, 1F);
            pingPerm.sendMessageToPlayersWithPermission("aadcf.notify", "§c<" + event.getPlayer().getName() + "> §m" + event.getMessage());
        }
    }
}
