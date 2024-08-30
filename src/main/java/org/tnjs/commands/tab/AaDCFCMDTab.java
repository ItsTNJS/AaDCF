package org.tnjs.commands.tab;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.List;

public class AaDCFCMDTab implements TabCompleter {

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();

        // Example: Provide tab completion for the command "pingperm"
        if (command.getName().equalsIgnoreCase("aadcf")) {
            if (args.length == 1) {
                completions.add("reload");
              //  completions.add("option2");
            } else if (args.length == 2) {
                // Suggest options for the second argument
              //  completions.add("suboption1");
              //  completions.add("suboption2");
            }
        }

        return completions;
    }
}
