package org.microburstdetection.framework.cnfg;

public class ConfigurationParameters {
    private static final   BurstParameters burstParameters = new BurstParameters();

    public static BurstParameters getBurstParameters() {
        return burstParameters;
    }
}
