package dev.hcr.hcf.hooks;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public interface PluginHook {

    boolean isHooked();

    String getRankByPlayer(Player player);

    String getRankName(String name);

    String getRankDisplayName(String name);

    String getPrefix(String name);

    String getSuffix(String name);

    ChatColor getRankColor(String name);
}
