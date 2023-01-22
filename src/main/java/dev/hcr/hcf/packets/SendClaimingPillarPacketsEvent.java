package dev.hcr.hcf.packets;

import dev.hcr.hcf.factions.claims.cuboid.Cuboid;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;

public class SendClaimingPillarPacketsEvent extends PlayerPacketEvent {
    private final Cuboid cuboid;
    private final Location point1, point2;
    private static final HandlerList handlers = new HandlerList();

    public SendClaimingPillarPacketsEvent(Player player, Cuboid cuboid) {
        super(player);
        this.cuboid = cuboid;
        this.point1 = cuboid.getPoint1();
        this.point2 = cuboid.getPoint2();
    }

    public Cuboid getCuboid() {
        return cuboid;
    }

    public Location getPoint1() {
        return point1;
    }

    public Location getPoint2() {
        return point2;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
