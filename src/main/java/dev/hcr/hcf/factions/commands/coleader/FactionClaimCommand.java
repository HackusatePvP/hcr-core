package dev.hcr.hcf.factions.commands.coleader;

import dev.hcr.hcf.factions.commands.FactionCommand;
import dev.hcr.hcf.factions.types.PlayerFaction;
import dev.hcr.hcf.listeners.factions.FactionClaimingListener;
import dev.hcr.hcf.listeners.factions.data.FactionClaimingData;
import dev.hcr.hcf.users.User;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.ArrayList;
import java.util.List;

public class FactionClaimCommand extends FactionCommand {

    public FactionClaimCommand() {
        super("claim", "Claim land for your faction.");
    }

    @Override
    public void execute(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) return;
        Player player = (Player) sender;
        User user = User.getUser(player.getUniqueId());
        if (user.getFaction() == null) {
            player.sendMessage(ChatColor.RED + "You must be in a faction to claim land.");
            return;
        }
        PlayerFaction playerFaction = (PlayerFaction) user.getFaction();
        if (playerFaction.getRole(user).getWeight() < 3) {
            player.sendMessage(ChatColor.RED + "Only Co-Leaders and above can claim land.");
            return;
        }
        if (playerFaction.hasClaims()) {
            player.sendMessage(ChatColor.RED + "Your faction already has territory claimed. To expand claims you must unclaim all and reclaim the land.");
            return;
        }
        if (FactionClaimingListener.claiming.containsKey(user)) {
            player.sendMessage(ChatColor.RED + "You are already claiming for a faction.");
            return;
        }
        if (!hasInventorySpace(player)) {
            player.sendMessage(ChatColor.RED + "You need to empty one slot in your hotbar.");
            return;
        }
        player.getInventory().addItem(FactionClaimingListener.getClaimingWand());
        FactionClaimingListener.startClaiming(player, new FactionClaimingData(user, playerFaction, false));
    }

    @Override
    public List<String> tabComplete(CommandSender sender, Command command, String alias, String[] args) {
        return new ArrayList<>();
    }

    public static boolean hasInventorySpace(Player player) {
        PlayerInventory inventory = player.getInventory();
        for (ItemStack itemStack : inventory.getContents()) {
            if (itemStack == null) {
                return true;
            }
        }
        return false;
    }
}
