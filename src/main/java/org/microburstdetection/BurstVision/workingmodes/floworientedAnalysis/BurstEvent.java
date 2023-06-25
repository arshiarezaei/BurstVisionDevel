package org.microburstdetection.BurstVision.workingmodes.floworientedAnalysis;

import java.math.BigInteger;

public record BurstEvent(int numberOfBurstyPackets, int traversedBytes, long arrivalTimeOfFirstPacket, long arrivalTimeOfLastPacket) {

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
