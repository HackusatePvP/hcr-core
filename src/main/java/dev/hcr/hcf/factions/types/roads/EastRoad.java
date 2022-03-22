package dev.hcr.hcf.factions.types.roads;

import org.bson.Document;
import org.bukkit.ChatColor;

public class EastRoad extends RoadFaction {

    public EastRoad() {
        super("EastRoad");
        setColor(ChatColor.GOLD);
    }

    public EastRoad(Document document) {
        super(document);
    }
}
