package dev.hcr.hcf.timers.types.server;

import dev.hcr.hcf.HCF;
import dev.hcr.hcf.timers.Timer;
import dev.hcr.hcf.timers.events.TimerExpireEvent;
import dev.hcr.hcf.timers.events.TimerStopEvent;
import dev.hcr.hcf.timers.structure.TimerType;
import dev.hcr.hcf.utils.CC;
import org.apache.commons.lang.time.DurationFormatUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;

public class SOTWTimer extends Timer implements Listener {
    private boolean enabled;
    private long delay;

    public SOTWTimer(long delay) {
        super("sotw", TimerType.SERVER);
        this.enabled = true;
        this.delay = System.currentTimeMillis() + delay;
        this.runTaskTimerAsynchronously(HCF.getPlugin(), 20L, 20L);
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        TimerStopEvent event = new TimerStopEvent(this);
        Bukkit.getPluginManager().callEvent(event);
        this.enabled = enabled;
    }

    public void setDelay(long delay) {
        this.delay = delay;
    }

    public long getTimeLeft() {
        return delay - System.currentTimeMillis();
    }

    @Override
    public void run() {
        if (!enabled) return;
        long left = delay - System.currentTimeMillis();
        if (left <= 0) {
            TimerExpireEvent event = new TimerExpireEvent(this);
            Bukkit.getPluginManager().callEvent(event);
            this.enabled = false;
            cancel();
            return;
        }
        Bukkit.getOnlinePlayers().forEach(player -> player.sendMessage(CC.translate("&7SOTW &a" + DurationFormatUtils.formatDurationWords(left, true, true))));
    }

    @EventHandler
    public void onDamage(EntityDamageEvent event) {
        // TODO: 2/12/2022 Implement SOTW enable
        if (!enabled) return;
        event.setCancelled(true);
    }

    @EventHandler
    public void onEntityAttack(EntityDamageByEntityEvent event) {
        // TODO: 2/12/2022 Implement SOTW enable
        if (!enabled) return;
        event.setCancelled(true);
        if (event.getEntity() instanceof Player && event.getDamager() instanceof Player) {
            Player entity = (Player) event.getEntity();
            entity.sendMessage(ChatColor.RED + "You cannot attack players whilst SOTW is enabled.");
        }
    }

}
