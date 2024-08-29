package org.tnjs.AaDCF;
import org.bukkit.command.CommandExecutor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.tnjs.commands.MyCommandExecutor;
import org.tnjs.commands.PingPermCMD;
import org.tnjs.commands.tab.PingPermTab;
import org.tnjs.functions.pingPerm;
import org.tnjs.listener.JoinQuitListener;
import org.tnjs.listener.ChatListener;

public final class Main extends JavaPlugin {
    static Main instance;
    FileConfiguration config = getConfig();

    public Main() {
      instance = this;
    }

    @Override
    public void onEnable() {
        config.options().copyDefaults(true);
        saveConfig();

        registerCommand("test", new MyCommandExecutor());

        registerListener(new JoinQuitListener());
        getServer().getPluginManager().registerEvents(new ChatListener(), this);

        pingPerm ping = new pingPerm();

        registerCommand("pingperm", new PingPermCMD());

        // Register the tab completer
        getCommand("pingperm").setTabCompleter(new PingPermTab());
    }


    @Override
    public void onDisable() {
        getLogger().info("Goodbye!");
    }

    // what i like to do (you dont have to)

    public void registerCommand(String command, CommandExecutor executor) {
        getInstance().getCommand(command).setExecutor(executor);
    }

    public void registerListener(Listener listener) {
        getInstance().getServer().getPluginManager().registerEvents(listener, getInstance());
    }

    public static Main getInstance() {
        return instance;
    }
}



