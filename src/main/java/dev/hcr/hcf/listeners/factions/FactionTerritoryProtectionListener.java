package dev.hcr.hcf.listeners.factions;

import dev.hcr.hcf.factions.Faction;
import dev.hcr.hcf.factions.types.*;
import dev.hcr.hcf.users.User;
import dev.hcr.hcf.utils.backend.ConfigurationType;
import dev.hcr.hcf.utils.backend.types.PropertiesConfiguration;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
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
        if (event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK || (event.getAction() == Action.RIGHT_CLICK_BLOCK && player.getItemInHand() != null && player.getItemInHand().getType() != Material.AIR) || (event.getAction() == Action.RIGHT_CLICK_AIR && player.getItemInHand() != null && player.getItemInHand().getType() != Material.AIR)) {
            if (preventTerritoryDamage(player, location)) {
                Faction faction = Faction.getByLocation(location);
                player.sendMessage(ChatColor.RED + "You cannot interact with " + faction.getColor() + faction.getName() + "'s" + ChatColor.RED + " territory.");
            }
            event.setCancelled(preventTerritoryDamage(player, location));
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onWaterPlace(PlayerBucketEmptyEvent event) {
        Player player = event.getPlayer();
        User user = User.getUser(player.getUniqueId());
        Block block = event.getBlockClicked();
        Location location = block.getLocation();
        // Scan factions nearby the placement of the bucket. Ignore if its in a playerfaction (self).
        Faction faction = Faction.getByLocation(location);
        PlayerFaction playerFaction = (PlayerFaction) user.getFaction();
        if (playerFaction != null) {
            if (faction.getUniqueID() == playerFaction.getUniqueID()) {
                System.out.println("PlayerFaction == LocationFaction");
                // If the player is placing a bucket in there own claim ignore.
                return;
            }
        }
        for (int x = 0, z = 0, i = 0; i < 9; i++) {
            // First scan check the original block placement 0,0;
            if (handleWaterPlacement(event, player, location, playerFaction, x,z)) return;

            // Second scan (x, -z) For example if this is ran for the first time (1,0)
            x++;
            if (handleWaterPlacement(event, player, location, playerFaction, x, z)) return;

            // Start third scan for negative coords. (-1,0)
            x = -x;
            if (handleWaterPlacement(event, player, location, playerFaction, x, z)) return;

            // Start fourth scan for z coordinate grid, subtract the current x to get the last value. For example the fist run would look like (0,1) Second run: (1,2)
            // Fix x so its positive again.
            x = Math.abs(x);
            --x;
            z++;
            if (handleWaterPlacement(event, player, location, playerFaction, x, z)) return;

            // Next handle the negative coordinate (0, -1)
            z = -z;
            if (handleWaterPlacement(event, player, location, playerFaction, x, z)) return;

            // Start the fifth scan for the negative z coordinate. (0,-1)
            if (handleWaterPlacement(event, player, location, playerFaction, x, z)) return;

            // Fix the z so its positive again
            z = Math.abs(z);

            // Start the sixth scan this for for the same coordinate pair. For example (1,1) (2,2) (3,3), (4,4)
            x++;
            if (handleWaterPlacement(event, player, location, playerFaction, x, z)) return;

            // Everything is positive now we need to just count up and down on both coordinate pairs separately.
            // lets start with counting up on the x (X,0) Example (1,0 2,0 3,0 ect...)
            int zZ = 0;
            int xZ = 0;
            if (handleWaterPlacement(event, player, location, playerFaction, x, zZ)) return;

            // Now go up on the z (0,x)
            if (handleWaterPlacement(event, player, location, playerFaction, xZ, z)) return;

            // Next go down with x (x,0)
            if (handleWaterPlacement(event, player, location, playerFaction, -x, zZ)) return;

            // Next go down with z (0, -z)
            if (handleWaterPlacement(event, player, location, playerFaction, xZ, -z)) return;

            // Next go down with x (-x, 0)
            x = -x;
            if (handleWaterPlacement(event, player, location, playerFaction, x, z)) return;

            x = Math.abs(x);
            z = -z;
            if (handleWaterPlacement(event, player, location, playerFaction, x, z)) return;

            // Start the last scan negative for both pairs. (-1,-1)
            x = -x;
            if (handleWaterPlacement(event, player, location, playerFaction, x, z)) return;

            // For maximum pain reset the coordinates so they are positive again.
            x = Math.abs(x);
            z = Math.abs(z);
         }
    }

    private boolean handleWaterPlacement(PlayerBucketEmptyEvent event, Player player, Location location, PlayerFaction playerFaction, int x, int z) {
        System.out.println("Scanning: (" + x + "," + z + ")");
        Faction scannedFaction;
        scannedFaction = Faction.getByLocation(location.add(x, 0, z));
        if (scannedFaction != null && scannedFaction.getUniqueID() != playerFaction.getUniqueID() && scannedFaction instanceof PlayerFaction) {
            System.out.println("Faction found: " + scannedFaction.getName());
            player.sendMessage(ChatColor.RED + "You cannot place buckets here. This action would impact other faction territory. Be sure to place buckets 16 blocks away from faction.");
            event.setCancelled(true);
            return true;
        }
        return false;
    }

    private boolean preventTerritoryDamage(Entity entity, Location location) {
        if (!(entity instanceof Player)) {
            //  if the "entity" who is damaging the territory is not a player we will prevent it from doing damage
            return true;
        }
        Player player = (Player) entity;
        User user = User.getUser(player.getUniqueId());
        Faction factionAtLocation = Faction.getByLocation(location);
        boolean toReturn;
        if (user.hasBypass()) return false;
        if (factionAtLocation instanceof SystemFaction) {
            if (factionAtLocation instanceof WildernessFaction) {
                toReturn = false;
            } else if (factionAtLocation instanceof WarzoneFaction) {
                if (location.getBlock().getType() == Material.LONG_GRASS) {
                    toReturn = false;
                } else {
                    int buildRadius = PropertiesConfiguration.getPropertiesConfiguration("faction.properties").getInteger("warzone-build-radius");
                    System.out.println(location);
                    System.out.println("Prevent build: " + (Math.abs(location.getBlockX()) <= buildRadius && Math.abs(location.getBlockZ()) <= buildRadius));
                    toReturn = Math.abs(location.getBlockX()) <= buildRadius && Math.abs(location.getBlockZ()) <= buildRadius;
                }
            } else if (factionAtLocation instanceof GlowStoneMountainFaction) {
                Block block = location.getBlock();
                toReturn = block.getType() != Material.GLOWSTONE;
            } else {
                toReturn = true;
            }
            return toReturn;
        }
        if (factionAtLocation instanceof PlayerFaction) {
            PlayerFaction targetFaction = (PlayerFaction) factionAtLocation;
            if (user.getFaction() == null) return true;
            if (targetFaction.isRaidable()) {
                return false;
            }
            PlayerFaction playerFaction = (PlayerFaction) user.getFaction();
            return !playerFaction.getName().equals(targetFaction.getName());
        }
        return true;
    }
}
