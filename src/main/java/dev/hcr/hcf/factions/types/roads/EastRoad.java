package dev.hcr.hcf.factions.types.roads;

import org.bukkit.ChatColor;

import java.util.Map;

public class EastRoad extends RoadFaction {

    public EastRoad() {
        super("EastRoad");
        setColor(ChatColor.GOLD);
    }

    public EastRoad(Map<String, Object> map) {
        super(map);
    }

    @Override
    public double getDTRMultiplier() {
        return 1;
    }
}
