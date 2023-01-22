package dev.hcr.hcf.factions.commands.member;

import dev.hcr.hcf.factions.commands.FactionCommand;
import dev.hcr.hcf.packets.RemoveFactionMapPillarPacketsEvent;
import dev.hcr.hcf.packets.SendFactionMapPacketsEvent;
import dev.hcr.hcf.users.User;
import dev.hcr.hcf.utils.TaskUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class FactionMapCommand extends FactionCommand {

    public FactionMapCommand() {
        super("map", "Displays all faction claims near you.");
    }

    @Override
    public void execute(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) return;
        Player player = (Player) sender;
        User user = User.getUser(player.getUniqueId());
        if (user.hasFactionMap()) {
            player.sendMessage(ChatColor.RED + "Removing pillars...");
            RemoveFactionMapPillarPacketsEvent packetsEvent = new RemoveFactionMapPillarPacketsEvent(player);
            TaskUtils.runAsync(() -> {
                Bukkit.getPluginManager().callEvent(packetsEvent);
            });
            return;
        }
        user.setFactionMap(true);
        SendFactionMapPacketsEvent mapPacketsEvent = new SendFactionMapPacketsEvent(player);
        TaskUtils.runAsync(() -> {
            Bukkit.getPluginManager().callEvent(mapPacketsEvent);
        });
    }

    @Override
    public List<String> tabComplete(CommandSender sender, Command command, String alias, String[] args) {
        return new ArrayList<>();
    }
}
