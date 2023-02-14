package dev.hcr.hcf.listeners.player;

import dev.hcr.hcf.HCF;
import dev.hcr.hcf.factions.Faction;
import dev.hcr.hcf.factions.claims.Claim;
import dev.hcr.hcf.factions.claims.cuboid.Cuboid;
import dev.hcr.hcf.factions.types.PlayerFaction;
import dev.hcr.hcf.factions.types.SafeZoneFaction;
import dev.hcr.hcf.users.User;
import dev.hcr.hcf.utils.TaskUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.*;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;

public class GlassListener implements Listener {
    private static final Map<String, List<Location>> rendered = new HashMap<>();

    @EventHandler
    public void onPlayerTeleport(PlayerTeleportEvent event) {
        if (event.getCause() == PlayerTeleportEvent.TeleportCause.ENDER_PEARL) {
            handleMove(event.getPlayer(), event.getTo(), event.getFrom(), false);
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerQuit(PlayerQuitEvent event) {
        BukkitTask task = User.getWallBorderTask().remove(event.getPlayer().getUniqueId());
        if (task != null) {
            task.cancel();
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        User.getWallBorderTask().put(player.getUniqueId(), new WarpTimerRunnable(this, player).runTaskTimerAsynchronously(HCF.getPlugin(), 10L, 10L));
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        removeGlass(event.getPlayer());
    }

    @EventHandler
    public void onKick(PlayerKickEvent event) {
        removeGlass(event.getPlayer());
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (User.getWallBorderTask().containsKey(player.getUniqueId())) {
            handleMove(player, player.getLocation(), player.getLocation(), true);
        }
    }

    private void handleMove(Player player, Location to, Location from, boolean forced) {
        if (!forced) {
            if (to.getBlockX() == from.getBlockX() && to.getBlockY() == from.getBlockY() && to.getBlockZ() == from.getBlockZ()) {
                return;
            }
            if (Math.abs(from.getX() - to.getX()) == 0.5 && Math.abs(from.getZ() - to.getZ()) == 0.5) {
                return; // zSpigot's weird movement
            }
            if ((from.getX() - 0.5 == to.getX() && from.getZ() - 0.5 == to.getZ()) || (from.getX() + 0.5 == to.getX() && from.getZ() + 0.5 == to.getZ())) {
                return;
            }
        }

        TaskUtils.runAsync(() -> {
            User user = User.getUser(player.getUniqueId());
            if (user == null) return;
            boolean hasPvpTimer = user.hasActiveTimer("pvp");
            boolean hasCombat = user.hasActiveTimer("combat");
            if (hasCombat || hasPvpTimer) {
                List<Faction> nearby = Faction.getNearByFactions(to, 50);
                if (nearby.isEmpty()) {
                    return;
                }
                
                for (Faction faction : nearby) {
                    if (hasPvpTimer && faction instanceof PlayerFaction) {
                        // If its the players own faction skip
                        if (((PlayerFaction) faction).hasMember(player.getUniqueId())) {
                            continue;
                        }
                        Faction factionTo = Faction.getByLocation(to), factionFrom = Faction.getByLocation(from);
                        TaskUtils.runSync(() -> {
                            if (factionTo != factionFrom && factionTo == faction) {
                                from.setX(from.getBlockX() + 0.5);
                                from.setZ(from.getBlockZ() + 0.5);
                                player.teleport(from);
                            } else if (factionTo == factionFrom && factionTo == faction) {
                                teleportOut(player, factionFrom.getClaims().stream().filter(claim -> claim.getCuboid().isInWithMarge(from, 50)).findAny().orElse(null));
                            }

                            for (Claim claim : faction.getClaims()) {
                                renderGlass(claim, player, to);
                            }
                        });
                        continue;
                    }

                    if (!hasCombat) {
                        continue;
                    }

                    if (!faction.isDeathBan() || faction instanceof SafeZoneFaction) {
                        Faction factionTo = Faction.getByLocation(to), factionFrom = Faction.getByLocation(from);
                        TaskUtils.runSync(() -> {
                            if (factionTo != factionFrom && factionTo == faction) {
                                from.setX(from.getBlockX() + 0.5);
                                from.setZ(from.getBlockZ() + 0.5);
                                player.teleport(from);
                            } else if (factionTo == factionFrom && factionTo == faction) {
                                teleportOut(player, factionFrom.getClaims().stream().filter(claim -> claim.getCuboid().isInWithMarge(from, 50)).findAny().orElse(null));
                            }

                            faction.getClaims().forEach(claim -> {
                                renderGlass(claim, player, to);
                            });
                        });
                    }
                }
            } else {
                TaskUtils.runSync(() -> {
                    removeGlass(player);
                });
            }
        });
    }

    public void teleportOut(Player player, Claim claim) {
        if (claim == null) {
            return;
        }
        Cuboid cuboid = claim.getCuboid();
        Location location = cuboid.getWorld().getHighestBlockAt(new Location(cuboid.getWorld(), cuboid.getMinX() - 1, 0, cuboid.getMinZ() - 1)).getLocation();
        Bukkit.getScheduler().runTaskLater(HCF.getPlugin(), () -> {
            player.teleport(location.add(0, 1, 0));
        }, 1L);
    }

    public static boolean hasGlass(Player player) {
        return rendered.containsKey(player.getUniqueId().toString()) && !rendered.get(player.getUniqueId().toString()).isEmpty();
    }

    public static boolean hasGlass(Player player, Location location) {
        return hasGlass(player) && rendered.get(player.getUniqueId().toString()).contains(location);
    }

    public static void removeGlass(Player player) {
        List<Location> locations = rendered.remove(player.getUniqueId().toString());
        if (locations != null) {
            for (Location location : locations) {
                Block block = location.getBlock();
                player.sendBlockChange(location, block.getType(), block.getData());
            }
        }
    }

    private void renderGlass(Player player, List<Location> locations) {
        if (!rendered.containsKey(player.getUniqueId().toString())) {
            rendered.put(player.getUniqueId().toString(), new ArrayList<>());
        }

        Iterator<Location> it = rendered.get(player.getUniqueId().toString()).iterator();
        while(it.hasNext()) {
            Location location = it.next();
            if (locations.contains(location)) {
                continue;
            }
            if (!(Faction.getByLocation(location) instanceof SafeZoneFaction)) {
                continue;
            }

            Block block = location.getBlock();
            player.sendBlockChange(location, block.getTypeId(), block.getData());
            it.remove();
        }

        locations.forEach(location -> {
            player.sendBlockChange(location, 95, (byte) ((int) 15));
        });
        rendered.get(player.getUniqueId().toString()).addAll(locations);
    }

    private void renderGlass(Claim claim, Player player, Location to) {
        int closerX = closestNumber(to.getBlockX(), claim.getCuboid().getPoint1().getBlockX(), claim.getCuboid().getPoint2().getBlockX());
        int closerZ = closestNumber(to.getBlockZ(), claim.getCuboid().getPoint1().getBlockZ(), claim.getCuboid().getPoint2().getBlockZ());

        boolean updateX = Math.abs(to.getX() - closerX) < 50;
        boolean updateZ = Math.abs(to.getZ() - closerZ) < 50;
        if (!updateX && !updateZ) {
            return;
        }

        List<Location> toUpdate = new LinkedList<>();
        if (updateX) {
            for (int y = -15; y < 15; ++y) {
                for (int x = -20; x < 20; ++x) {
                    Location location;
                    if (isInBetween(claim.getCuboid().getPoint1().getBlockZ(), claim.getCuboid().getPoint2().getBlockZ(), to.getBlockZ() + x)) {
                        if (!toUpdate.contains(location = new Location(to.getWorld(), (double) closerX, (double) (to.getBlockY() + y), (double) (to.getBlockZ() + x)))) {
                            if (location.getBlock().getType() == Material.AIR || !location.getBlock().getType().isSolid()) {
                                toUpdate.add(location);
                            }
                        }
                    }
                }
            }
        }

        if (updateZ) {
            for (int y = -15; y < 15; ++y) {
                for (int x = -20; x < 20; ++x) {
                    Location location;
                    if (isInBetween(claim.getCuboid().getPoint1().getBlockX(), claim.getCuboid().getPoint2().getBlockX(), to.getBlockX() + x) && !toUpdate.contains(location = new Location(to.getWorld(), (double) (to.getBlockX() + x), (double) (to.getBlockY() + y), (double) closerZ))) {
                        if (location.getBlock().getType() == Material.AIR || !location.getBlock().getType().isSolid()) {
                            toUpdate.add(location);
                        }
                    }
                }
            }
        }

        if (!toUpdate.isEmpty()) {
            renderGlass(player, toUpdate);
        }
    }

    private boolean isInBetween(int xone, int xother, int mid) {
        return Math.abs(xone - xother) == Math.abs(mid - xone) + Math.abs(mid - xother);
    }

    private int closestNumber(int from, int... numbers) {
        int distance = Math.abs(numbers[0] - from);
        int idx = 0;

        for (int c = 1; c < numbers.length; ++c) {
            int cdistance = Math.abs(numbers[c] - from);
            if (cdistance < distance) {
                idx = c;
                distance = cdistance;
            }
        }
        return numbers[idx];
    }

    private static class WarpTimerRunnable extends BukkitRunnable {

        private GlassListener listener;
        private Player player;

        private Location lastLocation = new Location(Bukkit.getWorld("world"), 0, 0, 0);

        public WarpTimerRunnable(GlassListener listener, Player player) {
            this.listener = listener;
            this.player = player;
        }

        @Override
        public void run() {
            Location location = player.getLocation();

            // Check if the player moved or is AFK.
            double x = location.getBlockX();
            double y = location.getBlockY();
            double z = location.getBlockZ();
            if (this.lastLocation.getX() == x && this.lastLocation.getY() == y && this.lastLocation.getZ() == z) {
                return;
            }

            this.listener.handleMove(player, location, lastLocation, false);
            this.lastLocation = location;
        }

        @Override
        public synchronized void cancel() throws IllegalStateException {
            super.cancel();
            this.listener = null;
            this.player = null;
        }
    }
}