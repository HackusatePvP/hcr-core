package dev.hcr.hcf.factions.commands.leader;

import dev.hcr.hcf.factions.commands.FactionCommand;
import dev.hcr.hcf.factions.types.PlayerFaction;
import dev.hcr.hcf.users.User;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class FactionDisbandCommand extends FactionCommand {
    private final Set<User> confirmDisbandSet = new HashSet<>();

    public FactionDisbandCommand() {
        super("disband", "Disband your current faction.");
    }

    @Override
    public void execute(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) return;
        Player player = (Player) sender;
        User user = User.getUser(player.getUniqueId());
        if (user.getFaction() == null) {
            player.sendMessage(ChatColor.RED + "You must be in a faction before you can disband.");
            return;
        }
        PlayerFaction playerFaction = (PlayerFaction) user.getFaction();
        if (playerFaction.getRole(user).getWeight() < 4) {
            player.sendMessage(ChatColor.RED + "Only the leader can disband a faction.");
            return;
        }
        if (confirmDisbandSet.contains(user)) {
            for (Player member : playerFaction.getOnlineMembers()) {
                User usr = User.getUser(member.getUniqueId());
                usr.setFaction(null);
            }
            playerFaction.disband();
            player.sendMessage(ChatColor.DARK_RED + "You have disbanded your faction.");
            confirmDisbandSet.remove(user);
            return;
        }
        player.sendMessage(ChatColor.RED + "Please type the command again to confirm disband.");
        confirmDisbandSet.add(user);
    }

    @Override
    public List<String> tabComplete(CommandSender sender, Command command, String alias, String[] args) {
        return new ArrayList<>();
    }
}
