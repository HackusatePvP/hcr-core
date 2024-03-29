package dev.hcr.hcf.listeners.player;

import dev.hcr.hcf.HCF;
import dev.hcr.hcf.factions.Faction;
import dev.hcr.hcf.factions.types.SafeZoneFaction;
import dev.hcr.hcf.users.User;
import dev.hcr.hcf.utils.CC;
import dev.hcr.hcf.utils.backend.ItemDatabase;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;

public class SignListener implements Listener {

    @EventHandler
    public void onSignPlace(SignChangeEvent event) {
        Player player = event.getPlayer();
        User user = User.getUser(player.getUniqueId());
        Faction faction = Faction.getByLocation(event.getBlock().getLocation());
        if (faction instanceof SafeZoneFaction && player.isOp() && Faction.getByLocation(player.getLocation()) instanceof SafeZoneFaction) {
            if (event.getLine(0).contains("[") && event.getLine(0).contains("]")) {
                String action = event.getLine(0).replace("[", "").replace("]", "");
                String first = "&1[" + action.substring(0, 1).toUpperCase() + action.substring(1) + "]";
                if (action.equalsIgnoreCase("buy") || action.equalsIgnoreCase("sell")) {
                    if (!player.isOp()) {
                        player.sendMessage(ChatColor.RED + "You are prohibited from creating these signs.");
                        return;
                    }
                }
                event.setLine(0, CC.translate(first));
            }
        }
        if (event.getLine(0).toLowerCase().contains("elevator")) {
            Faction playerFaction = user.getFaction();
            if (faction == playerFaction) {
                if (event.getLine(1).toLowerCase().contains("up") || event.getLine(1).toLowerCase().contains("down")) {
                    String action = event.getLine(0).replace("[", "").replace("]", "");
                    String first = "&1[" + action.substring(0, 1).toUpperCase() + action.substring(1) + "]";
                    event.setLine(0, CC.translate(first));
                } else {
                    player.sendMessage(ChatColor.RED + "Invalid elevator sign. First line is the action: [Elevator]. The second line is the direction: Up or Down.");
                }

            } else {
                if (player.isOp() && user.hasBypass()) {
                    if (event.getLine(1).toLowerCase().contains("up") || event.getLine(1).toLowerCase().contains("down")) {
                        String action = event.getLine(0).replace("[", "").replace("]", "");
                        String first = "&1[" + action.substring(0, 1).toUpperCase() + action.substring(1) + "]";
                        event.setLine(0, CC.translate(first));
                    } else {
                        player.sendMessage(ChatColor.RED + "Invalid elevator sign. First line is the action: [Elevator]. The second line is the direction: Up or Down.");
                    }
                }
            }
        }

    }

    @EventHandler
    public void onSignClick(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Block entity = event.getClickedBlock();
        if (entity != null && (entity.getType() == Material.SIGN || entity.getType() == Material.WALL_SIGN || entity.getType() == Material.SIGN_POST)) {
            Sign sign = (Sign) entity.getState();
            if (sign.getLine(0).toLowerCase().contains("buy") || sign.getLine(0).toLowerCase().contains("sell")) {
                Faction playerLocationFaction = Faction.getByLocation(player.getLocation());
                Faction signLocationFaction = Faction.getByLocation(sign.getLocation());
                if (playerLocationFaction instanceof SafeZoneFaction && signLocationFaction instanceof SafeZoneFaction) {
                    if (sign.getLines().length < 4) {
                        return;
                    }
                    handleShopSign(player, sign);
                }
            } else if (sign.getLine(0).toLowerCase().contains("disposal")) {
                Faction playerLocationFaction = Faction.getByLocation(player.getLocation());
                Faction signLocationFaction = Faction.getByLocation(sign.getLocation());
                if (playerLocationFaction instanceof SafeZoneFaction && signLocationFaction instanceof SafeZoneFaction) {
                    handleDisposal(player, sign);
                }
            } else if (sign.getLine(0).toLowerCase().contains("elevator")) {
                handleElevator(player, sign);
            }
        }
    }

