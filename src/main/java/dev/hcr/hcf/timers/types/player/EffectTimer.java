package dev.hcr.hcf.timers.types.player;

import dev.hcr.hcf.pvpclass.PvPClass;
import dev.hcr.hcf.pvpclass.structure.Abilities;
import dev.hcr.hcf.timers.Timer;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

public class EffectTimer extends Timer implements Listener {
    private final Player player;
    private final Abilities type;
    private boolean active;
    private long delay;

    private String display;

    public EffectTimer(Player player, Abilities type) {
        super(player, type.getName());
        this.player = player;
        this.type = type;
        this.active = true;
        this.delay = System.currentTimeMillis() + type.getCooldown();

        String[] split = type.getName().split("_");
        this.display = split[1].substring(0, 1).toUpperCase() + split[1].substring(1);

        switch (display.toLowerCase()) {
            case "speed":
                this.display = ChatColor.AQUA + display;
                break;
            case "strength":
                this.display = ChatColor.RED + display;
                break;
            case "resistance":
                this.display = ChatColor.YELLOW + display;
                break;
            case "regeneration":
                this.display = ChatColor.GREEN + display;
                break;
            case "jump":
                this.display = ChatColor.LIGHT_PURPLE + display;
                break;
        }
    }

    public Player getPlayer() {
        return player;
    }

    @Override
    public boolean isActive() {
        return active;
    }

    @Override
    public void setActive(boolean active) {
        this.active = active;
    }

    @Override
    public String getDisplayName() {
        return display;
    }

    @Override
    public void run() {
        if (!active) return;
        if (PvPClass.getApplicableClass(player) == null) {
            end(true);
            return;
        }
        long left = delay - System.currentTimeMillis();
        if (left <= 0) {
            end(false);
        }
    }

    @Override
    public long getDelay() {
        return delay;
    }

    @Override
    public void setDelay(long delay) {
        this.delay = delay;
    }

    @Override
    public long getTimeLeft() {
        return getDelay() - System.currentTimeMillis();
    }
}
