package org.microburstdetection.framework.utilities;

import java.io.File;

public class Utilities {

    private static Utilities utilities = new Utilities();
    private Utilities() {}

    public static String getDatasetFileName(String datasetAddress){
        File datasetFile  = new File(datasetAddress);
        return datasetFile.getName();
    }
}
