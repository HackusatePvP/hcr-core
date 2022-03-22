package dev.hcr.hcf.factions.types.roads;

import org.bson.Document;
import org.bukkit.ChatColor;

public class WestRoad extends RoadFaction {

    public WestRoad() {
        super("WestRoad");
        setColor(ChatColor.GOLD);
    }

    public WestRoad(Document document) {
        super(document);
    }
}
