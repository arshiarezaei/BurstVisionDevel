package org.microburstdetection.BurstVision.workingmodes.trafficbasedburstdetection;

import java.util.Set;

public record TrafficSampleInfo(int traversedPackets, int traversedBytes, int numFlowsInSample) implements sample{

    @Override
    public int traversedPackets() {
        return traversedPackets;
    }

    @Override
    public int traversedBytes() {
        return traversedBytes;
    }
    @Override
    public double getAverageThroughput(int sampleDuration){
        return (traversedBytes*1.0)/(sampleDuration*1.0);
    }

    @Override
    public double getBurstRatio(double avgThroughput,int sampleDuration) {
//        System.out.println(traversedBytes+"\t"+getAverageThroughput(sampleDuration)+"\t"+getAverageThroughput(sampleDuration)/avgThroughput);
        return getAverageThroughput(sampleDuration)/avgThroughput;
    }

    @Override
    public int getNumFlows() {
        return numFlowsInSample;
    }
}
