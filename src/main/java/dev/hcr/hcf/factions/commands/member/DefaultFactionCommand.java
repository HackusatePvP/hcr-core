package dev.hcr.hcf.factions.commands.member;

import dev.hcr.hcf.factions.commands.FactionCommand;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class DefaultFactionCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command c, String label, String[] args) {
        if (args.length == 0) {
            sender.sendMessage(ChatColor.RED + "/" + label + " help");
            return true;
        }
        FactionCommand command = FactionCommand.getCommand(args[0]);
        if (command == null) {
            sender.sendMessage(ChatColor.RED + "Could not find argument \"" + args[0] + "\". /" + label + " help");
            return true;
        }
        if (!command.getPermission().isEmpty()) {
            if (!sender.hasPermission(command.getPermission())) {
                sender.sendMessage(ChatColor.RED + "No permission.");
                return true;
            }
        }
        command.execute(sender, c, label, args);
        return false;
    }
}
