package org.microburstdetection.BurstVision.workingmodes.floworientedAnalysis;

public interface sample {
    double getAverageThroughput(int sampleDuration);
    double getBurstRatio(double avgThroughput,int sampleDuration);
}
