package dev.hcr.hcf.users.events;

import dev.hcr.hcf.users.User;
import org.bukkit.event.Event;

public abstract class UserEvent extends Event {
    private final User user;

    public UserEvent(User user) {
        super(true);
        this.user = user;
    }

    public User getUser() {
        return user;
    }
}
