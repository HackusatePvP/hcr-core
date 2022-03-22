package dev.hcr.hcf.timers;

import dev.hcr.hcf.HCF;
import dev.hcr.hcf.pvpclass.structure.Abilities;
import dev.hcr.hcf.timers.events.TimerExpireEvent;
import dev.hcr.hcf.timers.events.TimerStopEvent;
import dev.hcr.hcf.timers.structure.TimerType;
import dev.hcr.hcf.timers.types.player.*;
import dev.hcr.hcf.timers.types.player.effects.ArcherResistanceTimer;
import dev.hcr.hcf.timers.types.player.effects.ArcherSpeedTimer;
import dev.hcr.hcf.timers.types.player.faction.FactionHomeTimer;
import dev.hcr.hcf.users.User;
import org.apache.commons.lang.time.DurationFormatUtils;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;
import java.util.concurrent.TimeUnit;

public abstract class Timer extends BukkitRunnable {
    private final String name;
    private final TimerType timerType;
    private Player player;
    private static final Set<Timer> timers = new HashSet<>();

    public Timer(String name, TimerType timerType) {
        this.name = name;
        this.timerType = timerType;
        if (this instanceof Listener) {
            Bukkit.getPluginManager().registerEvents((Listener) this, HCF.getPlugin());
        }
        timers.add(this);
    }

    public Timer(Player player, String name) {
        this.name = name;
        this.timerType = TimerType.PLAYER;
        this.player = player;
        if (this instanceof Listener) {
            Bukkit.getPluginManager().registerEvents((Listener) this, HCF.getPlugin());
        }
        User user = User.getUser(player.getUniqueId());
        user.getActiveTimers().add(this);
    }

    public Timer(Player player, Document document) {
        this.name = document.getString("name");
        this.timerType = TimerType.PLAYER;
        if (this instanceof Listener) {
            Bukkit.getPluginManager().registerEvents((Listener) this, HCF.getPlugin());
        }
        User user = User.getUser(player.getUniqueId());
        user.getActiveTimers().add(this);
    }

    public String getName() {
        return name;
    }

    public TimerType getTimerType() {
        return timerType;
    }

    public static Timer getTimer(String name) {
        return timers.stream().filter(timer -> timer.getName().equals(name)).findAny().orElse(null);
    }


    public static Set<Timer> getTimers() {
        return timers;
    }

    public abstract String getDisplayName();

    public abstract void run();

    public abstract long getDelay();

    public abstract void setDelay(long delay);

    public abstract long getTimeLeft();

    public abstract boolean isActive();

    public abstract void setActive(boolean active);

    public void save() {
        for (User user : User.getUsers()) {
            for (Timer timer : user.getActiveTimers()) {
                if (timer == null) continue;

            }
        }
    }

    public void end(boolean forced) {
        if (forced) {
            TimerStopEvent event = new TimerStopEvent(this, player);
            Bukkit.getPluginManager().callEvent(event);
        } else {
            TimerExpireEvent event = new TimerExpireEvent(this, player);
            Bukkit.getPluginManager().callEvent(event);
        }
        setDelay(0);
        setActive(false);
        cancel();
        if (timerType == TimerType.PLAYER) {
            User user = User.getUser(player.getUniqueId());
            user.getActiveTimers().remove(this);
        }
    }

    public boolean isEffectTimer() {
        return Abilities.stream().filter(abilities -> abilities.getName().equalsIgnoreCase(name)).findAny().orElse(null) != null;
    }

    public String getTimerDisplay() {
        Timer timer = this;
        long duration = timer.getTimeLeft();
        long hours = TimeUnit.MILLISECONDS.toHours(duration);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(duration - TimeUnit.HOURS.toMillis(hours));
        if (hours <= 0L) {
            if (minutes <= 0L) {
                return DurationFormatUtils.formatDuration(duration, "ss", true);
            } else {
                return DurationFormatUtils.formatDuration(duration, "mm:ss", true);
            }
        } else {
            return DurationFormatUtils.formatDuration(duration, "hh:mm:ss", true);
        }
    }

    public static Timer createTimerForPlayer(String timer, Player player) {
        switch (timer.toLowerCase()) {
            case "class_warmup":
                return new ClassWarmupTimer(player);
            case "combat":
                return new CombatTimer(player);
            case "enderpearl":
                return new EnderPearlTimer(player);
            case "pvp":
                return new PvPTimer(player);
            case "faction_home":
                return new FactionHomeTimer(player);
            case "archer_resistance":
                return new ArcherResistanceTimer(player);
            case "archer_speed":
                return new ArcherSpeedTimer(player);
            case "archer_tag":
                return new ArcherTagTimer(player);
        }
        return null;
    }
}
