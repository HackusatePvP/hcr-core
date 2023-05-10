package dev.hcr.hcf.commands.players.sotw.admin;

import dev.hcr.hcf.commands.players.sotw.SOTWCommand;
import dev.hcr.hcf.timers.Timer;
import dev.hcr.hcf.timers.types.server.SOTWTimer;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;

public class SOTWStopCommand extends SOTWCommand {

    public SOTWStopCommand() {
        super("stop", "stop", "Stops the SOTW event.");
    }

    @Override
    public void execute(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 1) {
            if (args[0].equalsIgnoreCase("stop")) {
                SOTWTimer timer = (SOTWTimer) Timer.getTimer("sotw");
                if (timer == null) {
                    sender.sendMessage(ChatColor.RED + "SOTW is currently not running.");
                    return;
                }
                if (!timer.isActive()) {
                    sender.sendMessage(ChatColor.RED + "SOTW is currently not running.");
                    return;
                }
                if (timer.isActive()) {
                    timer.end(true);
                    sender.sendMessage(ChatColor.RED + "You have stopped the SOTW timer.");
                }
            } else {
                sender.sendMessage(ChatColor.RED + "\"" + args[0] + "\" is not a valid argument. Did you mean (stop)?");
            }
        }
    }

    @Override
    public List<String> tabComplete(CommandSender sender, Command command, String alias, String[] args) {
        return new ArrayList<>();
    }
}
