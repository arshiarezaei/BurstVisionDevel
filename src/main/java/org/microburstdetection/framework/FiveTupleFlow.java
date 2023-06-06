package org.microburstdetection.framework;

import java.util.Objects;

import io.pkts.packet.IPv4Packet;
import io.pkts.packet.Packet;
import io.pkts.packet.TCPPacket;
import io.pkts.packet.UDPPacket;
import io.pkts.protocol.Protocol;

import org.microburstdetection.framework.utilities.TraversedBytesUnits;
import org.microburstdetection.framework.utilities.Utilities;
import org.microburstdetection.networkstack.layer3.IPV4;
import org.microburstdetection.networkstack.layer3.Layer3;
import org.microburstdetection.networkstack.layer4.Layer4;
import org.microburstdetection.networkstack.layer4.TCP;
import org.microburstdetection.networkstack.layer4.UDP;


public class FiveTupleFlow implements RawFlow {
    private Layer3 layer3;
    private Layer4 layer4;
    private int traversedBytes;
    private long firstPacketTime;
    private long lastPacketTime;
    private boolean firstPacketArrived = false;

    private BurstEvents burstEvents;
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
            this.burstEvents.newPacket(packet);
        }else {
            this.burstEvents = new BurstEvents(packet);
            firstPacketArrived=true;
            this.firstPacketTime = packet.getArrivalTime();
        }
        this.increaseTraversedBytes(packet);
        this.lastPacketTime = packet.getArrivalTime();
    }

    @Override
    public long getFirstPacketTime() {
        return firstPacketTime;
    }

    @Override
    public long getlastPacketTime() {
        return lastPacketTime;
    }

    public long flowLiveTime(){
        return getlastPacketTime()-getFirstPacketTime();
    }
    @Override
    public int getTraversedBytes(){
        return traversedBytes;
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
        return this.burstEvents.getBurstsDuration().size() != 0;
    }

    @Override
    public BurstEvents getBurstEvents() {
        return burstEvents;
    }

    @Override
    public void increaseTraversedBytes(Packet packet) {

        traversedBytes = getTraversedBytes() + Utilities.getPacketPayloadSize(packet);
    }

    @Override
    public <T> double getAverageThroughput(TraversedBytesUnits T) {
        int pow ;
        switch (T){
            case BYTES_PER_SECONDS -> pow=0;
            case KILOBYTES_PER_SECOND -> pow=4;
            case MEGABYTE_PER_SECOND -> pow=6;
            default -> pow=0;
        }

        return getTraversedBytes()/(flowLiveTime()*1.0)*Math.pow(10,pow);
    }

    @Override
    public double getAverageThroughputInBursts() {
        // TODO: throws Exception if the flow is not bursty
        return this.burstEvents.getAverageBurstThroughput(TraversedBytesUnits.KILOBYTES_PER_SECOND);
    }
}
