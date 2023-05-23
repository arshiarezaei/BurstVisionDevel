package org.microburstdetection.framework;

import io.pkts.packet.IPv4Packet;
import io.pkts.packet.Packet;
import io.pkts.packet.TCPPacket;
import io.pkts.packet.UDPPacket;
import io.pkts.protocol.Protocol;
import org.microburstdetection.networkstack.layer3.IPV4;
import org.microburstdetection.networkstack.layer3.Layer3;
import org.microburstdetection.networkstack.layer4.Layer4;
import org.microburstdetection.networkstack.layer4.TCP;
import org.microburstdetection.networkstack.layer4.UDP;

import java.util.Objects;

public class FiveTupleFlow extends Flow{
    private Layer3 layer3;
    private Layer4 layer4;

//    private BurstEvents burstEvents;

    private boolean firstPacketArrived = false;

    private FiveTupleFlow(Layer3 layer3, Layer4 layer4) {
        this.layer3 = layer3;
        this.layer4 = layer4;
//        this.burstEvents = burstEvents;
    }

    public Layer3 getLayer3() {
        return layer3;
    }

    public Layer4 getLayer4() {
        return layer4;
    }


    public FiveTupleFlow(Packet packet) throws Exception {
        if(packet.hasProtocol(Protocol.IPv4)){
            IPv4Packet iPv4Packet = (IPv4Packet) packet.getPacket(Protocol.IPv4);
            IPV4 ipv4Headers = new IPV4(iPv4Packet.getSourceIP(),iPv4Packet.getDestinationIP());
            this.layer3 = ipv4Headers;
            if(iPv4Packet.hasProtocol(Protocol.TCP)){
                TCPPacket tcpPacket = (TCPPacket) packet.getPacket(Protocol.TCP);
                TCP tcpHeaders = new TCP(tcpPacket.getSourcePort(),tcpPacket.getDestinationPort());
                this.layer4 = tcpHeaders;
//                this.burstEvents = new BurstEvents(packet);
//                this(ipv4Headers,tcpHeaders);
            } else if (iPv4Packet.hasProtocol(Protocol.UDP)) {
                UDPPacket udpPacket = (UDPPacket) packet.getPacket(Protocol.UDP);
                UDP UDPHeaders = new UDP(udpPacket.getSourcePort(), udpPacket.getDestinationPort());
                this.layer4 = UDPHeaders;
//                this.burstEvents = new BurstEvents(packet);
            }
        } else if (packet.hasProtocol(Protocol.IPv6)) {
            throw new Exception("IPV6 Packet");
        }
    }

    public void newPacket(Packet packet){
        if(firstPacketArrived){
            super.burstEvents.newPacket(packet);
        }else {
            super.burstEvents = new BurstEvents(packet);
            firstPacketArrived=true;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FiveTupleFlow that = (FiveTupleFlow) o;
        return Objects.equals(layer3, that.layer3) && Objects.equals(layer4, that.layer4);
    }

    @Override
    public int hashCode() {
        return Objects.hash(layer3, layer4, burstEvents);
    }

    @Override
    public boolean isBursty() {
        return super.burstEvents.getBurstsDuration().size() != 0;
    }
}
