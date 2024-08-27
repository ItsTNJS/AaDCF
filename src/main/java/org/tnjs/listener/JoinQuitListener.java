package org.tnjs.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class JoinQuitListener implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        if(event.getPlayer().hasPermission("aadcf.join.premium")) {
            event.getPlayer().sendMessage("Why u join this shit?");
        }
    }
}
