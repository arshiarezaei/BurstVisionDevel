package org.microburstdetection.framework;

import io.pkts.packet.IPv4Packet;
import io.pkts.packet.Packet;
import io.pkts.packet.TCPPacket;
import org.microburstdetection.framework.cnfg.ConfigurationParameters;

import java.util.ArrayList;


public class BurstEvents {

    // the following parameters have to be set to detect a burst event
    //TODO: fix the order of threshold, the order of threshold depends on the accuracy of the .pcap file
    private static final long THRESHOLD= ConfigurationParameters.getBurstParameters().getTHRESHOLD(); // maximum inter-arrival time between two consecutive packets
    /* minimum number of packets with inter-arrival time less
        than threshold to construct a burst */
    private static final int MINIMUM_NUMBER_OF_PACKETS_IN_BURST=ConfigurationParameters.getBurstParameters().getMINIMUM_NUMBER_OF_PACKETS_IN_BURST();
    private static final int MAXIMUM_NUMBER_OF_PACKETS_IN_BURST=ConfigurationParameters.getBurstParameters().getMAXIMUM_NUMBER_OF_PACKETS_IN_BURST(); // maximum number of packets in a burst event

    // reset the following properties in each burst
    private int numberOfPacketsSinceLastBurst; // number of packets in the new probable burst
    private long startTimeOfTheCurrentPeriod; // elapsed time of new probable burst
    private long timeOfLastPacket; // arrival time of last matched packet
    private int traversedBytesInCurrentBurst;
    // the following properties capture information of burst, DO NOT Change them
    private final ArrayList<Integer> packetsInBurst= new ArrayList<>(); // number of packets in each burst event
    private final ArrayList<Long> burstsDuration= new ArrayList<>(); // burst duration in microseconds
    //TODO:  implement storing interBurst time
    private final ArrayList<Long> interBurstTime = new ArrayList<>(); // time between bursts
    private final ArrayList<Integer> bytesInEachBurst = new ArrayList<>();

    // getters
    public ArrayList<Integer> getPacketsInBurst() {
        return packetsInBurst;
    }

    public ArrayList<Long> getBurstsDuration() {
        return burstsDuration;
    }

    public int getNumberOfBurstEvents() {
        return burstsDuration.size();
    }

    public ArrayList<Integer> getBytesInEachBurst() {
        return bytesInEachBurst;
    }

    public BurstEvents(Packet packet) {
        // call this method only on first packet
        this.numberOfPacketsSinceLastBurst = 1;
        this.startTimeOfTheCurrentPeriod = packet.getArrivalTime();
        this.timeOfLastPacket = packet.getArrivalTime();
        this.traversedBytesInCurrentBurst = packet.getParentPacket().getPayload().getArray().length;
    }

    public void newPacket(Packet packet){
        long elapsedTimeSinceLastPacket = packet.getArrivalTime()-timeOfLastPacket;
        if (elapsedTimeSinceLastPacket<= THRESHOLD &&
                numberOfPacketsSinceLastBurst < MAXIMUM_NUMBER_OF_PACKETS_IN_BURST ){
            numberOfPacketsSinceLastBurst +=1;
            timeOfLastPacket = packet.getArrivalTime();
            traversedBytesInCurrentBurst+=packet.getParentPacket().getPayload().getArray().length;
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
        bytesInEachBurst.add(traversedBytesInCurrentBurst);
    }
    private void addNewBurst(Packet packet){
//        System.out.println("Burst 2");
        packetsInBurst.add(numberOfPacketsSinceLastBurst);
        long duration = packet.getArrivalTime()-startTimeOfTheCurrentPeriod;
        burstsDuration.add(duration);
        bytesInEachBurst.add(traversedBytesInCurrentBurst);
    }

    // reset burst parameters to capture next burst event
    private void resetBurstParameters(Packet packet){
        // TODO: check numberOfPacketsSinceLastBurst to be 0 or 1 ????
        this.numberOfPacketsSinceLastBurst=1;
        this.startTimeOfTheCurrentPeriod =  packet.getArrivalTime();
        this.timeOfLastPacket = packet.getArrivalTime();
        this.traversedBytesInCurrentBurst = 0 ;
    }
//    public static  Double getAverageBurstDuration(ArrayList<Long> burstsDuration) {
//        Long sum = 0L;
//        for (Long burstDuration:burstsDuration) {
//            sum+=burstDuration;
//        }
//        double size = burstsDuration.size()*1.0;
////        if(burstsDuration.size()!=packetsInBurst.size()){
////            System.out.println("ERRRROR");
////        }
//        return sum/size;
//    }

}
