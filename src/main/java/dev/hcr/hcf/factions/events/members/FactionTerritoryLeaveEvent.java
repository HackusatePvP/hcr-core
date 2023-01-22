package dev.hcr.hcf.factions.events.members;

import dev.hcr.hcf.factions.Faction;
import dev.hcr.hcf.factions.events.PlayerFactionEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;

public class FactionTerritoryLeaveEvent extends PlayerFactionEvent {
    private static final HandlerList handlers = new HandlerList();

    public FactionTerritoryLeaveEvent(Faction faction, Player player) {
        super(faction, player);
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
