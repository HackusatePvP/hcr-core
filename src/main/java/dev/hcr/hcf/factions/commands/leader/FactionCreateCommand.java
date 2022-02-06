package dev.hcr.hcf.factions.commands.leader;

import dev.hcr.hcf.factions.Faction;
import dev.hcr.hcf.factions.commands.FactionCommand;
import dev.hcr.hcf.factions.types.PlayerFaction;
import dev.hcr.hcf.users.User;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class FactionCreateCommand extends FactionCommand {

    public FactionCreateCommand() {
        super("create", "Create a new faction.");
    }

    @Override
    public void execute(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            return;
        }
        if (!args[0].equalsIgnoreCase("create")) return;
        Player player = (Player) sender;
        User user = User.getUser(player.getUniqueId());
        if (user.getFaction() != null) {
            player.sendMessage(ChatColor.RED + "You are already in a faction.");
            return;
        }
        if (args.length == 2) {
            String factionName = args[1];
            if (Faction.getFactionByName(args[1]) != null) {
                player.sendMessage(ChatColor.RED + "Faction \"" + args[1] + "\" already exists.");
                return;
            }
            new PlayerFaction(factionName, player.getUniqueId());
            player.sendMessage(ChatColor.GREEN + "Faction created!");
        }
    }

    @Override
    public List<String> tabComplete(CommandSender sender, Command command, String alias, String[] args) {
        return new ArrayList<>();
    }
}
