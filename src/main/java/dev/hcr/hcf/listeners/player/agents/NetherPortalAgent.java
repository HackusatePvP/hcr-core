package dev.hcr.hcf.listeners.player.agents;

import org.bukkit.Location;
import org.bukkit.TravelAgent;

public class NetherPortalAgent implements TravelAgent {
    private int searchRadius;
    private int creationRadius;
    private boolean canCreatePortal = true;

    @Override
    public TravelAgent setSearchRadius(int radius) {
        this.searchRadius = radius;
        return this;
    }

    @Override
    public int getSearchRadius() {
        return searchRadius;
    }

    @Override
    public TravelAgent setCreationRadius(int radius) {
        creationRadius = radius;
        return this;
    }

    @Override
    public int getCreationRadius() {
        return creationRadius;
    }

    @Override
    public boolean getCanCreatePortal() {
        return canCreatePortal;
    }

    @Override
    public void setCanCreatePortal(boolean create) {
        canCreatePortal = create;
    }

    @Override
    public Location findOrCreate(Location location) {
        return null;
    }

    @Override
    public Location findPortal(Location location) {
        return null;
    }

    @Override
    public boolean createPortal(Location location) {
        return false;
    }
}
