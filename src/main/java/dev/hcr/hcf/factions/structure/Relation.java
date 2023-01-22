package dev.hcr.hcf.factions.structure;

import dev.hcr.hcf.factions.Faction;
import dev.hcr.hcf.factions.types.SystemFaction;
import dev.hcr.hcf.factions.types.WarzoneFaction;
import dev.hcr.hcf.users.User;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public enum Relation {
    FRIENDLY(ChatColor.GREEN),
    ALLY(ChatColor.BLUE),
    ENEMY(ChatColor.RED);

    private final ChatColor color;
    Relation(ChatColor color) {
        this.color = color;
    }

    public ChatColor getColor() {
        return color;
    }

    public static Relation getFactionRelationship(Faction faction, Player player) {
        if (faction instanceof SystemFaction && !(faction instanceof WarzoneFaction)) {
            return FRIENDLY;
        }
        User user = User.getUser(player.getUniqueId());
        if (user.getFaction() == null) {
            return ENEMY;
        }
        if (user.getFaction().getName().equalsIgnoreCase(faction.getName())) {
            return FRIENDLY;
        }
        return ENEMY;
    }
}
