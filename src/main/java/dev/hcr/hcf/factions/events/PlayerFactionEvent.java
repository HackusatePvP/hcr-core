package dev.hcr.hcf.factions.events;

import dev.hcr.hcf.factions.Faction;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;

public abstract class PlayerFactionEvent extends Event {
    private final Faction faction;
    private final Player player;

    public PlayerFactionEvent(Faction faction, Player player) {
        this.faction = faction;
        this.player = player;
    }

    public Faction getFaction() {
        return faction;
    }

    public Player getPlayer() {
        return player;
    }

}
