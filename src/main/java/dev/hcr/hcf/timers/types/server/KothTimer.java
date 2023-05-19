package dev.hcr.hcf.timers.types.server;

import dev.hcr.hcf.koths.KothFaction;
import dev.hcr.hcf.timers.structure.TimerType;
import dev.hcr.hcf.timers.types.PauseTimer;
import dev.hcr.hcf.users.User;
import dev.hcr.hcf.utils.backend.types.PropertiesConfiguration;

import java.util.concurrent.TimeUnit;

public class KothTimer extends PauseTimer {
    private final KothFaction kothFaction;
    private boolean active;
    private long delay;
    private long timeLeft;
    private long pauseMillis;

    private final boolean debug = PropertiesConfiguration.getPropertiesConfiguration("hcf.properties").getBoolean("debug");

    public KothTimer(KothFaction kothFaction) {
        super(kothFaction.getName(), TimerType.SERVER);
        this.kothFaction = kothFaction;
        this.active = true;
        this.delay = System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(15L);
        this.timeLeft = delay - System.currentTimeMillis();
    }

    @Override
    public String getDisplayName(User user) {
        return kothFaction.getDisplayName().replace("Koth", "");
    }

    @Override
    public void run() {
        if (isPause()) return;
        if (!isActive()) return;
        long left = delay - System.currentTimeMillis();
        this.timeLeft = left; // saving for later
        if (left <= 0) {
            end(false);
        }
    }

    @Override
    public void reset() {
        this.delay = System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(15L);
        this.timeLeft = delay - System.currentTimeMillis();
        setPause(true);
        if (debug) {
            System.out.println("Restting timer.");
        }
    }

    @Override
    public long getTimeLeft() {
        return delay - System.currentTimeMillis();
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
    public long getPauseMillis() {
        return pauseMillis;
    }

    @Override
    public void setPauseMillis(long time) {
        this.pauseMillis = time;
    }

    @Override
    public void setPause(boolean pause) {
        // When paused save the current time of pausing and set the end time to be whatever the delay is + the current time
        if (pause) {
            // When paused set the time-left just to be total amount of time left in the delay without accounting for the current system time
            this.pauseMillis = System.currentTimeMillis();
        } else {
            // When resuming timer re-correct the time so it subtracts from when it was paused and doesn't "jump" forward in time
            this.delay = timeLeft + System.currentTimeMillis();
        }
        super.setPause(pause);
    }
}
