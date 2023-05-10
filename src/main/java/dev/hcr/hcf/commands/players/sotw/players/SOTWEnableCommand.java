package dev.hcr.hcf.commands.players.sotw.players;

import dev.hcr.hcf.commands.players.sotw.SOTWCommand;
import dev.hcr.hcf.timers.Timer;
import dev.hcr.hcf.users.User;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class SOTWEnableCommand extends SOTWCommand {

    public SOTWEnableCommand() {
        super("enable", "Enables PvP whilst SOTW is active.");
    }

    @Override
    public void execute(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) return;
        Player player = (Player) sender;
        User user = User.getUser(player.getUniqueId());
        Timer timer = Timer.getTimer("sotw");
        if (timer == null) {
            sender.sendMessage(ChatColor.RED + "SOTW is not active.");
            return;
        }
        if (!user.hasSotw()) {
            sender.sendMessage(ChatColor.RED + "You have already enabled you SOTW Timer.");
            return;
        }
        user.setSotw(false);
        sender.sendMessage(ChatColor.GREEN + "You have enabled your SOTW Timer.");
    }

    @Override
    public List<String> tabComplete(CommandSender sender, Command command, String alias, String[] args) {
        return new ArrayList<>();
    }
}
