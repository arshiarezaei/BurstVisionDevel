package org.microburstdetection.BurstVision.trafficbasedburstdetection;

import org.microburstdetection.BurstVision.trafficbasedburstdetection.Flow;

import java.util.ArrayList;

public record BurstEvent(int numberOfBurstyPackets, int traversedBytes, long arrivalTimeOfFirstPacket, long arrivalTimeOfLastPacket,
                         ArrayList<Flow> flowsContributedToBurst) {

    public int getNumberOfPackets() {
        return numberOfBurstyPackets;
    }

    public int getTraversedBytes() {
        return traversedBytes;
    }

    public long getArrivalTimeOfFirstPacket() {
        return arrivalTimeOfFirstPacket;
    }

    public long getArrivalTimeOfLastPacket() {
        return arrivalTimeOfLastPacket;
    }
    public long getBurstDuration(){
        return arrivalTimeOfLastPacket-arrivalTimeOfFirstPacket;
    }
}
