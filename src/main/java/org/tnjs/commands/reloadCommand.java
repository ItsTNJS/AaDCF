package org.tnjs.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;


public class reloadCommand implements CommandExecutor {

    private JavaPlugin plugin;

    public void ReloadCommand(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender.hasPermission("myplugin.reload")) {
            plugin.reloadConfig();
            sender.sendMessage(ChatColor.GREEN + "Configuration reloaded!");
            return true;
        } else {
            sender.sendMessage(ChatColor.RED + "You do not have permission to reload the config.");
            return false;
        }
    }


}
