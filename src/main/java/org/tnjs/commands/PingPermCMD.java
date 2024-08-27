package org.tnjs.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.tnjs.functions.pingPerm;

import java.util.Arrays;

public class PingPermCMD implements CommandExecutor {

    private final pingPerm pingPerm = new pingPerm();

    // Constructor to pass an instance of pingPerm


    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        // Check if enough arguments are provided
        if (args.length < 2) {
            sender.sendMessage("Usage: /pingperm <permission> <message>");
            return false;
        }

        String permission = args[0];
        String message = String.join(" ", Arrays.copyOfRange(args, 1, args.length));

        // Call the method from pingPerm to send messages
        pingPerm.sendMessageToPlayersWithPermission(permission, message);

        // Optionally, notify the sender
        sender.sendMessage("Message broadcasted to players with permission '" + permission + "'.");

        return true;
    }
}
