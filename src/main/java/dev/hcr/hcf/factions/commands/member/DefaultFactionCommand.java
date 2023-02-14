package dev.hcr.hcf.factions.commands.member;

import dev.hcr.hcf.factions.commands.FactionCommand;
import dev.hcr.yuni.ranks.Rank;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DefaultFactionCommand implements CommandExecutor, TabExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command c, String label, String[] args) {
        if (args.length == 0) {
            FactionCommand helpCommand = FactionCommand.getCommand("help");
            if (helpCommand != null) {
                helpCommand.execute(sender, c, label, args);
            }
            return true;
        }
        FactionCommand command = FactionCommand.getCommand(args[0]);
        if (command == null) {
            sender.sendMessage(ChatColor.RED + "Could not find argument \"" + args[0] + "\". /" + label + " help");
            return true;
        }
        if (command.getPermission() != null && !command.getPermission().contains("hcf.faction.commands.null")) {
            if (!sender.hasPermission(command.getPermission())) {
                sender.sendMessage(ChatColor.RED + "No permission.");
                sender.sendMessage(ChatColor.RED + "Needed Permissions: " + command.getPermission());
                sender.sendMessage(ChatColor.RED + "Current Permissions: " );
                sender.getEffectivePermissions().forEach(permissionAttachmentInfo -> sender.sendMessage(permissionAttachmentInfo.getPermission()));
                return true;
            }
        }
        command.execute(sender, c, label, args);
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command c, String alias, String[] args) {
        List<String> completions = new ArrayList<>();
        if (args.length == 1) {
            StringUtil.copyPartialMatches(args[0], FactionCommand.getRegisteredCommands(), completions);
        }
        if (args.length == 2) {
            FactionCommand command = FactionCommand.getCommand(args[0]);
            if (command != null) {
                StringUtil.copyPartialMatches(args[1], command.tabComplete(sender, c, alias, args), completions);
            }
        }
        Collections.sort(completions);
        return completions;
    }
}
