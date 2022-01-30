package dev.hcr.hcf.factions.types;

import dev.hcr.hcf.factions.Faction;
import dev.hcr.hcf.factions.structure.SystemFaction;

import java.util.UUID;

public class SafeZoneFaction extends Faction implements SystemFaction {

    public SafeZoneFaction(String name) {
        super(UUID.randomUUID(), name);
    }
}
