package org.microburstdetection.framework;

import io.pkts.packet.Packet;


public interface RawFlow {
    boolean isBursty();
    BurstEvents getBurstEvents();
    void newPacket(Packet packet);
    boolean equals(Object o);
}
