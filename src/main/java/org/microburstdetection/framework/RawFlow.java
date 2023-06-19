package org.microburstdetection.framework;

import io.pkts.packet.Packet;
import org.microburstdetection.framework.utilities.TraversedBytesUnits;


public interface RawFlow {
    boolean isBursty();
    boolean isHeavy();
    BurstEventHandler getBurstEvents();
    void newPacket(Packet packet);
    long getFirstPacketTime();
    long getlastPacketTime();
    int getTraversedBytes();
    long flowLiveTime();
    int getNumberOfPackets();
    double getAverageThroughput(TraversedBytesUnits T);
    Double getAverageThroughputInBursts(TraversedBytesUnits T);
    void increaseTraversedBytes(Packet packet);
    boolean equals(Object o);
}
