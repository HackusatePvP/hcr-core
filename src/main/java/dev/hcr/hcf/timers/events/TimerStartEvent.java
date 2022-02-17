package dev.hcr.hcf.timers.events;

import dev.hcr.hcf.timers.Timer;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;

public class TimerStartEvent extends TimerEvent implements Cancellable {
    private boolean cancelled = false;
    private static final HandlerList handlers = new HandlerList();

    public TimerStartEvent(Timer timer) {
        super(timer);
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
