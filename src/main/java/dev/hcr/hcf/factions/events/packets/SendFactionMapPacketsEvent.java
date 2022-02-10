package dev.hcr.hcf.factions.events.packets;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;

public class SendFactionMapPacketsEvent extends PlayerPacketEvent {
    private static final HandlerList handlers = new HandlerList();

    public SendFactionMapPacketsEvent(Player player) {
        super(player);
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
