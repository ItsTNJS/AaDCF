package org.tnjs.commands.tab;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.permissions.Permission;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.List;

public class PingPermTab implements TabCompleter {

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> suggestions = new ArrayList<>();

        // Check if the command is /pingperm
        if (command.getName().equalsIgnoreCase("pingperm")) {
            if (args.length == 1) {
                // Suggest permissions
                List<String> permissions = getAllPermissions();
                StringUtil.copyPartialMatches(args[0], permissions, suggestions);
            } else if (args.length > 1) {
                // The second argument is the message, no suggestions needed
                suggestions.add("");
            }
        }

        return suggestions;
    }

    private List<String> getAllPermissions() {
        List<String> permissions = new ArrayList<>();
        for (Permission permission : org.bukkit.plugin.java.JavaPlugin.getPlugin(org.tnjs.AaDCF.Main.class).getServer().getPluginManager().getPermissions()) {
            permissions.add(permission.getName());
        }
        return permissions;
    }
}
