package dev.hcr.hcf.commands.players.lives;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class DefaultLivesCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command c, String label, String[] args) {
        if (args.length == 0) {
            LivesCommand checkCommand = LivesCommand.getCommand("check");
            if (checkCommand == null) {
                LivesCommand helpCommand = LivesCommand.getCommand("help");
                if (helpCommand == null) {
                    sender.sendMessage(ChatColor.RED + "Argument not found.");
                    return true;
                }
                helpCommand.execute(sender, label, args);
            } else {
                checkCommand.execute(sender, label, args);
            }
        } else {
            LivesCommand command = LivesCommand.getCommand(args[0]);
            if (command == null) {
                sender.sendMessage(ChatColor.RED + "Argument \"" + args[0] + "\" not found.");
                return true;
            }
            command.execute(sender, label, args);
        }
        return false;
    }
}
