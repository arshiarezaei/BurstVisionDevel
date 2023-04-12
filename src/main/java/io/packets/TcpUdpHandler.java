package io.packets;

import io.pkts.PacketHandler;
import io.pkts.packet.*;
import java.io.IOException;


public class TcpUdpHandler implements PacketHandler {

    @Override
    public boolean nextPacket(Packet packet) throws IOException {

//         Check the packet protocol
//        if (packet.hasProtocol(Protocol.IPv4)){
//            IPv4Packet iPv4Packet = (IPv4Packet) packet.getPacket(Protocol.IPv4);
//            if(packet.hasProtocol(Protocol.TCP)){
//                TCPPacket tcpPacket = (TCPPacket) packet.getPacket(Protocol.TCP) ;
//                Buffer buffer = tcpPacket.getPayload();
//                if (buffer != null) {
//                    System.out.println("TCP: " + buffer);
//                }
//            }
//
//            }
//        }
//        if (packet.hasProtocol(Protocol.TCP)) {
//            // Cast the packet to subclass
//            TCPPacket tcpPacket = (TCPPacket) packet.getPacket(Protocol.TCP);
//
//            // Explore the available methods.
//            // This sample code prints the payload, but you can get other attributes as well
//            Buffer buffer = tcpPacket.getPayload();
//            if (buffer != null) {
////                System.out.println("TCP: " +  buffer);
//            }
//        } else if (packet.hasProtocol(Protocol.UDP)) {
//            // Cast the packet to subclass
//            UDPPacket udpPacket = (UDPPacket) packet.getPacket(Protocol.UDP);
//
//            // Explore the available methods.
//            // This sample code prints the payload, but you can get other attributes as well
//            Buffer buffer = udpPacket.getPayload();
//            if (buffer != null) {
////                System.out.println("UDP: " +  buffer);
//            }
//        }

        // Return true if you want to keep receiving next packet.
        // Return false if you want to stop traversal
        return true;
    }

}
