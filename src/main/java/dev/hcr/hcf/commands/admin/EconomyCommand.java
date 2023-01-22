package dev.hcr.hcf.commands.admin;

import dev.hcr.hcf.HCF;
import dev.hcr.hcf.users.User;
import dev.hcr.hcf.utils.CC;
import dev.hcr.hcf.utils.backend.types.PropertiesConfiguration;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class EconomyCommand implements CommandExecutor, TabCompleter {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            List<String> message = new ArrayList<>();
            message.add("&7&m-----------------------------------------------------");
            message.add("&4&lEconomy &8Admin");
            message.add("");
            message.add("&e* &7/" + label + " &cadd <player> <amount>");
            message.add("&e* &7/" + label + " &cremove <player> <amount>");
            message.add("&e* &7/" + label + " &creset <player>");
            message.add("");
            message.add("&7&m-----------------------------------------------------");
            message.forEach(msg -> sender.sendMessage(CC.translate(msg)));
        } else if (args.length == 2) {
            if (args[0].equalsIgnoreCase("reset")) {
                String name = args[1];
                User user = User.getUser(name);
                PropertiesConfiguration configuration = PropertiesConfiguration.getPropertiesConfiguration("hcf.properties");
                double balance = configuration.getDouble("default-balance");
                user.setBalance(balance);
                sender.sendMessage(ChatColor.GREEN + "You have reset &b" + name + " &abalance!");
            } else {
                sender.sendMessage(ChatColor.RED + "Unknown argument, did you mean reset?");
            }
        } else if (args.length == 3) {
            if (args[0].equalsIgnoreCase("add")) {
                String name = args[1];
                User user = User.getUser(name);
                double amount = -1D;
                try {
                    amount = Double.parseDouble(args[2]);
                } catch (NullPointerException ignored) {
                    sender.sendMessage(ChatColor.RED + "Expected double, integer instead of \"" + args[2] + "\".");
                    return true;
                } finally {
                    if (amount != -1D) {
                        user.addToBalance(amount);
                        sender.sendMessage(CC.translate("Successfully added \"" + HCF.getPlugin().getFormat().format(amount) + "\" to &b" + user.getName() + "&a."));
                    } else {
                        sender.sendMessage(ChatColor.RED + "Balance must be positive.");
                    }
                }
            } else if (args[0].equalsIgnoreCase("remove")) {
                String name = args[1];
                User user = User.getUser(name);
                double amount = -1D;
                try {
                    amount = Double.parseDouble(args[2]);
                } catch (NullPointerException ignored) {
                    sender.sendMessage(ChatColor.RED + "Expected double, integer instead of \"" + args[2] + "\".");
                    return true;
                } finally {
                    if (amount != -1D) {
                        user.takeFromBalance(amount);
                        sender.sendMessage(CC.translate("Successfully removed \"" + HCF.getPlugin().getFormat().format(amount) + "\" to &b" + user.getName() + "&a."));
                    } else {
                        sender.sendMessage(ChatColor.RED + "Balance must be positive.");
                    }
                }
            } else if (args[0].equalsIgnoreCase("set")) {
                String name = args[1];
                User user = User.getUser(name);
                double amount = -1D;
                try {
                    amount = Double.parseDouble(args[2]);
                } catch (NullPointerException ignored) {
                    sender.sendMessage(ChatColor.RED + "Expected double, integer instead of \"" + args[2] + "\".");
                    return true;
                } finally {
                    if (amount != -1D) {
                        user.setBalance(amount);
                        sender.sendMessage(CC.translate("Successfully set \"" + HCF.getPlugin().getFormat().format(amount) + "\" to &b" + user.getName() + "&a."));
                    } else {
                        sender.sendMessage(ChatColor.RED + "Balance must be positive.");
                    }
                }
            } else {
                sender.sendMessage(ChatColor.RED + "Unknown argument, did you mean add,remove,set?");
            }
        }
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();
        if (args.length == 1) {
            StringUtil.copyPartialMatches(args[0], Arrays.asList("add", "remove", "reset", "set"), completions);
        }
        if (args.length == 2) {
            Bukkit.getOnlinePlayers().forEach(player -> completions.add(player.getName()));
        }
        Collections.sort(completions);
        return completions;
    }
}
