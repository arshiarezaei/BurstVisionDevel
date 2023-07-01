package org.microburstdetection.BurstVision.cnfg;

public class BurstParameters {
    private  int burstRatio; // maximum inter-arrival time between two consecutive packets
    /* minimum number of packets with inter-arrival time less
        than threshold to construct a burst */


    public BurstParameters(int burstRatio) {
        this.burstRatio = burstRatio;
    }

    public BurstParameters() {
    }

    public long getBurstRatio() {
        return burstRatio;
    }
}
