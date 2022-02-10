package dev.hcr.hcf.listeners.factions;

import dev.hcr.hcf.HCF;
import dev.hcr.hcf.factions.Faction;
import dev.hcr.hcf.factions.claims.cuboid.Cuboid;
import dev.hcr.hcf.factions.events.coleaders.FactionClaimLandEvent;
import dev.hcr.hcf.factions.events.packets.RemoveClaimingPillarPacketsEvent;
import dev.hcr.hcf.factions.events.packets.SendClaimingPillarPacketsEvent;
import dev.hcr.hcf.factions.types.PlayerFaction;
import dev.hcr.hcf.factions.types.WildernessFaction;
import dev.hcr.hcf.users.User;
import dev.hcr.hcf.utils.CC;
import dev.hcr.hcf.utils.TaskUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public class FactionClaimingListener implements Listener {
    public static final Map<User, Faction> claiming = new HashMap<>();
    private static final Map<User, Location> position1ClaimingMap = new HashMap<>();
    private static final Map<User, Location> position2ClaimingMap = new HashMap<>();

    @EventHandler
    public void onPlayerInteractWithWand(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        User user = User.getUser(player.getUniqueId());
        if (!claiming.containsKey(user)) return;
        Faction faction = claiming.get(user);
        ItemStack hand = player.getItemInHand();
        if (hand == null) return;
        if (hand.getType() == Material.GOLD_HOE && hand.hasItemMeta() && hand.getItemMeta().getDisplayName().equals(getClaimingWand().getItemMeta().getDisplayName())) {
            event.setUseItemInHand(Event.Result.DENY);
            if (event.getAction() == Action.LEFT_CLICK_BLOCK) {
                Location location = event.getClickedBlock().getLocation();
                position1ClaimingMap.put(user, location);
                player.sendMessage(CC.translate("&7Set position 1 at (&c" + location.getBlockX() + "," + location.getBlockZ() + "&7)"));
                handlePacketEvents(event, player, user, faction, true);
            }
            if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
                Location location = event.getClickedBlock().getLocation();
                position2ClaimingMap.put(user, location);
                player.sendMessage(CC.translate("&7Set position 2 at (&c" + location.getBlockX() + "," + location.getBlockZ() + "&7)"));
                handlePacketEvents(event, player, user, faction, true);
            }
            if (player.isSneaking()) {
                if (event.getAction() == Action.LEFT_CLICK_AIR) {
                    player.sendMessage(ChatColor.YELLOW + "Attempting to claim land...");
                    FactionClaimLandEvent claimLandEvent = new FactionClaimLandEvent(faction, player, position1ClaimingMap.get(user), position2ClaimingMap.get(user));
                    Bukkit.getPluginManager().callEvent(claimLandEvent);
                    if (claimLandEvent.isCancelled()) {
                        return;
                    }
                    faction.addClaim(new Cuboid(position1ClaimingMap.get(user), position2ClaimingMap.get(user)));
                    player.sendMessage(ChatColor.GREEN + "Successfully claimed land!");
                    handlePacketEvents(event, player, user, faction, false);
                    player.getInventory().remove(player.getItemInHand());
                    claiming.remove(user);
                    position1ClaimingMap.remove(user);
                    position2ClaimingMap.remove(user);
                }
                if (event.getAction() == Action.RIGHT_CLICK_AIR) {
                    player.sendMessage(ChatColor.RED + "Removing claiming selection...");
                    handlePacketEvents(event, player, user, faction, false);
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

    private void handlePacketEvents(PlayerInteractEvent event, Player player, User user, Faction faction, boolean add) {
        event.setUseInteractedBlock(Event.Result.DENY);
        if (add) {
            if (position1ClaimingMap.containsKey(user) && position2ClaimingMap.containsKey(user)) {
                SendClaimingPillarPacketsEvent pillarPacketsEvent = new SendClaimingPillarPacketsEvent(player, new Cuboid(position1ClaimingMap.get(user), position2ClaimingMap.get(user)));
                TaskUtils.runAsync(() -> {
                    Bukkit.getPluginManager().callEvent(pillarPacketsEvent);
                });
                player.sendMessage(CC.translate("&7The land will cost you &c" + HCF.getPlugin().getFormat().format(faction.getClaimingLandPrice(position1ClaimingMap.get(user), position2ClaimingMap.get(user)))));
            }
        } else {
                RemoveClaimingPillarPacketsEvent pillarPacketsEvent = new RemoveClaimingPillarPacketsEvent(player, new Cuboid(position1ClaimingMap.get(user), position2ClaimingMap.get(user)));
                TaskUtils.runAsync(() -> {
                    Bukkit.getPluginManager().callEvent(pillarPacketsEvent);
                });
        }
    }

    @EventHandler
     public void onClaimLand(FactionClaimLandEvent event) {
         Player player = event.getPlayer();
         Faction faction = event.getFaction();
         if (faction instanceof PlayerFaction) {
            PlayerFaction playerFaction = (PlayerFaction) faction;
            Faction factionLocation1 = Faction.getByLocation(event.getLocation1());
            Faction factionLocation2 = Faction.getByLocation(event.getLocation2());
             if (!(factionLocation1 instanceof WildernessFaction || factionLocation2 instanceof WildernessFaction)) {
                 player.sendMessage(ChatColor.RED + "You can only claim in wilderness.");
                 event.setCancelled(true);
                 return;
             }
            double cost = event.getCost();
            if (cost > playerFaction.getBalance()) {
                player.sendMessage(CC.translate("&cYour faction cannot afford this land. &7(/f deposit all)"));
                event.setCancelled(true);
            }
         }
     }

     @EventHandler
     public void onQuitWhileClaiming(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        User user = User.getUser(player.getUniqueId());
        claiming.remove(user);
        position1ClaimingMap.remove(user);
        position2ClaimingMap.remove(user);
        removeWand(player.getInventory());
     }

     private void removeWand(PlayerInventory inventory) {
        for (ItemStack itemStack : inventory.getContents()) {
            if (!itemStack.hasItemMeta()) continue;
            if (itemStack.getItemMeta().getDisplayName().equalsIgnoreCase(getClaimingWand().getItemMeta().getDisplayName())) {
                itemStack.setType(Material.AIR);
            }
        }
     }

     public static void startClaiming(Player player, Faction faction) {
        User user = User.getUser(player.getUniqueId());
        claiming.put(user, faction);
     }

    // Why not static abuse
    public static ItemStack getClaimingWand() {
        ItemStack itemStack = new ItemStack(Material.GOLD_HOE);
        ItemMeta meta = itemStack.getItemMeta();
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
        itemStack.setItemMeta(meta);
        return itemStack;
    }
}
