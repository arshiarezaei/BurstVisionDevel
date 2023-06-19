package org.microburstdetection.framework;

import io.pkts.packet.Packet;

import java.util.ArrayList;
import java.util.stream.Collectors;


public class BurstEventHandler {
    long THRESHOLD = 500;
    int MINIMUM_NUMBER_OF_PACKETS_IN_BURST = 3;
    int MAXIMUM_NUMBER_OF_PACKETS_IN_BURST = 100;
    private final ArrayList<BurstEvent> burstEvents = new ArrayList<>();
    // temp variables
    private int numberOfPacketsSinceLastBurst;
    private int traversedBytesInCurrentBurst;
    private long arrivalTimeOfPreviousPacket;
    private long arrivalTimeOfFirstBurstyPacket;
    private long arrivalTimeOfLastBurstyPacket;
    private boolean firstPacketArrived=false;


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
        if(!firstPacketArrived){
            numberOfPacketsSinceLastBurst +=1;
            traversedBytesInCurrentBurst = packet.getParentPacket().getPayload().getArray().length;
            arrivalTimeOfPreviousPacket = packet.getArrivalTime();
            firstPacketArrived=true;
        }else if(firstPacketArrived) {
            long arrivalTimeOfLastPacket = packet.getArrivalTime();
            long elapsedTimeSinceLastPacket = arrivalTimeOfLastPacket - arrivalTimeOfPreviousPacket;
            if(elapsedTimeSinceLastPacket <= THRESHOLD){
                numberOfPacketsSinceLastBurst+=1;
                if(numberOfPacketsSinceLastBurst == MINIMUM_NUMBER_OF_PACKETS_IN_BURST){
                    arrivalTimeOfFirstBurstyPacket = arrivalTimeOfLastPacket;
                }
                if(numberOfPacketsSinceLastBurst > MINIMUM_NUMBER_OF_PACKETS_IN_BURST
                        && numberOfPacketsSinceLastBurst <= MAXIMUM_NUMBER_OF_PACKETS_IN_BURST){
                    traversedBytesInCurrentBurst += packet.getParentPacket().getPayload().getArray().length;
                    arrivalTimeOfLastBurstyPacket = arrivalTimeOfLastPacket;
                }
                if(numberOfPacketsSinceLastBurst > MINIMUM_NUMBER_OF_PACKETS_IN_BURST
                        && numberOfPacketsSinceLastBurst == MAXIMUM_NUMBER_OF_PACKETS_IN_BURST){
                    BurstEvent burstEvent = new BurstEvent(numberOfPacketsSinceLastBurst, traversedBytesInCurrentBurst,
                            arrivalTimeOfFirstBurstyPacket,arrivalTimeOfLastBurstyPacket);
//                    System.out.println(arrivalTimeOfLastBurstyPacket-arrivalTimeOfFirstBurstyPacket);
                    burstEvents.add(burstEvent);
                    resetBurstParameters(packet);
                }
            }else if(numberOfPacketsSinceLastBurst > MINIMUM_NUMBER_OF_PACKETS_IN_BURST){
                BurstEvent burstEvent = new BurstEvent(numberOfPacketsSinceLastBurst, traversedBytesInCurrentBurst,
                        arrivalTimeOfFirstBurstyPacket,arrivalTimeOfLastBurstyPacket);
//                System.out.println(arrivalTimeOfLastBurstyPacket-arrivalTimeOfFirstBurstyPacket);
                burstEvents.add(burstEvent);
                resetBurstParameters(packet);
            }else {
                resetBurstParameters(packet);
            }
        }

    }
    public ArrayList<Long> getBurstInterBurstTime(){
        ArrayList<Long> burstInterArrivalTime = new ArrayList<>();
        if(burstEvents.size()>2){
            for (int i = 0; i < this.burstEvents.size(); i++) {
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
    private void resetBurstParameters(Packet packet){
        numberOfPacketsSinceLastBurst=0;
        traversedBytesInCurrentBurst =0;
        arrivalTimeOfPreviousPacket = packet.getArrivalTime();
//        arrivalTimeOfLastBurstyPacket= packet.getArrivalTime();
//        arrivalTimeOfFirstBurstyPacket = packet.getArrivalTime();
    }

//    public double  getAverageBurstThroughput(TraversedBytesUnits T ){
//        // check for correct function
////        if(getBurstsDuration().size()!=getBytesInEachBurst().size()){
////            System.out.println("ERROR");
////        }
////        long sumBurstsDuration=0;
////        int sumTraversedBytes = 0;
////        Iterator iterator = getBurstsDuration().iterator();
////        Iterator iterator1 = getBytesInEachBurst().iterator();
////        while (iterator.hasNext() && iterator1.hasNext() ){
////            sumBurstsDuration += (long) iterator.next();
////            sumTraversedBytes += (int) iterator1.next();
////        }
////        return (sumTraversedBytes/(sumBurstsDuration*1.0))*Math.pow(10,(T.getTraversedBytesUnits()));
//        return 0;
//    }
}