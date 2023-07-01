package org.microburstdetection.BurstVision.cnfg;

public class ConfigurationParameters {

    private static ConfigurationParameters configurationParameters = new ConfigurationParameters();
    private static BurstParameters burstParameters;
    private static TrafficMonitoringParameters trafficMonitoringParameters;

    private ConfigurationParameters() {
    }

    public static void setConfigurationParameters(TrafficMonitoringParameters trafficMonitoringParameters,
                                                  BurstParameters burstParameters){
        ConfigurationParameters.trafficMonitoringParameters = trafficMonitoringParameters;
        ConfigurationParameters.burstParameters = burstParameters;
    }
    public static BurstParameters getBurstParameters() {
        return burstParameters;
    }

    public static TrafficMonitoringParameters getTrafficMonitoringParameters() {
        return trafficMonitoringParameters;
    }
}
