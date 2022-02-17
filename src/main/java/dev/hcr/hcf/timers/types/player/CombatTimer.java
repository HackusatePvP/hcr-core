package dev.hcr.hcf.timers.types.player;

import dev.hcr.hcf.HCF;
import dev.hcr.hcf.timers.Timer;
import dev.hcr.hcf.timers.events.TimerExpireEvent;
import dev.hcr.hcf.timers.events.TimerStopEvent;
import dev.hcr.hcf.utils.CC;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

public class CombatTimer extends Timer implements Listener {
    private final Player player;
    private boolean active;
    private long delay;

    public CombatTimer(Player player) {
        super(player, "combat");
        this.active = true;
        this.player = player;
        this.delay = 30L;
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
        if (delay <= 0) {
            TimerExpireEvent event = new TimerExpireEvent(this, player);
            Bukkit.getPluginManager().callEvent(event);
            end(false);
            return;
        }
        player.sendMessage(CC.translate("&7Combat: &c" + delay + "s"));
        --delay;
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
        if (entity.getUniqueId() != player.getUniqueId()) return;
        player.sendMessage(ChatColor.RED + "You are no longer in combat.");
        end(false);
    }

    @EventHandler
    public void onTimerExpire(TimerExpireEvent event) {
        handleEndTimer(event.getTimer(), event.getAffected());
    }

    @EventHandler
    public void onTimerStop(TimerStopEvent event) {
        handleEndTimer(event.getTimer(), event.getAffected());
    }

    private void handleEndTimer(Timer timer, Player[] affected) {
        if (timer == this) {
            timer.end();
            if (affected.length == 0) return;
            for (Player player : affected) {
                player.sendMessage(ChatColor.RED + "You are no longer in combat.");
            }
        }
    }
}
