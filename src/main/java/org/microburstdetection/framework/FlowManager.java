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
import java.util.Arrays;
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
//            Check the packet protocol
            if (packet.hasProtocol(Protocol.IPv4)){
                IPv4Packet iPv4Packet = (IPv4Packet) packet.getPacket(Protocol.IPv4);
                if(packet.hasProtocol(Protocol.TCP) || packet.hasProtocol(Protocol.UDP)) {
                    newFlow = new FiveTupleFlow(packet);
                    boolean result = addNewFlowToFlowsList(newFlow,iPv4Packet);
                    if(!result){
                        System.out.println("Fail to add a new Flow");
                        System.exit(3);
                    }
                }
            }else if(packet.hasProtocol(Protocol.IPv6)) {
                //TODO: add IPV6
//                System.out.println("IPV6 protocol");
            }
        }catch (Exception e){
            System.out.println(e);
        }

    }
    private static boolean addNewFlowToFlowsList(RawFlow flow,Packet packet) {
        if (flows.contains(flow)) {
            List<Integer> indices = IntStream.range(0, flows.size())
                    .filter(i -> flows.get(i).equals(flow))
                    .boxed().toList();
            if (indices.size() == 1) {
                RawFlow fflow =  flows.get(flows.indexOf(flow));
                fflow.newPacket(packet);
                return true;
            } else if (indices.size() != 0) {
                System.out.println("ERROR->addNewFlowToFlowsList ");
                System.exit(4);
                return false;
            }else {
                return false;
            }
        }else {
            flows.add(flow);
            flow.newPacket(packet);
            return true;
        }
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
