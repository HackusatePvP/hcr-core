package dev.hcr.hcf.factions.types;

import dev.hcr.hcf.factions.Faction;
import org.bson.Document;

import java.util.UUID;

public class WildernessFaction extends Faction implements SystemFaction {

    public WildernessFaction() {
        super(UUID.randomUUID(), "Wilderness");
    }

    public WildernessFaction(Document document) {
        super(document);
        load(document);
    }


}
