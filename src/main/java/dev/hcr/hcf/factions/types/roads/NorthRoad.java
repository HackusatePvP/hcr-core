package dev.hcr.hcf.factions.types.roads;

import org.bson.Document;
import org.bukkit.ChatColor;

public class NorthRoad extends RoadFaction {

    public NorthRoad() {
        super("NorthRoad");
        setColor(ChatColor.GOLD);
    }

    public NorthRoad(Document document) {
        super(document);
    }

    @Override
    public double getDTRMultiplier() {
        return 1;
    }
}
