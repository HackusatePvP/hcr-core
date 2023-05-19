package dev.hcr.hcf.scoreboard.events;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

public class PlayerInvisibilityEvent extends PlayerEvent {
    private static final HandlerList handlers = new HandlerList();

    public PlayerInvisibilityEvent(Player who) {
        super(who);
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

}
