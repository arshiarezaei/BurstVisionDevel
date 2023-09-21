package org.microburstdetection.BurstVision.workingmodes.floworientedAnalysis;

import com.github.psambit9791.jdsp.misc.UtilMethods;
import com.github.psambit9791.jdsp.signal.peaks.FindPeak;
import com.github.psambit9791.jdsp.signal.peaks.Peak;
import io.pkts.packet.PCapPacket;
import io.pkts.packet.Packet;
import io.pkts.protocol.Protocol;
import org.microburstdetection.BurstVision.cnfg.ConfigurationParameters;


import java.util.ArrayList;
import java.util.stream.Collectors;


public class BurstEventHandler {
    private final ArrayList<BurstEvent> burstEvents = new ArrayList<>();
    private static final ArrayList<DetectedBurst> detectedBursts = new ArrayList<>();
    //TODO: add variables related to new burst detection method
    private final ArrayList<TrafficSampleInfo> capturedSamples = new ArrayList<>();
    // temp variables
    private int numberOfPacketsSinceLastBurst;
    private int traversedBytesInCurrentBurst;
    private long arrivalTimeOfPreviousPacket;
    private long arrivalTimeOfFirstBurstyPacket;
    private long arrivalTimeOfLastBurstyPacket;
    private boolean firstPacketArrived=false;
    private long arrivalTimeOfFirstPacketInCurrentSample;
    private int elapsedTimeInSamplingWindow;
    private int bytesInSample;
    private int numPacketsInSample;
    public ArrayList<BurstEvent> getBurstEvents() {
        return burstEvents;
    }
    public int getNumberOfBursts(){
        return burstEvents.size();
    }
    public boolean isBursty(){
        return !this.burstEvents.isEmpty();
    }
    public void newPacket(Packet packet){
        if(firstPacketArrived){
            long arrivalTimeOfLastPacket = packet.getArrivalTime();
            long elapsedTimeInCurrentSample = arrivalTimeOfLastPacket - arrivalTimeOfFirstPacketInCurrentSample;
            elapsedTimeInSamplingWindow += elapsedTimeInCurrentSample;
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
                long time = elapsedTimeInCurrentSample-ConfigurationParameters.getTrafficMonitoringParameters().getSampleDuration();
                int i = (int) time/ConfigurationParameters.getTrafficMonitoringParameters().getSampleDuration();
                if(i>=1){
                    for (int j = 0; j < i ; j++) {
                        updateSamplesList(0,0);;
                    }
                }
                arrivalTimeOfFirstPacketInCurrentSample = packet.getArrivalTime()-time;
            }
        }else if(!firstPacketArrived) {
            firstPacketArrived=true;
            numPacketsInSample +=1;
            bytesInSample = packet.getParentPacket().getPayload().getArray().length;
            arrivalTimeOfPreviousPacket = packet.getArrivalTime();
            arrivalTimeOfFirstPacketInCurrentSample = packet.getArrivalTime();
        }
//        if(!firstPacketArrived){
//            numberOfPacketsSinceLastBurst +=1;
//            traversedBytesInCurrentBurst = packet.getParentPacket().getPayload().getArray().length;
//            arrivalTimeOfPreviousPacket = packet.getArrivalTime();
//            firstPacketArrived=true;
//        }else if(firstPacketArrived) {
//            long arrivalTimeOfLastPacket = packet.getArrivalTime();
//            long elapsedTimeSinceLastPacket = arrivalTimeOfLastPacket - arrivalTimeOfPreviousPacket;
//            if(elapsedTimeSinceLastPacket <= THRESHOLD){
//                numberOfPacketsSinceLastBurst+=1;
//                if(numberOfPacketsSinceLastBurst == MINIMUM_NUMBER_OF_PACKETS_IN_BURST){
//                    arrivalTimeOfFirstBurstyPacket = arrivalTimeOfLastPacket;
//                }
//                if(numberOfPacketsSinceLastBurst > MINIMUM_NUMBER_OF_PACKETS_IN_BURST
//                        && numberOfPacketsSinceLastBurst <= MAXIMUM_NUMBER_OF_PACKETS_IN_BURST){
//                    traversedBytesInCurrentBurst += packet.getParentPacket().getPayload().getArray().length;
//                    arrivalTimeOfLastBurstyPacket = arrivalTimeOfLastPacket;
//                }
//                if(numberOfPacketsSinceLastBurst > MINIMUM_NUMBER_OF_PACKETS_IN_BURST
//                        && numberOfPacketsSinceLastBurst == MAXIMUM_NUMBER_OF_PACKETS_IN_BURST){
//                    BurstEvent burstEvent = new BurstEvent(numberOfPacketsSinceLastBurst, traversedBytesInCurrentBurst,
//                            arrivalTimeOfFirstBurstyPacket,arrivalTimeOfLastBurstyPacket);
////                    System.out.println(arrivalTimeOfLastBurstyPacket-arrivalTimeOfFirstBurstyPacket);
//                    burstEvents.add(burstEvent);
//                    resetBurstParameters(packet);
//                }
//            }else if(numberOfPacketsSinceLastBurst > MINIMUM_NUMBER_OF_PACKETS_IN_BURST){
//                BurstEvent burstEvent = new BurstEvent(numberOfPacketsSinceLastBurst, traversedBytesInCurrentBurst,
//                        arrivalTimeOfFirstBurstyPacket,arrivalTimeOfLastBurstyPacket);
////                System.out.println(arrivalTimeOfLastBurstyPacket-arrivalTimeOfFirstBurstyPacket);
//                burstEvents.add(burstEvent);
//                resetBurstParameters(packet);
//            }else {
//                resetBurstParameters(packet);
//            }
//        }

    }
    public ArrayList<Long> getBurstInterBurstTime(){
        ArrayList<Long> burstInterArrivalTime = new ArrayList<>();
        return burstInterArrivalTime;
    }
    public ArrayList<Long> getBurstsDuration(){
        ArrayList<Long> burstDuration = new ArrayList<>();
        return this.burstEvents.stream().map(BurstEvent::getBurstDuration).collect(Collectors.toCollection(ArrayList::new));
    }
    public long getLivenessTimeOfBurstyFlow(){
        // TODO: must throw an error if not bursty
        if(isBursty()){
            long firstBurstyPacket = this.burstEvents.get(0).getArrivalTimeOfFirstPacket();
            long lastBurstyPacket = this.burstEvents.get(this.burstEvents.size()-1).getArrivalTimeOfLastPacket();
            return lastBurstyPacket-firstBurstyPacket;
        }else {
            return 0;
        }
    }

    public ArrayList<Integer> getTraversedBytesInEachBurst(){
        return null;
    }
    public double getAverageThroughputInBursts(){
        return 0;
    }

    public int getTotalNumberOfPacketsInBursts(){
        return 0;
    }
    public ArrayList<Integer> getNumberOfPacketsInEachBurst(){
        return null;
    }
    public ArrayList<Double> getThroughputInEachBurst(){
        ArrayList<Double> throughputInEachBurst = new ArrayList<>();
        //FiXMe:
        return throughputInEachBurst;
    }
    //    private void resetBurstParameters(Packet packet){
