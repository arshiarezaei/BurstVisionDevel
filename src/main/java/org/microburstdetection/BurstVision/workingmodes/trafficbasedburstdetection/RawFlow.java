package org.microburstdetection.BurstVision.workingmodes.trafficbasedburstdetection;

import io.pkts.packet.Packet;
import org.microburstdetection.BurstVision.utilities.TraversedBytesUnits;

import java.util.ArrayList;


public interface RawFlow {
    boolean isBursty();
    boolean isHeavy();
    void newPacket(Packet packet);
    long getFirstPacketTime();
    long getlastPacketTime();
    int getTraversedBytes();
    long flowLiveTime();
    int getNumberOfPackets();
    double getAverageThroughput(TraversedBytesUnits T);
    void increaseTraversedBytes(Packet packet);

    ArrayList<Integer> getBitRate();
    boolean equals(Object o);
    int hashCode();
}
