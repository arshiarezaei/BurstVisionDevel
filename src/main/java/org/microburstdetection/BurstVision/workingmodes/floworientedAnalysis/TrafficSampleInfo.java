package org.microburstdetection.BurstVision.workingmodes.floworientedAnalysis;

import org.microburstdetection.BurstVision.workingmodes.floworientedAnalysis.sample;

public record TrafficSampleInfo(int traversedPackets, int traversedBytes) implements sample {

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

}
