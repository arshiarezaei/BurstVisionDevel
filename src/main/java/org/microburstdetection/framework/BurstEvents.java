package org.microburstdetection.framework;

import io.pkts.packet.IPv4Packet;
import io.pkts.packet.Packet;
import io.pkts.packet.TCPPacket;
import org.microburstdetection.framework.cnfg.ConfigurationParameters;

import java.util.ArrayList;


public class BurstEvents {
    private int numberOfPacketsSinceLastBurst; // number of packets in the new probable burst
    private long startTimeOfTheCurrentPeriod; // elapsed time of new probable burst
    private long timeOfLastPacket; // arrival time of last matched packet
    private ArrayList<Integer> packetsInBurst= new ArrayList<>(); // number of packets in each burst event
    private ArrayList<Long> burstsDuration= new ArrayList<>(); // burst duration in microseconds
    private ArrayList<Long> interBurstTime = new ArrayList<>(); // time between bursts

    //TODO: fix the order of threshold
    private static long THRESHOLD= ConfigurationParameters.getBurstParameters().getTHRESHOLD(); // maximum inter-arrival time between two consecutive packets
    /* minimum number of packets with inter-arrival time less
        than threshold to construct a burst */
    private static int MINIMUM_NUMBER_OF_PACKETS_IN_BURST=ConfigurationParameters.getBurstParameters().getMINIMUM_NUMBER_OF_PACKETS_IN_BURST();
    private static int MAXIMUM_NUMBER_OF_PACKETS_IN_BURST=ConfigurationParameters.getBurstParameters().getMAXIMUM_NUMBER_OF_PACKETS_IN_BURST(); // maximum number of packets in a burst event

    public ArrayList<Integer> getPacketsInBurst() {
        return packetsInBurst;
    }

    public ArrayList<Long> getBurstsDuration() {
        return burstsDuration;
    }

    public BurstEvents(Packet packet) {
        // call this method only on first packet
        this.numberOfPacketsSinceLastBurst = 1;
        this.startTimeOfTheCurrentPeriod = packet.getArrivalTime();
        this.timeOfLastPacket = packet.getArrivalTime();
    }

    public void newPacket(Packet packet){
        long elapsedTimeSinceLastPacket = packet.getArrivalTime()-timeOfLastPacket;
        if (elapsedTimeSinceLastPacket<= THRESHOLD &&
                numberOfPacketsSinceLastBurst < MAXIMUM_NUMBER_OF_PACKETS_IN_BURST ){
            numberOfPacketsSinceLastBurst +=1;
            timeOfLastPacket = packet.getArrivalTime();
            if(numberOfPacketsSinceLastBurst == MAXIMUM_NUMBER_OF_PACKETS_IN_BURST){
                addNewBurst(packet);
//                System.out.println("New Burst");
                resetBurstParameters(packet);
            }
        }
        else if(elapsedTimeSinceLastPacket > THRESHOLD &&
                numberOfPacketsSinceLastBurst>=BurstEvents.MINIMUM_NUMBER_OF_PACKETS_IN_BURST){
            addNewBurst();
//            System.out.println("New Burst 2");
            resetBurstParameters(packet);
        }
        else if (elapsedTimeSinceLastPacket > THRESHOLD &&
                numberOfPacketsSinceLastBurst < BurstEvents.MINIMUM_NUMBER_OF_PACKETS_IN_BURST) {
            resetBurstParameters(packet);
//            System.out.println("Not Burst");
        }


    }

    private void addNewBurst(){
//        System.out.println("Burst 1");
        packetsInBurst.add(numberOfPacketsSinceLastBurst);
        long duration = timeOfLastPacket-startTimeOfTheCurrentPeriod;
        burstsDuration.add(duration);

    }
    private void addNewBurst(Packet packet){
//        System.out.println("Burst 2");
        packetsInBurst.add(numberOfPacketsSinceLastBurst);
        long duration = packet.getArrivalTime()-startTimeOfTheCurrentPeriod;
        burstsDuration.add(duration);
    }

    // reset burst parameters to capture next burst event
    private void resetBurstParameters(Packet packet){
        // TODO: check numberOfPacketsSinceLastBurst to be 0 or 1 ????
        numberOfPacketsSinceLastBurst=1;
        startTimeOfTheCurrentPeriod =  packet.getArrivalTime();
        timeOfLastPacket = packet.getArrivalTime();
    }
    public static  Double getAverageBurstDuration(ArrayList<Long> burstsDuration) {
        Long sum = 0L;
        for (Long burstDuration:burstsDuration) {
            sum+=burstDuration;
        }
        double size = burstsDuration.size()*1.0;
//        if(burstsDuration.size()!=packetsInBurst.size()){
//            System.out.println("ERRRROR");
//        }
        return sum/size;
    }
    public static Double CDFOfBurstDurationOfAllFlows(ArrayList<RawFlow> flows){
//        List<Integer> indices = IntStream.range(0, flows.size())
//                .filter(i -> Objects.equals(flows.get(i),newFlow))
//                .boxed().collect(Collectors.toList());
//        ArrayList<Long> burstDurationOfallFlows = new ArrayList<>();
//        for (Flow flow:flows) {
//            burstDurationOfallFlows.addAll(flow.getBurstEvents().getBurstsDuration());
//        }
//        burstDurationOfallFlows.removeIf(n->Objects.equals(n,0));
//        Collections.sort(burstDurationOfallFlows);
//        // FIXME: correct the return value
//        ArrayList<Integer> steps=new ArrayList<>();
//        ArrayList<Double> value=new ArrayList<>();
//        for (int i =5; i <= 100 ; i+=5) {
//            steps.add(i);
//        }
//        for (Integer step:steps) {
//            int XPercentile = (int) Math.ceil(burstDurationOfallFlows.size() * (step)/100.0);
//            Long max = Collections.max(burstDurationOfallFlows.subList(0,XPercentile));
//            value.add(((burstDurationOfallFlows.stream().filter(i-> i<= max).count())/(burstDurationOfallFlows.size()*1.0))*100.0);
//        }
//        for (int i = 0; i < steps.size(); i++) {
//            System.out.println(steps.get(i)+"\t"+value.get(i));
//        }

        return 0.0D;
    }

}
