package org.microburstdetection.BurstVision.workingmodes.trafficbasedburstdetection;

import java.util.Set;

public interface sample {
    double getAverageThroughput(int sampleDuration);
    double getBurstRatio(double avgThroughput,int sampleDuration);
    int getNumFlows();
}
