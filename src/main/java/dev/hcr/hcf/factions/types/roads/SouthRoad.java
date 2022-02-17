package dev.hcr.hcf.factions.types.roads;

import org.bson.Document;
import org.bukkit.ChatColor;

public class SouthRoad extends RoadFaction {

    public SouthRoad() {
        super("South_Road");
        setColor(ChatColor.GOLD);
    }

    public SouthRoad(Document document) {
        super(document);
        load(document);
    }
}
