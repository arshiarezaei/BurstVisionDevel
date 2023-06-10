package org.microburstdetection.framework;

import io.pkts.packet.IPv4Packet;
import io.pkts.packet.Packet;
import io.pkts.protocol.Protocol;
import org.microburstdetection.framework.cnfg.TrafficType;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class FlowManager {

    private static final ArrayList<RawFlow> flows = new ArrayList<>();
    private static final FlowManager flowManager = new FlowManager();
    private FlowManager(){}

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
///*    public static  int getNumberOfFlowsByType(){
////        System.out.println(flowClass.toString());
//        System.out.println(getFlows().get(0).getClass().getInterfaces()[0].getSimpleName().equals( getFlows().get(0).getClass().toString()));
//        for (RawFlow rawFlow: getFlows()) {
//            System.out.println(rawFlow.getClass().getInterfaces()[0].getSimpleName().equals( rawFlow.getClass().toString()));
//        }
//        return 0;
//    }*/
    public static ArrayList<RawFlow> getFlowsByTrafficType(TrafficType trafficType){
        switch (trafficType){
            case HEAVY -> {return getFlows().stream().filter(RawFlow::isHeavy).collect(Collectors.toCollection(ArrayList::new));}
            case BURSTY -> {return getFlows().stream().filter(RawFlow::isBursty).collect(Collectors.toCollection(ArrayList::new));}
            default -> { return new ArrayList<>();}
        }
    }
    private static <flowClass>  ArrayList<flowClass> getFlowsByClassName (ArrayList<RawFlow> flows, Class<flowClass> flowClass){
        return flows.stream().filter(rawFlow -> rawFlow.getClass().getSimpleName().equals(flowClass.getSimpleName())).map(rawFlow -> (flowClass) rawFlow).collect(Collectors.toCollection(ArrayList::new));
    }
    public static <Flow> int getNumberOfFlowsByType(Class<Flow> flowType, TrafficType flowTrafficClass,Class... protocols) throws IllegalStateException{
        int counter = 0;
//        System.out.println(protocols[0].getSimpleName()+"\t"+protocols[1].getSimpleName());
        for (Flow flow: getFlowsByClassName(getFlowsByTrafficType(flowTrafficClass),flowType)) {
            String s = flow.getClass().getSimpleName();
            switch (flow){
                case FiveTupleFlow f: {
                    f = (FiveTupleFlow) flow;
                    if(f.getLayer3().getClass().getSimpleName().equals(protocols[0].getSimpleName())&&
                        f.getLayer4().getClass().getSimpleName().equals(protocols[1].getSimpleName())) {
                        counter++;
                        break;
                    }
                }
                default:
//                    throw new IllegalStateException("Unexpected value: " + flow);
            }
        }
//        System.out.println(counter);
        return counter;
    }

}
