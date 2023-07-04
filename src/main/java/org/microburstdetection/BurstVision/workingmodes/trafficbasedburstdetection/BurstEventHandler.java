package org.microburstdetection.BurstVision.workingmodes.trafficbasedburstdetection;

import io.pkts.packet.Packet;

import org.microburstdetection.BurstVision.cnfg.ConfigurationParameters;

import java.util.ArrayList;


public class BurstEventHandler {

    private final ArrayList<BurstEvent> burstEvents = new ArrayList<>();
    //TODO: add variables related to new burst detection method
    private final ArrayList<TrafficSampleInfo> capturedSamples = new ArrayList<>();
    private long arrivalTimeOfFirstPacketInCurrentSample;
    private int bytesInSample;
    private int numPacketsInSample;
    //
    private long arrivalTimeOfPreviousPacket;
    // temp variables
    private int numberOfPacketsSinceLastBurst;
    private int traversedBytesInCurrentBurst;

    private long arrivalTimeOfFirstBurstyPacket;
    private long arrivalTimeOfLastBurstyPacket;
    private boolean firstPacketArrived=false;
    private ArrayList<Flow> flowsContributedToBurst = new ArrayList<>();

    public ArrayList<BurstEvent> getBurstEvents() {
        return burstEvents;
    }
    public int getNumberOfBursts(){
        return burstEvents.size();
    }
    public boolean isBursty(){
        return !this.burstEvents.isEmpty();
    }

    public ArrayList<TrafficSampleInfo> getCapturedSamples() {
        return capturedSamples;
    }

    public void newPacket(Packet packet){
        long arrivalTimeOfLastPacket = packet.getArrivalTime();


        if(firstPacketArrived){
            long elapsedTimeInCurrentSample = arrivalTimeOfLastPacket - arrivalTimeOfFirstPacketInCurrentSample;
            if(elapsedTimeInCurrentSample<ConfigurationParameters.getTrafficMonitoringParameters().getSampleDuration()){
                updateCapturedPacketsParameters(packet);
            } else if (elapsedTimeInCurrentSample==ConfigurationParameters.getTrafficMonitoringParameters().getSampleDuration()) {
                updateCapturedPacketsParameters(packet);
                updateSamplesList(numPacketsInSample,bytesInSample);
                resetParameters();
                arrivalTimeOfFirstPacketInCurrentSample = packet.getArrivalTime();
            }else if(elapsedTimeInCurrentSample>ConfigurationParameters.getTrafficMonitoringParameters().getSampleDuration()){
                updateSamplesList(numPacketsInSample,bytesInSample);
                resetParameters();
                long time = elapsedTimeInCurrentSample-20;
                int i = (int) time/20;
                if(i>=1){
                    for (int j = 0; j < i ; j++) {
                        updateSamplesList(0,0);;
                    }
                }
                arrivalTimeOfFirstPacketInCurrentSample = packet.getArrivalTime()-time;
            }
        }else if(!firstPacketArrived) {
            numPacketsInSample +=1;
            bytesInSample = packet.getParentPacket().getPayload().getArray().length;
            arrivalTimeOfPreviousPacket = packet.getArrivalTime();
            arrivalTimeOfFirstPacketInCurrentSample = packet.getArrivalTime();
            firstPacketArrived=true;
        }

    }
    public ArrayList<Long> getBurstInterBurstTime(){
        return null;
    }
    public ArrayList<Long> getBurstsDuration(){
        return null;
    }

    public ArrayList<Integer> getTraversedBytesInEachBurst(){
        return null;
    }
    public double getAverageThroughputInBursts(){
        return 0.0;
    }

    public int getTotalNumberOfPacketsInBursts(){
        return 0;
    }
    public ArrayList<Integer> getNumberOfPacketsInEachBurst(){
        return null;
    }
    public ArrayList<Double> getThroughputInEachBurst(){
        return null;
    }

    public double getAverageBurstThroughput(){
        return 0.0;
    }

    public ArrayList<Double> getAveragePacketSize(){
        return null;
    }
    private void resetParameters(){
        numPacketsInSample=0;
        bytesInSample=0;
    }
    private void updateCapturedPacketsParameters(Packet packet){
        bytesInSample += packet.getParentPacket().getPayload().getArray().length;
        numPacketsInSample +=1;
    }
    private void updateSamplesList(int numPacketsInSample, int bytesInSample){
        TrafficSampleInfo trafficSampleInfo = new TrafficSampleInfo(numPacketsInSample,bytesInSample);
        capturedSamples.add(trafficSampleInfo);
    }
}
