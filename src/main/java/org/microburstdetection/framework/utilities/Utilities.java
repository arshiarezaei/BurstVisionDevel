package org.microburstdetection.framework.utilities;

import io.pkts.packet.Packet;

import java.io.File;
import java.math.RoundingMode;
import java.text.DecimalFormat;

public class Utilities {
    static DecimalFormat df = new DecimalFormat("#.##");
    private static final Utilities utilities = new Utilities();
    private Utilities() {}

    static {
        df.setRoundingMode(RoundingMode.DOWN);
    }
    public static String getDatasetFileName(String datasetAddress){
        File datasetFile  = new File(datasetAddress);
        return datasetFile.getName().replace(".pcap","");
    }
    public static double getRoundedValue(double value){
        return Double.valueOf(df.format(value));
    }

    public static int getPacketPayloadSize(Packet packet){
        return packet.getParentPacket().getPayload().getArray().length;
    }
}
