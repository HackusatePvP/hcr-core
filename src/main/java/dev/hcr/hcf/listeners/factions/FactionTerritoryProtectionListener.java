package dev.hcr.hcf.listeners.factions;

import dev.hcr.hcf.factions.Faction;
import dev.hcr.hcf.factions.types.PlayerFaction;
import dev.hcr.hcf.factions.types.SystemFaction;
import dev.hcr.hcf.factions.types.WarzoneFaction;
import dev.hcr.hcf.factions.types.WildernessFaction;
import dev.hcr.hcf.users.User;
import dev.hcr.hcf.utils.backend.ConfigurationType;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class FactionTerritoryProtectionListener implements Listener {

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        Location location = event.getBlock().getLocation();;
        event.setCancelled(preventTerritoryDamage(player, location));
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        Location location = event.getBlock().getLocation();
        event.setCancelled(preventTerritoryDamage(player, location));
    }

    @EventHandler
    public void onInteractEvent(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (event.getClickedBlock() == null) return;
        Location location = event.getClickedBlock().getLocation();
        if (preventTerritoryDamage(player, location)) {
            Faction faction = Faction.getByLocation(location);
            player.sendMessage(ChatColor.RED + "You cannot interact with " + faction.getColor() + faction.getName() + "'s territory.");
        }
        event.setCancelled(preventTerritoryDamage(player, location));
    }

    private boolean preventTerritoryDamage(Entity entity, Location location) {
        if (!(entity instanceof Player)) {
            //  if the "entity" who is damaging the territory is not a player we will prevent it from doing damage
            return true;
        }
        Player player = (Player) entity;
        User user = User.getUser(player.getUniqueId());
        Faction factionAtLocation = Faction.getByLocation(location);
        if (factionAtLocation instanceof SystemFaction) {
            if (user.hasBypass()) return false;
            if (factionAtLocation instanceof WildernessFaction) {
                return false;
            } else if (factionAtLocation instanceof WarzoneFaction) {
                int buildRadius = ConfigurationType.getConfiguration("faction.properties").getInteger("warzone-build-radius");
                System.out.println(location.toString());
                System.out.println("Prevent build: " + (Math.abs(location.getBlockX()) <= buildRadius && Math.abs(location.getBlockZ()) <= buildRadius));
                return Math.abs(location.getBlockX()) <= buildRadius && Math.abs(location.getBlockZ()) <= buildRadius;
            } else {
                return true;
            }
        }
        if (factionAtLocation instanceof PlayerFaction) {
            PlayerFaction targetFaction = (PlayerFaction) factionAtLocation;
            if (user.getFaction() == null) return true;
            if (targetFaction.isRaidable()) {
                return true;
            }
            PlayerFaction playerFaction = (PlayerFaction) user.getFaction();
            return !playerFaction.getName().equals(targetFaction.getName());
        }
        return true;
    }
}
