package dev.hcr.hcf.hooks;

import dev.hcr.cove.api.ranks.RankAPI;
import dev.hcr.cove.ranks.Rank;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class CoveHook implements PluginHook {
    private final boolean hooked;

    public CoveHook() {
        this.hooked = Bukkit.getPluginManager().getPlugin("Cove") != null;
    }

    @Override
    public boolean isHooked() {
        return hooked;
    }

    @Override
    public String getRankByPlayer(Player player) {
        Rank rank = RankAPI.getPlayerRank(player);
        if (rank == null) {
            return Rank.getDefaultRank().getName();
        }
        return RankAPI.getPlayerRank(player).getName();
    }

    @Override
    public String getRankName(String name) {
        Rank rank = RankAPI.getRank(name);
        if (rank != null) {
            return rank.getName();
        }
        return "";
    }

    @Override
    public String getRankDisplayName(String name) {
        Rank rank = RankAPI.getRank(name);
        if (rank != null) {
            return rank.getDisplayName();
        }
        return "";
    }

    @Override
    public String getPrefix(String name) {
        Rank rank = RankAPI.getRank(name);
        if (rank != null) {
            return rank.getPrefix();
        }
        return "";
    }

    @Override
    public String getSuffix(String name) {
        Rank rank = RankAPI.getRank(name);
        if (rank != null) {
            return rank.getSuffix();
        }
        return "";
    }

    @Override
    public ChatColor getRankColor(String name) {
        Rank rank = RankAPI.getRank(name);
        if (rank != null) {
            return rank.getColor();
        }
        return ChatColor.WHITE;
    }
}
