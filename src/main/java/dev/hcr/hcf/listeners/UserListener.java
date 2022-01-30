package dev.hcr.hcf.listeners;

import dev.hcr.hcf.users.User;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;

public class UserListener implements Listener {

    @EventHandler
    public void onAsyncJoin(AsyncPlayerPreLoginEvent event) {
        if (event.getLoginResult() == AsyncPlayerPreLoginEvent.Result.ALLOWED) {
            // TODO: 1/29/2022 Load users on database implementation
            new User(event.getUniqueId(), event.getName());
        }
    }
}
