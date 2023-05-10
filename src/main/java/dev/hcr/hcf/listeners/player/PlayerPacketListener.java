package dev.hcr.hcf.listeners.player;

import dev.hcr.hcf.factions.Faction;
import dev.hcr.hcf.factions.claims.Claim;
import dev.hcr.hcf.factions.claims.cuboid.Cuboid;
import dev.hcr.hcf.factions.events.coleaders.FactionClaimLandEvent;
import dev.hcr.hcf.factions.types.PlayerFaction;
import dev.hcr.hcf.factions.types.SafeZoneFaction;
import dev.hcr.hcf.factions.types.WarzoneFaction;
import dev.hcr.hcf.factions.types.WildernessFaction;
import dev.hcr.hcf.packets.*;
import dev.hcr.hcf.users.User;
import dev.hcr.hcf.utils.TaskUtils;
import dev.hcr.hcf.utils.client.lunar.LunarClientPlayerPacket;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.util.BlockIterator;
import org.bukkit.util.Vector;

import java.util.*;

public class PlayerPacketListener implements Listener {
    private final Map<Player, Integer> playerXMoveTracker = new HashMap<>();
    private final Map<Player, Integer> playerZMoveTracker = new HashMap<>();

    @EventHandler
    public void onSendClaimingPillars(SendClaimingPillarPacketsEvent event) {
        Player player = event.getPlayer();
        Cuboid cuboid = event.getCuboid();
        // BUG FIX: Corners bug out when claiming, this is caused by both RemoveClaimingPillarsEvent and SendClaimingPillarsEvent being called at the same time.
        for (int corner = 1; corner < 5; corner++) {
            player.sendMessage("Calculating corner: " + corner);
            Location location = cuboid.getCorner(corner);
            player.sendMessage("Corner " + corner + ": " + location.getBlockX() + "," + location.getBlockZ());
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
            case 4:
                return Material.LAPIS_BLOCK;
        }
        return Material.DIRT;
    }

    @EventHandler
    public void onRemoveClaimingPillars(RemoveClaimingPillarPacketsEvent event) {
        Player player = event.getPlayer();
        Cuboid cuboid = event.getCuboid();
        for (int corner = 0; corner < 4; corner++) {
            ArrayList<Location> corners = new ArrayList<>(Arrays.asList(cuboid.getCorner(1), cuboid.getCorner(2), cuboid.getCorner(3), cuboid.getCorner(4)));
            for (Location location1 : corners) {
                for (int y = 0; y < 256; y++) {
                    location1.setY(y);
                    player.sendBlockChange(location1, location1.getBlock().getType(), location1.getBlock().getData());
                    location1.add(0, 1, 0);
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onFactionClaim(FactionClaimLandEvent event) {
        if (event.isCancelled()) {
            return;
        }
        Player player = event.getPlayer();
        Cuboid cuboid = new Cuboid(event.getLocation1(), event.getLocation2());
        TaskUtils.runAsync(() -> {
            Bukkit.getPluginManager().callEvent(new RemoveClaimingPillarPacketsEvent(player, cuboid));
        });
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
        List<Faction> factionsNearby = Faction.getNearByFactions(location, (remove ? 50 : 25));
        if (factionsNearby.isEmpty()) {
            player.sendMessage(ChatColor.RED + "No claims found near you.");
            return;
        }
        Map<Integer, Material> materialMap = generateMaterialRandomList();
        for (Faction faction : factionsNearby) {
            Random rand = new Random();
            int random = rand.nextInt(10);
            Claim claim = faction.getClaims().stream().findFirst().orElse(faction.getClaims().stream().findFirst().orElse(null));
            if (claim == null) continue;
            for (int corner = 1; corner < 5; corner++) {
                Location location1 = claim.getCuboid().getCorner(corner);
                for (int y = 0; y < 256; y++) {
                    location1.setY(y);
                    if (remove) {
                        player.sendBlockChange(location1, location1.getBlock().getType(), location1.getBlock().getData());
                        location1.add(0, 1, 0);
                    } else {
                        if (faction instanceof PlayerFaction) {
                            PlayerFaction playerFaction = (PlayerFaction) faction;
                            if (playerFaction.hasMember(player.getUniqueId())) {
                                // Make blocks glass and random material every 10 blocks
                                player.sendBlockChange(location1, Material.EMERALD_BLOCK, (byte) 0);
                            } else {
                                if (location.getBlockY() % 10 == 0) {
                                    player.sendBlockChange(location1, materialMap.get(random), (byte) 0);
                                    player.sendMessage(ChatColor.RED + "Displaying " + playerFaction.getDisplayName() + " as " + materialMap.get(random).name());
                                } else {
                                    player.sendBlockChange(location1, Material.GLASS, (byte) 0);
                                }
                            }
                        } else if (faction instanceof SafeZoneFaction) {
                            player.sendBlockChange(location1, Material.EMERALD_BLOCK, (byte) 0);
                        } else {
                            player.sendBlockChange(location1, Material.REDSTONE_BLOCK, (byte) 0);
                        }
                    }
                }
            }
        }
        playerXMoveTracker.put(player, location.getBlockX());
        playerZMoveTracker.put(player, location.getBlockZ());
    }

    private Map<Integer, Material> generateMaterialRandomList() {
        Map<Integer, Material> random = new HashMap<>();
        random.put(0, Material.DIRT);
        random.put(1, Material.GLOWSTONE);
        random.put(2, Material.EMERALD_BLOCK);
        random.put(3, Material.DIAMOND_BLOCK);
        random.put(4, Material.GOLD_BLOCK);
        random.put(5, Material.IRON_BLOCK);
        random.put(6, Material.REDSTONE_BLOCK);
        random.put(7, Material.LAPIS_BLOCK);
        random.put(8, Material.COAL_BLOCK);
        random.put(9, Material.COBBLESTONE);
        random.put(10, Material.BEDROCK);
        return random;
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
                    TaskUtils.runAsync(() -> {
                        updateMapData(player, from, true);
                        updateMapData(player, to, false);
                    });
                }
            }
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
       LunarClientPlayerPacket packet = new LunarClientPlayerPacket(player);
       packet.sendWaypoint("Spawn", 5, new Location(Bukkit.getWorld("world"), 0, 70 ,0), true);
    }

}
