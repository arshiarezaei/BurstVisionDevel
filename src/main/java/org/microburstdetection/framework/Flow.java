package org.microburstdetection.framework;

import java.util.Objects;

public abstract class Flow implements RawFlow {
    protected BurstEvents burstEvents;
    public BurstEvents getBurstEvents() {
        return burstEvents;
    }

}
