package org.microburstdetection.BurstVision.workingmodes.trafficbasedburstdetection;


import io.pkts.packet.Packet;




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
        capturedBytes += getCapturedBytes()+packetLength;
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
        return arrivalTimeOfLastPacket-arrivalTimeOfFirstPacket;
    }

    public static BurstEventHandler getBurstEventHandler() {
        return burstEventHandler;
    }
    public static double getAvgThroughput(){
        return  (getCapturedBytes()*1.0 )/ (getCapturingTime()*1.0);
    }

    public static void newPacketArrived(Packet packet){
        burstEventHandler.newPacket(packet);
        if(firstPacketArrived){
            // updating statistics
            arrivalTimeOfLastPacket = packet.getArrivalTime();
            increaseNumCapturedPackets();
            increaseCapturedBytes(packet.getParentPacket().getPayload().getArray().length);
        }else {
            firstPacketArrived=true;
            arrivalTimeOfFirstPacket = packet.getArrivalTime();
            arrivalTimeOfLastPacket = packet.getArrivalTime();
            arrivalTimeOfPreviousPacket = packet.getArrivalTime();
            increaseNumCapturedPackets();
            increaseCapturedBytes(packet.getParentPacket().getPayload().getArray().length);
        }
    }


}
