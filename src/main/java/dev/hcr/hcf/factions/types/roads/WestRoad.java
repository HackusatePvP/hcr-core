package dev.hcr.hcf.factions.types.roads;

import org.bson.Document;
import org.bukkit.ChatColor;

import java.util.Map;

public class WestRoad extends RoadFaction {

    public WestRoad() {
        super("WestRoad");
        setColor(ChatColor.GOLD);
    }

    public WestRoad(Map<String, Object> map) {
        super(map);
    }

    @Override
    public double getDTRMultiplier() {
        return 1;
    }
}
