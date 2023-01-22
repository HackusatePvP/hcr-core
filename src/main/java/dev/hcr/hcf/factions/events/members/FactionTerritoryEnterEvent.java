package dev.hcr.hcf.factions.events.members;

import dev.hcr.hcf.factions.Faction;
import dev.hcr.hcf.factions.events.PlayerFactionEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;

public class FactionTerritoryEnterEvent extends PlayerFactionEvent {
    private static final HandlerList handlers = new HandlerList();

    public FactionTerritoryEnterEvent(Faction faction, Player player) {
        super(faction, player);
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

}
