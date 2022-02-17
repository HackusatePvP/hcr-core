package dev.hcr.hcf.timers.events;

import dev.hcr.hcf.timers.Timer;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;

public class TimerExpireEvent extends TimerEvent {
    private Player[] players;
    private static final HandlerList handlers = new HandlerList();

    public TimerExpireEvent(Timer timer) {
        super(timer);
    }

    public TimerExpireEvent(Timer timer, Player... players) {
        super(timer);
        this.players = players;
    }

    /**
     * Gets all players who were affected by the timer, or had the timer active.
     *
     * @return an array of players.
     * @throws NullPointerException if there were no affected players.
     */
    public Player[] getAffected() {
        return players;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

}
