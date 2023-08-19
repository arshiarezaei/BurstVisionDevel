package org.microburstdetection.BurstVision.workingmodes.trafficbasedburstdetection;

import io.pkts.packet.PCapPacket;
import io.pkts.packet.Packet;

import io.pkts.protocol.Protocol;
import org.microburstdetection.BurstVision.cnfg.ConfigurationParameters;

import java.util.*;


public class BurstEventHandler {
    private final ArrayList<BurstEvent> burstEvents = new ArrayList<>();
    private static final ArrayList<DetectedBurst> detectedBursts = new ArrayList<>();
    //TODO: add variables related to new burst detection method
    private final ArrayList<TrafficSampleInfo> capturedSamples = new ArrayList<>();
    private long arrivalTimeOfFirstPacketInCurrentSample;
    private int elapsedTimeInSamplingWindow;
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
    private Set<FlowRecord> flowsInSample = new HashSet<>();
    private int numFlowsInSample;

    public ArrayList<BurstEvent> getBurstEvents() {
        return burstEvents;
    }
    public static ArrayList<DetectedBurst> getDetectedBursts(){
        return detectedBursts;
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
            elapsedTimeInSamplingWindow += elapsedTimeInCurrentSample;
            if(elapsedTimeInCurrentSample<ConfigurationParameters.getTrafficMonitoringParameters().getSampleDuration()){
                updateCapturedPacketsParameters(packet);
            } else if (elapsedTimeInCurrentSample==ConfigurationParameters.getTrafficMonitoringParameters().getSampleDuration()) {
                updateCapturedPacketsParameters(packet);
                updateSamplesList(numPacketsInSample,bytesInSample, numFlowsInSample);
                resetParameters();
                arrivalTimeOfFirstPacketInCurrentSample = packet.getArrivalTime();
            }else if(elapsedTimeInCurrentSample>ConfigurationParameters.getTrafficMonitoringParameters().getSampleDuration()){
                updateSamplesList(numPacketsInSample,bytesInSample, numFlowsInSample);
                resetParameters();
                long time = elapsedTimeInCurrentSample-20;
                int i = (int) time/20;
                if(i>=1){
                    for (int j = 0; j < i ; j++) {
                        updateSamplesList(0,0,0);;
                    }
                }
                arrivalTimeOfFirstPacketInCurrentSample = packet.getArrivalTime()-time;
            }
//            if(elapsedTimeInSamplingWindow>=ConfigurationParameters.getTrafficMonitoringParameters().getSamplingWindowDuration()){
//                /* TODO:
//                    1. find average throughput in window
//                    2. find throughput in each sample
//                    3. for each sample find burst ratio = (sample_throughput/ avg_throughput)
//                    4. burstEventsInCurrentWindow = find samples burst ratio more than ConfigurationParameters.getBurstParameters().getMinBurstRatio()
//                    5. for each burstEventInCurrentWindow check neighbors to find start and finish time of bursts, traversed/bytes packets in burst,
//                    3. report burst events in burstEvents
//                    4. reset capturedSamples firstPacketArrived
//                    */
////                double avgThroughputInWindow = calculateAvgThroughputInSamples();
////                int sd = ConfigurationParameters.getTrafficMonitoringParameters().getSampleDuration();
////                int sw = ConfigurationParameters.getTrafficMonitoringParameters().getSamplingWindowDuration();
////                List<Double> d = capturedSamples.stream().map(r -> r.getBurstRatio(avgThroughputInWindow, sd)).collect(Collectors.toList());
////                double[] y = new double[d.size()];
////                for (int i = 0; i <d.size() ; i++) {
////                    y[i] = d.get(i);
////                }
////                //                System.out.println(capturedSamples.size()+"\t"+d.size()+"\t"+sw/sd);
////                double[] x = new double[y.length];
////                double value  = -20.0;
////                for (int i = 0; i < d.size(); i++) {
////                    value +=20;
////                    x[i]=value;
////                }
////                System.out.println("plp");
////                Plotting fig = new Plotting( "Sample Figure", "Time", "Signal");
////                fig.initialisePlot();
////                fig.addSignal("burst",x, y,false);
////                fig.plot();
////                if(!a) {
////                    try {
////                        fig.saveAsPNG("hi.png");
////                    } catch (Exception e) {
////                        System.out.println("Errror");
////                    }finally {
////                        a=true;
////                    }
////                }

//                capturedSamples.clear();
//                elapsedTimeInSamplingWindow = 0;
//                arrivalTimeOfPreviousPacket = packet.getArrivalTime();
//                arrivalTimeOfFirstPacketInCurrentSample = packet.getArrivalTime();
//                resetParameters();
//            }
//            int sd = ConfigurationParameters.getTrafficMonitoringParameters().getSampleDuration();
//            int sw = ConfigurationParameters.getTrafficMonitoringParameters().getSamplingWindowDuration();
//            System.out.println(capturedSamples.size()+"\t"+sw/sd);
        }else if(!firstPacketArrived) {
            firstPacketArrived=true;
            numPacketsInSample +=1;
            bytesInSample = packet.getParentPacket().getPayload().getArray().length;
            arrivalTimeOfPreviousPacket = packet.getArrivalTime();
            arrivalTimeOfFirstPacketInCurrentSample = packet.getArrivalTime();
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
        flowsInSample.clear();
        numFlowsInSample = 0;
    }
    private void updateCapturedPacketsParameters(Packet packet){
        try{
            bytesInSample +=  ((PCapPacket) packet.getPacket(Protocol.PCAP)).getTotalLength();
            numPacketsInSample +=1;
            flowsInSample.add(FlowRecord.getFlowFromPacket(packet));
        }catch (Exception e){
            System.out.println(e);
        }
//        bytesInSample += packet.getParentPacket().getPayload().getArray().length;
//        numPacketsInSample +=1;
    }
    private void updateSamplesList(int numPacketsInSample, int bytesInSample,int flowsInSample){
        TrafficSampleInfo trafficSampleInfo = new TrafficSampleInfo(numPacketsInSample,bytesInSample,flowsInSample);
        capturedSamples.add(trafficSampleInfo);
    }
    private double calculateAvgThroughputInSamples(){
        int sd =ConfigurationParameters.getTrafficMonitoringParameters().getSampleDuration();
        double sumThroughput = capturedSamples.stream().mapToDouble(r->r.getAverageThroughput(sd)).sum();
        double avg = sumThroughput/(capturedSamples.size()*1.0);
        return avg;
    }
    public static void addNewBurst(DetectedBurst detectedBurst){
        detectedBursts.add(detectedBurst);
    }
    public static ArrayList<Integer> getInterBurstTime(ArrayList<DetectedBurst> detectedBursts,int samplingDuration){
        ArrayList<Integer> interBurstTime = new ArrayList<>();
        for (int i = 0; i < detectedBursts.size()-1; i++) {
            int interBurst = (detectedBursts.get(i+1).getIndexInCapturedTraffic()-
                    detectedBursts.get(i).getIndexInCapturedTraffic())*samplingDuration;
            interBurstTime.add(interBurst);
        }
        return interBurstTime;
    }
}
