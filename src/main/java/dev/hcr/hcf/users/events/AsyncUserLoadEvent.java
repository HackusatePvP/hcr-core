package dev.hcr.hcf.users.events;

import dev.hcr.hcf.users.User;
import org.bukkit.event.HandlerList;

/**
 * Called when a user has successfully been loaded. Provides more efficiency firing this event instead of using AsyncPlayerPreLoginEvent with different event priorities.
 */
public class AsyncUserLoadEvent extends UserEvent {
    private final static HandlerList handlers = new HandlerList();

    public AsyncUserLoadEvent(User user) {
        super(user);
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
