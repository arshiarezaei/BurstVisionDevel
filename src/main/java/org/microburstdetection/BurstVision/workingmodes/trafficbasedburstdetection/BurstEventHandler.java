package org.microburstdetection.BurstVision.workingmodes.trafficbasedburstdetection;

import io.pkts.packet.Packet;

import org.microburstdetection.BurstVision.cnfg.ConfigurationParameters;
import org.microburstdetection.BurstVision.utilities.Utilities;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.stream.Collectors;



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
        if(firstPacketArrived){
            long arrivalTimeOfLastPacket = packet.getArrivalTime();
            long elapsedTimeInCurrentSample = arrivalTimeOfLastPacket - arrivalTimeOfFirstPacketInCurrentSample;
            if(elapsedTimeInCurrentSample<ConfigurationParameters.getTrafficMonitoringParameters().getSampleDuration()){
                bytesInSample += packet.getParentPacket().getPayload().getArray().length;
                numPacketsInSample +=1;
            } else if (elapsedTimeInCurrentSample==ConfigurationParameters.getTrafficMonitoringParameters().getSampleDuration()) {
                bytesInSample += packet.getParentPacket().getPayload().getArray().length;
                numPacketsInSample +=1;
                TrafficSampleInfo trafficSampleInfo = new TrafficSampleInfo(numPacketsInSample,bytesInSample);
                capturedSamples.add(trafficSampleInfo);
                resetParameters();
                arrivalTimeOfFirstPacketInCurrentSample = packet.getArrivalTime();

            }else if(elapsedTimeInCurrentSample>ConfigurationParameters.getTrafficMonitoringParameters().getSampleDuration()){
                TrafficSampleInfo trafficSampleInfo = new TrafficSampleInfo(numPacketsInSample,bytesInSample);
                capturedSamples.add(trafficSampleInfo);
                resetParameters();
                long time = elapsedTimeInCurrentSample-20;
                int i = (int) time/20;
                if(i>=1){
                    for (int j = 0; j < i ; j++) {
                        capturedSamples.add(capturedSamples.size(),new TrafficSampleInfo(0,0));
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
        ArrayList<Long> burstInterArrivalTime = new ArrayList<>();
        if(burstEvents.size()>=2){
            for (int i = 0; i < this.burstEvents.size()-1; i++) {
                long endOfCurrentBurst = burstEvents.get(i).getArrivalTimeOfLastPacket();
                long startOfNextBurst = this.burstEvents.get(i+1).arrivalTimeOfFirstPacket();
                burstInterArrivalTime.add(startOfNextBurst-endOfCurrentBurst);
            }
        }else if(burstEvents.size()==1) {
            long l = burstEvents.get(0).getArrivalTimeOfLastPacket()-burstEvents.get(0).getArrivalTimeOfFirstPacket();
            burstInterArrivalTime.add(l);
            return burstInterArrivalTime;
        }
        return burstInterArrivalTime;
    }
    public ArrayList<Long> getBurstsDuration(){
        ArrayList<Long> burstDuration = new ArrayList<>();
        return this.burstEvents.stream().map(BurstEvent::getBurstDuration).collect(Collectors.toCollection(ArrayList::new));
    }

    public ArrayList<Integer> getTraversedBytesInEachBurst(){
        return burstEvents.stream().map(BurstEvent::getTraversedBytes).collect(Collectors.toCollection(ArrayList::new));
    }
    public double getAverageThroughputInBursts(){
        Integer sumOfTraversedBytes = burstEvents.stream().map(BurstEvent::getTraversedBytes).mapToInt(value -> value).sum();
        Long sumOfBurstsDuration = burstEvents.stream().map(BurstEvent::getBurstDuration).mapToLong(value -> value).sum();
        return (sumOfTraversedBytes*1.0)/sumOfBurstsDuration;
    }

    public int getTotalNumberOfPacketsInBursts(){
        return burstEvents.stream().map(BurstEvent::getNumberOfPackets).mapToInt(a->a).sum();
    }
    public ArrayList<Integer> getNumberOfPacketsInEachBurst(){
        return burstEvents.stream().map(BurstEvent::getNumberOfPackets).collect(Collectors.toCollection(ArrayList::new));
    }
    public ArrayList<Double> getThroughputInEachBurst(){
        ArrayList<Double> throughputInEachBurst = new ArrayList<>();
        //FiXMe:
        if(getBurstsDuration().size()!=getTraversedBytesInEachBurst().size()){
            System.out.println("ERROR->getThroughputInEachBurst()");
        }
        Iterator<Long> i1 = getBurstsDuration().iterator();
        Iterator<Integer> i2 = getTraversedBytesInEachBurst().iterator();
        while(i1.hasNext() && i2.hasNext()) {
            throughputInEachBurst.add((i2.next())*1.0/(i1.next()*1.0));
        }
        return throughputInEachBurst;
    }
    private void resetBurstParameters(Packet packet){
        numberOfPacketsSinceLastBurst=0;
        traversedBytesInCurrentBurst =0;
        arrivalTimeOfPreviousPacket = packet.getArrivalTime();
        flowsContributedToBurst = new ArrayList<>();
//        arrivalTimeOfLastBurstyPacket= packet.getArrivalTime();
//        arrivalTimeOfFirstBurstyPacket = packet.getArrivalTime();
    }

    public double getAverageBurstThroughput(){
        long sumBurstsDuration = burstEvents.stream().mapToLong(BurstEvent::getBurstDuration).sum();
        long sumTraversedBytes = burstEvents.stream().mapToInt(BurstEvent::getTraversedBytes).sum();
        return (sumTraversedBytes*1.0)/(sumBurstsDuration*1.0);
    }

    public ArrayList<Double> getAveragePacketSize(){
        ArrayList<Double> avgPktSize = new ArrayList<>();
        ArrayList<Integer> listNumPacketsEachBurst = TrafficBasedAnalyser.getBurstEventHandler().getNumberOfPacketsInEachBurst();
        ArrayList<Integer> listTraversedByteEachBurst = TrafficBasedAnalyser.getBurstEventHandler().getTraversedBytesInEachBurst();
        Iterator<Integer> i1 = listNumPacketsEachBurst.iterator();
        Iterator<Integer> i2 = listTraversedByteEachBurst.iterator();
        while (i1.hasNext() && i2.hasNext()){
            avgPktSize.add(Utilities.getRoundedValue((double)i2.next())/((double) i1.next()));
        }
        return avgPktSize;
    }
    private void resetParameters(){
        numPacketsInSample=0;
        bytesInSample=0;
    }
}
