package org.microburstdetection.BurstVision.trafficbasedburstdetection;


import java.util.ArrayList;


import io.pkts.packet.Packet;

import org.microburstdetection.BurstVision.trafficbasedburstdetection.BurstEventHandler;
import org.microburstdetection.BurstVision.cnfg.ConfigurationParameters;


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

    public static void newPacketArrived(Packet packet){
        if(firstPacketArrived){
            // updating statistics
            arrivalTimeOfLastPacket = packet.getArrivalTime();
            increaseNumCapturedPackets();
            increaseCapturedBytes(packet.getPayload().getArray().length);
            burstEventHandler.newPacket(packet);
        }else {
            firstPacketArrived=true;
            arrivalTimeOfFirstPacket = packet.getArrivalTime();
            arrivalTimeOfLastPacket = packet.getArrivalTime();
            arrivalTimeOfPreviousPacket = packet.getArrivalTime();
        }
    }
}
