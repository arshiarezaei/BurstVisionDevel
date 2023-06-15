package org.microburstdetection.framework;

import io.pkts.packet.Packet;
import org.microburstdetection.framework.cnfg.ConfigurationParameters;
import org.microburstdetection.framework.utilities.TraversedBytesUnits;

import java.util.ArrayList;
import java.util.Iterator;


public class BurstEvents {

    // the following parameters have to be set to detect a burst event
    //TODO: fix the order of threshold, the order of threshold depends on the accuracy of the .pcap file
    private static final long THRESHOLD= ConfigurationParameters.getBurstParameters().getTHRESHOLD(); // maximum inter-arrival time between two consecutive packets
    /* minimum number of packets with inter-arrival time less
        than threshold to construct a burst */
    private static final int MINIMUM_NUMBER_OF_PACKETS_IN_BURST=ConfigurationParameters.getBurstParameters().getMINIMUM_NUMBER_OF_PACKETS_IN_BURST();
    private static final int MAXIMUM_NUMBER_OF_PACKETS_IN_BURST=ConfigurationParameters.getBurstParameters().getMAXIMUM_NUMBER_OF_PACKETS_IN_BURST(); // maximum number of packets in a burst event

    // reset the following properties in each burst
//    private long startTimeOfTheCurrentPeriod; // elapsed time of new probable burst
    private long arrivalTimeOfPreviousPacket; // arrival time of last matched packet
    private long arrivalTimeOfLastBusrtyPacket;
    private long arrivalTimeOfFirstBurstyPacket;
    private long arrivalTimeOfLastBurstyPacketInLastBurst;
    private int numberOfPacketsSinceLastBurst; // number of packets in the new probable burst
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
        long firstArrivalTime = packet.getArrivalTime();
        this.arrivalTimeOfPreviousPacket = firstArrivalTime;
        this.arrivalTimeOfLastBusrtyPacket = firstArrivalTime;
        this.arrivalTimeOfFirstBurstyPacket = firstArrivalTime;
        this.arrivalTimeOfLastBurstyPacketInLastBurst = firstArrivalTime;
        this.traversedBytesInCurrentBurst = packet.getParentPacket().getPayload().getArray().length;
    }

    public void newPacket(Packet packet){
        long elapsedTimeSinceLastPacket = packet.getArrivalTime()- arrivalTimeOfPreviousPacket;
        arrivalTimeOfPreviousPacket = packet.getArrivalTime();
        if(elapsedTimeSinceLastPacket <= THRESHOLD
                && numberOfPacketsSinceLastBurst <= MINIMUM_NUMBER_OF_PACKETS_IN_BURST
                && numberOfPacketsSinceLastBurst < MAXIMUM_NUMBER_OF_PACKETS_IN_BURST){
            // DOC: not yet burst
            numberOfPacketsSinceLastBurst+=1;
        }
        if(elapsedTimeSinceLastPacket<=THRESHOLD
                && numberOfPacketsSinceLastBurst == MINIMUM_NUMBER_OF_PACKETS_IN_BURST ){
            this.arrivalTimeOfFirstBurstyPacket = packet.getArrivalTime();
        }
        if (elapsedTimeSinceLastPacket<=THRESHOLD
                && numberOfPacketsSinceLastBurst > MINIMUM_NUMBER_OF_PACKETS_IN_BURST
                && numberOfPacketsSinceLastBurst <=MAXIMUM_NUMBER_OF_PACKETS_IN_BURST){
            this.numberOfPacketsSinceLastBurst+=1;
            this.arrivalTimeOfLastBusrtyPacket = packet.getArrivalTime();
            if(numberOfPacketsSinceLastBurst==MAXIMUM_NUMBER_OF_PACKETS_IN_BURST){
                addNewBurst();
                resetBurstParameters(packet);
            }
        }
        if(elapsedTimeSinceLastPacket > THRESHOLD &&
                numberOfPacketsSinceLastBurst > MINIMUM_NUMBER_OF_PACKETS_IN_BURST) {
            addNewBurst();
            resetBurstParameters(packet);
        }else if(elapsedTimeSinceLastPacket>THRESHOLD){
            resetBurstParameters(packet);
        }
    }

    private void addNewBurst(){
        packetsInBurst.add(numberOfPacketsSinceLastBurst);
        bytesInEachBurst.add(traversedBytesInCurrentBurst);
        long duration = arrivalTimeOfLastBusrtyPacket - arrivalTimeOfFirstBurstyPacket;
        if(duration<=0){
            System.out.println("Error1 \t"+arrivalTimeOfLastBusrtyPacket+"\t"+arrivalTimeOfFirstBurstyPacket+
                    " size duration\t"+burstsDuration.size()+"\t"+numberOfPacketsSinceLastBurst);
        }
        burstsDuration.add(duration);
        if(burstsDuration.size()>1){
            long interBurstTime = this.arrivalTimeOfFirstBurstyPacket - this.arrivalTimeOfLastBurstyPacketInLastBurst;
//            System.out.println(interBurstTime);
            if(interBurstTime<=0){
                System.out.println("Error in inter burst1 "+this.interBurstTime+"\t"+interBurstTime);
            }
            this.interBurstTime.add(interBurstTime);
        }
    }
    private void addNewBurst(Packet packet){
        System.out.println("Burst2");
        // executes when maximum number of packets in burst is reached
        packetsInBurst.add(numberOfPacketsSinceLastBurst);
        bytesInEachBurst.add(traversedBytesInCurrentBurst);
        long duration = arrivalTimeOfLastBusrtyPacket - arrivalTimeOfFirstBurstyPacket;
//        System.out.println(duration);
        if(duration<=0){
            System.out.println("Error2");
        }
        burstsDuration.add(duration);
        if(burstsDuration.size()>1){
            long interBurstTime =  arrivalTimeOfFirstBurstyPacket -arrivalTimeOfLastBurstyPacketInLastBurst;
//            System.out.println(interBurstTime);
            if(interBurstTime<=0){
                System.out.println("Error in inter burst2");
            }
            this.interBurstTime.add(interBurstTime);
        }
        // add inter-burst time


    }

    // reset burst parameters to capture next burst event
    private void resetBurstParameters(Packet packet){
        // TODO: check numberOfPacketsSinceLastBurst to be 0 or 1 ????
        this.numberOfPacketsSinceLastBurst = 0;
        this.traversedBytesInCurrentBurst = 0;
        this.arrivalTimeOfLastBurstyPacketInLastBurst = arrivalTimeOfLastBusrtyPacket;
    }
    public ArrayList<Long> getInterBurstTime() {
        return interBurstTime;
    }
    public double  getAverageBurstThroughput(TraversedBytesUnits T ){
        // check for correct function
        if(getBurstsDuration().size()!=getBytesInEachBurst().size()){
            System.out.println("ERROR");
        }
        long sumBurstsDuration=0;
        int sumTraversedBytes = 0;
        Iterator iterator = getBurstsDuration().iterator();
        Iterator iterator1 = getBytesInEachBurst().iterator();
        while (iterator.hasNext() && iterator1.hasNext() ){
            sumBurstsDuration += (long) iterator.next();
            sumTraversedBytes += (int) iterator1.next();
        }
        return (sumTraversedBytes/(sumBurstsDuration*1.0))*Math.pow(10,(T.getTraversedBytesUnits()));
    }
}
