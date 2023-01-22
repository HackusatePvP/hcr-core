package dev.hcr.hcf.factions.events.coleaders;

import dev.hcr.hcf.factions.Faction;
import dev.hcr.hcf.factions.events.PlayerFactionEvent;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;

public class FactionClaimLandEvent extends PlayerFactionEvent implements Cancellable {
    private boolean cancelled = false;
    private double cost;
    private final Location location1, location2;
    private String denyReason;
    private static final HandlerList handlers = new HandlerList();

    public FactionClaimLandEvent(Faction faction, Player player, Location location1, Location location2) {
        super(faction, player);
        this.location1 = location1;
        this.location2 = location2;
        this.cost = faction.getClaimingLandPrice(location1, location2);
    }

    public Location getLocation1() {
        return location1;
    }

    public Location getLocation2() {
        return location2;
    }

    public double getCost() {
        return cost;
    }

    public void setCost(double cost) {
        this.cost = cost;
    }

    public String getDenyReason() {
        return denyReason;
    }

    public void setDenyReason(String denyReason) {
        this.denyReason = denyReason;
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
