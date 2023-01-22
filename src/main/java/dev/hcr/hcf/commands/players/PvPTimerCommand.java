package dev.hcr.hcf.commands.players;

import dev.hcr.hcf.users.User;
import dev.hcr.yuni.utils.UUIDUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class PvPTimerCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            if (args.length == 2) {
                if (args[0].equalsIgnoreCase("give")) {
                    String name = args[1];
                    UUID uuid = UUIDUtils.getUUID(name);
                    if (uuid == null) {
                        sender.sendMessage("Invalid target.");
                        return true;
                    }
                    User target = User.getUser(uuid, name);
                    if (target == null) {
                        sender.sendMessage("Invalid target.");
                        return true;
                    }
                    target.setTimer("pvp", true);
                }
            }
            return true;
        }
        Player player = (Player) sender;
        User user = User.getUser(player.getUniqueId());
        if (args.length == 0) {
            if (user.hasActiveTimer("pvp")) {
                player.sendMessage(ChatColor.GREEN + "Your PvP Timer is active for another: " + user.getActiveTimer("pvp").getTimerDisplay());
            } else {
                player.sendMessage(ChatColor.RED + "You currently do not have an active PvP Timer.");
            }
        }
        if (args.length == 1) {
            if (args[0].equalsIgnoreCase("enable")) {
                if (user.hasActiveTimer("pvp")) {
                    player.sendMessage(ChatColor.RED + "Your PVP Timer has been disabled. Other plays can now damage you.");
                    user.setTimer("pvp", false);
                } else {
                    sender.sendMessage(ChatColor.RED + "You do not currently have an active PvP Timer.");
                }
            }
        }
        if (args.length == 2) {
            if (args[0].equalsIgnoreCase("give")) {
                if (player.hasPermission("hcf.commands.pvptimer.give")) {
                    String name = args[1];
                    UUID uuid = UUIDUtils.getUUID(name);
                    if (uuid == null) {
                        player.sendMessage(ChatColor.RED + "Could not find a valid with the name \"" + name + "\".");
                        return true;
                    }
                    User target = User.getUser(uuid, name);
                    if (target == null) {
                        player.sendMessage(ChatColor.RED + "Failed to create User for \"" + name + "\".");
                        return true;
                    }
                    target.setTimer("pvp", true);
                }
            }
        }
        return false;
    }
}
