package dev.hcr.hcf.timers.types.player;

import dev.hcr.hcf.pvpclass.PvPClass;
import dev.hcr.hcf.timers.Timer;
import dev.hcr.hcf.timers.events.TimerExpireEvent;
import dev.hcr.hcf.users.User;
import dev.hcr.hcf.utils.TaskUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

import java.util.concurrent.TimeUnit;

public class ArcherTagTimer extends Timer implements Listener {
    private final Player player;
    private boolean active;
    private long delay;

    public ArcherTagTimer(Player player) {
        super(player, "archer_tag");
        this.player = player;
        this.active = true;
        this.delay = System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(5L);
    }


    @Override
    public String getDisplayName() {
        return "&eArcher Tagged";
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
        if (PvPClass.getApplicableClass(player) == null) {
            end(true);
            return;
        }
        long left = delay - System.currentTimeMillis();
        if (left <= 0) {
            end(false);
        }
    }

    @Override
    public long getTimeLeft() {
        return delay - System.currentTimeMillis();
    }

    @EventHandler
    public void onTimerExpireEvent(TimerExpireEvent event) {
        Player player = event.getAffected()[0];

    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {

    }
}
