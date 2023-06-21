package org.microburstdetection.framework;

import java.util.ArrayList;
import java.util.Objects;
import java.util.stream.Collectors;

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


public class FiveTupleFlow extends Flow implements RawFlow {
    // Flow headers
    private Layer3 layer3;
    private Layer4 layer4;
    // end of flow headers
    private long firstPacketTime; // arrival time of first packer
    private long lastPacketTime; // arrival time of last packet
    private int numberOfPackets;
    private int traversedBytes; // traversed bytes between first and last packets
    private boolean firstPacketArrived = false;

    private BurstEventHandler burstEventHandler = new BurstEventHandler(); // captures burst events

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
//            throw new Exception("IPV6 Packet");
        }
    }

    public void newPacket(Packet packet){
        if(firstPacketArrived){
            this.burstEventHandler.newPacket(packet);
            numberOfPackets+=1;
        }else {
             burstEventHandler.newPacket(packet);
            firstPacketArrived=true;
            this.firstPacketTime = Math.abs(packet.getArrivalTime());
            numberOfPackets=1;
        }
        this.increaseTraversedBytes(packet);
        this.lastPacketTime = Math.abs(packet.getArrivalTime());
    }
    @Override
    public boolean isBursty() {
        return !this.burstEventHandler.getBurstEvents().isEmpty();
    }

    @Override
    public boolean isHeavy() {
        return this.getAverageThroughput(TraversedBytesUnits.BYTES_PER_SECONDS) >= -1;
//        this.getAverageThroughput(TraversedBytesUnits.BYTES_PER_SECONDS) >= HeavyFlowStaticProperties.getHeavyFlowThroughputThreshold();
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
        return Math.abs(getlastPacketTime()-getFirstPacketTime());
    }

    @Override
    public int getNumberOfPackets() {
        return this.numberOfPackets;
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
        return Objects.hash(layer3, layer4, burstEventHandler);
    }

    @Override
    public BurstEventHandler getBurstEvents() {
        return burstEventHandler;
    }

    @Override
    public void increaseTraversedBytes(Packet packet) {

        traversedBytes = getTraversedBytes() + Utilities.getPacketPayloadSize(packet);
    }

    @Override
    public double getAverageThroughput(TraversedBytesUnits T) {
//        return (getNumberOfPackets()<=2) ? 0:(getTraversedBytes()/(flowLiveTime()*1.0)*Math.pow(10,T.getTraversedBytesUnits()));//*Math.pow(10,pow);
//        double t = getTraversedBytes()/(flowLiveTime()*1.0);
//        if(t<=0){
//            System.out.println(flowLiveTime()+"\t"+numberOfPackets+"\t"+layer3+"\t"+layer4);
//        }
        return (getNumberOfPackets()==1) ? 0:(getTraversedBytes()*1.0/(flowLiveTime()*1.0))*Math.pow(10,T.getTraversedBytesUnits());
    }

    @Override
    public  Double getAverageThroughputInBursts(TraversedBytesUnits T) {
        // TODO: throws Exception if the flow is not bursty
        if(this.isBursty()){
            return  this.burstEventHandler.getAverageThroughputInBursts();
        }
        return null;
    }

    @Override
    public ArrayList<Double> getThroughputInEachBurst() {
        return burstEventHandler.getThroughputInEachBurst();
    }

    @Override
    public ArrayList<Double> getListOfBurstsRatio() {
        ArrayList<Double> listOfBurstsRatio = new ArrayList<>();
        double avgThroughput= getAverageThroughput(TraversedBytesUnits.BYTES_PER_SECONDS);
        ArrayList<Double> listOfThroughputInEachBurst = getThroughputInEachBurst();
        if(isBursty()){
            listOfBurstsRatio =  listOfThroughputInEachBurst.stream().map(th -> th/avgThroughput).collect(Collectors.toCollection(ArrayList::new));
        }
        return listOfBurstsRatio;
    }
}
