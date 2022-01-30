package dev.hcr.hcf.utils;

import org.bukkit.ChatColor;

public class CC {

    public static String translate(String line) {
        return ChatColor.translateAlternateColorCodes('&', line);
    }
}
