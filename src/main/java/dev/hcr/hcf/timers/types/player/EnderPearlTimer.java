package dev.hcr.hcf.timers.types.player;

import dev.hcr.hcf.timers.Timer;
import dev.hcr.hcf.timers.events.TimerExpireEvent;
import dev.hcr.hcf.users.User;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

import java.util.concurrent.TimeUnit;

public class EnderPearlTimer extends Timer implements Listener {
    private final Player player;
    private boolean active;
    private long delay;

    public EnderPearlTimer(Player player) {
        super(player, "enderpearl");
        this.player = player;
        this.active = true;
        this.delay = System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(15L);
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        if (active) {
            User user = User.getUser(player.getUniqueId());
            user.getActiveTimers().add(this);
        }
        this.active = active;
    }

    public long getDelay() {
        return delay;
    }

    public void setDelay(long delay) {
        this.delay = delay;
    }

    @Override
    public String getDisplayName(User user) {
        return "&9Ender-Pearl";
    }

    @Override
    public void run() {
        if (isPause()) return;
        if (!active) return;
        long left = delay - System.currentTimeMillis();
        if (left <= 0) {
            TimerExpireEvent event = new TimerExpireEvent(this, player);
            Bukkit.getPluginManager().callEvent(event);
            end(false);
        }
    }

    @Override
    public long getTimeLeft() {
        return delay - System.currentTimeMillis();
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        Player entity = event.getEntity();
        if (entity.getUniqueId() == player.getUniqueId()) {
            end(false);
        }
    }
}
