package org.microburstdetection.framework;

import io.pkts.packet.IPv4Packet;
import io.pkts.packet.Packet;
import io.pkts.protocol.Protocol;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class FlowManager {
    private static ArrayList<RawFlow> flows = new ArrayList<>();

    public static ArrayList<RawFlow> getFlows() {
        return flows;
    }

    public static int getNumberOfFlows(){
        return flows.size();
    }
    public static void newPacket(Packet packet){
        RawFlow newFlow;
        try{
//                    Check the packet protocol
            if (packet.hasProtocol(Protocol.IPv4)){
                IPv4Packet iPv4Packet = (IPv4Packet) packet.getPacket(Protocol.IPv4);
//                FlowManager.newPacket(iPv4Packet);
                if(packet.hasProtocol(Protocol.TCP) || packet.hasProtocol(Protocol.UDP)) {
//                    System.out.println("TCP Packet");
//                    TCPPacket tcpPacket = (TCPPacket) packet.getPacket(Protocol.TCP) ;
                    newFlow = new FiveTupleFlow(packet);
                    boolean result = addNewFlowToFlowsList(newFlow,iPv4Packet);
                    if(!result){
                        System.out.println("OOOOPS");
                        System.exit(3);
                    }
                } else {
//                    System.out.println("Not parsed transport-layer protocol");
                }
            }else if(packet.hasProtocol(Protocol.IPv6)) {
//                System.out.println("IPV6 protocol");
            }else {
//                System.out.println("Not parsed layer-3 protocol");
            }
        }catch (Exception e){
            System.out.println(e);
        }

    }
    private static boolean addNewFlowToFlowsList(RawFlow flow,Packet packet) {
        if (flows.contains(flow)) {
            List<Integer> indices = IntStream.range(0, flows.size())
                    .filter(i -> flows.get(i).equals(flow))
                    .boxed().collect(Collectors.toList());
            if (indices.size() == 0) {
                System.out.println("ERROR");
                System.exit(1);
//                flows.add(flow);
                return false;
            } else if (indices.size() == 1) {
                FiveTupleFlow fflow = (FiveTupleFlow) flows.get(flows.indexOf(flow));
                fflow.newPacket(packet);
                return true;
            }else {
                System.out.println("ERROR");
                System.exit(1);
                return false;
            }
        }else {
            flows.add(flow);
//            FiveTupleFlow fiveTupleFlow = flows.indexOf(flow);
            ((FiveTupleFlow)flow).newPacket(packet);
            return true;
        }
    }
//            else if (indices.size()>1) {
//                System.out.println("error duplicate flows");
//            }
//            if (indices.size()>1){
//
//                System.out.println(indices);
//            }else if (indices.size()==1){
//                System.out.println("Exist");
////                flows.get(flows.indexOf(flow)).newMatchedPacket(flow);
//                return flows.get(flows.indexOf(flow));
//            } else{
//                System.out.println("error");
//            }
//
//        }else {
//                flows.add(flow);


//    public static Double printAverageBurstduration(){
//        Double sumOfAvgBurstsDuration = 0.0D;
//        for (Flow flow:flows) {
//            Double av =BurstEvents.getAverageBurstDuration(flow.getBurstEvents().getBurstsDuration()); //flow.CalculateAverageBurstDuration();
//            if(!av.isNaN()){
////                System.out.println(av);
//                sumOfAvgBurstsDuration+=av;
//            }
//        }
//        Double avgBurstDuration = sumOfAvgBurstsDuration/(flows.size()*1.0);
//        return avgBurstDuration;
//    }
//    public static int numberOfBurstyFlows(){
//        int count =0 ;
//        for (Flow flow:flows) {
//            if(flow.isBursty()){
//                count++;
//            }
//        }
//        return count;
//    }
}
