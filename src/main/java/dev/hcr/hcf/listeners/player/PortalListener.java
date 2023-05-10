package dev.hcr.hcf.listeners.player;

import dev.hcr.hcf.factions.Faction;
import dev.hcr.hcf.factions.types.GlowStoneMountainFaction;
import dev.hcr.hcf.factions.types.SafeZoneFaction;
import dev.hcr.hcf.factions.types.WarzoneFaction;
import dev.hcr.hcf.factions.types.WildernessFaction;
import dev.hcr.hcf.factions.types.roads.RoadFaction;
import dev.hcr.hcf.koths.KothFaction;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

public class PortalListener implements Listener {

    @EventHandler
    public void onNetherPortalTravel(PlayerPortalEvent event) {
        System.out.println("Player using portal");
        Player player = event.getPlayer();
        PlayerTeleportEvent.TeleportCause cause = event.getCause();
        Location from = event.getFrom();
        Location to = event.getTo();
        event.useTravelAgent(false);
        if (cause == PlayerTeleportEvent.TeleportCause.NETHER_PORTAL) {
            System.out.println("Player using nether portal");
            if (from.getWorld().getEnvironment() == World.Environment.NORMAL) {
                to = new Location(to.getWorld(), (from.getX() / 4), from.getY(), (from.getZ() / 4)); //Multiply the coordinates by 8 so they are more spaced out and away from spawn
                to = fixPortalLocation(Faction.getByLocation(to), to); //fixes the portal location if it detects it on a system faction
                event.useTravelAgent(true);
                event.setTo(to);
            }
        } else if (to.getWorld().getEnvironment() == World.Environment.NORMAL) {
            // If player was in nether spawn just teleport the player to spawn
            Faction fromFaction = Faction.getByLocation(from);
            if (fromFaction instanceof SafeZoneFaction) {
                player.teleport(to.getWorld().getSpawnLocation());
                return;
            }
            to = new Location(to.getWorld(), (from.getX() * 4), from.getY(), (from.getZ() * 4));
            if (Faction.getByLocation(from) instanceof SafeZoneFaction) {
                player.teleport(Bukkit.getWorld("world").getSpawnLocation()); // Teleports them to overworld spawn if they are in nether spawn
                return;
            }
            Faction faction = Faction.getByLocation(to);
            if (faction instanceof RoadFaction || faction instanceof KothFaction) {
                to = fixPortalLocation(faction, to);
            }
            event.setTo(to);
        }
    }

    private boolean portalInTerritory(Location location) {
        Faction faction = Faction.getByLocation(location);
        if (faction == null) {
            return false;
        }
        if (faction instanceof SafeZoneFaction || faction instanceof RoadFaction || faction instanceof KothFaction || faction instanceof GlowStoneMountainFaction) {
            return faction.inClaim(location);
        }
        return false;
    }

    public Location fixPortalLocation(Faction faction, Location to) {
        if (faction == null) {
            return to;
        }
        for (int x = 0, z = 0; portalInTerritory(to); x+= 20, z+= 20) { //add 20 coords and keep looping if it's still in a system faction
            to = to.add(x, 1, z); // update the current state of the location
        }
        return to;
    }

}
