package dev.hcr.hcf.factions.commands.staff;

import dev.hcr.hcf.factions.Faction;
import dev.hcr.hcf.factions.commands.FactionCommand;
import dev.hcr.hcf.factions.types.PlayerFaction;
import dev.hcr.hcf.users.User;
import dev.hcr.hcf.users.faction.Role;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class FactionForceLeaderCommand extends FactionCommand {

    public FactionForceLeaderCommand() {
        super("forceleader", "forceleader", "Forcefully set yourself as a leader in a faction.");
    }

    @Override
    public void execute(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) return;
        Player player = (Player) sender;
        User user = User.getUser(player.getUniqueId());
        if (args.length == 2) {
            Faction faction = Faction.getFactionByName(args[0]);
            if (faction == null) {
                player.sendMessage(ChatColor.RED + "Could not find faction by the name of \"" + args[1] + "\".");
                return;
            }
            if (!(faction instanceof PlayerFaction)) {
                player.sendMessage(ChatColor.RED + "You cannot join system factions. To modify system factions use; /f claimfor, /f forcesethome, /f setcolor");
                return;
            }
            PlayerFaction playerFaction = (PlayerFaction) faction;
            if (!playerFaction.hasMember(player.getUniqueId())) {
                player.sendMessage(ChatColor.RED + "You must be in the faction before you can force yourself to leader. /f forcejoin <" + playerFaction.getHome() + ">.");
                return;
            }
            User currentLeader;
            if (Bukkit.getPlayer(playerFaction.getLeader()) == null) {
                currentLeader = User.getUser(playerFaction.getLeader());
            } else {
                currentLeader = User.getUser(playerFaction.getLeader(), Bukkit.getOfflinePlayer(playerFaction.getLeader()).getName());
            }
            playerFaction.setRole(currentLeader, Role.COLEADER);
            playerFaction.setRole(user, Role.LEADER);
            player.sendMessage(ChatColor.GREEN + "Successfully forced yourself as leader.");
        }
    }

    @Override
    public List<String> tabComplete(CommandSender sender, Command command, String alias, String[] args) {
        return null;
    }
}
