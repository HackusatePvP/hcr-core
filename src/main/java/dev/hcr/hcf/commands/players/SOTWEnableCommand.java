package dev.hcr.hcf.commands.players;

import dev.hcr.hcf.timers.Timer;
import dev.hcr.hcf.users.User;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SOTWEnableCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Player only command!");
            return true;
        }
        Player player = (Player) sender;
        User user = User.getUser(player.getUniqueId());
        if (!user.hasSotw()) {
            player.sendMessage(ChatColor.RED + "Your SOTW timer is not currently active.");
            return true;
        }
        if (Timer.getTimer("sotw") == null || !Timer.getTimer("sotw").isActive()) {
            sender.sendMessage(ChatColor.RED + "SOTW is not active.");
            return true;
        }
        user.setSotw(true);
        player.sendMessage(ChatColor.GREEN + "SOTW Timer enabled PvP will now be enabled.");
        return false;
    }
}
