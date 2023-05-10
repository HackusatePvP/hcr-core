package dev.hcr.hcf.timers.types.player;

import dev.hcr.hcf.HCF;
import dev.hcr.hcf.timers.Timer;
import dev.hcr.hcf.timers.events.TimerExpireEvent;
import dev.hcr.hcf.users.User;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

import java.util.Map;
import java.util.concurrent.TimeUnit;

public class ArcherTagTimer extends Timer implements Listener {
    private Player player;
    private boolean active;
    private long delay;
    private final long timeLeft;

    public ArcherTagTimer(Player player) {
        super(player, "archer_tag");
        System.out.println("Archer tagged for " + player.getName());
        this.player = player;
        this.active = true;
        this.delay = System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(5L);
        this.timeLeft = delay - System.currentTimeMillis();
    }

    public ArcherTagTimer(Player player, Map<String, Object> map) {
        super(player.getUniqueId(), map);
        this.active = (Boolean) map.get("active");
        this.delay = (Long) map.get("delay");
        this.timeLeft = (Long) map.get("timeLeft");
    }

    @Override
    public String getDisplayName() {
        return "&eArcher Tagged";
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        if (isPause()) return;
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
        if (isPause()) return;
        if (!active) return;
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
        HCF.getPlugin().getTeamManager().setArcherTag(player, false);
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {

    }
}
