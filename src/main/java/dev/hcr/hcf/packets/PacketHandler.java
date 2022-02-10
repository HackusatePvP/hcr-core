package dev.hcr.hcf.packets;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;

public class PacketHandler {
    private final ProtocolManager manager;

    public PacketHandler() {
        this.manager = ProtocolLibrary.getProtocolManager();
        registerIncomingPackets();
    }

    private void registerIncomingPackets() {
        // new PositionalPacketController(); Testing only
    }

    private void registerOutgoingPackets() {
        // TODO: 2/8/2022
    }

    public ProtocolManager getManager() {
        return manager;
    }
}
