package org.microburstdetection.BurstVision.workingmodes.floworientedAnalysis;

import com.github.psambit9791.jdsp.signal.peaks.FindPeak;
import com.github.psambit9791.jdsp.signal.peaks.Peak;
import io.pkts.packet.PCapPacket;
import io.pkts.packet.Packet;
import io.pkts.protocol.Protocol;
import org.microburstdetection.BurstVision.cnfg.ConfigurationParameters;


import javax.swing.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.stream.Collectors;


public class BurstEventHandler {
    //    private final ArrayList<BurstEvent> burstEvents = new ArrayList<>();
    private final ArrayList<DetectedBurst> detectedBursts = new ArrayList<>();
    //TODO: add variables related to new burst detection method
    private final ArrayList<TrafficSampleInfo> capturedSamples = new ArrayList<>();
    // temp variables
    private int numberOfPacketsSinceLastBurst;
    private int traversedBytesInCurrentBurst;
    private long arrivalTimeOfPreviousPacket;
    private long arrivalTimeOfFirstBurstyPacket;
    private long arrivalTimeOfLastBurstyPacket;
    private long arrivalTimeOfFirstPacket;
    private long arrivalTimeOfLastPacket;
    private boolean firstPacketArrived=false;
    private long arrivalTimeOfFirstPacketInCurrentSample;
    private int elapsedTimeInSamplingWindow;
    private int bytesInSample;
    private int numPacketsInSample;
    public ArrayList<DetectedBurst> getBurstEvents() {
        return this.detectedBursts;
    }
    public int getNumberOfBursts(){
        return this.detectedBursts.size();
    }
    public boolean isBursty(){
        return !this.detectedBursts.isEmpty();
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
            arrivalTimeOfFirstPacket = packet.getArrivalTime();
            arrivalTimeOfFirstPacketInCurrentSample = packet.getArrivalTime();
        }
    }

    public  ArrayList<Integer> getInterBurstTime(int samplingDuration){
        ArrayList<Integer> interBurstTime = new ArrayList<>();
        for (int i = 0; i < detectedBursts.size()-1; i++) {
            int interBurst = (detectedBursts.get(i+1).getIndexInCapturedTraffic()-
                    detectedBursts.get(i).getIndexInCapturedTraffic())*samplingDuration;
            interBurstTime.add(interBurst);
        }
        return interBurstTime;
    }

    public ArrayList<Integer> getBurstsDuration(){
        return detectedBursts.stream().map(DetectedBurst::getBurstDuration).collect(Collectors.toCollection(ArrayList::new));
    }
    public long getLivenessTimeOfBurstyFlow(){
        // TODO: must throw an error if not bursty
        if(isBursty()){
//            long firstBurstyPacket = this.arrivalTimeOfFirstPacket;
//            long lastBurstyPacket = this.burstEvents.get(this.burstEvents.size()-1).getArrivalTimeOfLastPacket();
            return this.arrivalTimeOfLastPacket-this.arrivalTimeOfFirstPacket;
        }else {
            return 0;
        }
    }

    public ArrayList<Integer> getTraversedBytesInEachBurst(){
        ArrayList<Integer> traversedBytesInEachBurst = new ArrayList<>();
        for (DetectedBurst detectedBurst:detectedBursts) {
            traversedBytesInEachBurst.add(detectedBurst.getTraversedBytes());
        }
        return traversedBytesInEachBurst;
    }
    public ArrayList<Double> getThroughputInBursts(){
        ArrayList<Double> traversedBytesInEachBurst = new ArrayList<>();
        return traversedBytesInEachBurst;
    }
    public int getNumberOfBurst(){
        return detectedBursts.size();
    }

    public int getTotalNumberOfPacketsInBursts(){
        return detectedBursts.stream().mapToInt(DetectedBurst::getNumberOfBurstyPackets).sum();
    }
    public ArrayList<Integer> getNumberOfPacketsInEachBurst(){

        return detectedBursts.stream().map(DetectedBurst::getNumberOfBurstyPackets).collect(Collectors.toCollection(ArrayList::new));
    }
    public ArrayList<Double> getThroughputInEachBurst(){
        ArrayList<Double> throughputInEachBurst =
                detectedBursts.stream().mapToDouble(db-> (double) db.getTraversedBytes()/db.getBurstDuration()).
                        boxed().collect(Collectors.toCollection(ArrayList::new));

        return throughputInEachBurst;
    }
    public ArrayList<Double> getBurstRatio(){
        ArrayList<Double> br = new ArrayList<>();
        for (DetectedBurst detectedBurst:detectedBursts) {
            br.add(detectedBurst.getBurstRatio());
        }
        return br;
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
    public  void getBursts( double threshold, int samplingDuration, int avgWindow, int burstRatio)  {
        if(!capturedSamples.isEmpty()){
            double[] bitrate = capturedSamples.stream().mapToDouble(c -> c.getAverageThroughput(samplingDuration)).toArray();
            double[] data = new double[bitrate.length+1];
            System.arraycopy(bitrate,0,data,1,bitrate.length);
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
}
