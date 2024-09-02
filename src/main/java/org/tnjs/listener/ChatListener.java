package org.tnjs.listener;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.*;
import org.bukkit.event.player.PlayerChatEvent;
import org.tnjs.AaDCF.Main;
import org.tnjs.functions.pingPerm;

import java.util.*;

public class ChatListener implements Listener {
    private Main plugin;
    private FileConfiguration config;
    private Map<String, Integer> wordSeverityMap;
    private Map<Integer, String> severityActionMap;
    private Map<String, List<String>> substitutionsMap;

    public ChatListener(Main plugin) {
        this.plugin = plugin;
        reloadConfig(); // Initialize config and mappings
    }

    public void reloadConfig() {
        this.config = plugin.getConfig(); // Update config from the plugin instance
        this.wordSeverityMap = new HashMap<>();
        this.severityActionMap = new HashMap<>();
        this.substitutionsMap = new HashMap<>();

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

        // Load substitutions from config
        if (config.contains("substitutions")) {
            for (String key : config.getConfigurationSection("substitutions").getKeys(false)) {
                String replacement = config.getString("substitutions." + key);
                substitutionsMap.put(key, Arrays.asList(replacement.split(",\\s*")));
            }
        } else {
            Bukkit.getLogger().warning("No 'substitutions' section found in the config.yml");
        }
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerChat(PlayerChatEvent event) {
        String originalMessage = event.getMessage().toLowerCase();
        String playerName = event.getPlayer().getName();

        // Check the original message and all possible variations
        if (containsBannedWord(originalMessage)) {
            handleBlockedWord(event, originalMessage);
        } else {
            // Generate all variations of the message with substitutions
            Set<String> variations = generateAllVariations(originalMessage);
            for (String variation : variations) {
                if (containsBannedWord(variation)) {
                    event.setCancelled(true);
                    event.getPlayer().sendMessage(Objects.requireNonNull(config.getString("messages.default", "Your message was not sent due to inappropriate content.")));
                    Bukkit.getConsoleSender().sendMessage(event.getPlayer().getName() + " was prevented from sending ' " + ChatColor.RED + event.getMessage() + ChatColor.WHITE + " '");
                    event.getPlayer().playSound(event.getPlayer().getLocation(), Sound.ENTITY_VILLAGER_NO, 10, 1F);
                    pingPerm.sendMessageToPlayersWithPermission("aadcf.notify", "§c<" + event.getPlayer().getName() + "> " + event.getMessage());
                    return;
                }
            }
        }
    }

    private boolean containsBannedWord(String message) {
        for (String bannedWord : wordSeverityMap.keySet()) {
            if (message.contains(bannedWord)) {
                return true;
            }
        }
        return false;
    }

    private Set<String> generateAllVariations(String message) {
        Set<String> variations = new HashSet<>();
        variations.add(message); // Start with the original message
        generateVariations(message, 0, new StringBuilder(), variations);
        return variations;
    }

    private void generateVariations(String message, int index, StringBuilder current, Set<String> variations) {
        if (index == message.length()) {
            variations.add(current.toString());
            return;
        }

        char ch = message.charAt(index);
        String key = String.valueOf(ch);
        List<String> replacementChars = substitutionsMap.getOrDefault(key, Collections.singletonList(key));

        for (String replacement : replacementChars) {
            current.append(replacement);
            generateVariations(message, index + 1, current, variations);
            current.delete(current.length() - replacement.length(), current.length());
        }
    }

    private void handleBlockedWord(PlayerChatEvent event, String originalMessage) {
        for (Map.Entry<String, Integer> entry : wordSeverityMap.entrySet()) {
            String bannedWord = entry.getKey();
            int severity = entry.getValue();

            if (originalMessage.contains(bannedWord)) {
                event.setCancelled(true);
                handleSeverity(severity, event, event.getPlayer().getName(), bannedWord);
                return;
            }
        }
    }

    private void handleSeverity(int severity, PlayerChatEvent event, String playerName, String term) {
        String commandTemplate = severityActionMap.getOrDefault(severity, "/notify $player");
        String command = commandTemplate
                .replace("$player", playerName)
                .replace("$term", term);

        final String finalCommand = command;
        Bukkit.getScheduler().runTask(plugin, () -> {
            Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), finalCommand);
        });

        event.getPlayer().sendMessage(Objects.requireNonNull(config.getString("messages.default", "Your message was not sent due to inappropriate content.")));
        Bukkit.getConsoleSender().sendMessage(event.getPlayer().getName() + " was prevented from sending ' " + ChatColor.RED + event.getMessage() + ChatColor.WHITE + " '");
        event.getPlayer().playSound(event.getPlayer().getLocation(), Sound.ENTITY_VILLAGER_NO, 10, 1F);
        pingPerm.sendMessageToPlayersWithPermission("aadcf.notify", "§c<" + event.getPlayer().getName() + "> " + event.getMessage().replace(term, "§m" + term + "§f§c"));
    }
}
