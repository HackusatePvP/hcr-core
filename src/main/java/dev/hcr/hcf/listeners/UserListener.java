package dev.hcr.hcf.listeners;

import dev.hcr.hcf.HCF;

import dev.hcr.hcf.users.User;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class UserListener implements Listener {

    @EventHandler
    public void onAsyncJoin(AsyncPlayerPreLoginEvent event) {
        if (event.getLoginResult() == AsyncPlayerPreLoginEvent.Result.ALLOWED) {
            // TODO: 1/29/2022 Load users on database implementation
            HCF.getPlugin().getMongoImplementation().loadUserAsync(event.getUniqueId(), event.getName());
        }
    }

    @EventHandler
    public void onDisconnect(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        User user = User.getUser(player.getUniqueId());
        HCF.getPlugin().getMongoImplementation().appendUserDataSync(user.save());
    }
}
