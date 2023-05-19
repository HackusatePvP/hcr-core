package dev.hcr.hcf.scoreboard.events;

import dev.hcr.hcf.users.User;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.scoreboard.Scoreboard;

public class UserTeamLoadEvent extends Event {
    private final User user;
    private final Scoreboard scoreboard;
    private static final HandlerList handlers = new HandlerList();

    public UserTeamLoadEvent(User user, Scoreboard scoreboard) {
        this.user = user;
        this.scoreboard = scoreboard;
    }

    public User getUser() {
        return user;
    }

    public Scoreboard getScoreboard() {
        return scoreboard;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}