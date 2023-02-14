package dev.hcr.hcf.koths.commands.staff;

import dev.hcr.hcf.factions.Faction;
import dev.hcr.hcf.factions.claims.cuboid.Cuboid;
import dev.hcr.hcf.koths.KothFaction;
import dev.hcr.hcf.koths.commands.KothCommand;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class KothSetCenterZoneCommand extends KothCommand {


    public KothSetCenterZoneCommand() {
        super("setcenterzone", "Set the radius of the center cap zone.");
    }

    @Override
    public void execute(CommandSender sender, String label, String[] args) {
        if (!(sender instanceof Player)) return;
        Player player = (Player) sender;
        if (args.length == 0) {
            player.sendMessage(ChatColor.RED + "Stand in the center of the capzone and enter the radius. /koth setcenterzone <koth> <int>");
            return;
        }
        String name = args[1];
        Faction faction = Faction.getFactionByName(name);
        if (faction == null) {
            player.sendMessage(ChatColor.RED + "Koth faction not found.");
            return;
        }
        int radius = -1;
        try {
            radius = Integer.parseInt(args[2]);
            player.sendMessage("Calculating...");
        } catch (NumberFormatException ignored) {
            sender.sendMessage(ChatColor.RED + "Invalid integer type \"" + args[1] + "\"");
        } finally {
            if (radius != -1) {
                player.sendMessage("Wait a moment.");
                Location location = player.getLocation();
                if (Faction.getByLocation(location) != null && !Faction.getByLocation(location).getName().equalsIgnoreCase(faction.getName())) {
                    player.sendMessage(ChatColor.RED + "You must be inside the claim of the koth you are trying to claim.");
                } else if (Faction.getByLocation(location) != null) {
                    Location corner1 = player.getLocation().add(-radius, 0, -radius);
                    player.sendMessage("Corner 1: " + corner1.toString());
                    Location corner2 = player.getLocation().add(radius, 255, radius);
                    player.sendMessage("Corner 2: " + corner2.toString());
                    Cuboid cuboid = new Cuboid(corner1, corner2);
                    KothFaction koth = (KothFaction) faction;
                    koth.setCapZone(cuboid);
                    koth.setCenter(player.getLocation());
                    koth.setRadius(radius);
                    player.sendMessage("Done!");
                }
            }
        }
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String alias, String[] args) {
        return new ArrayList<>();
    }
}
