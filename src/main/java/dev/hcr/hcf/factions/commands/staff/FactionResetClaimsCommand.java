package dev.hcr.hcf.factions.commands.staff;

import dev.hcr.hcf.factions.Faction;
import dev.hcr.hcf.factions.commands.FactionCommand;
import dev.hcr.hcf.factions.types.PlayerFaction;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.List;

public class FactionResetClaimsCommand extends FactionCommand {

    public FactionResetClaimsCommand() {
        super("resetclaims", "Reset/Erase a factions claims.");
    }

    @Override
    public void execute(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 2) {
            Faction faction = Faction.getFactionByName(args[1]);
            if (faction == null) {
                sender.sendMessage(ChatColor.RED + "Could not find faction \"" + args[1] + "\".");
                return;
            }
            if (faction instanceof PlayerFaction) {
                // If its a PlayerFaction refund the land value.
                PlayerFaction playerFaction = (PlayerFaction) faction;
                playerFaction.addToBalance(playerFaction.getLandRefundPrice());
            }
            faction.getClaims().clear();
        }
    }

    @Override
    public List<String> tabComplete(CommandSender sender, Command command, String alias, String[] args) {
        ArrayList<String> completions = new ArrayList<>();
        ArrayList<String> factionNames = new ArrayList<>();
        Faction.getFactions().forEach(faction -> factionNames.add(faction.getName()));
        if (args.length == 2) {
            StringUtil.copyPartialMatches(args[1], factionNames, completions);
        }
        return completions;
    }
}
