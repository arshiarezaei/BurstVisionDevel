package org.microburstdetection.framework.utilities;

public enum TraversedBytesUnits {
    BYTES_PER_SECONDS(0),KILOBYTES_PER_SECOND(3),MEGABYTE_PER_SECOND(6);
    private final int traversedBytesUnits;

    TraversedBytesUnits(int unit) {
        this.traversedBytesUnits = unit;
    }

    public int getTraversedBytesUnits() {
        return traversedBytesUnits;
    }

    @Override
    public String toString() {
        switch (this){
            case  BYTES_PER_SECONDS -> {return "B/s";}
            case KILOBYTES_PER_SECOND -> {return "KB/s";}
            case MEGABYTE_PER_SECOND -> {return "MB/s";}
            default -> {
                return "Error";
            }
        }
    }
}
