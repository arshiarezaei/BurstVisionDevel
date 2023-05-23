package org.microburstdetection.framework;

import io.pkts.packet.IPv4Packet;
import io.pkts.packet.Packet;
import io.pkts.protocol.Protocol;
import org.microburstdetection.networkstack.layer3.IPV4;
import org.microburstdetection.networkstack.layer3.IPV6;
import org.microburstdetection.networkstack.layer3.Layer3;
import org.microburstdetection.networkstack.layer4.Layer4;
import org.microburstdetection.networkstack.layer4.TCP;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class FlowManager {

    private static final FlowManager flowManager = new FlowManager();
    private FlowManager(){}
    private static final ArrayList<RawFlow> flows = new ArrayList<>();

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
            flow.newPacket(packet);
            return true;
        }
    }

    public static int getNumberOfBurstFlows(){
        int count =0 ;
        for (RawFlow flow:flows) {
            if(flow.isBursty()){
                count++;
            }
        }
        return count;
    }

    public static  <layer3,layer4> int getNumberOfFlowsWithProtocol(Class<layer3> layer3, Class<layer4>  layer4){
        //TODO: reimplement function and use generics
        int counter = 0;
        for (RawFlow flow:getFlows()) {
            FiveTupleFlow flowr = (FiveTupleFlow) flow;
            if(flowr.getLayer3().getClass().isAssignableFrom( layer3 )&& flowr.getLayer4().getClass().isAssignableFrom( layer4 )) {
                counter++;
            }
        }
        return counter;
    }
}
