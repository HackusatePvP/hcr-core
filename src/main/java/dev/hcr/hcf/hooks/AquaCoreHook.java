package dev.hcr.hcf.hooks;

import me.activated.core.api.rank.RankData;
import me.activated.core.plugin.AquaCoreAPI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class AquaCoreHook implements PluginHook {
    private final boolean hooked;

    public AquaCoreHook() {
        hooked = Bukkit.getPluginManager().getPlugin("AquaCore") != null;
    }

    @Override
    public boolean isHooked() {
        return hooked;
    }

    @Override
    public String getRankByPlayer(Player player) {
        return AquaCoreAPI.INSTANCE.getPlayerRank(player.getUniqueId()).getName();
    }

    @Override
    public String getRankName(String name) {
        RankData rank = AquaCoreAPI.INSTANCE.getRankByName(name);
        if (rank != null) {
            return rank.getName();
        }
        return "";
    }

    @Override
    public String getRankDisplayName(String name) {
        RankData rank = AquaCoreAPI.INSTANCE.getRankByName(name);
        if (rank != null) {
            return rank.getDisplayName();
        }
        return "";
    }

    @Override
    public String getPrefix(String name) {
        RankData rank = AquaCoreAPI.INSTANCE.getRankByName(name);
        if (rank != null) {
            return rank.getPrefix();
        }
        return "";
    }

    @Override
    public String getSuffix(String name) {
        RankData rank = AquaCoreAPI.INSTANCE.getRankByName(name);
        if (rank != null) {
            return rank.getSuffix();
        }
        return "";
    }

    @Override
    public ChatColor getRankColor(String name) {
        RankData rank = AquaCoreAPI.INSTANCE.getRankByName(name);
        if (rank != null) {
            return rank.getColor();
        }
        return ChatColor.WHITE;
    }


}
