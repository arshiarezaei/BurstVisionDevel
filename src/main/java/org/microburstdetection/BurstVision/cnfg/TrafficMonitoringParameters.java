package org.microburstdetection.BurstVision.cnfg;

public class TrafficMonitoringParameters {
    private  int  samplingWindowDuration;// in microseconds
    private  int sampleDuration;// in microseconds

    public TrafficMonitoringParameters(int samplingWindowDuration, int sampleDuration) {
        this.samplingWindowDuration = samplingWindowDuration;
        this.sampleDuration = sampleDuration;
    }

    public  int getSamplingWindowDuration() {
        return samplingWindowDuration;
    }

    public  int getSampleDuration() {
        return sampleDuration;
    }
}
