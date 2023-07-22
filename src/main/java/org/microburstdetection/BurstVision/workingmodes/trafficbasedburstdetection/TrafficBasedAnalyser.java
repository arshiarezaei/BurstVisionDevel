package org.microburstdetection.BurstVision.workingmodes.trafficbasedburstdetection;


import io.pkts.packet.Packet;
import io.pkts.protocol.Protocol;


public class TrafficBasedAnalyser {
    private static TrafficBasedAnalyser trafficBasedAnalyser = new TrafficBasedAnalyser();
    private static long arrivalTimeOfFirstPacket;
    private static long arrivalTimeOfLastPacket;
    private static long numberOfCapturedPackets;
    private static long capturedBytes;
    private static BurstEventHandler burstEventHandler = new BurstEventHandler();

    // auxiliary variables
    private static boolean firstPacketArrived=false;
    private static long arrivalTimeOfPreviousPacket;

    private TrafficBasedAnalyser() {
    }
    private static void increaseCapturedBytes(int packetLength){
        capturedBytes += packetLength;
    }
    private static void increaseNumCapturedPackets(){
        numberOfCapturedPackets++;
    }
    public static long getNumberOfCapturedPackets() {
        return numberOfCapturedPackets;
    }
    public static long getCapturedBytes() {
        return capturedBytes;
    }
    public static long getCapturingTime(){
//        System.out.println(arrivalTimeOfLastPacket+"\t"+arrivalTimeOfFirstPacket);
//        System.out.println(arrivalTimeOfLastPacket-arrivalTimeOfFirstPacket);
        return arrivalTimeOfLastPacket-arrivalTimeOfFirstPacket;
//        System.out.println(packetCapturingTime);
//        return packetCapturingTime;
    }

    public static long getArrivalTimeOfFirstPacket() {
        return arrivalTimeOfFirstPacket;
    }

    public static long getArrivalTimeOfLastPacket() {
        return arrivalTimeOfLastPacket;
    }

    public static BurstEventHandler getBurstEventHandler() {
        return burstEventHandler;
    }
    public static double getAvgThroughput(){
        return  (getCapturedBytes()*1.0 )/ (getCapturingTime()*1.0);
    }

    public static double getAvgPacketLength(){
        return (capturedBytes*1.0)/(numberOfCapturedPackets*1.0);
    }

    public static void newPacketArrived(Packet packet){
        burstEventHandler.newPacket(packet);
//        System.out.println(packet.getParentPacket().getPayload().capacity());
        if(firstPacketArrived){
            // updating statistics
            arrivalTimeOfPreviousPacket = arrivalTimeOfLastPacket;
            arrivalTimeOfLastPacket = packet.getArrivalTime();
//            System.out.println(packetCapturingTime);
        }else {
            firstPacketArrived=true;
            arrivalTimeOfFirstPacket = packet.getArrivalTime();
            arrivalTimeOfLastPacket = packet.getArrivalTime();
            arrivalTimeOfPreviousPacket = packet.getArrivalTime();
        }

        increaseNumCapturedPackets();
        increaseCapturedBytes(packet.getParentPacket().getPayload().getArray().length);
    }
}
