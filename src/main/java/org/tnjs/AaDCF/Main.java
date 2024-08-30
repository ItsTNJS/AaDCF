package org.tnjs.AaDCF;

import org.bukkit.command.CommandExecutor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.tnjs.commands.AaDCFCMD;
import org.tnjs.commands.PingPermCMD;
import org.tnjs.commands.tab.AaDCFCMDTab;
import org.tnjs.commands.tab.PingPermTab;
import org.tnjs.functions.pingPerm;
import org.tnjs.listener.ChatListener;

public final class Main extends JavaPlugin {
    private static Main instance;
    private ChatListener chatListener;

    @Override
    public void onEnable() {
        instance = this;

        // Load and save the default configuration
        FileConfiguration config = getConfig();
        config.options().copyDefaults(true);
        saveConfig();

        // Initialize and register listeners
        chatListener = new ChatListener(this);
        getServer().getPluginManager().registerEvents(chatListener, this);

        // Register commands
        getCommand("aadcf").setExecutor(new AaDCFCMD(this));
        getCommand("aadcf").setTabCompleter(new AaDCFCMDTab());

        getCommand("pingperm").setExecutor(new PingPermCMD());
        getCommand("pingperm").setTabCompleter(new PingPermTab());
    }

    @Override
    public void onDisable() {
        getLogger().info("Goodbye!");
    }

    public void reloadPluginConfig() {
        reloadConfig();
        getConfig(); // Update the local config reference

        // Reload the ChatListener configuration
        if (chatListener != null) {
            chatListener.reloadConfig();
        }

        getLogger().info("Configuration reloaded!");
    }

    public static Main getInstance() {
        return instance;
    }
}
