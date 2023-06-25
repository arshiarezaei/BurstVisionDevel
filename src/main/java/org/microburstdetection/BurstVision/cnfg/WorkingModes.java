package org.microburstdetection.BurstVision.cnfg;

public enum WorkingModes {
    FLOW_ORIENTED(0),TRAFFIC_ORIENTED(1);
    public static WorkingModes workingMode;

    private WorkingModes(int i) {
        // constructor
    }

    public static void setWorkingModes(int i) {
        if(workingMode==null){
            workingMode = getWorkingMode(i);
        }
    }

    public static WorkingModes getWorkingMode() {
        return workingMode;
    }

    private static int getWorkingMode(WorkingModes workingMode){
        switch (workingMode){
            case FLOW_ORIENTED -> {
                return 0;
            }
            case TRAFFIC_ORIENTED -> {
                return 1;
            }
            default -> {
                System.out.println("Invalid Working mode");
                return -1;
            }
        }
    }
    private static WorkingModes getWorkingMode(int i){
        switch (i){
            case 0 -> {
                return WorkingModes.FLOW_ORIENTED;
            }
            case 1 -> {
                return WorkingModes.TRAFFIC_ORIENTED;
            }
            default -> {
                System.out.println("Invalid Working mode");
                return null;
            }
        }
    }
}
