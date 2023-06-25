package org.microburstdetection.BurstVision.workingmodes.trafficbasedburstdetection;

import io.pkts.PacketHandler;
import io.pkts.packet.*;
import io.pkts.protocol.Protocol;


class TrafficHandler implements PacketHandler {
    @Override
    public boolean nextPacket(Packet packet){
//         Check the packet protocol
        try {
            if (packet.hasProtocol(Protocol.IPv4)){
                IPv4Packet iPv4Packet = (IPv4Packet) packet.getPacket(Protocol.IPv4);
                if(iPv4Packet.hasProtocol(Protocol.TCP)|| iPv4Packet.hasProtocol(Protocol.UDP)){
                    TrafficBasedAnalyser.newPacketArrived(iPv4Packet);
                }
            }else if(packet.hasProtocol(Protocol.IPv6)) {
//                System.out.println("Add implementation of IPV6");
            }
        }catch (Exception e){
            System.out.println(e);
        }

        return true;
    }

}
