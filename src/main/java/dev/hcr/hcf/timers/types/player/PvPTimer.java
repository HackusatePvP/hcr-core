package dev.hcr.hcf.timers.types.player;

import dev.hcr.hcf.timers.Timer;
import dev.hcr.hcf.timers.events.TimerExpireEvent;
import dev.hcr.hcf.timers.types.PauseTimer;
import dev.hcr.hcf.utils.backend.types.PropertiesConfiguration;
import org.apache.commons.lang.time.DurationFormatUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class PvPTimer extends PauseTimer implements Listener {
    private Player player;
    private UUID uuid;
    private boolean active;
    private long delay;
    private long timeLeft;
    private long pauseMillis;

    public PvPTimer(Player player) {
        super(player, "pvp");
        this.player = player;
        this.active = true;
        this.delay = System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(30L);
        this.timeLeft = delay - System.currentTimeMillis();
    }

    public PvPTimer(UUID uuid, Map<String, Object> map) {
        super(uuid, map);
        boolean debug = PropertiesConfiguration.getPropertiesConfiguration("hcf.properties").getBoolean("debug");
        if (debug) {
            System.out.println("Creating PvPTimer for " + uuid);
        }
        this.uuid = uuid;
        this.active = (Boolean) map.get("active");
        if (debug) {
            System.out.println("Active: " + active);
        }
        this.delay = (Long) map.get("delay");
        if (debug) {
            System.out.println("Delay: " + delay);
            System.out.println("Display: " + DurationFormatUtils.formatDuration(delay, "hh:mm:ss"));
        }
        this.timeLeft = ((Number) map.get("timeLeft")).longValue(); // This has the current display
        if (debug) {
            System.out.println("Time-Left: " + timeLeft);
            System.out.println("Display: " + DurationFormatUtils.formatDuration(timeLeft, "hh:mm:ss"));
        }
        this.delay = timeLeft + System.currentTimeMillis();
        this.pauseMillis = ((Number) map.get("pauseMillis")).longValue();
        setPause(false);
        if (debug) {
            System.out.println("Validating...");
            System.out.println("Display: " + getTimerDisplay());
        }
    }

    @Override
    public void reset() {

    }

    @Override
    public long getPauseMillis() {
        return pauseMillis;
    }

    @Override
    public void setPauseMillis(long time) {
        this.pauseMillis = time;
    }

    public Player getPlayer() {
        if (player == null) {
            player = Bukkit.getPlayer(uuid);
            return player;
        }
        return player;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public long getDelay() {
        return delay;
    }

    public void setDelay(long delay) {
        this.delay = delay;
    }

    @Override
    public String getDisplayName() {
        return "&aPvP-Timer";
    }

    @Override
    public void run() {
        if (isPause()) return;
        if (!active) return;
        long left = delay - System.currentTimeMillis();
        this.timeLeft = left; // saving for later
        if (left <= 0) {
            end(false);
        }
    }

    @Override
    public long getTimeLeft() {
        return delay - System.currentTimeMillis();
    }

    @Override
    public void setPause(boolean pause) {
        // When paused save the current time of pausing and set the end time to be whatever the delay is + the current time
        if (pause) {
            // When paused set the time-left just to be total amount of time left in the delay without accounting for the current system time
            this.pauseMillis = System.currentTimeMillis();
        } else {
            // When resuming timer re-correct the time so it subtracts from when it was paused and doesn't "jump" forward in time
            this.delay = timeLeft + System.currentTimeMillis();
        }
        super.setPause(pause);
    }

    @EventHandler
    public void onPvPExpire(TimerExpireEvent event) {
        Timer timer = event.getTimer();
        Player[] affected = event.getAffected();
        if (timer == this) {
            if (affected.length == 0) return;
            for (Player player : affected) {
                if (player != null) {
                    player.sendMessage(ChatColor.RED + "You no longer have invincibility from pvp.");
                }
            }
        }
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        Player entity = event.getEntity();
        if (player != null) {
            if (entity.getUniqueId() == player.getUniqueId()) {
                end(false);
            }
        } else {
            if (entity.getUniqueId() == uuid) {
                end(false);
            }
        }
    }
}
