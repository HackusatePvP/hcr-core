package dev.hcr.hcf.factions.commands.staff;

import dev.hcr.hcf.factions.Faction;
import dev.hcr.hcf.factions.commands.FactionCommand;
import dev.hcr.hcf.factions.commands.coleader.FactionClaimCommand;
import dev.hcr.hcf.listeners.factions.FactionClaimingListener;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class FactionClaimForCommand extends FactionCommand {

    public FactionClaimForCommand() {
        super("claimfor", "claimfor", "Create claims for a faction.");
    }

    @Override
    public void execute(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) return;
        Player player = (Player) sender;
        if (args.length == 1) {
            sender.sendMessage(ChatColor.RED + "Usage: /" + label + " claimfor <faction>");
        }
        if (args.length == 2) {
            String name = args[1];
            Faction faction = Faction.getFactionByName(name);
            if (faction == null) {
                player.sendMessage(ChatColor.RED + "Could not find faction with name \"" + name + "\".");
                return;
            }
            if (!FactionClaimCommand.hasInventorySpace(player)) {
                player.sendMessage(ChatColor.RED + "You need to empty one slot in your hotbar.");
                return;
            }
            player.getInventory().addItem(FactionClaimingListener.getClaimingWand());
            FactionClaimingListener.startClaiming(player, faction);
        }
    }

    @Override
    public List<String> tabComplete(CommandSender sender, Command command, String alias, String[] args) {
        return null;
    }
}
