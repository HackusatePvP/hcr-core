package dev.hcr.hcf.pvpclass.events.archer;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class ArcherTagPlayerEvent extends Event implements Cancellable {
    private final Player archer, tagged;
    private boolean cancelled = false;
    private static final HandlerList handlers = new HandlerList();

    public ArcherTagPlayerEvent(Player archer, Player tagged) {
        this.archer = archer;
        this.tagged = tagged;
    }

    public Player getArcher() {
        return archer;
    }

    public Player getTagged() {
        return tagged;
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
