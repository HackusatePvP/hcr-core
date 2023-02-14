package dev.hcr.hcf.factions.types.roads;

import org.bson.Document;
import org.bukkit.ChatColor;

import java.util.Map;

public class SouthRoad extends RoadFaction {

    public SouthRoad() {
        super("SouthRoad");
        setColor(ChatColor.GOLD);
    }

    public SouthRoad(Map<String, Object> map) {
        super(map);
    }

    @Override
    public double getDTRMultiplier() {
        return 1;
    }
}
