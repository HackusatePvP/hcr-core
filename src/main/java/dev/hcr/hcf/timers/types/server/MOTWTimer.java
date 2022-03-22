package dev.hcr.hcf.timers.types.server;

import dev.hcr.hcf.HCF;
import dev.hcr.hcf.timers.Timer;
import dev.hcr.hcf.timers.events.TimerExpireEvent;
import dev.hcr.hcf.timers.events.TimerStopEvent;
import dev.hcr.hcf.timers.structure.TimerType;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;

public class MOTWTimer extends Timer implements Listener {
    private boolean active;
    private long delay;

    public MOTWTimer(long delay) {
        super("motw", TimerType.SERVER);
        this.active = true;
        this.delay = System.currentTimeMillis() + delay;
        this.runTaskTimerAsynchronously(HCF.getPlugin(), 20L, 20L);
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean enabled) {
        TimerStopEvent event = new TimerStopEvent(this);
        Bukkit.getPluginManager().callEvent(event);
        this.active = enabled;
    }

    public void setDelay(long delay) {
        this.delay = delay;
    }

    public long getTimeLeft() {
        return delay - System.currentTimeMillis();
    }

    @Override
    public String getDisplayName() {
        return "&a&lMOTW";
    }

    @Override
    public void run() {
        if (!active) return;
        long left = delay - System.currentTimeMillis();
        if (left <= 0) {
            end(false);
        }
    }

    @Override
    public long getDelay() {
        return delay;
    }

    public void end(boolean forced) {
        if (forced) {
            TimerStopEvent event = new TimerStopEvent(this, (Player[]) Bukkit.getOnlinePlayers().toArray());
            Bukkit.getPluginManager().callEvent(event);
        } else {
            TimerExpireEvent event = new TimerExpireEvent(this);
            Bukkit.getPluginManager().callEvent(event);
        }
        this.delay = 0;
        this.active = false;
        cancel();
        Timer.getTimers().remove(this);
    }

    @EventHandler
    public void onDamage(EntityDamageEvent event) {
        // TODO: 2/12/2022 Implement SOTW enable
        if (!active) return;
        event.setCancelled(true);
    }

    @EventHandler
    public void onEntityAttack(EntityDamageByEntityEvent event) {
        // TODO: 2/12/2022 Implement SOTW enable
        if (!active) return;
        event.setCancelled(true);
        if (event.getEntity() instanceof Player && event.getDamager() instanceof Player) {
            Player entity = (Player) event.getEntity();
            entity.sendMessage(ChatColor.RED + "You cannot attack players whilst MOTW is enabled.");
        }
    }
}
