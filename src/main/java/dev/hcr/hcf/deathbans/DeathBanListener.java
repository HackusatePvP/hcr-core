package dev.hcr.hcf.deathbans;

import dev.hcr.hcf.HCF;
import dev.hcr.hcf.factions.Faction;
import dev.hcr.hcf.timers.Timer;
import dev.hcr.hcf.users.User;
import dev.hcr.hcf.utils.CC;
import org.apache.commons.lang.time.DurationFormatUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;

import java.util.HashSet;
import java.util.UUID;

public class DeathBanListener implements Listener {
    private final HashSet<UUID> livesReconnect = new HashSet<>();
    private final HCF plugin = HCF.getPlugin();

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        // Before applying a deathban we must check
        //  1. If the territory the player is in has death bans active.
        //  2. If the player has bypass permissions.
        //  3. If SOTW or MOTW is active.
        //  4. Calculate rank deathban time.
        Faction deathTerritory = Faction.getByLocation(player.getLocation());
        if (!deathTerritory.isDeathBan()) return;
        if (player.hasPermission("hcf.deathban.bypass")) return;
        if (Timer.getTimer("sotw") != null || Timer.getTimer("motw") != null) return;
        String rank = plugin.getCore().getRankByPlayer(player);
        long duration = DeathBan.getRankDuration(rank);
        DeathBan deathBan = new DeathBan(player.getUniqueId(), duration);
        deathBan.execute(player);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onAsyncPreLogin(AsyncPlayerPreLoginEvent event) {
        UUID uuid = event.getUniqueId();
        DeathBan deathBan = DeathBan.getActiveDeathBan(uuid);
        if (deathBan != null) {
            if (deathBan.getTimeLeft() <= 0L) {
                deathBan.complete();
                return;
            }
            User user = User.getUser(uuid);
            if (user.getLives() > 0) {
                if (!livesReconnect.contains(uuid)) {
                    event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_BANNED, CC.translate("&cYou are now deathbanned for: " +
                            "\n&c" + DurationFormatUtils.formatDurationWords(deathBan.getTimeLeft(), true, true) +
                            "\n&cYou can buy lives @ www.hcr.dev" +
                            "\n&cReconnect again to use a life."));
                    livesReconnect.add(uuid);
                } else {
                    user.setLives(user.getLives() - 1);
                    livesReconnect.remove(uuid);
                    deathBan.complete();
                }
            } else {
                event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_BANNED, CC.translate("&cYou are now deathbanned for:" +
                        "\n&c" + DurationFormatUtils.formatDurationWords(deathBan.getTimeLeft(), true, true) +
                        "\n&cYou can buy lives @ www.hcr.dev"));
            }
        }
    }

}
