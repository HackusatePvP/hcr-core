package dev.hcr.hcf.factions.commands.member;

import dev.hcr.hcf.HCF;
import dev.hcr.hcf.factions.commands.FactionCommand;
import dev.hcr.hcf.factions.types.PlayerFaction;
import dev.hcr.hcf.users.User;
import dev.hcr.hcf.utils.CC;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class FactionDepositCommand extends FactionCommand {

    public FactionDepositCommand() {
        super("deposit", "Deposit money into the faction balance.", "", new String[]{"d"});
    }

    @Override
    public void execute(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) return;
        Player player = (Player) sender;
        User user = User.getUser(player.getUniqueId());
        PlayerFaction faction = (PlayerFaction) user.getFaction();
        if (faction == null) {
            sender.sendMessage(ChatColor.RED + "You must be in a faction to deposit money.");
            return;
        }
        if (args.length == 2) {
            if (args[1].equalsIgnoreCase("a") || args[1].equalsIgnoreCase("all")) {
                double amount = user.getBalance();
                if (amount <= 0) {
                    player.sendMessage(ChatColor.RED + "You must have a positive balance.");
                    return;
                }
                user.takeFromBalance(amount);
                faction.depositBalance(amount);
                faction.broadcast(CC.translate("&7[&4" + faction.getName().toUpperCase() + "&7] &c" + player.getName() + " &7has deposited &6" + HCF.getPlugin().getFormat().format(amount) + " &7to the faction balance."));
            } else {
                double amount = -1D;
                try {
                    amount = Double.parseDouble(args[1]);
                } catch (NullPointerException ignored) {
                    sender.sendMessage(ChatColor.RED + "Expected double, integer instead of \"" + args[1] + "\".");
                } finally {
                    if (amount != -1D) {
                        user.takeFromBalance(amount);
                        faction.depositBalance(amount);
                        faction.broadcast(CC.translate("&7[&4" + faction.getName().toUpperCase() + "&7] &c" + player.getName() + " &7has deposited &6" + HCF.getPlugin().getFormat().format(amount) + " &7to the faction balance."));
                    } else {
                        sender.sendMessage(ChatColor.RED + "Amount must be a positive number.");
                    }
                }
            }
        }
    }

    @Override
    public List<String> tabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();
        if (args.length == 2) {
            StringUtil.copyPartialMatches(args[1], Arrays.asList("all"), completions);
        }
        Collections.sort(completions);
        return completions;
    }
}
