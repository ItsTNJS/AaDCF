package org.tnjs.functions;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

public class pingPerm {
    public static void sendMessageToPlayersWithPermission(String permission, String message) {
        Bukkit.getOnlinePlayers().forEach(player -> {
            if (player.hasPermission(permission)) {
                player.sendMessage(message);
                int volume = 10;
                int pitch = 1;
                player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_LAND, volume, pitch);
            }
        });
    }
}
