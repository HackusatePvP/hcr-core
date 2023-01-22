package dev.hcr.hcf.commands.players.lives;

import dev.hcr.hcf.users.User;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class LivesSendCommand extends LivesCommand {

    public LivesSendCommand() {
        super("send", "Send lives to another player.");
    }

    @Override
    public void execute(CommandSender sender, String label, String[] args) {
        if (!(sender instanceof Player)) {
            if (args.length < 3) {
                sender.sendMessage(ChatColor.RED + "Usage: /" + label + " send <player> <amount>");
                return;
            }

            User user = User.getOfflineUser(args[1]);
            if (user == null) {
                sender.sendMessage(ChatColor.RED + "Could not find user \"" + args[0] + "\"");
                return;
            }
            try {
                int amount = Integer.parseInt(args[2]);
                user.setLives(user.getLives() + amount);
                sender.sendMessage(ChatColor.GREEN + "You have sent " + amount + " live(s) to " + user.getName());
            } catch (NumberFormatException ignored) {
                sender.sendMessage(ChatColor.RED + args[0] + " is an invalid number type.");
            }
            return;
        }
        Player player = (Player) sender;
        if (player.hasPermission(getPermission() + ".bypass")) {
            if (args.length < 2) {
                sender.sendMessage(ChatColor.RED + "Usage: /" + label + " send <player> <amount>");
                return;
            }
            User user = User.getOfflineUser(args[1]);
            if (user == null) {
                sender.sendMessage(ChatColor.RED + "Could not find user \"" + args[0] + "\"");
                return;
            }
            try {
                int amount = Integer.parseInt(args[2]);
                user.setLives(user.getLives() + amount);
                sender.sendMessage(ChatColor.GREEN + "You have sent " + amount + " live(s) to " + user.getName());
            } catch (NumberFormatException ignored) {
                sender.sendMessage(ChatColor.RED + args[0] + " is an invalid number type.");
            }
        } else {
            User user = User.getUser(player.getUniqueId());
            User target = User.getOfflineUser(args[1]);
            if (target == null) {
                sender.sendMessage(ChatColor.RED + "Could not find user \"" + args[0] + "\"");
                return;
            }
            try {
                int amount = Integer.parseInt(args[2]);
                if (user.getLives() < amount) {
                    sender.sendMessage(ChatColor.RED + "You do not have enough lives.");
                    return;
                }
                target.setLives(target.getLives() + amount);
                user.setLives(user.getLives() - amount);
                sender.sendMessage(ChatColor.GREEN + "You have sent " + amount + " live(s) to " + target.getName());
            } catch (NumberFormatException ignored) {
                sender.sendMessage(ChatColor.RED + args[0] + " is an invalid number type.");
            }
        }
    }

    @Override
    public List<String> tabComplete(CommandSender sender, Command command, String alias, String[] args) {
        return new ArrayList<>();
    }

}
