package org.microburstdetection.BurstVision.workingmodes.trafficbasedburstdetection;

public record TrafficSampleInfo(int traversedPackets,int traversedBytes) implements sample{

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

}
