package dev.hcr.hcf.utils;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.util.UUID;

public class PlayerUtils {

    public static String getPlayerNameByUUID(UUID uuid) throws NullPointerException {
        OfflinePlayer player = Bukkit.getOfflinePlayer(uuid);
        if (player == null) {
            throw new NullPointerException();
        }
        return player.getName();
    }
}
