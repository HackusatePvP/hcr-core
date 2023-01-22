package dev.hcr.hcf.commands.players;

import dev.hcr.hcf.HCF;
import dev.hcr.hcf.users.User;
import dev.hcr.hcf.utils.CC;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class PayCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            return true;
        }
        Player player = (Player) sender;
        User user = User.getUser(player.getUniqueId());
        if (args.length < 2) {
            sender.sendMessage(ChatColor.RED + "Usage: /" + label + " <player> <amount>");
        } else {
            Player target = Bukkit.getPlayer(args[0]);
            double amount;
            try {
                amount = Double.parseDouble(args[1]);
            } catch (NumberFormatException ignored) {
                player.sendMessage(ChatColor.RED + "Please use a valid numerical value.");
                return true;
            }
            if (target == null) {
                OfflinePlayer offTarget = Bukkit.getOfflinePlayer(args[0]);
                if (offTarget == null) {
                    player.sendMessage(ChatColor.RED + "Player by the name of \"" + args[0] + "\" not found.");
                    return true;
                }
                handlePayment(user, offTarget.getUniqueId(), offTarget.getName(), amount);
            } else {
                handlePayment(user, target.getUniqueId(), target.getName(), amount);
            }
        }
        return false;
    }

    private void handlePayment(User user, UUID targetUUID, String targetName, double amount) {
        User target = User.getUser(targetUUID);
        user.takeFromBalance(amount);
        target.addToBalance(amount);

        if (user.toPlayer() != null) {
            user.toPlayer().sendMessage(CC.translate("&7You have sent &a" + HCF.getPlugin().getFormat().format(amount) + " &7to &e" + targetName));
        }
        if (target.toPlayer() != null) {
            target.toPlayer().sendMessage(CC.translate("&7You have received &a" + HCF.getPlugin().getFormat().format(amount) + " &7from &e" + user.getName()));
        }
    }
}
