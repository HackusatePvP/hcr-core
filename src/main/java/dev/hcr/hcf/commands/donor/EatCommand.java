package dev.hcr.hcf.commands.donor;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class EatCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) return true;
        Player player = (Player) sender;
        if (args.length == 0) {
            // TODO: 5/9/2023 create cooldown
            player.setFoodLevel(100);
            player.setSaturation(3.5f);
            return true;
        }
        if (player.hasPermission("hcf.commands.eat.other")) {
            String name = args[0];
            Player target = Bukkit.getPlayer(name);
            if (target == null) {
                player.sendMessage(ChatColor.RED + "Unknown player: " + name);
                return true;
            }
            target.setFoodLevel(100);
            target.setSaturation(3.5f);
        }
        return false;
    }
}
