package dev.hcr.hcf.listeners.player;

import dev.hcr.hcf.HCF;

import dev.hcr.hcf.timers.Timer;
import dev.hcr.hcf.timers.types.PauseTimer;
import dev.hcr.hcf.timers.types.player.PvPTimer;
import dev.hcr.hcf.users.User;
import dev.hcr.hcf.utils.backend.types.PropertiesConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.Collection;
import java.util.HashSet;
import java.util.UUID;

public class UserListener implements Listener {
    private final Collection<UUID> newPlayers = new HashSet<>();

    @EventHandler
    public void onAsyncJoin(AsyncPlayerPreLoginEvent event) {
        if (event.getLoginResult() == AsyncPlayerPreLoginEvent.Result.ALLOWED) {
            boolean profileExists = HCF.getPlugin().getStorage().userExists(event.getUniqueId());
            HCF.getPlugin().getStorage().loadUserAsync(event.getUniqueId(), event.getName());
            if (!profileExists) {
                // If the profile does not exist and once the user is loaded give the player a pvptimer
                newPlayers.add(event.getUniqueId());
            }
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        User user = User.getUser(player.getUniqueId());
        for (Timer timer : user.getActiveTimers()) {
            timer.setPause(false);
        }
        if (newPlayers.contains(player.getUniqueId())) {
            if (Timer.getTimer("sotw") == null || !Timer.getTimer("sotw").isActive()) {
                if (!user.hasActiveTimer("pvp")) {
                    user.setTimer("pvp", true);
                }
                newPlayers.remove(player.getUniqueId());
                PropertiesConfiguration configuration = PropertiesConfiguration.getPropertiesConfiguration("hcf.properties");
                double balance = configuration.getDouble("default-balance");
                user.setBalance(balance);
            }
        }
    }

    @EventHandler
    public void onDisconnect(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        save(player.getUniqueId());
    }

    private void save(UUID uuid) {
        User user = User.getUser(uuid);
        HCF.getPlugin().getStorage().appendUserDataSync(user.save());
        for (Timer timer : user.getActiveTimers()) {
            if (timer instanceof PvPTimer) {
                ((PvPTimer) timer).setPause(true);
                System.out.println("Paused PVPTimer!");
            }
            if (timer instanceof PauseTimer) {
                timer.save();
            }
        }
    }
}
