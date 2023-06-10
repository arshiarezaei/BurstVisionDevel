package org.microburstdetection.framework.cnfg;

public enum TrafficType {
    HEAVY(0),BURSTY(1);
    private final int trafficType;

    TrafficType(int trafficType) {
        this.trafficType = trafficType;
    }

    public TrafficType getTrafficType() {
        switch (this){
            case HEAVY -> {return  HEAVY;}
            case BURSTY -> {return BURSTY;}
            default -> {
                return null;
            }
        }
    }
//    public int getTrafficType(){
//        switch (this){
//            case HEAVY -> {return 0;}
//            case BURSTY -> {return 1;}
//        }
//    }


    @Override
    public String toString() {
        switch (this){
            case  HEAVY-> {return "HEAVY";}
            case BURSTY -> {return "BURSTY";}
            default -> {
                return "Error";
            }
        }
    }
}
