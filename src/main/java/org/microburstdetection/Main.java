package org.microburstdetection;

import io.pkts.Pcap;
import org.apache.commons.cli.*;
import org.microburstdetection.BurstVision.FlowManager;
import org.microburstdetection.BurstVision.RawFlow;
import org.microburstdetection.BurstVision.results.Results;
import org.microburstdetection.BurstVision.utilities.TraversedBytesUnits;
import org.microburstdetection.BurstVision.utilities.Utilities;
import org.microburstdetection.networkstack.layer4.TransportLayerProtocols;
import org.microburstdetection.packethandler.TcpUdpHandler;

import java.util.ArrayList;
import java.util.Arrays;

public class Main {

    public static void main(String[] args) throws Exception {

//         args[0]: path of pcap file
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
                pcap.loop(new TcpUdpHandler());
                pcap.close();
            }
            // make directory to save results
            Results.createDirsToStoreResults(resultsPath, Utilities.getDatasetFileName(sourceFilePath[0]));
            // save general results into a single text file
            Results.saveGeneralResultsToFile(FlowManager.getFlows());
            Results.printCDFOfNumBurstsOfAllFlows(FlowManager.getFlows());
            Results.calculateCDFOfBurstsDurationOfFlows(FlowManager.getFlows());
            Results.saveCDFBytesTraversedBursts(FlowManager.getFlows());
            Results.printCDFBytesTraversedBurstsToFile(FlowManager.getFlows(), TransportLayerProtocols.UDP.getTransportLayerProtocol());
            Results.printCDFBytesTraversedBurstsToFile(FlowManager.getFlows(), TransportLayerProtocols.TCP.getTransportLayerProtocol());
            Results.saveCDFOfFlowsThroughput(FlowManager.getFlows(), TraversedBytesUnits.BYTES_PER_SECONDS);
            Results.saveCDFOFInterBurstTime (FlowManager.getFlows());
            Results.saveCDFOfBurstRatio(FlowManager.getFlows());
//            FlowManager.getNumberOfFlowsByType(FiveTupleFlow.class,TrafficType.HEAVY.getTrafficType(), IPV4.class, UDP.class);
//            FlowManager.getNumberOfFlowsByType(FiveTupleFlow.class,TrafficType.BURSTY.getTrafficType(), IPV4.class, TCP.class);
            ArrayList<RawFlow> flows = FlowManager.getFlows();
            System.out.println(flows.size());
//            for (RawFlow rawFlow : FlowManager.getFlows()) {
//                ArrayList<Long> bd = rawFlow.getBurstEvents().getBurstsDuration();
//                if(!bd.isEmpty()){
//                    System.out.println(bd);
//                }
//            }
        }catch (Exception e){
            System.out.println("Error in main");
            System.out.println(Arrays.toString(e.getStackTrace()));
        }

    }
}
