package dev.hcr.hcf.hooks;

import dev.hcr.yuni.ranks.Rank;
import dev.hcr.yuni.users.User;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class YuniHook implements PluginHook {
    private final boolean hooked;

    public YuniHook() {
        this.hooked = Bukkit.getPluginManager().getPlugin("Yuni") != null;
    }

    @Override
    public boolean isHooked() {
        return hooked;
    }

    @Override
    public String getRankByPlayer(Player player) {
        return User.getUser(player.getUniqueId()).getRank().getName();
    }

    @Override
    public String getRankName(String name) {
        return Rank.getRank(name).getName();
    }

    @Override
    public String getRankDisplayName(String name) {
        return Rank.getRank(name).getName();
    }

    @Override
    public String getPrefix(String name) {
        return Rank.getRank(name).getPrefix();
    }

    @Override
    public String getSuffix(String name) {
        return Rank.getRank(name).getSuffix();
    }

    @Override
    public ChatColor getRankColor(String name) {
        return Rank.getRank(name).getColor();
    }
}
