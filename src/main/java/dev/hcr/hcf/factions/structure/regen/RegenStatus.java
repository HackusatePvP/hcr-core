package dev.hcr.hcf.factions.structure.regen;

import org.bukkit.ChatColor;

public enum RegenStatus {
    FULL("\u25B6", ChatColor.GREEN),
    REGENERATING("\u2191", ChatColor.YELLOW),
    PAUSED("\u2193", ChatColor.RED);

    private final String unicode;
    private final ChatColor color;
    RegenStatus(String unicode, ChatColor color) {
        this.unicode = unicode;
        this.color = color;
    }

    public String getUnicode() {
        return unicode;
    }

    public ChatColor getColor() {
        return color;
    }
}
