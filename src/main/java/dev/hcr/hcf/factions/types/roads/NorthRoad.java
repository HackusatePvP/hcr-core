package dev.hcr.hcf.factions.types.roads;

import org.bson.Document;
import org.bukkit.ChatColor;

import java.util.Map;

public class NorthRoad extends RoadFaction {

    public NorthRoad() {
        super("NorthRoad");
        setColor(ChatColor.GOLD);
    }

    public NorthRoad(Map<String, Object> map) {
        super(map);
    }

    @Override
    public double getDTRMultiplier() {
        return 1;
    }
}
