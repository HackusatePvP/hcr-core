package dev.hcr.hcf.commands.admin;

import dev.hcr.hcf.timers.Timer;
import dev.hcr.hcf.timers.events.TimerStartEvent;
import dev.hcr.hcf.timers.types.server.SOTWTimer;
import dev.hcr.hcf.utils.CC;
import dev.hcr.hcf.utils.Duration;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class SOTWCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) return true;
        Player player = (Player) sender;
        if (args.length == 0) {
            List<String> message = new ArrayList<>();
            message.add("&7&m--------------------------------------------------");
            message.add("&c/" + label + " start <delay>");
            message.add("&c/" + label + " stop");
            message.add("&c/" + label + " extend <delay>");
            message.add("&7&m--------------------------------------------------");
            message.forEach(msg -> player.sendMessage(CC.translate(msg)));
            return true;
        }
        if (args.length == 1) {
            if (args[0].equalsIgnoreCase("stop")) {
                SOTWTimer timer = (SOTWTimer) Timer.getTimer("sotw");
                if (timer == null) {
                    player.sendMessage(ChatColor.RED + "SOTW is currently not running.");
                    return true;
                }
                if (!timer.isEnabled()) {
                    player.sendMessage(ChatColor.RED + "SOTW is currently not running.");
                    return true;
                }
                if (timer.isEnabled()) {
                    timer.setEnabled(false);
                    timer.cancel();
                    player.sendMessage(ChatColor.RED + "You have stopped the SOTW timer.");
                }
            } else {
                player.sendMessage(ChatColor.RED + "\"" + args[0] + "\" is not a valid argument. Did you mean (stop)?");
            }
        }
        if (args.length == 2) {
            if (args[0].equalsIgnoreCase("start")) {
                Duration duration = Duration.fromString(args[1]);
                SOTWTimer timer = (SOTWTimer) Timer.getTimer("sotw");
                if (timer == null) {
                    timer = new SOTWTimer(duration.getValue());
                } else {
                    if (timer.isEnabled()) {
                        player.sendMessage(ChatColor.RED + "SOTW timer is already running.");
                        return true;
                    }
                    timer.setDelay(duration.getValue());
                    timer.run();
                }
                TimerStartEvent event = new TimerStartEvent(timer);
                Bukkit.getPluginManager().callEvent(event);
                if (event.isCancelled()) {
                    return true;
                }
                player.sendMessage(ChatColor.GREEN + "Successfully started SOTW.");
            } else if (args[0].equalsIgnoreCase("extend")) {

            } else {
                player.sendMessage(ChatColor.RED + "\"" + args[0] + "\" is not a valid argument. Did you mean (start,extend)?");
            }
        }
        return false;
    }
}
