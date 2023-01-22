package dev.hcr.hcf.timers.types.player.faction;

import dev.hcr.hcf.HCF;
import dev.hcr.hcf.factions.types.PlayerFaction;
import dev.hcr.hcf.timers.Timer;
import dev.hcr.hcf.timers.events.TimerExpireEvent;
import dev.hcr.hcf.users.User;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.concurrent.TimeUnit;

public class FactionHomeTimer extends Timer implements Listener {
    private final Player player;
    private boolean active;
    private long delay;

    public FactionHomeTimer(Player player) {
        super(player, "faction_home");
        this.player = player;
        this.active = true;
        this.delay = System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(10L);
    }

    @Override
    public String getDisplayName() {
        return "&9Faction Home";
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
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
        Timer timer = event.getTimer();
        if (timer != this) return;
        if (player == null) return;
        User user = User.getUser(player.getUniqueId());
        PlayerFaction playerFaction = (PlayerFaction) user.getFaction();
        if (playerFaction == null) {
            player.sendMessage(ChatColor.RED + "Error occurred contact staff.");
            return;
        }
        player.teleport(playerFaction.getHome());
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        User user = User.getUser(player.getUniqueId());
        if (user.getActiveTimer("faction_home") == null) return;
        Location to = event.getTo();
        Location from = event.getFrom();
        if (to.getBlockX() != from.getBlockX() || to.getBlockZ() != from.getBlockZ()) {
            player.sendMessage(ChatColor.RED + "You have moved, teleportation cancelled.");
            end(true);
        }
    }

}
