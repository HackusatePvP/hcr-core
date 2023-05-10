package dev.hcr.hcf.koths;

import dev.hcr.hcf.factions.Faction;
import dev.hcr.hcf.factions.claims.cuboid.Cuboid;
import dev.hcr.hcf.factions.events.members.FactionTerritoryEnterEvent;
import dev.hcr.hcf.factions.events.members.FactionTerritoryMoveEvent;
import dev.hcr.hcf.timers.Timer;
import dev.hcr.hcf.timers.types.server.KothTimer;
import dev.hcr.hcf.utils.backend.types.PropertiesConfiguration;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerTeleportEvent;

public class KothListener implements Listener {
    private PropertiesConfiguration config = PropertiesConfiguration.getPropertiesConfiguration("hcf.properties");

    @EventHandler
    public void onFactionTerritoryMoveEvent(FactionTerritoryMoveEvent event) {
        Player player = event.getPlayer();
        Faction faction = event.getFaction();
        if (faction instanceof KothFaction) {
            // Player entered the koth fac, maybe change scoreboard or something idk
            // See if they are in the capzone.
            KothFaction kothFaction = (KothFaction) faction;
            if (!kothFaction.isActive()) return;
            if (kothFaction.getCapZone() == null) return;
            if (kothFaction.getCapZone().isIn(player)) {
                // Check if the koth is active
                if (kothFaction.isActive()) {
                    if (kothFaction.getCapper() == null || !kothFaction.getCapZone().isIn(kothFaction.getCapper())) {
                        kothFaction.setCapper(player);
                        player.sendMessage(ChatColor.GREEN + "You are now controlling " + kothFaction.getDisplayName());
                    }
                }
            } else {
                // Check if they were the capper
                if (kothFaction.getCapper() != null && kothFaction.getCapper().getUniqueId() == player.getUniqueId()) {
                    // They were a capper reset koth
                    KothTimer timer = kothFaction.getTimer();
                    timer.reset();
                    // Display that they got knocked or somthin
                }
            }
        }
    }

    @EventHandler
    public void onEnderPearl(PlayerTeleportEvent event) {
        Player player = event.getPlayer();
        if (event.getCause() == PlayerTeleportEvent.TeleportCause.ENDER_PEARL) {
            Faction factionTo = Faction.getByLocation(event.getTo());
            if (factionTo instanceof KothFaction) {
                if (!config.getBoolean("koth-pearl-allowed")) {
                    player.sendMessage(ChatColor.RED + "You are not allowed to use enderpeal into a koth faction.");
                    event.setCancelled(true);
                }
            } // else if conquest.... else if citadel.... ect ect.....
        }
    }
}
