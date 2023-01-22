package dev.hcr.hcf.timers.types.player;

import dev.hcr.hcf.timers.Timer;
import dev.hcr.hcf.timers.events.TimerExpireEvent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

import java.util.concurrent.TimeUnit;

public class CombatTimer extends Timer implements Listener {
    private final Player player;
    private boolean active;
    private long delay;

    public CombatTimer(Player player) {
        super(player, "combat");
        this.active = true;
        this.player = player;
        this.delay = System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(30L);
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
    public String getDisplayName() {
       return  "&cCombat";
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
        if (entity.getUniqueId() != player.getUniqueId()) return;
        handleEndTimer(this, new Player[]{entity});
        end(false);
    }

    @EventHandler
    public void onTimerExpire(TimerExpireEvent event) {
        handleEndTimer(event.getTimer(), event.getAffected());
    }

    private void handleEndTimer(Timer timer, Player[] affected) {
        if (timer == this) {
            if (affected.length == 0) return;
            for (Player player : affected) {
                player.sendMessage(ChatColor.RED + "You are no longer in combat.");
            }
        }
    }
}
