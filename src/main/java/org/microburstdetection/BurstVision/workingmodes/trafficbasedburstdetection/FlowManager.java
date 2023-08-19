package org.microburstdetection.BurstVision.workingmodes.trafficbasedburstdetection;

import io.pkts.packet.IPv4Packet;
import io.pkts.packet.Packet;
import io.pkts.packet.TCPPacket;
import io.pkts.packet.UDPPacket;
import io.pkts.protocol.Protocol;
import org.microburstdetection.networkstack.layer3.IPV4;
import org.microburstdetection.networkstack.layer4.TCP;
import org.microburstdetection.networkstack.layer4.UDP;

import java.io.IOException;
import java.util.HashSet;

public class FlowManager {
    private static HashSet<FiveTupleFlow> flowHashSet = new HashSet<>();

    private FlowManager() {
    }

    public static int getNumberOfFlows(){
        return flowHashSet.size();
    }
    public static void addFlowToFlowsHashset(Packet packet) throws Exception {
        FiveTupleFlow fiveTupleFlow = null;
        if(packet.hasProtocol(Protocol.IPv4)){
            IPv4Packet iPv4Packet = (IPv4Packet) packet.getPacket(Protocol.IPv4);
            IPV4 layer31 = new IPV4(iPv4Packet.getSourceIP(),iPv4Packet.getSourceIP());
            if(iPv4Packet.hasProtocol(Protocol.TCP) ||iPv4Packet.hasProtocol(Protocol.UDP) ){
                fiveTupleFlow =  new FiveTupleFlow(packet);
            }
        }else {
            return;
        }
        if(!flowHashSet.contains(fiveTupleFlow)){
            flowHashSet.add(fiveTupleFlow);
            assert fiveTupleFlow != null;
            fiveTupleFlow.newPacket(packet);
        }else {
            for (FiveTupleFlow flow: flowHashSet) {
                if(flow.equals(fiveTupleFlow)){
                    flow.newPacket(packet);
                }
            }
        }
    }
}
