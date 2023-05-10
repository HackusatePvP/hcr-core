package dev.hcr.hcf.factions.events.coleaders;

import dev.hcr.hcf.factions.events.FactionEvent;
import dev.hcr.hcf.factions.types.PlayerFaction;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;

public class FactionAllyEvent extends FactionEvent implements Cancellable {
    private boolean cancelled = false;
    private static final HandlerList handlers = new HandlerList();
    private final PlayerFaction ally;

    public FactionAllyEvent(PlayerFaction faction, PlayerFaction ally) {
        super(faction);
        this.ally = ally;
    }

    public PlayerFaction getPlayerFaction() {
        return (PlayerFaction) getFaction();
    }

    public PlayerFaction getAlly() {
        return ally;
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
