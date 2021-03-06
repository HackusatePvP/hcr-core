package dev.hcr.hcf.factions.events.members;

import dev.hcr.hcf.factions.Faction;
import dev.hcr.hcf.factions.events.PlayerFactionEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;

public class PlayerFactionLeaveEvent extends PlayerFactionEvent implements Cancellable {
    private boolean cancelled = false;
    private static final HandlerList handlers = new HandlerList();

    public PlayerFactionLeaveEvent(Faction faction, Player player) {
        super(faction, player);
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.cancelled = cancel;
    }
}
