package dev.hcr.hcf.listeners.player;

import dev.hcr.hcf.factions.Faction;
import dev.hcr.hcf.factions.claims.Claim;
import dev.hcr.hcf.factions.claims.cuboid.Cuboid;
import dev.hcr.hcf.factions.events.packets.RemoveClaimingPillarPacketsEvent;
import dev.hcr.hcf.factions.events.packets.RemoveFactionMapPillarPacketsEvent;
import dev.hcr.hcf.factions.events.packets.SendClaimingPillarPacketsEvent;
import dev.hcr.hcf.factions.events.packets.SendFactionMapPacketsEvent;
import dev.hcr.hcf.users.User;
import dev.hcr.hcf.utils.TaskUtils;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PlayerPacketListener implements Listener {
    private final Map<Player, Integer> playerXMoveTracker = new HashMap<>();
    private final Map<Player, Integer> playerZMoveTracker = new HashMap<>();

    @EventHandler
    public void onSendClaimingPillars(SendClaimingPillarPacketsEvent event) {
        Player player = event.getPlayer();
        Cuboid cuboid = event.getCuboid();
        // TODO: 2/9/2022 Currently making a deprecation solution with PacketControllers
        for (int corner = 0; corner < 4; corner++) {
            Location location = cuboid.getCorner(corner);
            for (int y = 0; y < 256; y++) {
                location.setY(y);
                player.sendBlockChange(location, getPillar(corner), (byte) 0);
            }
        }
    }

    private Material getPillar(int corner) {
        switch (corner) {
            case 1:
                return Material.DIAMOND_BLOCK;
            case 2:
                return Material.EMERALD_BLOCK;
            case 3:
                return Material.GOLD_BLOCK;
            default:
                return Material.LAPIS_BLOCK;
        }
    }

    @EventHandler
    public void onRemoveClaimingPillars(RemoveClaimingPillarPacketsEvent event) {
        Player player = event.getPlayer();
        Cuboid cuboid = event.getCuboid();
        for (int corner = 1; corner < 5; corner++) {
            Location location = cuboid.getCorner(corner);
            System.out.println("Corner " + corner + ": " + location.toString());
            for (int y = 0; y < 256; y++) {
                player.sendBlockChange(location, location.getBlock().getType(), location.getBlock().getData());
                location.add(0, 1, 0);
            }
        }
    }

    @EventHandler
    public void sendFactionsMapData(SendFactionMapPacketsEvent event) {
        Player player = event.getPlayer();
        Location location = player.getLocation();
        player.sendMessage(ChatColor.GREEN + "Creating pillars...");
        TaskUtils.runAsync(() -> {
            updateMapData(player, location, false);
        });
    }

    @EventHandler
    public void onRemoveFactionMapData(RemoveFactionMapPillarPacketsEvent event) {
        Player player = event.getPlayer();
        User user = User.getUser(player.getUniqueId());
        user.setFactionMap(false);
        updateMapData(player, player.getLocation(), true);
    }

    private void updateMapData(Player player, Location location, boolean remove) {
        List<Faction> factionsNearby = Faction.getNearByFactions(location);
        if (factionsNearby.isEmpty()) {
            player.sendMessage(ChatColor.RED + "No claims found near you.");
            return;
        }
        for (Faction faction : factionsNearby) {
            System.out.println("Faction found! " + faction.getName());
            Claim claim = faction.getClaims().stream().findFirst().orElse(faction.getClaims().stream().findFirst().orElse(null));
            if (claim == null) continue;
            System.out.println("Claims found!");
            for (int corner = 0; corner < 4; corner++) {
                System.out.println("Corner found: " + corner);
                Location location1 = claim.getCuboid().getCorner(corner);
                for (int y = 0; y < 256; y++) {
                    location1.setY(y);
                    if (remove) {
                        System.out.println("Removing pillar at " + location1.toString());
                        player.sendBlockChange(location1, location1.getBlock().getType(), location1.getBlock().getData());
                        location1.add(0, 1, 0);
                    } else {
                        System.out.println("Adding pillar at " + location1.toString());
                        player.sendBlockChange(location1, Material.EMERALD_BLOCK, (byte) 0);
                    }
                }
            }
        }
        playerXMoveTracker.put(player, location.getBlockX());
        playerZMoveTracker.put(player, location.getBlockZ());
    }

    @EventHandler
    public void updateFactionMapData(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        User user = User.getUser(player.getUniqueId());
        if (user.hasFactionMap()) {
            Location to = event.getTo();
            Location from = event.getFrom();
            if (to.getBlockX() != from.getBlockX() || to.getBlockZ() != from.getBlockZ()) {
                int xDifference = to.getBlockX() - playerXMoveTracker.get(player);
                int zDifference = to.getBlockZ() - playerZMoveTracker.get(player);
                if (xDifference > 25 || xDifference < -25 || zDifference > 25 || zDifference < -25) {
                    TaskUtils.runAsync(() -> updateMapData(player, to, false));
                }
            }
        }
    }
}
