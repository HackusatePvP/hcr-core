package dev.hcr.hcf.factions.commands.leader;

import dev.hcr.hcf.factions.commands.FactionCommand;
import dev.hcr.hcf.factions.types.PlayerFaction;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class FactionCreateCommand extends FactionCommand {

    public FactionCreateCommand() {
        super("create");
    }

    @Override
    public void execute(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            return;
        }
        if (!args[0].equalsIgnoreCase("create")) return;
        Player player = (Player) sender;
        if (args.length == 2) {
            String factionName = args[1];
            new PlayerFaction(factionName, player.getUniqueId());
            player.sendMessage(ChatColor.GREEN + "Faction created!");
        }
    }

    @Override
    public List<String> tabComplete(CommandSender sender, Command command, String alias, String[] args) {
        return null;
    }
}