//        numberOfPacketsSinceLastBurst=0;
//        traversedBytesInCurrentBurst =0;
//        arrivalTimeOfPreviousPacket = packet.getArrivalTime();
////        arrivalTimeOfLastBurstyPacket= packet.getArrivalTime();
////        arrivalTimeOfFirstBurstyPacket = packet.getArrivalTime();
//    }
    private void updateCapturedPacketsParameters(Packet packet){
        try{
            bytesInSample += (int) ((PCapPacket) packet.getPacket(Protocol.PCAP)).getTotalLength();
            numPacketsInSample +=1;
        }catch (Exception e){
            System.out.println(e);
        }
    }
    private void updateSamplesList(int numPacketsInSample, int bytesInSample){
        TrafficSampleInfo trafficSampleInfo = new TrafficSampleInfo(numPacketsInSample,bytesInSample);
        capturedSamples.add(trafficSampleInfo);
    }
    private void resetParameters(){
        numPacketsInSample=0;
        bytesInSample=0;
    }
    public  void addNewBurst(DetectedBurst detectedBurst){
        detectedBursts.add(detectedBurst);
    }
    public  void getBursts(ArrayList<TrafficSampleInfo> capturedSamples, double threshold, int samplingDuration, int avgWindow, int burstRatio){
//        System.out.println("here");
        double[] bitrate = capturedSamples.stream().mapToDouble(c -> c.getAverageThroughput(samplingDuration)).toArray();
        double[] data = UtilMethods.splitByIndex(bitrate, 0, bitrate.length);
        FindPeak fp = new FindPeak(data);
        Peak out = fp.detectPeaks();

        int[] indexOfPeaks = out.getPeaks();
//        System.out.println(Arrays.toString(out.getPeaks()));
//        int[] indexOfBursts = getIndexOfBursts(capturedSamples,indexOfPeaks,avgWindow,samplingDuration,10);
        int aw = avgWindow/2;
        int numberOfSamplesToMeasure = aw/samplingDuration;
        ArrayList<Integer> indexOfBursts = new ArrayList<>();
//        ArrayList<Double> calbr = new ArrayList<>();
        ArrayList<DetectedBurst> detectedBursts = new ArrayList<>();
        for (int i = 0; i < indexOfPeaks.length; i++) {
            // to prevent indexOutOfBoundException
            int lowerBound = Math.max(indexOfPeaks[i]-numberOfSamplesToMeasure,0);
            int upperBound = Math.min(indexOfPeaks[i]+numberOfSamplesToMeasure, capturedSamples.size());

            int sumBytes = capturedSamples.subList(lowerBound,upperBound).stream().mapToInt(TrafficSampleInfo::traversedBytes).sum();
            double throughputInWindow = (double) sumBytes/avgWindow;
            double throughputInSample = capturedSamples.get(indexOfPeaks[i]).getAverageThroughput(samplingDuration);
            double br = throughputInSample/throughputInWindow;
//            System.out.println(throughputInWindow+"\t"+throughputInSample+"\t"+br);\

            if(br>=burstRatio){
                indexOfBursts.add(indexOfPeaks[i]);
//                calbr.add(Utilities.getRoundedValue(br));
                // burst duration right side of the peak
                int indexOfRightSideOfPeak=0;
                int indexOfBurst = indexOfPeaks[i];
//                if(indexOfBurst==823431){
//                    System.out.println("break");
//                }
                for (int j = indexOfBurst+1; j <= bitrate.length; j++) {
                    if(bitrate[j-1]<throughputInWindow){
                        indexOfRightSideOfPeak = j;
                        break;
                    } else if (j==bitrate.length) {
                        indexOfRightSideOfPeak = bitrate.length;
                        break;
                    }
                }
                int indexOfLeftSideOfPeak=0;
                for (int j = indexOfBurst-1; j >= 0 ; j--) {
                    if(bitrate[j]<throughputInWindow){
                        indexOfLeftSideOfPeak = j;
                        break;
                    }
                }
                int bd = indexOfRightSideOfPeak-indexOfLeftSideOfPeak;
                bd = (bd+1)*samplingDuration;
                int burstyPackets = capturedSamples.subList(indexOfLeftSideOfPeak,indexOfRightSideOfPeak).stream().
                        mapToInt(TrafficSampleInfo::traversedPackets).sum();
//                int flowsContributedToBurst = capturedSamples.subList(indexOfLeftSideOfPeak,indexOfRightSideOfPeak).stream().mapToInt(TrafficSampleInfo::getNumFlows).sum();
                DetectedBurst detectedBurst = new DetectedBurst(indexOfBurst,burstyPackets,sumBytes,bd,br);
//                    detectedBursts.add(detectedBurst);
                addNewBurst(detectedBurst);
            }
        }
    }
}
