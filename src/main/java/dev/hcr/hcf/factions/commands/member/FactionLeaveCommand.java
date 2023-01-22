package dev.hcr.hcf.factions.commands.member;

import dev.hcr.hcf.factions.commands.FactionCommand;
import dev.hcr.hcf.factions.events.members.PlayerFactionLeaveEvent;
import dev.hcr.hcf.factions.types.PlayerFaction;
import dev.hcr.hcf.users.User;
import dev.hcr.hcf.users.faction.Role;
import dev.hcr.hcf.utils.CC;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FactionLeaveCommand extends FactionCommand {

    public FactionLeaveCommand() {
        super("leave", "Leave your current faction.");
    }

    @Override
    public void execute(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) return;
        Player player = (Player) sender;
        User user = User.getUser(player.getUniqueId());
        if (user.getFaction() == null) {
            player.sendMessage(ChatColor.RED + "You must be first be in a faction before you can leave.");
            return;
        }
        PlayerFaction faction = (PlayerFaction) user.getFaction();

        if (faction.getRole(player.getUniqueId()) == Role.LEADER) {
            player.sendMessage(ChatColor.RED + "Leaders cannot leave factions. You must either transfer ownership or disband the faction.");
            return;
        }

        PlayerFactionLeaveEvent event = new PlayerFactionLeaveEvent(faction, player);
        Bukkit.getPluginManager().callEvent(event);
        if (event.isCancelled()) {
            player.sendMessage(ChatColor.RED + "You cannot leave your faction at this time.");
            return;
        }
        user.setFaction(null);
        if (faction.removeUserFromFaction(user, false)) {
            faction.broadcast(CC.translate("&7[&4" + faction.getName().toUpperCase() + "&7] &c" + player.getName() + " &7has left the faction."));
        } else {
            player.sendMessage(ChatColor.RED + "Could not leave faction. Are you in combat? Is one of your teammates in combat? Try again later.");
        }
    }

    @Override
    public List<String> tabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();

        Collections.sort(completions);
        return completions;
    }
}
