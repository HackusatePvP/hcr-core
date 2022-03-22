package dev.hcr.hcf.factions.commands.member;

import dev.hcr.hcf.HCF;
import dev.hcr.hcf.factions.Faction;
import dev.hcr.hcf.factions.commands.FactionCommand;
import dev.hcr.hcf.factions.types.SafeZoneFaction;
import dev.hcr.hcf.factions.types.SystemFaction;
import dev.hcr.hcf.factions.types.PlayerFaction;
import dev.hcr.hcf.factions.types.roads.RoadFaction;
import dev.hcr.hcf.users.User;
import dev.hcr.hcf.users.faction.Role;
import dev.hcr.hcf.utils.CC;
import dev.hcr.hcf.utils.PlayerUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class FactionShowCommand extends FactionCommand {

    public FactionShowCommand() {
        super("show", "View other factions information.");
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

    @Override
    public List<String> tabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();
        List<String> factions = new ArrayList<>();
        Faction.getFactions().forEach(faction -> factions.add(faction.getName()));
        if (args.length == 2) {
            StringUtil.copyPartialMatches(args[1], factions, completions);
        }
        Collections.sort(completions);
        return completions;
    }

    private void printSystemInfo(Faction faction, CommandSender sender) {
        List<String> message = new ArrayList<>();
        message.add("&7&m-----------------------------------------------------");
        if (faction instanceof SafeZoneFaction) {
            message.add("&a&lSafeZone");
            message.add("");
            message.add("&7Location: &c[" + faction.getHome().getBlockX() + "&7," + faction.getHome().getBlockZ() + "]");
        } else if (faction instanceof RoadFaction) {
            message.add("&6&l" + faction.getDisplayName());
            message.add("");
            message.add("&7Direction: &c" + ((RoadFaction) faction).getDirection());
        } else {
            message.add(faction.getColor() + faction.getName());
            message.add("");
            message.add("&cNo information.");
        }
        message.add("&7&m-----------------------------------------------------");
        message.forEach(msg -> sender.sendMessage(CC.translate(msg)));
    }

    private void printPlayerInfo(Faction faction, CommandSender sender) {
        List<String> message = new ArrayList<>();
        PlayerFaction playerFaction = (PlayerFaction) faction;
        message.add("&7&m-----------------------------------------------------");
        message.add("&c&l" + playerFaction.getName().toUpperCase() + ": &7[&6" + playerFaction.getOnlineMembers().size() + "&7/&6" + playerFaction.getFactionMembers().size() + "&7]");
        if (playerFaction.getHome() != null) {
            message.add("  &7Home: &7[&c" + playerFaction.getHome().getBlockX() + "," + playerFaction.getHome().getBlockZ() + "&7]"); //TODO implement faction home + claims
        } else {
            message.add("  &7Home: &c[N/A]");
        }
        message.add("  &7Balance: &c" + HCF.getPlugin().getFormat().format(playerFaction.getBalance()));
        if (Bukkit.getPlayer(playerFaction.getLeader()) == null) {
            message.add("  &7Leader: &c" + PlayerUtils.getPlayerNameByUUID(playerFaction.getLeader()));
        } else {
            message.add("  &7Leader: &a" + Bukkit.getPlayer(playerFaction.getLeader()).getName());
        }
        List<UUID> coleaders = new ArrayList<>();
        List<UUID> captains = new ArrayList<>();
        List<UUID> members = new ArrayList<>();
        for (UUID uuid : playerFaction.getFactionMembers()) {
            if (playerFaction.getRole(uuid) == Role.COLEADER) {
                coleaders.add(uuid);
            } else if (playerFaction.getRole(uuid) == Role.CAPTAIN) {
                captains.add(uuid);
            } else if (playerFaction.getRole(uuid) != Role.LEADER) {
                members.add(uuid);
            }
        }
        StringBuilder builder = new StringBuilder();
        builder.append("  &7Co-Leaders: &7[");
        handlePlayerRoles(coleaders, builder);
        builder.append("&7]");
        message.add(builder.toString());
        builder = new StringBuilder();
        builder.append("  &7Captain: &7[");
        handlePlayerRoles(captains, builder);
        builder.append("&7]");
        message.add(builder.toString());
        builder = new StringBuilder();
        builder.append("  &7Members: &7[");
        handlePlayerRoles(members, builder);
        builder.append("&7]");
        message.add(builder.toString());
        message.add("  &7Current DTR: " + playerFaction.getFormattedCurrentDTR());
        message.add("  &7Points: &c" + playerFaction.getPoints()); //TODO implement faction points
        message.add("&7&m-----------------------------------------------------");
        message.forEach(msg -> sender.sendMessage(CC.translate(msg)));
    }

    private void handlePlayerRoles(List<UUID> group, StringBuilder builder) {
        if (group.isEmpty()) {
            builder.append("&cN/A");
            return;
        }
        for (UUID uuid : group) {
            if (Bukkit.getPlayer(uuid) == null) {
                System.out.println("Length: " + builder.length());
                if (builder.length() > 16) {
                    builder.append(", &c");
                } else {
                    builder.append("&c");
                }
                builder.append(PlayerUtils.getPlayerNameByUUID(uuid));
            } else {
                System.out.println("Length: " + builder.length());
                if (builder.length() > 16) {
                    builder.append(", &a");
                } else {
                    builder.append("&a");
                }
                builder.append(Bukkit.getPlayer(uuid).getName());
            }
        }
    }

}
