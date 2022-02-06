package dev.hcr.hcf.listeners.factions;

import dev.hcr.hcf.HCF;
import dev.hcr.hcf.factions.claims.cuboid.Cuboid;
import dev.hcr.hcf.factions.types.PlayerFaction;
import dev.hcr.hcf.users.User;
import dev.hcr.hcf.utils.CC;
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
    public static final Set<User> claiming = new HashSet<>();
    private static final Map<User, Location> position1ClaimingMap = new HashMap<>();
    private static final Map<User, Location> position2ClaimingMap = new HashMap<>();

    @EventHandler
    public void onPlayerInteractWithWand(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        User user = User.getUser(player.getUniqueId());
        PlayerFaction faction = (PlayerFaction) user.getFaction();
        if (faction == null) return;
        if (!claiming.contains(user)) return;
        ItemStack hand = player.getItemInHand();
        if (hand == null) return;
        if (hand.getType() == Material.GOLD_HOE && hand.hasItemMeta() && hand.getItemMeta().getDisplayName().equals(getClaimingWand().getItemMeta().getDisplayName())) {
            if (event.getAction() == Action.LEFT_CLICK_BLOCK) {
                Location location = event.getClickedBlock().getLocation();
                position1ClaimingMap.put(user, location);
                player.sendMessage(CC.translate("&7Set position 1 at (&c" + location.getBlockX() + "," + location.getBlockZ() + "&7)"));
                event.setUseInteractedBlock(Event.Result.DENY);

                if (position1ClaimingMap.containsKey(user) && position2ClaimingMap.containsKey(user)) {
                    player.sendMessage(CC.translate("&7The land will cost you &c" + HCF.getPlugin().getFormat().format(faction.getClaimingLandPrice(position1ClaimingMap.get(user), position2ClaimingMap.get(user)))));
                }
            }
            if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
                Location location = event.getClickedBlock().getLocation();
                position2ClaimingMap.put(user, location);
                player.sendMessage(CC.translate("&7Set position 2 at (&c" + location.getBlockX() + "," + location.getBlockZ() + "&7)"));
                event.setUseInteractedBlock(Event.Result.DENY);

                // If the player has both positions set, lets generate a price for the claim
                if (position1ClaimingMap.containsKey(user) && position2ClaimingMap.containsKey(user)) {
                    player.sendMessage(CC.translate("&7The land will cost you &c" + HCF.getPlugin().getFormat().format(faction.getClaimingLandPrice(position1ClaimingMap.get(user), position2ClaimingMap.get(user)))));
                }
            }
            if (player.isSneaking()) {
                if (event.getAction() == Action.LEFT_CLICK_AIR) {
                    faction.addClaim(new Cuboid(position1ClaimingMap.get(user), position2ClaimingMap.get(user)));
                    player.sendMessage(ChatColor.GREEN + "Successfully claimed land!");
                    player.getInventory().remove(getClaimingWand());
                    claiming.remove(user);
                    position1ClaimingMap.remove(user);
                    position2ClaimingMap.remove(user);
                    event.setUseInteractedBlock(Event.Result.DENY);
                }
                if (event.getAction() == Action.RIGHT_CLICK_AIR) {
                    position1ClaimingMap.remove(user);
                    position2ClaimingMap.remove(user);
                    event.setUseInteractedBlock(Event.Result.DENY);
                }
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
