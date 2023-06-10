package org.microburstdetection.framework.cnfg;

public class HeavyFlowStaticProperties {
    private static Double HeavyFlowThroughputThreshold; // Unit: bytes per second

    private HeavyFlowStaticProperties() {
    }

    public static void setHeavyFlowThroughputThreshold(Double heavyFlowThroughputThreshold) {
        HeavyFlowThroughputThreshold = heavyFlowThroughputThreshold;
    }

    public static Double getHeavyFlowThroughputThreshold() {
        return HeavyFlowThroughputThreshold;
    }
}
