package org.microburstdetection.networkstack.layer3;

import java.util.Objects;

public record IPV6(String srdAddress, String dstAddress) implements Layer3 {
    public static final Layer3Protocols IPVersion = Layer3Protocols.IPV6;
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
        IPV6 ipv6 = (IPV6) o;
        return Objects.equals(srdAddress, ipv6.srdAddress) && Objects.equals(dstAddress, ipv6.dstAddress);
    }

    @Override
    public int hashCode() {
        return Objects.hash(srdAddress, dstAddress);
    }

    public static int getProtocolNumber(){
        return IPVersion.getLayer3Protocol();
    }
}
