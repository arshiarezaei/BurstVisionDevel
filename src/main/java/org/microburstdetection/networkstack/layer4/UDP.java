package org.microburstdetection.networkstack.layer4;

import java.util.Objects;

public record UDP(int srcPort, int dstPort) implements Layer4 {
    public static final TransportLayerProtocols transportLayerProtocolCode = TransportLayerProtocols.UDP;
    public UDP(int srcPort, int dstPort) {
        this.srcPort = srcPort;
        this.dstPort = dstPort;
    }

    @Override
    public int srcPort() {
        return srcPort;
    }

    @Override
    public int dstPort() {
        return dstPort;
    }

    @Override
    public int getTransportProtocol() {
        return transportLayerProtocolCode.getTransportLayerProtocol();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UDP udp = (UDP) o;
        return srcPort == udp.srcPort && dstPort == udp.dstPort;
    }

    @Override
    public int hashCode() {
        return Objects.hash(srcPort, dstPort);
    }
}
