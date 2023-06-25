package org.microburstdetection.BurstVision.workingmodes.floworientedAnalysis;

import io.pkts.packet.Packet;
import org.microburstdetection.BurstVision.utilities.TraversedBytesUnits;

import java.util.ArrayList;


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
    ArrayList<Double> getListOfBurstsRatio();
    ArrayList<Double> getThroughputInEachBurst();
    boolean equals(Object o);
}
