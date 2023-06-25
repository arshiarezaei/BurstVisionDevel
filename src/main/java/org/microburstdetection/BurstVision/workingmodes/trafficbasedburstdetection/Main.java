package org.microburstdetection.BurstVision.workingmodes.trafficbasedburstdetection;

import io.pkts.Pcap;
import org.apache.commons.cli.*;
import org.microburstdetection.BurstVision.utilities.Utilities;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Collectors;


public class Main {
    public static void main(String[] args) {
        Options options = new Options();

        Option input = new Option("i", "input", true, "input(s) file path");
        input.setRequired(true);
        options.addOption(input);

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
            formatter.printHelp("utility-name", options);

            System.exit(1);
        }

        String inputFilePath = cmd.getOptionValue("input");
        String outputFilePath = cmd.getOptionValue("output");
        // print input and results paths
//        System.out.println(inputFilePath);
//        System.out.println(outputFilePath);
        String[] sourceFilePath = inputFilePath.split(",");
        String resultsPath = outputFilePath;
        // read pcap file
        try {
            for (String filePath : sourceFilePath) {
                Pcap pcap = Pcap.openStream(filePath);
                pcap.loop(new TrafficHandler());
                pcap.close();
            }
            Results.createDirsToStoreResults(resultsPath, Utilities.getDatasetFileName(sourceFilePath[0]));
            Results.saveGeneralResultsToFile();
            Results.generateCDFOfBurstDuration(TrafficBasedAnalyser.getBurstEventHandler().getBurstsDuration());
            Results.generateCDFOfNumberOfPacketsInEachBurst(TrafficBasedAnalyser.getBurstEventHandler().getBurstEvents());
            Results.generateCDFOfTraversedBytesInEachBurst(TrafficBasedAnalyser.getBurstEventHandler().getTraversedBytesInEachBurst());
            Results.generateCDFOfInterBurstTime(TrafficBasedAnalyser.getBurstEventHandler().getBurstInterBurstTime());
            ArrayList<Integer> listFlowsContributeToBurst = TrafficBasedAnalyser.getBurstEventHandler().getBurstEvents().stream().
                    map(a->a.flowsContributedToBurst().size()).collect(Collectors.toCollection(ArrayList::new));
            Results.generateCDFOfNumberOfFlowsContributingToBursts(listFlowsContributeToBurst);
            Results.generateCDFAveragePacketSize(TrafficBasedAnalyser.getBurstEventHandler().getAveragePacketSize());

//            ArrayList arrayList = (TrafficBasedAnalyser.getBurstEventHandler().getBurstEvents()).
//                    stream().mapToInt(BurstEvent::getNumberOfPackets).collect(Collectors.toCollection(ArrayList::new));
//            ArrayList<Integer> arrayList= TrafficBasedAnalyser.getBurstEventHandler().getBurstEvents()
//                    .stream().map(BurstEvent::getNumberOfPackets).collect(Collectors.toCollection(ArrayList::new));

//        ArrayList<BurstEvent> burstEvents = TrafficBasedAnalyser.getBurstEventHandler().getBurstEvents();
//        System.out.println(Collections.max(burstEvents.stream().map(a->a.getNumberOfConcurrentBurstyFlows()).collect(Collectors.toList())));

    }catch (Exception e){
            System.out.println("Error in Main class");
            System.out.println(Arrays.toString(e.getStackTrace()));
        }
    }
}
