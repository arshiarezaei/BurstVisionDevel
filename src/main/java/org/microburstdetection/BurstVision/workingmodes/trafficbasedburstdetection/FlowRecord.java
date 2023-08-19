package org.microburstdetection.BurstVision.workingmodes.trafficbasedburstdetection;

import io.pkts.packet.*;
import io.pkts.protocol.Protocol;
import org.microburstdetection.networkstack.layer3.IPV4;
import org.microburstdetection.networkstack.layer3.Layer3;
import org.microburstdetection.networkstack.layer4.Layer4;
import org.microburstdetection.networkstack.layer4.TCP;
import org.microburstdetection.networkstack.layer4.UDP;

import java.io.IOException;
import java.util.Objects;

record FlowRecord(Layer3 layer3, Layer4 layer4) {

    public static FiveTupleFlow convertPacketToFlow(Packet packet) throws IOException {
        if(packet.hasProtocol(Protocol.IPv4)){
            IPv4Packet iPv4Packet = (IPv4Packet) packet.getPacket(Protocol.IPv4);
            IPV4 layer31 = new IPV4(iPv4Packet.getSourceIP(),iPv4Packet.getSourceIP());
            if(iPv4Packet.hasProtocol(Protocol.TCP)){
                TCPPacket tcpPacket = (TCPPacket) iPv4Packet.getPacket(Protocol.TCP);
                TCP tcp = new TCP(tcpPacket.getSourcePort(),tcpPacket.getDestinationPort());
                return new FiveTupleFlow(layer31,tcp);
            }else if (iPv4Packet.hasProtocol(Protocol.UDP)) {
                UDPPacket udpPacket = (UDPPacket) iPv4Packet.getPacket(Protocol.UDP);
                UDP udp = new UDP(udpPacket.getSourcePort(),udpPacket.getDestinationPort());
                return new FiveTupleFlow(layer31,udp);
            }
        }
        return null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FlowRecord flow = (FlowRecord) o;
        return Objects.equals(layer3, flow.layer3) && Objects.equals(layer4, flow.layer4);
    }

    @Override
    public int hashCode() {
        return Objects.hash(layer3, layer4);
    }

    public static FlowRecord getFlowFromPacket(Packet packet) {
        Layer3 layer3 = null;
        Layer4 layer4 = null;
        try{
            if(packet.hasProtocol(Protocol.IPv4)){
                IPv4Packet iPv4Packet = (IPv4Packet) packet.getPacket(Protocol.IPv4);
                layer3 = new IPV4(iPv4Packet.getSourceIP(),iPv4Packet.getDestinationIP());
            }else if(packet.hasProtocol(Protocol.IPv6)){
                IPv6Packet iPv6Packet = (IPv6Packet) packet.getPacket(Protocol.IPv6);
                layer3 = new IPV4(iPv6Packet.getSourceIP(),iPv6Packet.getDestinationIP());
            }
            if(packet.hasProtocol(Protocol.TCP)){
                TCPPacket tcpPacket = (TCPPacket) packet.getPacket(Protocol.TCP);
                layer4 = new TCP(tcpPacket.getSourcePort(),tcpPacket.getDestinationPort());
            }else if(packet.hasProtocol(Protocol.UDP)){
                UDPPacket udpPacket = (UDPPacket) packet.getPacket(Protocol.UDP);
                layer4 = new UDP(udpPacket.getSourcePort(),udpPacket.getDestinationPort());
                return new FlowRecord(layer3,layer4);
            }
        }catch (Exception e){
            System.out.println(e);
        }
        return new FlowRecord(layer3,layer4);

    }
}
