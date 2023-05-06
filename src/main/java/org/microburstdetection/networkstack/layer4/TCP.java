package org.microburstdetection.networkstack.layer4;

import java.util.Objects;

public record TCP(int srcPort, int dstPort) implements Layer4{
    public static final TransportLayerProtocols transportLayerProtocolCode = TransportLayerProtocols.TCP;
    public TCP(int srcPort, int dstPort) {
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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TCP tcp = (TCP) o;
        return srcPort == tcp.srcPort && dstPort == tcp.dstPort;
    }

    @Override
    public int hashCode() {
        return Objects.hash(srcPort, dstPort);
    }
}
