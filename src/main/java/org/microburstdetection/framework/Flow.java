package org.microburstdetection.framework;

public abstract class Flow implements RawFlow {
    protected BurstEvents burstEvents;

    public BurstEvents getBurstEvents() {
        return burstEvents;
    }
}
