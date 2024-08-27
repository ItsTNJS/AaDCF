package org.tnjs.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.tnjs.AaDCF.Main;

public class MyCommandExecutor implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        // Check if the sender is a player or console
        if (sender.hasPermission("aadcf.commands.test")) {
            // Send a message to the sender
            sender.sendMessage("Hello, this is a basic command message!");

            new BukkitRunnable() {
                public void run() {
                    sender.sendMessage("This is a runnable...");
                }
            }.runTaskAsynchronously(Main.getInstance());
            return true;
        } else {
            sender.sendMessage("You don't have permission to use this command.");
            return false;
        }
    }
}
