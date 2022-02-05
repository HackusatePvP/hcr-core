package dev.hcr.hcf.factions.types;

import dev.hcr.hcf.factions.Faction;
import org.bson.Document;

import java.util.UUID;

public class WarzoneFaction extends Faction implements SystemFaction {

    public WarzoneFaction() {
        super(UUID.randomUUID(), "Warzone");
    }

    public WarzoneFaction(Document document) {
        super(document);
        load(document);
    }
}
