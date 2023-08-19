package org.microburstdetection.BurstVision.workingmodes.trafficbasedburstdetection;

import java.util.Objects;
import java.util.Set;

public class DetectedBurst {
    private final int indexInCapturedTraffic;
    private final int numberOfBurstyPackets;
    private final int  traversedBytes;
    private final int burstDuration;

    private final double burstRatio;
    private final int flowsContributedToBurst;

    public DetectedBurst(int indexInCapturedTraffic, int numberOfBurstyPackets, int traversedBytes, int burstDuration,
                         double burstRatio, int flowsContributedToBurst) {
        this.indexInCapturedTraffic = indexInCapturedTraffic;
        this.numberOfBurstyPackets = numberOfBurstyPackets;
        this.traversedBytes = traversedBytes;
        this.burstDuration = burstDuration;
        this.burstRatio = burstRatio;
        this.flowsContributedToBurst = flowsContributedToBurst;
    }

    public int getBurstDuration() {
        return burstDuration;
    }
    public int getIndexInCapturedTraffic() {
        return indexInCapturedTraffic;
    }

    public int getNumberOfBurstyPackets() {
        return numberOfBurstyPackets;
    }

    public int getTraversedBytes() {
        return traversedBytes;
    }

    public double getBurstRatio() {
        return burstRatio;
    }

    public int getFlowsContributedToBurst() {
        return flowsContributedToBurst;
    }
    public double getAveragePacketSize(){
        return (double) traversedBytes/(numberOfBurstyPackets+1);
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DetectedBurst that = (DetectedBurst) o;
        return indexInCapturedTraffic == that.indexInCapturedTraffic && numberOfBurstyPackets == that.numberOfBurstyPackets && traversedBytes == that.traversedBytes && burstDuration == that.burstDuration && Double.compare(that.burstRatio, burstRatio) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(indexInCapturedTraffic, numberOfBurstyPackets, traversedBytes, burstDuration, burstRatio);
    }

    @Override
    public String toString() {
        return "DetectedBurst{" +
                "indexInCapturedTraffic=" + indexInCapturedTraffic +
                ", numberOfBurstyPackets=" + numberOfBurstyPackets +
                ", traversedBytes=" + traversedBytes +
                ", burstDuration=" + burstDuration +
                ", burstRatio=" + burstRatio +
                '}';
    }

}
