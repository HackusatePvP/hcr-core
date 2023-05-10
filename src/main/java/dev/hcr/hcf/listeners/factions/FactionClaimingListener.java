package dev.hcr.hcf.listeners.factions;

import dev.hcr.hcf.HCF;
import dev.hcr.hcf.factions.Faction;
import dev.hcr.hcf.factions.claims.Claim;
import dev.hcr.hcf.factions.claims.cuboid.Cuboid;
import dev.hcr.hcf.factions.events.coleaders.FactionClaimLandEvent;
import dev.hcr.hcf.packets.RemoveClaimingPillarPacketsEvent;
import dev.hcr.hcf.packets.SendClaimingPillarPacketsEvent;
import dev.hcr.hcf.packets.SendFactionMapPacketsEvent;
import dev.hcr.hcf.factions.types.PlayerFaction;
import dev.hcr.hcf.factions.types.SystemFaction;
import dev.hcr.hcf.factions.types.WildernessFaction;
import dev.hcr.hcf.listeners.factions.data.FactionClaimingData;
import dev.hcr.hcf.users.User;
import dev.hcr.hcf.utils.CC;
import dev.hcr.hcf.utils.TaskUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class FactionClaimingListener implements Listener {
    private static ItemStack claimingWand;
    public static final Map<User, FactionClaimingData> claiming = new HashMap<>();
    private static final Map<User, Location> position1ClaimingMap = new HashMap<>();
    private static final Map<User, Location> position2ClaimingMap = new HashMap<>();

    public FactionClaimingListener() {
        claimingWand = new ItemStack(Material.GOLD_HOE);
        ItemMeta meta = claimingWand.getItemMeta();
        meta.setDisplayName(CC.translate("&6&lClaiming Wand"));
        List<String> lore = new ArrayList<>();
        lore.add("&7&m-----------------------------------------");
        lore.add("&7Right-Click - &eTo set position 1.");
        lore.add("&7Left-Click: - &eTo set position 2.");
        lore.add("");
        lore.add("&6While Sneaking: ");
        lore.add("&7Right-Click-Air - &eTo clear claiming selection.");
        lore.add("&7Left-Click-Air: - &eTo finalize claiming.");
        lore.add("&7&m-----------------------------------------");
        meta.setLore(CC.translate(lore));
        claimingWand.setItemMeta(meta);
    }

    @EventHandler
    public void onPlayerInteractWithWand(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        User user = User.getUser(player.getUniqueId());
        if (!claiming.containsKey(user)) return;
        Faction faction = claiming.get(user).getFaction();
        ItemStack hand = player.getItemInHand();
        if (hand == null) return;
        if (hand.getType() == Material.GOLD_HOE && hand.hasItemMeta() && hand.getItemMeta().getDisplayName().equals(getClaimingWand().getItemMeta().getDisplayName())) {
            event.setUseItemInHand(Event.Result.DENY);
            event.setUseInteractedBlock(Event.Result.DENY);
            if (event.getAction() == Action.LEFT_CLICK_BLOCK) {
                Location location = event.getClickedBlock().getLocation();
                if (position1ClaimingMap.containsKey(user)) {
                    handlePacketEvents(user, faction, false, false);
                }
                position1ClaimingMap.put(user, location);
                player.sendMessage(CC.translate("&7Set position 1 at (&c" + location.getBlockX() + "," + location.getBlockZ() + "&7)"));
                handlePacketEvents(user, faction, true, true);
            }
            if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
                Location location = event.getClickedBlock().getLocation();
                if (position2ClaimingMap.containsKey(user)) {
                    handlePacketEvents(user, faction, false, false);
                }
                position2ClaimingMap.put(user, location);
                player.sendMessage(CC.translate("&7Set position 2 at (&c" + location.getBlockX() + "," + location.getBlockZ() + "&7)"));
                handlePacketEvents(user, faction, true, true);
            }
            if (player.isSneaking()) {
                if (event.getAction() == Action.LEFT_CLICK_AIR) {
                    player.sendMessage(ChatColor.YELLOW + "Attempting to claim land...");
                    FactionClaimLandEvent claimLandEvent = new FactionClaimLandEvent(faction, player, position1ClaimingMap.get(user), position2ClaimingMap.get(user));
                    Bukkit.getPluginManager().callEvent(claimLandEvent);
                    if (claimLandEvent.isCancelled()) {
                        player.sendMessage(ChatColor.RED + "Unable to claim land!");
                        return;
                    }
                    faction.addClaim(new Cuboid(position1ClaimingMap.get(user), position2ClaimingMap.get(user)));
                    player.sendMessage(ChatColor.GREEN + "Successfully claimed land!");
                    handlePacketEvents(user, faction, false, false);
                    player.getInventory().remove(player.getItemInHand());
                    claiming.remove(user);
                    position1ClaimingMap.remove(user);
                    position2ClaimingMap.remove(user);
                }
                if (event.getAction() == Action.RIGHT_CLICK_AIR) {
                    player.sendMessage(ChatColor.RED + "Removing claiming selection...");
                    if (position1ClaimingMap.containsKey(user) && position2ClaimingMap.containsKey(user)) {
                        handlePacketEvents(user, faction, false, false);
                    }
                    position1ClaimingMap.remove(user);
                    position2ClaimingMap.remove(user);
                    claiming.remove(user);
                    position1ClaimingMap.remove(user);
                    position2ClaimingMap.remove(user);
                    player.setItemInHand(new ItemStack(Material.AIR));
                }
            }
        }
     }

    private void handlePacketEvents(User user, Faction faction, boolean add, boolean refresh) {
        Player player = user.toPlayer();
        if (position1ClaimingMap.containsKey(user) && position2ClaimingMap.containsKey(user)) {
            SendClaimingPillarPacketsEvent sendClaimingPillarPacketsEvent = new SendClaimingPillarPacketsEvent(player, new Cuboid(position1ClaimingMap.get(user), position2ClaimingMap.get(user)));
            RemoveClaimingPillarPacketsEvent removeClaimingPillarPacketsEvent = new RemoveClaimingPillarPacketsEvent(player, new Cuboid(position1ClaimingMap.get(user), position2ClaimingMap.get(user)));

            TaskUtils.runAsync(() -> {
                if (add) {
                    if (position1ClaimingMap.containsKey(user) && position2ClaimingMap.containsKey(user)) {
                        Bukkit.getPluginManager().callEvent(sendClaimingPillarPacketsEvent);
                        player.sendMessage(CC.translate("&7The land will cost you &c" + HCF.getPlugin().getFormat().format(faction.getClaimingLandPrice(position1ClaimingMap.get(user), position2ClaimingMap.get(user)))));
                    }
                } else {
                    Bukkit.getPluginManager().callEvent(removeClaimingPillarPacketsEvent);
                }
            });

            if (refresh) {
                TaskUtils.runAsync(() -> {
                    Bukkit.getPluginManager().callEvent(removeClaimingPillarPacketsEvent);
                });
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        Bukkit.getPluginManager().callEvent(sendClaimingPillarPacketsEvent);
                    }
                }.runTaskLaterAsynchronously(HCF.getPlugin(), 10L);
            }
        }
    }

    @EventHandler
     public void onClaimLand(FactionClaimLandEvent event) {
        Player player = event.getPlayer();
        User user = User.getUser(player.getUniqueId());
        Faction faction = event.getFaction();
        Faction factionLocation1 = Faction.getByLocation(event.getLocation1());
        Faction factionLocation2 = Faction.getByLocation(event.getLocation2());

        if (faction instanceof PlayerFaction) {
            PlayerFaction playerFaction = (PlayerFaction) faction;
            if (!(factionLocation1 instanceof WildernessFaction || factionLocation2 instanceof WildernessFaction)) {
                player.sendMessage(ChatColor.RED + "You can only claim in wilderness.");
                event.setCancelled(true);
                return;
            }

            // Check to see if a faction territory already exists
            for (Faction nearbyFaction : Faction.getNearByFactions(player.getLocation(), 100)) {
                // This check maybe intense and could have a significant performance impact
                for (Claim claim : nearbyFaction.getClaims()) {
                    if (claim.getCuboid().isInWithMarge(event.getLocation1(), (event.getLocation1().distance(event.getLocation2())))) {
                        player.sendMessage(ChatColor.RED + "You cannot claim over an existing territory.");
                        event.setCancelled(true);
                        return;
                    }
                }
            }

            // If the faction was forcefully claimed skip the balance check
            FactionClaimingData data = claiming.get(user);
            double cost = 0D;
            if (claiming.containsKey(user) && data != null && !data.isBypassing()) {
                cost = event.getCost();
                if (cost > playerFaction.getBalance()) {
                    player.sendMessage(CC.translate("&cYour faction cannot afford this land. &7(/f deposit all)"));
                    event.setCancelled(true);
                    return;
                }
            }
            // calc claim size
            int xAxis = (Math.max(event.getLocation1().getBlockX(), event.getLocation2().getBlockX()) - Math.min(event.getLocation1().getBlockX(), event.getLocation2().getBlockX()) + 1);
            int zAxis = (Math.max(event.getLocation1().getBlockZ(), event.getLocation2().getBlockZ()) - Math.min(event.getLocation1().getBlockZ(), event.getLocation2().getBlockZ()) + 1);
            if (xAxis < 5 || zAxis < 5) {
                player.sendMessage(ChatColor.RED + "Claim size must be at least 5x5.");
                event.setCancelled(true);
            }
            playerFaction.removeFromBalance(cost);
        }
    }

    private boolean overlapsWithFaction(Cuboid cuboid) {
        for (int x = 0, z = 0; x < cuboid.getXWidth(); x++, z++) {

        }
        return false;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onFactionClaimLand(FactionClaimLandEvent event) {
        // This event is used for fixing a bug.
        // If a player has faction map enabled but if the claim changes (usually forcefully) the map pillars do not go away.
        // Get the faction that just claimed and check for surrounding players who have f map on. And reset the pillars.
        for (Entity entity : event.getLocation1().getWorld().getNearbyEntities(event.getLocation1(), event.getLocation1().getX(), event.getLocation1().getY(), event.getLocation1().getZ())) {
            if (!(event instanceof Player)) continue;
            Player player = (Player) entity;
            User user = User.getUser(player.getUniqueId());
            if (!user.hasFactionMap()) continue;
            Bukkit.getPluginManager().callEvent(new RemoveClaimingPillarPacketsEvent(player, new Cuboid(event.getLocation1(), event.getLocation2())));
            Bukkit.getPluginManager().callEvent(new SendFactionMapPacketsEvent(player));
        }
    }

     @EventHandler
     public void onKick(PlayerKickEvent event) {
         Player player = event.getPlayer();
         User user = User.getUser(player.getUniqueId());
         removeWand(player.getInventory());
         claiming.remove(user);
         position1ClaimingMap.remove(user);
         position2ClaimingMap.remove(user);
     }

     @EventHandler
     public void onQuitWhileClaiming(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        User user = User.getUser(player.getUniqueId());
        removeWand(player.getInventory());
        claiming.remove(user);
        position1ClaimingMap.remove(user);
        position2ClaimingMap.remove(user);
     }

     @EventHandler
     public void onItemClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) return;
        Player player = (Player) event.getWhoClicked();
        ItemStack itemStack = event.getCurrentItem();
        if (itemStack == null) return;
        if (!itemStack.hasItemMeta()) return;
        if (itemStack.getItemMeta().getDisplayName().toLowerCase().contains("claiming")) {
            event.setCancelled(true);
        }
     }

     @EventHandler
     public void onItemDrop(PlayerDropItemEvent event) {
         ItemStack itemStack = event.getItemDrop().getItemStack();
         if (itemStack == null) return;
         if (!itemStack.hasItemMeta()) return;
         if (itemStack.getItemMeta().getDisplayName().toLowerCase().contains("claiming")) {
             event.setCancelled(true);
         }
     }

     @EventHandler
     public void onItemDropEvent(InventoryDragEvent event) {
         if (!(event.getWhoClicked() instanceof Player)) return;
         Player player = (Player) event.getWhoClicked();
         ItemStack itemStack = event.getOldCursor();
         if (itemStack == null) return;
         if (!itemStack.hasItemMeta()) return;
         if (itemStack.getItemMeta().getDisplayName().toLowerCase().contains("claiming")) {
             event.setCancelled(true);
         }
     }

     public static void removeWand(PlayerInventory inventory) {
         for (int slot = 0; slot < 36; slot++) {
             ItemStack itemStack = inventory.getItem(slot);
             if (itemStack == null) continue;
             if (!itemStack.hasItemMeta()) continue;
             if (itemStack.getItemMeta().getDisplayName().toLowerCase().contains("claiming")) {
                 inventory.setItem(slot, new ItemStack(Material.AIR));
             }
         }
     }

     public static void startClaiming(Player player, FactionClaimingData data) {
        User user = User.getUser(player.getUniqueId());
        claiming.put(user, data);
     }

    // Why not static abuse
    public static ItemStack getClaimingWand() {
        return claimingWand;
    }
}
