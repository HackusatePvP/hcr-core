package dev.hcr.hcf.factions.commands.member;

import dev.hcr.hcf.factions.Faction;
import dev.hcr.hcf.factions.commands.FactionCommand;
import dev.hcr.hcf.factions.types.PlayerFaction;
import dev.hcr.hcf.factions.types.SafeZoneFaction;
import dev.hcr.hcf.timers.types.player.faction.FactionHomeTimer;
import dev.hcr.hcf.users.User;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class FactionHomeCommand extends FactionCommand {

    public FactionHomeCommand() {
        super("home", "Teleport to your factions home.");
    }

    @Override
    public void execute(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) return;
        Player player = (Player) sender;
        User user = User.getUser(player.getUniqueId());
        if (user.getFaction() == null) {
            player.sendMessage(ChatColor.RED + "You must be in a faction to teleport to a faction home.");
            return;
        }
        PlayerFaction faction = (PlayerFaction) user.getFaction();
        boolean playersNearby = false;
        for (Entity entity : player.getNearbyEntities(50, 100, 50)) {
            if (entity instanceof Player) {
                Player found = (Player) entity;
                if (faction.hasMember(found.getUniqueId())) continue;
                playersNearby = true;
            }
        }
        if (faction.getHome() == null) {
            player.sendMessage(ChatColor.RED + "Yor faction does not have a home set. /" + label + " sethome");
            return;
        }

        if (playersNearby && (!(Faction.getByLocation(player.getLocation()) instanceof SafeZoneFaction))) {
            user.setTimer("faction_home", true);
        } else {
            player.teleport(faction.getHome());
        }
    }

    @Override
    public List<String> tabComplete(CommandSender sender, Command command, String alias, String[] args) {
        return new ArrayList<>();
    }
}
