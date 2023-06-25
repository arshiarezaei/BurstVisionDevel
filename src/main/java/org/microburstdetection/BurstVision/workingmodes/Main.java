package org.microburstdetection.BurstVision.workingmodes;

import io.pkts.Pcap;
import org.apache.commons.cli.*;
import org.microburstdetection.BurstVision.cnfg.WorkingModes;

import java.util.Arrays;


public class Main {
    public static void main(String[] args) {
        Options options = new Options();

        Option input = new Option("i", "input", true, "input(s) file path");
        input.setRequired(true);
        input.setValueSeparator(',');
        options.addOption(input);

        Option workingMode = new Option("m","Analysis Mode",true,"t: traffic-oriented, f:flow-oriented");
        workingMode.setRequired(true);
        options.addOption(workingMode);

        Option output = new Option("o", "output", true, "output file");
        output.setRequired(true);
        options.addOption(output);

        CommandLineParser parser = new DefaultParser();
        HelpFormatter formatter = new HelpFormatter();
        CommandLine cmd = null;//not a good practice, it serves it purpose

        try {
            cmd = parser.parse(options, args);
        } catch (ParseException e) {
            System.out.println(e.getMessage());
            formatter.printHelp("BurstVision", options);
            System.exit(1);
        }

        String inputFilePath = cmd.getOptionValue("i");
        String mode = cmd.getOptionValue("Analysis Mode");
        String outputFilePath = cmd.getOptionValue("output");

        String[] sourceFilePath = inputFilePath.split(",");
        // print input and results paths
//        System.out.println(inputFilePath);
//        System.out.println(outputFilePath);

        switch (mode){
            case "f"-> WorkingModes.setWorkingModes(0);
            case "t"-> WorkingModes.setWorkingModes(1);
        }


        try {
            for (String filePath : sourceFilePath) {
                Pcap pcap = Pcap.openStream(filePath);
                switch (WorkingModes.getWorkingMode()){
                    case FLOW_ORIENTED -> {
                        //TODO: add handling with flow_oriented method
                    }
                    case TRAFFIC_ORIENTED -> {
                        //TODO: add handling with traffic_oriented method
                    }
                }
            }

        }catch (Exception e){
            System.out.println("Error in Main class");
            System.out.println(Arrays.toString(e.getStackTrace()));
        }

    }
}
