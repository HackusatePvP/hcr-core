package dev.hcr.hcf.timers.types.player.effects;

import dev.hcr.hcf.HCF;
import dev.hcr.hcf.pvpclass.structure.Abilities;
import dev.hcr.hcf.timers.Timer;
import dev.hcr.hcf.timers.events.TimerExpireEvent;
import dev.hcr.hcf.timers.events.TimerStopEvent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

public class ArcherResistanceTimer extends Timer implements Listener {
    private final Player player;
    private boolean active;
    private long delay;

    public ArcherResistanceTimer(Player player) {
        super(player, "archer_resistance");
        this.active = true;
        this.player = player;
        this.delay = System.currentTimeMillis() + Abilities.ARCHER_RESISTANCE.getCooldown();
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
    public String getDisplayName() {
        return "&5Resistance";
    }

    @Override
    public void run() {
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
                player.sendMessage(ChatColor.GREEN + "You can use resistance again.");
            }
        }
    }
}
