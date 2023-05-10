package dev.hcr.hcf.commands.players.sotw.admin;

import dev.hcr.hcf.commands.players.sotw.SOTWCommand;
import dev.hcr.hcf.timers.Timer;
import dev.hcr.hcf.timers.events.TimerStartEvent;
import dev.hcr.hcf.timers.types.server.SOTWTimer;
import dev.hcr.hcf.users.User;
import dev.hcr.hcf.utils.Duration;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class SOTWStartCommand extends SOTWCommand {

    public SOTWStartCommand() {
        super("start", "start", "Starts the SOTW event.");
    }

    @Override
    public void execute(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 2) {
            if (args[0].equalsIgnoreCase("start")) {
                Duration duration = Duration.fromString(args[1]);
                SOTWTimer timer = (SOTWTimer) Timer.getTimer("sotw");
                if (timer == null) {
                    timer = new SOTWTimer(duration.getValue());
                } else {
                    if (timer.isActive()) {
                        sender.sendMessage(ChatColor.RED + "SOTW timer is already running.");
                        return;
                    }
                    timer.setDelay(duration.getValue());
                    timer.run();
                }
                TimerStartEvent event = new TimerStartEvent(timer);
                Bukkit.getPluginManager().callEvent(event);
                if (event.isCancelled()) {
                    return;
                }
                sender.sendMessage(ChatColor.GREEN + "Successfully started SOTW.");
                for (User user : User.getUsers()) {
                    user.setSotw(true);
                }
            }
        }
    }

    @Override
    public List<String> tabComplete(CommandSender sender, Command command, String alias, String[] args) {
        return new ArrayList<>();
    }
}
