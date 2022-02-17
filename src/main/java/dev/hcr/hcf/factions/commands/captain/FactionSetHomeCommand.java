package dev.hcr.hcf.factions.commands.captain;

import dev.hcr.hcf.factions.claims.Claim;
import dev.hcr.hcf.factions.commands.FactionCommand;
import dev.hcr.hcf.factions.types.PlayerFaction;
import dev.hcr.hcf.users.User;
import dev.hcr.hcf.utils.CC;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class FactionSetHomeCommand extends FactionCommand {

    public FactionSetHomeCommand() {
        super("sethome", "Set your factions home.");
    }

    @Override
    public void execute(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) return;
        Player player = (Player) sender;
        User user = User.getUser(player.getUniqueId());
        if (user.getFaction() == null) {
            player.sendMessage(ChatColor.RED + "You must be in a faction to set a faction home.");
            return;
        }
        PlayerFaction playerFaction = (PlayerFaction) user.getFaction();
        if (playerFaction.getRole(user).getWeight() < 2) {
            player.sendMessage(ChatColor.RED + "You must be atleast a captain to set a faction home.");
            return;
        }
        if (!playerFaction.hasClaims()) {
            player.sendMessage(ChatColor.RED + "Your faction needs a claim before you can set a faction home.");
            return;
        }
        Location location = player.getLocation();
        //Claim claim = playerFaction.getClaims().stream().findFirst().orElse(playerFaction.getClaims().stream().findFirst().orElse(null));
        Claim claim = playerFaction.getClaims().stream().filter(claim1 -> claim1.getCuboid().isIn(location)).findAny().orElse(null);
        if (claim == null) {
            player.sendMessage(ChatColor.RED + "You can only a set a faction home inside your own territory.");
            return;
        }
        playerFaction.setHome(location);
        playerFaction.broadcast(CC.translate("&d[" + playerFaction.getName() + "] " + playerFaction.getRole(user).getAstrix() + " " + player.getName() + " has updated the faction home."));
    }

    @Override
    public List<String> tabComplete(CommandSender sender, Command command, String alias, String[] args) {
        return null;
    }
}
