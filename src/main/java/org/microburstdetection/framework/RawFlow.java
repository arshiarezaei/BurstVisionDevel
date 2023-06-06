package org.microburstdetection.framework;

import io.pkts.packet.Packet;
import org.microburstdetection.framework.utilities.TraversedBytesUnits;


public interface RawFlow {
    boolean isBursty();
    BurstEvents getBurstEvents();
    void newPacket(Packet packet);
    long getFirstPacketTime();
    long getlastPacketTime();
    int getTraversedBytes();
    <T> double getAverageThroughput(TraversedBytesUnits T);
    double getAverageThroughputInBursts();
    void increaseTraversedBytes(Packet packet);
    boolean equals(Object o);
}
