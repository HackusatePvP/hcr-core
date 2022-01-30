package dev.hcr.hcf.factions.events;

import dev.hcr.hcf.factions.Faction;
import org.bukkit.event.Event;

public abstract class FactionEvent extends Event {
    private final Faction faction;

    public FactionEvent(Faction faction) {
        this.faction = faction;
    }

    public Faction getFaction() {
        return faction;
    }
}
