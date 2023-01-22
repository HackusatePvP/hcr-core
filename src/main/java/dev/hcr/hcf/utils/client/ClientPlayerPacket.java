package dev.hcr.hcf.utils.client;

import org.bukkit.entity.Player;

public abstract class ClientPlayerPacket {
    private final Player player;

    public ClientPlayerPacket(Player player) {
        this.player = player;
    }

    public Player getPlayer() {
        return player;
    }
}
