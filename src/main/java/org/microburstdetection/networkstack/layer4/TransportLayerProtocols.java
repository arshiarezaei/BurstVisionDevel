package org.microburstdetection.networkstack.layer4;

public enum TransportLayerProtocols {
    TCP(6),
    UDP(17);
    private final int transportLayerProtocol;

    TransportLayerProtocols(int transportLayerProtocol) {
        this.transportLayerProtocol = transportLayerProtocol;
    }

    public int getTransportLayerProtocol() {
        return transportLayerProtocol;
    }
}
