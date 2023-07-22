package org.microburstdetection.BurstVision.workingmodes.trafficbasedburstdetection;

import io.pkts.PacketHandler;
import io.pkts.packet.*;
import io.pkts.protocol.Protocol;

import java.io.IOException;


class TrafficHandler implements PacketHandler {
    public static long bytes;
    public static long counter;
    @Override
    public boolean nextPacket(Packet packet) throws IOException {
//         Check the packet protocol
//        System.out.println(packe);
//        System.out.println(packet.getPaylo);
        PCapPacket pCapPacket = (PCapPacket) packet.getPacket(Protocol.PCAP);
//        pCapPacket.getProtocol()
        long a = pCapPacket.getTotalLength();
        bytes+=a;
        counter++;
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
