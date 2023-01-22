package dev.hcr.hcf.listeners.factions.data;

import dev.hcr.hcf.factions.Faction;
import dev.hcr.hcf.users.User;

/**
 * Used to store data pertaining to claiming for a faction.
 */
public class FactionClaimingData {
    private final User user;
    private final Faction faction;
    private final boolean bypassing;

    public FactionClaimingData(User user, Faction faction, boolean bypassing) {
        this.user = user;
        this.faction = faction;
        this.bypassing = bypassing;
    }

    public User getUser() {
        return user;
    }

    public Faction getFaction() {
        return faction;
    }

    public boolean isBypassing() {
        return bypassing;
    }
}
