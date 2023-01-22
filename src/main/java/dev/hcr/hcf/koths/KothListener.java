package dev.hcr.hcf.koths;

import dev.hcr.hcf.factions.Faction;
import dev.hcr.hcf.factions.claims.cuboid.Cuboid;
import dev.hcr.hcf.factions.events.members.FactionTerritoryEnterEvent;
import dev.hcr.hcf.utils.backend.types.PropertiesConfiguration;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerTeleportEvent;

public class KothListener {
    private PropertiesConfiguration config = PropertiesConfiguration.getPropertiesConfiguration("hcf.properties");

    @EventHandler
    public void onFactionTerritoryEnterEvent(FactionTerritoryEnterEvent event) {
        Player player = event.getPlayer();
        Faction fac = event.getFaction();
        if (fac instanceof KothFaction) {
            // Player entered the koth fac, maybe change scoreboard or something idk

            // FIXME: 1/22/2023 Move this to a move event
            // See if they are in the capzone.
            KothFaction kothFaction = (KothFaction) fac;
            Location centerLocation = kothFaction.getCenter();
            // Calculate a cuboid given the radius and center.
            // Each corner will be the center z + radius and the center x + center
            // Corner 1 = z + radius, x+ radius (if at 0,0 with a radius of 5 the corners would be 5,5 -5,-5, 5,-5 and -5,5
            Cuboid cuboid = new Cuboid(new Location(centerLocation.getWorld(), centerLocation.getX() + kothFaction.getRadius(), centerLocation.getY(), centerLocation.getZ() + kothFaction.getRadius()),
                    new Location(centerLocation.getWorld(), centerLocation.getX() + -kothFaction.getRadius(), centerLocation.getY(), centerLocation.getBlockZ() + -kothFaction.getRadius()));
            // Check if the player is in the cuboid
            if (cuboid.isIn(player)) {
                // Check if the koth is active
                if (kothFaction.isActive()) {
                    //Check if there is an active capper.
                }
            }
        }
    }

    @EventHandler
    public void onEnderPearl(PlayerTeleportEvent event) {
        Player player = event.getPlayer();
        Faction factionTo = Faction.getByLocation(event.getTo());
        if (factionTo instanceof KothFaction) {
            if (!config.getBoolean("koth-pearl-allowed")) {
                player.sendMessage(ChatColor.RED + "You are not allowed to use enderpeal into a koth faction.");
                event.setCancelled(true);
            }
        } // else if conquest.... else if citadel.... ect ect.....
    }
}
