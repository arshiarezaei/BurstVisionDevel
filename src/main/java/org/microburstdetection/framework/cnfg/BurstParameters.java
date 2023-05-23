package org.microburstdetection.framework.cnfg;

public class BurstParameters {

    private final long THRESHOLD; // maximum inter-arrival time between two consecutive packets
    /* minimum number of packets with inter-arrival time less
        than threshold to construct a burst */
    private final int MINIMUM_NUMBER_OF_PACKETS_IN_BURST;
    private final int MAXIMUM_NUMBER_OF_PACKETS_IN_BURST; // maximum number of packets in a burst event

    public BurstParameters() {
        THRESHOLD = 500_000;
        MINIMUM_NUMBER_OF_PACKETS_IN_BURST=3;
        MAXIMUM_NUMBER_OF_PACKETS_IN_BURST = 20;
    }

    public BurstParameters(long THRESHOLD, int MINIMUM_NUMBER_OF_PACKETS_IN_BURST, int MAXIMUM_NUMBER_OF_PACKETS_IN_BURST) {
        this.THRESHOLD = THRESHOLD;
        this.MINIMUM_NUMBER_OF_PACKETS_IN_BURST = MINIMUM_NUMBER_OF_PACKETS_IN_BURST;
        this.MAXIMUM_NUMBER_OF_PACKETS_IN_BURST = MAXIMUM_NUMBER_OF_PACKETS_IN_BURST;
    }

    public long getTHRESHOLD() {
        return THRESHOLD;
    }

    public int getMINIMUM_NUMBER_OF_PACKETS_IN_BURST() {
        return MINIMUM_NUMBER_OF_PACKETS_IN_BURST;
    }

    public int getMAXIMUM_NUMBER_OF_PACKETS_IN_BURST() {
        return MAXIMUM_NUMBER_OF_PACKETS_IN_BURST;
    }
}
