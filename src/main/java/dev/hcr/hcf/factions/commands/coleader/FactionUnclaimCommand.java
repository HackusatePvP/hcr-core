package dev.hcr.hcf.factions.commands.coleader;

import dev.hcr.hcf.HCF;
import dev.hcr.hcf.factions.commands.FactionCommand;
import dev.hcr.hcf.factions.types.PlayerFaction;
import dev.hcr.hcf.users.User;
import dev.hcr.hcf.utils.CC;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class FactionUnclaimCommand extends FactionCommand {


    public FactionUnclaimCommand() {
        super("unclaim", "Unclaims all land owned by the faction.");
    }

    @Override
    public void execute(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) return;
        Player player = (Player) sender;
        User user = User.getUser(player.getUniqueId());
        if (user.getFaction() == null) {
            player.sendMessage(ChatColor.RED + "You must be in a faction to unclaim land.");
            return;
        }
        PlayerFaction playerFaction = (PlayerFaction) user.getFaction();
        if (playerFaction.getRole(user).getWeight() < 3) {
            player.sendMessage(ChatColor.RED + "Only co-leaders and above can claim land.");
            return;
        }
        if (!playerFaction.hasClaims()) {
            player.sendMessage(ChatColor.RED + "You have no land to unclaim.");
            return;
        }
        double landValue = playerFaction.getLandRefundPrice();
        if (landValue <= 0) {
            player.sendMessage(ChatColor.RED + "Safety check triggered! It appears you have land however the refund price could not be calculated. Please report to staff immediately.");
            return;
        }
        playerFaction.unclaim();
        playerFaction.addToBalance(landValue);
        playerFaction.broadcast(CC.translate("&7[&4" + getName().toUpperCase() + "&7] &c" + player.getName() + " &7has &c&nUNCLAIMED&r &7all land. " + HCF.getPlugin().getFormat().format(landValue) + " &7has been refunded to the faction balance."));
    }

    @Override
    public List<String> tabComplete(CommandSender sender, Command command, String alias, String[] args) {
        return new ArrayList<>();
    }
}
