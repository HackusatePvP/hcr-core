package dev.hcr.hcf.packets;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;

public class RemoveFactionMapPillarPacketsEvent extends PlayerPacketEvent {
    private static final HandlerList handlers = new HandlerList();

    public RemoveFactionMapPillarPacketsEvent(Player player) {
        super(player);
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
