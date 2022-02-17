package dev.hcr.hcf.timers.types.player;

import dev.hcr.hcf.HCF;
import dev.hcr.hcf.timers.Timer;
import dev.hcr.hcf.timers.events.TimerExpireEvent;
import dev.hcr.hcf.timers.events.TimerStopEvent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

import java.util.concurrent.TimeUnit;

public class PvPTimer extends Timer implements Listener {
    private final Player player;
    private boolean active;
    private long delay;

    public PvPTimer(Player player) {
        super(player, "pvp");
        this.player = player;
        this.active = true;
        this.delay = System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(30L);
        this.runTaskTimerAsynchronously(HCF.getPlugin(), 20L, 20L);
    }

    public Player getPlayer() {
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
    public void run() {
        if (!active) return;
        delay -= System.currentTimeMillis();
        if (delay <= 0) {
            end(true);
        }
    }

    public void end(boolean forced) {
        if (forced) {
            TimerStopEvent event = new TimerStopEvent(this, player);
            Bukkit.getPluginManager().callEvent(event);
        } else {
            TimerExpireEvent event = new TimerExpireEvent(this);
            Bukkit.getPluginManager().callEvent(event);
        }
        this.delay = 0;
        this.active = false;
        cancel();
        super.end();
    }

    @EventHandler
    public void onPvPExpire(TimerExpireEvent event) {
        Timer timer = event.getTimer();
        Player[] affected = event.getAffected();
        if (timer == this) {
            timer.end();
            if (affected.length == 0) return;
            for (Player player : affected) {
                player.sendMessage(ChatColor.RED + "You no longer have invincibility from pvp.");
            }
        }
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        Player entity = event.getEntity();
        if (entity.getUniqueId() == player.getUniqueId()) {
            end(false);
        }
    }
}
