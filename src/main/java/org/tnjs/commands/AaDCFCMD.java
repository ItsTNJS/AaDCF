package org.tnjs.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.tnjs.AaDCF.Main;

import java.util.Objects;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class AaDCFCMD implements CommandExecutor {

    private final Main plugin;

    // Constructor to pass an instance of Main
    public AaDCFCMD(Main plugin) {
        this.plugin = plugin;
    }
    public int getRandomNumber(int min, int max) {
        return (int) ((Math.random() * (max - min)) + min);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        // Check if enough arguments are provided
        if (args.length == 0) {
            sender.sendMessage("AaDCF Running version " + plugin.getDescription().getVersion());
            return true;
        }

        if (Objects.equals(args[0], "reload")) {
            if (sender.hasPermission("aadcf.admin")) {
                sender.sendMessage("Reloading plugin...");
                plugin.reloadPluginConfig();
                try {
                    TimeUnit.MILLISECONDS.sleep(getRandomNumber(500,1000));
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                sender.sendMessage("Reloaded!");
            } else {
                sender.sendMessage("No permission.");
            }
            return true;
        }

        // Handle unknown commands
        sender.sendMessage("Unknown command.");
        return false;
    }
}
