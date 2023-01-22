package dev.hcr.hcf.packets;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;

public abstract class PlayerPacketEvent extends Event {
    private final Player player;

    public PlayerPacketEvent(Player player) {
        super(true);
        this.player = player;
    }

    public Player getPlayer() {
        return player;
    }
}
