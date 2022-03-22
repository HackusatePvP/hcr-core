package dev.hcr.hcf.timers.types.player;

import dev.hcr.hcf.HCF;
import dev.hcr.hcf.timers.Timer;
import dev.hcr.hcf.timers.events.TimerExpireEvent;
import org.bson.Document;
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

    public PvPTimer(Player player, Document document) {
        super(player, document);
        this.player = player;
        this.active = true;
        this.delay = document.getLong("delay");
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
        return "&aPvP-Timer";
    }

    @Override
    public void run() {
        if (!active) return;
        long left = delay - System.currentTimeMillis();
        if (left <= 0) {
            end(true);
        }
    }

    @Override
    public long getTimeLeft() {
        return delay - System.currentTimeMillis();
    }

    @EventHandler
    public void onPvPExpire(TimerExpireEvent event) {
        Timer timer = event.getTimer();
        Player[] affected = event.getAffected();
        if (timer == this) {
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