    private void handleShopSign(Player player, Sign sign) {
        String action = sign.getLine(0);
        int amount = -1;
        try {
            amount = Integer.parseInt(sign.getLine(1).replace("$", ""));
        } catch (NumberFormatException ignored) {
            player.sendMessage(ChatColor.RED + "Invalid sign format. First line is the action buy or sell. Second line is the amount. Third line is the item. Fourth line is the price.");
            return;
        }
        String fullitem = sign.getLine(2);
        ItemStack itemStack;
        if (fullitem.contains(":")) {
            String[] split = fullitem.toLowerCase().split(":");
            String item = split[0];
            byte data = Byte.parseByte(split[1]);
            Material material = ItemDatabase.getItem(item);
            if (material == null) {
                material = Material.getMaterial(Integer.parseInt(item));
            }
            if (material == null) {
                player.sendMessage(ChatColor.RED + "Item \"" + item + "\" does not exist in the items database.");
                return;
            }
            itemStack = new ItemStack(material, amount, data);
        } else {
            String item = sign.getLine(2).toLowerCase();
            Material material = ItemDatabase.getItem(item);
            if (material == null) {
                material = Material.getMaterial(Integer.parseInt(item));
            }
            if (material == null) {
                player.sendMessage(ChatColor.RED + "Item \"" + item + "\" does not exist in the items database.");
                return;
            }
            itemStack = new ItemStack(material, amount);
        }
        double cost = Double.parseDouble(sign.getLine(3).replace("$", ""));

        User user = User.getUser(player.getUniqueId());
        if (action.toLowerCase().contains("buy")) {
            if (user.getBalance() < cost) {
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        player.sendSignChange(sign.getLocation(), sign.getLines());
                    }
                }.runTaskLaterAsynchronously(HCF.getPlugin(), 20 * 3);
                player.sendSignChange(sign.getLocation(), new String[]{CC.translate("&cInsufficient"), CC.translate("Funds"),
                        CC.translate(""), CC.translate("")});
                player.sendMessage(ChatColor.RED + "You cannot afford to purchase this item.");
                return;
            }
            user.takeFromBalance(cost);
            HashMap<Integer, ItemStack> items = player.getInventory().addItem(itemStack);
            if (items.size() > 0) {
                for (ItemStack drop : items.values()) {
                    player.getWorld().dropItem(player.getLocation(), drop);
                }
            }
            player.sendSignChange(sign.getLocation(), new String[]{CC.translate("&aYou have bought"), CC.translate("&a" + amount + "x" + fullitem),
                    CC.translate("&afor " + cost), CC.translate("")});
            new BukkitRunnable() {
                @Override
                public void run() {
                    player.sendSignChange(sign.getLocation(), sign.getLines());
                }
            }.runTaskLaterAsynchronously(HCF.getPlugin(), 20 * 3);

        } else if (action.toLowerCase().contains("sell")) {
            System.out.println("Selling item...");
            if (!player.getInventory().contains(itemStack.getMaxStackSize())) {
                player.sendSignChange(sign.getLocation(), new String[]{CC.translate("&cInsufficient"), CC.translate("Items"), "", ""});
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        player.sendSignChange(sign.getLocation(), sign.getLines());
                    }
                }.runTaskLaterAsynchronously(HCF.getPlugin(), 20 * 3);
                player.sendMessage(ChatColor.RED + "You do not have sufficient items to sell.");
                return;
            }
            player.getInventory().remove(itemStack.getType());
            user.addToBalance(amount);
            player.sendSignChange(sign.getLocation(), new String[]{CC.translate("&aYou have sold"), CC.translate("&a" + amount + "x" + fullitem),
                    CC.translate("&afor " + cost), CC.translate("")});
            new BukkitRunnable() {
                @Override
                public void run() {
                    player.sendSignChange(sign.getLocation(), sign.getLines());
                }
            }.runTaskLaterAsynchronously(HCF.getPlugin(), 20 * 3);
        }
    }

    private void handleDisposal(Player player, Sign sign) {
        Inventory inventory = Bukkit.createInventory(null, 36);
        player.openInventory(inventory);
    }

    private void handleElevator(Player player, Sign sign) {
        Location location = sign.getLocation();
        String originalDirection = sign.getLine(1).toLowerCase();
        if (originalDirection.contains("up")) {
            for (int y = 1; y < 255; y++) {
                // Teleportation up.
                Location find = location.add(0, y, 0);
                System.out.println("Location: " + find.toString());
                System.out.println("Block: " + find.getBlock().toString());
                System.out.println("Material: " + find.getBlock().getType().name());
                if (find.getBlock() != null && find.getBlock().getState() instanceof Sign) {
                    Sign second = (Sign) find.getBlock().getState();
                    if (second.getLine(0).toLowerCase().contains("elevator")) {
                        String direction = second.getLine(1);
                        if (direction.toLowerCase().contains("down")) {
                            player.sendMessage(ChatColor.RED + "Sign direction is incorrect. Make sure the signs match up to each other. One sign should be up the other should be down.");
                            return;
                        }
                        player.teleport(second.getLocation());
                        return;
                    }
                }
            }
        } else if (originalDirection.contains("down")) {
            for (int y = 2; y < 255; y++) {
                Location find = location.subtract(0, y, 0);
                if (find.getBlock() != null && find.getBlock().getState() instanceof Sign) {
                    Sign second = (Sign) find.getBlock().getState();
                    if (second.getLine(0).toLowerCase().contains("elevator")) {
                        String direction = second.getLine(1);
                        if (direction.toLowerCase().contains("up")) {
                            player.sendMessage(ChatColor.RED + "Sign direction is incorrect. Make sure the signs match up to each other. One sign should be up the other should be down.");
                            return;
                        }
                        player.teleport(second.getLocation());
                        return;
                    }
                }
            }
        } else {
            player.sendMessage(ChatColor.RED + "Unknown direction: " + originalDirection);
        }
    }
}
