package dev.hcr.hcf.packets;

import dev.hcr.hcf.factions.claims.cuboid.Cuboid;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;

public class RemoveClaimingPillarPacketsEvent extends PlayerPacketEvent {
    private final Cuboid cuboid;
    private static final HandlerList handlers = new HandlerList();

    public RemoveClaimingPillarPacketsEvent(Player player, Cuboid cuboid) {
        super(player);
        this.cuboid = cuboid;
    }

    public Cuboid getCuboid() {
        return cuboid;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
