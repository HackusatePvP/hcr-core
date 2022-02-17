package dev.hcr.hcf.timers.types.player;

import dev.hcr.hcf.HCF;
import dev.hcr.hcf.timers.Timer;
import dev.hcr.hcf.timers.events.TimerExpireEvent;
import dev.hcr.hcf.timers.events.TimerStopEvent;
import dev.hcr.hcf.users.User;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

public class EnderPearlTimer extends Timer implements Listener {
    private final Player player;
    private boolean active;
    private long delay;

    public EnderPearlTimer(Player player) {
        super(player, "enderpearl");
        this.player = player;
        this.active = true;
        this.delay = 15;
        this.runTaskTimerAsynchronously(HCF.getPlugin(), 20L, 20L);
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
    public void run() {
        if (!active) return;
        --delay;
        if (delay <= 0) {
            TimerExpireEvent event = new TimerExpireEvent(this, player);
            Bukkit.getPluginManager().callEvent(event);
            end(false);
        }
    }

    public void end(boolean forced) {
        if (forced) {
            TimerStopEvent event = new TimerStopEvent(this, player);
            Bukkit.getPluginManager().callEvent(event);
        }
        this.delay = 0;
        this.active = false;
        cancel();
        super.end();
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        Player entity = event.getEntity();
        if (entity.getUniqueId() == player.getUniqueId()) {
            end(false);
        }
    }
}
