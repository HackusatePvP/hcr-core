package dev.hcr.hcf.timers.types.server;

import dev.hcr.hcf.timers.Timer;
import dev.hcr.hcf.timers.events.TimerExpireEvent;
import dev.hcr.hcf.timers.events.TimerStopEvent;
import dev.hcr.hcf.timers.structure.TimerType;
import dev.hcr.hcf.users.User;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;

public class SOTWTimer extends Timer implements Listener {
    private boolean active;
    private long delay;

    public SOTWTimer(long delay) {
        super("sotw", TimerType.SERVER);
        this.active = true;
        this.delay = System.currentTimeMillis() + delay;
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
    public String getDisplayName(User user) {
        if (user.hasSotw()) {
            return "&a&lSOTW";
        } else {
            return "&a&mSOTW";
        }
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
            Player[] array = Bukkit.getOnlinePlayers().toArray(new Player[0]);

            TimerStopEvent event = new TimerStopEvent(this, array);
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
        if (!active) return;
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            User user = User.getUser(player.getUniqueId());
            if (user.hasSotw()) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onEntityAttack(EntityDamageByEntityEvent event) {
        if (!active) return;
        if (event.getEntity() instanceof Player && event.getDamager() instanceof Player) {
            Player player = (Player) event.getEntity();
            Player damager = (Player) event.getDamager();
            User user = User.getUser(player.getUniqueId());
            User target = User.getUser(damager.getUniqueId());
            boolean cancelled = false;
            if (target.hasSotw()) {
                damager.sendMessage(ChatColor.RED + "You cannot pvp whilst SOTW is active. /sotw enable");
                cancelled = true;
            } else if (user.hasSotw()) {
                damager.sendMessage(ChatColor.RED + player.getName() + " has an active SOTW Timer.");
                cancelled = true;
            }
            event.setCancelled(cancelled);
        }
    }

}
