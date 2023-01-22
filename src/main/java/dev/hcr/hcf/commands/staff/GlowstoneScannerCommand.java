package dev.hcr.hcf.commands.staff;

import dev.hcr.hcf.factions.Faction;
import dev.hcr.hcf.factions.types.GlowStoneMountainFaction;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class GlowstoneScannerCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        GlowStoneMountainFaction glowStoneMountainFaction = Faction.getGlowStoneMountainFaction();
        if (args.length == 0) {
            if (!glowStoneMountainFaction.hasClaims()) {
                sender.sendMessage(ChatColor.RED + "Glowstone mountain does not have claims. Aka theres nothing to scan.");
                return true;
            }
            glowStoneMountainFaction.getGlowStoneLocationCache().clear();
            glowStoneMountainFaction.startGlowStoneScanner();
            sender.sendMessage(ChatColor.GREEN + "Successfully scanner for glowstone.");
        }
        if (args.length == 1) {
            if (args[0].equalsIgnoreCase("test")) {
                sender.sendMessage(glowStoneMountainFaction.getGlowStoneLocationCache().size() + "");
            }
            if (args[0].equalsIgnoreCase("regen")) {
                glowStoneMountainFaction.regenerateGlowStone();
                sender.sendMessage(ChatColor.GREEN + "Replaced all glowstone.");
            }
        }
        return false;
    }
}
