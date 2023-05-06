package org.microburstdetection.networkstack.layer3;

import java.util.Objects;

public record IPV4(String srdAddress, String dstAddress) implements Layer3{
    public static final Layer3Protocols IPVersion = Layer3Protocols.IPV4;

    @Override
    public String srdAddress() {
        return srdAddress;
    }

    @Override
    public String dstAddress() {
        return dstAddress;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        IPV4 ipv4 = (IPV4) o;
        return Objects.equals(srdAddress, ipv4.srdAddress) && Objects.equals(dstAddress, ipv4.dstAddress);
    }

    @Override
    public int hashCode() {
        return Objects.hash(srdAddress, dstAddress);
    }

    public static int getProtocolNumber(){
        return IPVersion.getLayer3Protocol();
    }
}
