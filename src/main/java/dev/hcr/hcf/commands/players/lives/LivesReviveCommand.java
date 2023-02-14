package dev.hcr.hcf.commands.players.lives;

import dev.hcr.hcf.deathbans.DeathBan;
import dev.hcr.hcf.users.User;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class LivesReviveCommand extends LivesCommand {

    public LivesReviveCommand() {
        super("revive", "Remove a deathban from a player.");
    }

    @Override
    public void execute(CommandSender sender, String label, String[] args) {
        if (args.length < 2) {
            sender.sendMessage(ChatColor.RED + "Usage: /" + label + " revive <player>");
            return;
        }
        User user = User.getOfflineUser(args[1]);
        if (user == null) {
            sender.sendMessage(ChatColor.RED + "Target not found.");
            return;
        }
        DeathBan deathBan = DeathBan.getActiveDeathBan(user.getUniqueID());
        if (deathBan == null) {
            sender.sendMessage(ChatColor.RED + user.getName() + " is not deathbanned.");
            return;
        }
        deathBan.complete();
    }

    @Override
    public List<String> tabComplete(CommandSender sender, Command command, String alias, String[] args) {
        return new ArrayList<>();
    }
}
