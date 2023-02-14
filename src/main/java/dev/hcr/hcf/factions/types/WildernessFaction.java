package dev.hcr.hcf.factions.types;

import dev.hcr.hcf.factions.Faction;
import org.bson.Document;
import org.bukkit.ChatColor;

import java.util.Map;
import java.util.UUID;

public class WildernessFaction extends Faction implements SystemFaction {

    public WildernessFaction() {
        super(UUID.randomUUID(), "Wilderness", true);
        this.setColor(ChatColor.DARK_GREEN);
    }

    public WildernessFaction(Map<String, Object> map) {
        super(map);
        super.load(map);
    }

    @Override
    public double getDTRMultiplier() {
        return 1;
    }


}
