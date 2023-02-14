package dev.hcr.hcf.factions.types;

import dev.hcr.hcf.factions.Faction;
import org.bson.Document;
import org.bukkit.ChatColor;

import java.util.Map;
import java.util.UUID;

public class WarzoneFaction extends Faction implements SystemFaction {

    public WarzoneFaction() {
        super(UUID.randomUUID(), "Warzone", true);
        this.setColor(ChatColor.RED);
    }

    public WarzoneFaction(Map<String, Object> map) {
        super(map);
        super.load(map);
    }

    @Override
    public double getDTRMultiplier() {
        return 1;
    }
}
