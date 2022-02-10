package dev.hcr.hcf.packets;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import dev.hcr.hcf.HCF;

import java.util.Collection;
import java.util.HashSet;

public abstract class PacketController extends PacketAdapter {
    private final ListenerPriority listenerPriority;
    private final PacketType packetType;

    private static final Collection<PacketController> registeredPackets = new HashSet<>();

    public PacketController(ListenerPriority listenerPriority, PacketType packetType) {
        super(HCF.getPlugin(), listenerPriority, packetType);
        this.listenerPriority = listenerPriority;
        this.packetType = packetType;
        registeredPackets.add(this);
        HCF.getPlugin().getPacketHandler().getManager().addPacketListener(this);
    }

    public PacketController(PacketType packetType) {
        super(HCF.getPlugin(), ListenerPriority.NORMAL, packetType);
        this.listenerPriority = ListenerPriority.NORMAL;
        this.packetType = packetType;
        registeredPackets.add(this);
    }

    public PacketType getPacketType() {
        return packetType;
    }

    public ListenerPriority getListenerPriority() {
        return listenerPriority;
    }

    public static Collection<PacketController> getRegisteredPackets() {
        return registeredPackets;
    }
}