package dev.hcr.hcf.factions.types.roads;

import dev.hcr.hcf.factions.Faction;
import dev.hcr.hcf.factions.types.SystemFaction;
import org.bson.Document;

import java.util.Map;
import java.util.UUID;

public abstract class RoadFaction extends Faction implements SystemFaction {
    private final String name;

    public RoadFaction(String name) {
        super(UUID.randomUUID(), name, true);
        this.name = name;
    }

    public RoadFaction(Map<String, Object> map) {
        super(map);
        this.name = (String) map.get("name");
    }

    @Override
    public String getName() {
        return name;
    }

    public String getDisplayName() {
        return name.replaceAll("(?!^)([A-Z])", " $1");
    }

    public String getDirection() {
        return name.replace("Road", "");
    }
}
