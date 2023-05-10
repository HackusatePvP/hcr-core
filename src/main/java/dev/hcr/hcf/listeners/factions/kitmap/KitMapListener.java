package dev.hcr.hcf.listeners.factions.kitmap;

import dev.hcr.hcf.factions.Faction;
import dev.hcr.hcf.factions.types.SafeZoneFaction;
import dev.hcr.hcf.utils.CC;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;

public class KitMapListener implements Listener {
    private final Inventory refillInventory;

    public KitMapListener() {
        this.refillInventory = Bukkit.createInventory(null, 36, CC.translate("&aRefill"));
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        // Instant respawn player on death.
        Player player = event.getEntity();
        player.spigot().respawn();
    }

    @EventHandler
    public void onSignChange(SignChangeEvent event) {
        Player player = event.getPlayer();
        String[] lines = event.getLines();
        if (lines[0].toLowerCase().contains("kit") || lines[0].toLowerCase().contains("refill")) {
            if (player.isOp() && Faction.getByLocation(player.getLocation()) instanceof SafeZoneFaction && Faction.getByLocation(event.getBlock().getLocation()) instanceof SafeZoneFaction) {
                String part = lines[0].replace("[", "").replace("]", "");
                part = part.substring(0, 1).toLowerCase().substring(1);
                event.setLine(0, CC.translate("&7[&1" + part + "&7]"));
                player.sendMessage(ChatColor.GREEN + "Kit sign created.");
            }
        }
    }

    @EventHandler
    public void onSignInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Block block = event.getClickedBlock();
        if (block.getState() instanceof Sign) {
            Sign sign = (Sign) block.getState();
            if (sign.getLine(0).toLowerCase().contains("kit") || sign.getLine(0).toLowerCase().contains("refill")) {
                if (Faction.getByLocation(sign.getLocation()) instanceof SafeZoneFaction && Faction.getByLocation(player.getLocation()) instanceof SafeZoneFaction) {
                    handleSign(player, sign);
                }
            }
        }
    }

    private void handleSign(Player player, Sign sign) {
        String first = sign.getLine(0).toLowerCase();
        String second = sign.getLine(1).toLowerCase();
        if (first.contains("kit")) {

        } else if (first.contains("refill")) {
            player.openInventory(refillInventory);
        }
    }
}
