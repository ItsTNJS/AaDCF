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
    private Map<Integer, List<String>> severityActionMap;
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
                List<String> commands = config.getStringList("severity-actions." + key);
                int severityLevel = Integer.parseInt(key.replace("severity-", ""));
                severityActionMap.put(severityLevel, commands);
            }
        } else {
            Bukkit.getLogger().warning("No 'severity-actions' section found in the config.yml");
        }

        // Load substitutions from config
        if (config.contains("substitutions")) {
            for (String key : config.getConfigurationSection("substitutions").getKeys(false)) {
                String replacements = config.getString("substitutions." + key);
                substitutionsMap.put(key, Arrays.asList(replacements.split(",\\s*")));
            }
        } else {
            Bukkit.getLogger().warning("No 'substitutions' section found in the config.yml");
        }
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerChat(PlayerChatEvent event) {
        String originalMessage = event.getMessage().toLowerCase();
        String playerName = event.getPlayer().getName();

        // Check if the original message contains any banned words
        if (containsBannedWord(originalMessage)) {
            handleBlockedWord(event, originalMessage);
        } else {
            // If no direct matches, apply substitutions and check again
            if (containsBannedWordAfterSubstitutions(originalMessage)) {
                event.setCancelled(true);
                event.getPlayer().sendMessage(Objects.requireNonNull(config.getString("messages.default", "Your message was not sent due to inappropriate content.")));
                Bukkit.getConsoleSender().sendMessage(event.getPlayer().getName() + " was prevented from sending ' " + ChatColor.RED + event.getMessage() + ChatColor.WHITE + " '");
                event.getPlayer().playSound(event.getPlayer().getLocation(), Sound.ENTITY_VILLAGER_NO, 10, 1F);
                pingPerm.sendMessageToPlayersWithPermission("aadcf.notify", "§c<" + event.getPlayer().getName() + "> " + event.getMessage());
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

    private boolean containsBannedWordAfterSubstitutions(String message) {
        // Generate all possible variations of the message with substitutions applied
        Set<String> variations = new HashSet<>();
        variations.add(message);

        // Apply substitutions
        for (Map.Entry<String, List<String>> substitution : substitutionsMap.entrySet()) {
            String target = substitution.getKey();
            List<String> replacements = substitution.getValue();
            Set<String> newVariations = new HashSet<>();

            for (String variation : variations) {
                for (String replacement : replacements) {
                    newVariations.add(variation.replace(target, replacement));
                }
            }
            variations.addAll(newVariations);
        }

        // Check if any variation contains a banned word
        for (String variation : variations) {
            if (containsBannedWord(variation)) {
                return true;
            }
        }
        return false;
    }

    private void handleBlockedWord(PlayerChatEvent event, String originalMessage) {
        for (Map.Entry<String, Integer> entry : wordSeverityMap.entrySet()) {
            String bannedWord = entry.getKey();
            int severity = entry.getValue();

            if (originalMessage.contains(bannedWord)) {
                // Cancel the event to prevent the message from being sent
                event.setCancelled(true);

                // Handle actions based on severity
                handleSeverity(severity, event, event.getPlayer().getName(), bannedWord);

                return; // Exit after handling the first found banned word
            }
        }
    }

    private void handleSeverity(int severity, PlayerChatEvent event, String playerName, String term) {
        List<String> commands = severityActionMap.getOrDefault(severity, Collections.singletonList(""));

        // Execute each command
        for (String commandTemplate : commands) {
            String command = commandTemplate
                    .replace("$player", playerName)
                    .replace("$term", term);

            final String finalCommand = command; // Make the command variable effectively final
            Bukkit.getScheduler().runTask(plugin, () -> {
                Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), finalCommand);
            });
        }

       // event.getPlayer().sendMessage(Objects.requireNonNull(config.getString("messages.default", "Your message was not sent due to inappropriate content.")));
        Bukkit.getConsoleSender().sendMessage(event.getPlayer().getName() + " was prevented from sending ' " + ChatColor.RED + event.getMessage() + ChatColor.WHITE + " '");
        event.getPlayer().playSound(event.getPlayer().getLocation(), Sound.ENTITY_VILLAGER_NO, 10, 1F);
        pingPerm.sendMessageToPlayersWithPermission("aadcf.notify", "§c<" + event.getPlayer().getName() + "> " + event.getMessage().replace(term, "§m" + term + "§f§c"));
    }
}
