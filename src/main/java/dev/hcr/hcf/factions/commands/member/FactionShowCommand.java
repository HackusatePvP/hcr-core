package dev.hcr.hcf.factions.commands.member;

import dev.hcr.hcf.factions.Faction;
import dev.hcr.hcf.factions.commands.FactionCommand;
import dev.hcr.hcf.factions.structure.SystemFaction;
import dev.hcr.hcf.factions.types.PlayerFaction;
import dev.hcr.hcf.users.User;
import dev.hcr.hcf.utils.CC;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class FactionShowCommand extends FactionCommand {

    public FactionShowCommand() {
        super("show");
    }

    @Override
    public void execute(CommandSender sender, Command command, String label, String[] args) {
        if (args[0].equalsIgnoreCase("show")) {
            if (args.length == 1) {
                if (sender instanceof Player) {
                    User user = User.getUser(((Player) sender).getUniqueId());
                    if (user.getFaction() == null) {
                        sender.sendMessage(ChatColor.RED + "Unknown argument. Usage: /" + label + " show <faction>");
                        return;
                    }
                    printPlayerInfo(user.getFaction(), sender);
                } else {
                    sender.sendMessage(ChatColor.RED + "Unknown argument. Usage: /" + label + " show <faction>");
                }
            }
            if (args.length == 2) {
                String factionName = args[1];
                Faction faction = Faction.getFactionByName(factionName);
                if (faction == null) {
                    sender.sendMessage(ChatColor.RED + "Could not find faction \"" + factionName + "\".");
                    return;
                }
                if (faction instanceof SystemFaction) {
                    printSystemInfo(faction, sender);
                } else if (faction instanceof PlayerFaction) {
                    printPlayerInfo(faction, sender);
                }
            }
        }
    }

    private void printSystemInfo(Faction faction, CommandSender sender) {
        List<String> message = new ArrayList<>();
        message.add("&7&m-----------------------------------------------------");
        message.add("&4" + faction.getName() + ": ");
        message.add("  &7Location: &7(&c0&7,&c0&7)");
        message.add("&7&m-----------------------------------------------------");
        message.forEach(msg -> sender.sendMessage(CC.translate(msg)));
    }

    private void printPlayerInfo(Faction faction, CommandSender sender) {
        List<String> message = new ArrayList<>();

        PlayerFaction playerFaction = (PlayerFaction) faction;
        DecimalFormat format = new DecimalFormat("#.##");
        message.add("&7&m-----------------------------------------------------");
        message.add("&c&l" + playerFaction.getName().toUpperCase() + ": &7[&6" + playerFaction.getOnlineMembers().size() + "&7/&6" + playerFaction.getFactionMembers().size() + "&7]");
        message.add("  &7Home: &cNA"); //TODO implement faction home + claims
        message.add("  &7Balance: &c" + format.format(playerFaction.getBalance()));
        message.add("  &7Leader: &c" + playerFaction.getLeader()); //TODO convert uuid to leader name
        message.add("  &7DTR: &c1.01"); //TODO Implement dtr for factions
        message.add("  &7Points: &c" + playerFaction.getPoints()); //TODO implement faction points
        message.add("&7&m-----------------------------------------------------");

        message.forEach(msg -> sender.sendMessage(CC.translate(msg)));
    }

}
