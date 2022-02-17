package dev.hcr.hcf.timers;

import dev.hcr.hcf.HCF;
import dev.hcr.hcf.timers.structure.TimerType;
import dev.hcr.hcf.users.User;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

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

    public abstract void run();

    public void end() {
        if (timerType == TimerType.PLAYER) {
            User user = User.getUser(player.getUniqueId());
            user.getActiveTimers().remove(this);
        }
    }
}
