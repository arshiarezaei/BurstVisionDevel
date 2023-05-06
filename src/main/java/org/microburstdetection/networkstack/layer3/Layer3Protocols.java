package org.microburstdetection.networkstack.layer3;

public enum Layer3Protocols {
    IPV4(4),
    IPV6(41);
    private final int Layer3Protocol;

    Layer3Protocols(int layer3Protocol) {
        Layer3Protocol = layer3Protocol;
    }

    public int getLayer3Protocol() {
        return Layer3Protocol;
    }

}
