package dev.hcr.hcf.factions.types.roads;

import dev.hcr.hcf.factions.Faction;
import dev.hcr.hcf.factions.types.SystemFaction;
import org.bson.Document;

import java.util.UUID;

public abstract class RoadFaction extends Faction implements SystemFaction {

    public RoadFaction(String name) {
        super(UUID.randomUUID(), name);
    }

    public RoadFaction(Document document) {
        super(document);
        load(document);
    }
}
