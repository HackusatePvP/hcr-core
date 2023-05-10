package dev.hcr.hcf.factions.commands.coleader;

import dev.hcr.hcf.factions.Faction;
import dev.hcr.hcf.factions.commands.FactionCommand;
import dev.hcr.hcf.factions.types.PlayerFaction;
import dev.hcr.hcf.users.User;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;

public class FactionAllyCommand extends FactionCommand {
    public FactionAllyCommand() {
        super("ally", "Make an alliance with another faction.");
    }

    @Override
    public void execute(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) return;
        Player player = (Player) sender;
        User user = User.getUser(player.getUniqueId());
        if (user.getFaction() == null) {
            sender.sendMessage(ChatColor.RED + "You must be in a faction to make an alliance.");
            return;
        }
        PlayerFaction faction = (PlayerFaction) user.getFaction();
        if (faction.getRole(user).getWeight() < 3) {
            sender.sendMessage(ChatColor.RED + "Only leaders and co-leader can make alliances.");
            return;
        }
        if (args.length < 4) {
            sender.sendMessage(ChatColor.RED + "Usage: /" + label + " ally <send|accept> <faction>");
            return;
        }
        String action = args[2];
        String allyName = args[3];
        PlayerFaction allyFaction = (PlayerFaction) Faction.getFactionByName(allyName);
        if (allyFaction == null) {
            sender.sendMessage(ChatColor.RED + "Faction with the name \"" + allyName + "\" does not exist.");
            return;
        }
        if (action.equalsIgnoreCase("accept")) {
            if (!faction.hasPendingAlly(allyFaction)) {
                sender.sendMessage(ChatColor.RED + "That faction has not sent an ally request. You can send one to them using &e'/f ally send " + allyFaction.getName() + "' &&to make an allegiance.");
                return;
            }
            faction.removePendingAlly(allyFaction);
            faction.addAlly(allyFaction);
        } else if (action.equalsIgnoreCase("send")) {
            if (allyFaction.hasPendingAlly(faction)) {
                sender.sendMessage(ChatColor.RED + "You have already sent an invite request to the faction. If you feel like they didn't see it you can send one of the members a private message.");
                return;
            }
            if (faction.hasPendingAlly(allyFaction)) {
                sender.sendMessage(ChatColor.RED + "The faction already sent you an ally request. Instead of sending one back you can just accept it. &e/f ally accept " + allyFaction.getName());
                return;
            }
            allyFaction.addPendingAlly(faction);
        } else {
            sender.sendMessage(ChatColor.RED + "Unknown argument. You tried: " + Arrays.toString(args) + " Expected: /f ally send or accept <faction>");
        }

        // TODO: 5/8/2023 Create a check to prevent alliances with a faction if they are actively fighting them
    }

    @Override
    public List<String> tabComplete(CommandSender sender, Command command, String alias, String[] args) {
        return null;
    }
}
