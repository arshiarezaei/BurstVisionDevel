package org.microburstdetection.BurstVision.workingmodes.trafficbasedburstdetection;

import java.util.ArrayList;

record BurstEvent(int numberOfBurstyPackets, int traversedBytes, long arrivalTimeOfFirstPacket, long arrivalTimeOfLastPacket,
                         ArrayList<FlowRecord> flowsContributedToBurst) {

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

    @Override
    public ArrayList<FlowRecord> flowsContributedToBurst() {
        return flowsContributedToBurst;
    }
    public int getNumberOfConcurrentBurstyFlows(){
        return flowsContributedToBurst.size();
    }


    public long getBurstDuration(){
        return arrivalTimeOfLastPacket-arrivalTimeOfFirstPacket;
    }
}
