package org.microburstdetection.BurstVision.utilities;

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
            case  BYTES_PER_SECONDS -> {return "B/us";}
            case KILOBYTES_PER_SECOND -> {return "KB/us";}
            case MEGABYTE_PER_SECOND -> {return "MB/us";}
            default -> {
                return "Error";
            }
        }
    }
}
