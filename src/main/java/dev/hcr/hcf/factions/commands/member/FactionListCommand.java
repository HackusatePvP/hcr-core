package dev.hcr.hcf.factions.commands.member;

import dev.hcr.hcf.HCF;
import dev.hcr.hcf.factions.Faction;
import dev.hcr.hcf.factions.commands.FactionCommand;
import dev.hcr.hcf.factions.types.PlayerFaction;
import dev.hcr.hcf.utils.CC;
import dev.hcr.hcf.utils.backend.types.PropertiesConfiguration;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class FactionListCommand extends FactionCommand {
    private final Map<PlayerFaction, Integer> pageMap = new HashMap<>();

    public FactionListCommand() {
        super("list", "View all online factions' general information.");

        new BukkitRunnable() {
            @Override
            public void run() {
                pageMap.clear();
                pagination();
            }
        }.runTaskTimerAsynchronously(HCF.getPlugin(), 5L, 20 * 10); // 20 ticks represents 1 second. 600 seconds = 10min (20 * 600)
    }

    void pagination() {
        List<PlayerFaction> onlineFactions = new ArrayList<>();
        for (Faction faction : Faction.getFactions()) {
            if (faction instanceof PlayerFaction) {
                PlayerFaction playerFaction = (PlayerFaction) faction;
                if (playerFaction.getTotalOnlineMembers() > 0) {
                    onlineFactions.add(playerFaction);
                }
            }
        }

       onlineFactions.sort(Comparator.comparing(PlayerFaction::getTotalOnlineMembers)); // Sort the list from highest to lowest hopefully?

        int items = onlineFactions.size();
        int count = 1;
        int page = 1;
        if (items == 0) {
            return;
        }
        do {
            if (count == 10) {
                // For every 10 factions we will make a new page
                page++;
                count = 0;
            }
            PlayerFaction playerFaction = onlineFactions.get(items - 1);
            // Store command to the page map
            pageMap.put(playerFaction, page);
            // Remove the command from the array
            onlineFactions.remove(playerFaction);
            count++;
            --items;
        } while (!onlineFactions.isEmpty());
    }

    @Override
    public void execute(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) return;
        Player player = (Player) sender;
        List<String> message = new ArrayList<>();
        if (args.length < 2) {
            player.sendMessage(CC.translate("&7&m-------------------------------------------------------------"));
            player.sendMessage(CC.translate("&cFaction List &7(&c" + 1 + "&7/&c" + getMaxPage() + "&7)"));

            if (pageMap.isEmpty()) {
                player.sendMessage("");
                player.sendMessage(CC.translate("No faction information found."));

            } else {
                int index = 0;
                for (PlayerFaction playerFaction : pageMap.keySet()) {
                    index++;
                    if (pageMap.get(playerFaction) == 1) {
                        TextComponent part1 = new TextComponent(index + ". ");
                        part1.setColor(net.md_5.bungee.api.ChatColor.GRAY);
                        TextComponent part2 = new TextComponent(playerFaction.getName() + " ");
                        part2.setColor(net.md_5.bungee.api.ChatColor.RED);
                        part2.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                                new ComponentBuilder("DTR: ").color(net.md_5.bungee.api.ChatColor.GRAY)
                                        .append(playerFaction.getCurrentDTR() + " ").color(playerFaction.getRegenStatus().getColor().asBungee())
                                        .append("/ ").color(net.md_5.bungee.api.ChatColor.GRAY)
                                        .append(playerFaction.getMaxDTR() + "").color((playerFaction.isRaidable() ? net.md_5.bungee.api.ChatColor.DARK_RED : net.md_5.bungee.api.ChatColor.GREEN)).create()));
                        part2.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/f show " + playerFaction.getName()));
                        TextComponent part3 = new TextComponent("(");
                        part3.setColor(net.md_5.bungee.api.ChatColor.GRAY);
                        TextComponent part4 = new TextComponent(playerFaction.getTotalOnlineMembers() + " ");
                        part4.setColor(net.md_5.bungee.api.ChatColor.GREEN);
                        TextComponent part5 = new TextComponent("/");
                        part5.setColor(net.md_5.bungee.api.ChatColor.GRAY);
                        TextComponent part6 = new TextComponent(playerFaction.getFactionMembers().size() + "");
                        part6.setColor(net.md_5.bungee.api.ChatColor.GREEN);
                        TextComponent part7 = new TextComponent(")");
                        part7.setColor(net.md_5.bungee.api.ChatColor.GREEN);
                        player.spigot().sendMessage(part1, part2, part3, part4, part5, part6, part7);
                    }
                }
            }
            player.sendMessage(CC.translate(""));
            player.sendMessage(CC.translate("&7You are on page (" + 1 + "/" + getMaxPage() + ") To view the next page use the command \"/" + label + " " + this.getName() + " <page>.\""));
            player.sendMessage(CC.translate("&7&m-------------------------------------------------------------"));
        }
        if (args.length == 2) {
            message.add("&7&m-------------------------------------------------------------");
            int page = Integer.parseInt(args[1]);
            if (page > getMaxPage()) {
                message.add(ChatColor.RED + "This page doesn't exist!");
            } else {
                message.add("&cFaction List &7(&c" + page + "&7/&c" + getMaxPage() + "&7)");
                message.add("");
                int index = 0;
                for (PlayerFaction playerFaction : pageMap.keySet()) {
                    index++;
                    if (pageMap.get(playerFaction) == page) {
                        TextComponent part1 = new TextComponent(index + ". ");
                        part1.setColor(net.md_5.bungee.api.ChatColor.GRAY);
                        TextComponent part2 = new TextComponent(playerFaction.getName() + " ");
                        part2.setColor(net.md_5.bungee.api.ChatColor.RED);
                        part2.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                                new ComponentBuilder("DTR: ").color(net.md_5.bungee.api.ChatColor.GRAY)
                                        .append(playerFaction.getCurrentDTR() + " ").color(playerFaction.getRegenStatus().getColor().asBungee())
                                        .append("/ ").color(net.md_5.bungee.api.ChatColor.GRAY)
                                        .append(playerFaction.getMaxDTR() + "").color((playerFaction.isRaidable() ? net.md_5.bungee.api.ChatColor.DARK_RED : net.md_5.bungee.api.ChatColor.GREEN)).create()));
                        TextComponent part3 = new TextComponent("(");
                        part3.setColor(net.md_5.bungee.api.ChatColor.GRAY);
                        TextComponent part4 = new TextComponent(playerFaction.getTotalOnlineMembers() + " ");
                        part4.setColor(net.md_5.bungee.api.ChatColor.GREEN);
                        TextComponent part5 = new TextComponent("/");
                        part5.setColor(net.md_5.bungee.api.ChatColor.GRAY);
                        TextComponent part6 = new TextComponent(PropertiesConfiguration.getPropertiesConfiguration("faction.properties").getInteger("max-team-size") + "");
                        part6.setColor(net.md_5.bungee.api.ChatColor.GREEN);
                        player.spigot().sendMessage(part1, part2, part3, part4, part5, part6);
                    }
                }
                message.add("");
                message.add("&7You are on page (" + 1 + "/" + getMaxPage() + ") To view the next page use the command \"/" + label + " " + this.getName() + " <page>.\"");
            }
            message.add("&7&m-------------------------------------------------------------");
        }
        message.forEach(msg -> sender.sendMessage(CC.translate(msg)));
    }

    private int getMaxPage() {
        if (pageMap.isEmpty()) {
            return 0;
        }
        Map.Entry<PlayerFaction, Integer> maxEntry = Collections.max(pageMap.entrySet(), Map.Entry.comparingByValue());
        return maxEntry.getValue();
    }

    @Override
    public List<String> tabComplete(CommandSender sender, Command command, String alias, String[] args) {
        return new ArrayList<>();
    }
}
