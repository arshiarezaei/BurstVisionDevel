package org.microburstdetection.BurstVision.trafficbasedburstdetection;

import io.pkts.PacketHandler;
import io.pkts.packet.*;
import io.pkts.protocol.Protocol;
import org.microburstdetection.BurstVision.FlowManager;
import org.microburstdetection.BurstVision.trafficbasedburstdetection.TrafficBasedAnalyser;

import java.io.IOException;


class TrafficHandler implements PacketHandler {

    @Override
    public boolean nextPacket(Packet packet) throws IOException {

//         Check the packet protocol
        try {
            if (packet.hasProtocol(Protocol.IPv4)){
                IPv4Packet iPv4Packet = (IPv4Packet) packet.getPacket(Protocol.IPv4);
                if(iPv4Packet.hasProtocol(Protocol.TCP)|| iPv4Packet.hasProtocol(Protocol.UDP)){
                    TrafficBasedAnalyser.newPacketArrived(iPv4Packet);
                }
            }else if(packet.hasProtocol(Protocol.IPv6)) {
            }
        }catch (Exception e){
            System.out.println(e);
        }

        return true;
    }

}
