package dev.hcr.hcf.pvpclass.events;

import dev.hcr.hcf.pvpclass.PvPClass;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;

public class ClassEquippedEvent extends PvPClassEvent implements Cancellable {
    private boolean cancelled = false;
    private static final HandlerList handlers = new HandlerList();

    public ClassEquippedEvent(PvPClass pvPClass, Player player) {
        super(pvPClass, player);
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
