package org.tnjs.listener;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.command.CommandSender;
import org.bukkit.event.*;
import org.bukkit.event.player.PlayerChatEvent;
import org.tnjs.AaDCF.Main;
import org.tnjs.functions.pingPerm;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ChatListener implements Listener {
    private Main plugin;
    private FileConfiguration config;
    private Map<String, Integer> wordSeverityMap;
    private Map<Integer, String> severityActionMap;

    public ChatListener(Main plugin) {
        this.plugin = plugin;
        reloadConfig(); // Initialize config and mappings
    }

    public void reloadConfig() {
        this.config = plugin.getConfig(); // Update config from the plugin instance
        this.wordSeverityMap = new HashMap<>();
        this.severityActionMap = new HashMap<>();

        // Load words and their severity levels from config
        if (config.contains("words")) {
            for (String word : config.getConfigurationSection("words").getKeys(false)) {
                int severity = config.getInt("words." + word + ".severity");
                wordSeverityMap.put(word.toLowerCase(), severity);
            }
        } else {
            Bukkit.getLogger().warning("No 'words' section found in the config.yml");
        }

        // Load severity actions from config
        if (config.contains("severity-actions")) {
            for (String key : config.getConfigurationSection("severity-actions").getKeys(false)) {
                String command = config.getString("severity-actions." + key);
                int severityLevel = Integer.parseInt(key.replace("severity-", ""));
                severityActionMap.put(severityLevel, command);
            }
        } else {
            Bukkit.getLogger().warning("No 'severity-actions' section found in the config.yml");
        }
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerChat(PlayerChatEvent event) {
        String message = event.getMessage().toLowerCase();
        String playerName = event.getPlayer().getName();

        for (Map.Entry<String, Integer> entry : wordSeverityMap.entrySet()) {
            String bannedWord = entry.getKey();
            int severity = entry.getValue();

            if (message.contains(bannedWord)) {
                // Cancel the event to prevent the message from being sent
                event.setCancelled(true);

                // Handle actions based on severity
                handleSeverity(severity, event, playerName, bannedWord);

                return; // Exit after handling the first found banned word
            }
        }
    }

    private void handleSeverity(int severity, PlayerChatEvent event, String playerName, String term) {
        // Get the command from the severityActionMap or use a default one
        String commandTemplate = severityActionMap.getOrDefault(severity, "/notify $player");

        // Replace $player placeholder with the actual player name
        String command = commandTemplate.replace("$player", playerName).replace("$term", term);

        // Execute the command
        final String finalCommand = command; // Make the command variable effectively final
        Bukkit.getScheduler().runTask(plugin, () -> {
            Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), finalCommand);
        });

        // Additional common actions
        event.getPlayer().sendMessage(Objects.requireNonNull(config.getString("messages.default", "Your message was not sent due to inappropriate content.")));
        Bukkit.getConsoleSender().sendMessage(event.getPlayer().getName() + " was prevented from sending ' " + term + ChatColor.RED + event.getMessage() + ChatColor.WHITE + " '");
        event.getPlayer().playSound(event.getPlayer().getLocation(), Sound.ENTITY_VILLAGER_NO, 10, 1F);
        pingPerm.sendMessageToPlayersWithPermission("aadcf.notify", "§c<" + event.getPlayer().getName() + "> " + event.getMessage().replace(term,"§m"+term+"§f§c"));
    }
}
