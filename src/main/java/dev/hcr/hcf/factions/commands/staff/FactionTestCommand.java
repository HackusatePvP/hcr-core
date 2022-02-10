package dev.hcr.hcf.factions.commands.staff;

import dev.hcr.hcf.factions.claims.Claim;
import dev.hcr.hcf.factions.commands.FactionCommand;
import dev.hcr.hcf.factions.types.PlayerFaction;
import dev.hcr.hcf.users.User;
import dev.hcr.hcf.utils.CC;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class FactionTestCommand extends FactionCommand {

    public FactionTestCommand() {
        super("test", "test", "test");
    }

    @Override
    public void execute(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) return;
        Player player = (Player) sender;
        User user = User.getUser(player.getUniqueId());
        if (user.getFaction() == null) {
            player.sendMessage("No faction!");
            return;
        }
        PlayerFaction playerFaction = (PlayerFaction) user.getFaction();
        List<String> message = new ArrayList<>();
        message.add("&7&m--------------------------------------");
        if (playerFaction.hasClaims()) {
            message.add("&7Claims: &aTRUE");
            for (Claim claim : playerFaction.getClaims()) {
                message.add("  &7Position 1: &c" + claim.getCuboid().getPoint1().getX() + "," + claim.getCuboid().getPoint1().getZ());
                message.add("  &7Position 2: &c" + claim.getCuboid().getPoint2().getX() + "," + claim.getCuboid().getPoint2().getZ());
                message.add("  &7Distance: &c" + claim.getCuboid().getDistance());
                message.add("  &7Distance^: &c" + claim.getCuboid().getDistanceSquared());
                // test mathematical shit that annun failed
                Location point1 = claim.getCuboid().getPoint1();
                Location point2 = claim.getCuboid().getPoint2();
                double m1 = (point2.getZ() - point1.getZ()) / (point1.getX()-point2.getX());
                double m2 = - 1 / m1;
                message.add("  &7M1: &c" + m1);
                message.add("  &7M2: &c" + m2);
            }
        } else {
            message.add("&7Claims: &cFALSE");
        }
        message.add("&7&m--------------------------------------");
        message.forEach(msg -> player.sendMessage(CC.translate(msg)));
    }

    @Override
    public List<String> tabComplete(CommandSender sender, Command command, String alias, String[] args) {
        return null;
    }
}
