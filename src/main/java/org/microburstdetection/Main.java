package org.microburstdetection;


import io.pkts.Pcap;
import org.apache.commons.cli.*;
import org.microburstdetection.framework.FlowManager;
import org.microburstdetection.framework.results.Results;
import org.microburstdetection.framework.utilities.Utilities;
import org.microburstdetection.packethandler.TcpUdpHandler;


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
        Results.createDirsToStoreResults(resultsPath,Utilities.getDatasetFileName(sourceFilePath[0]));

        // read pcap file
        try {
            for (String filePath:sourceFilePath) {
                Pcap pcap = Pcap.openStream(filePath);
                pcap.loop(new TcpUdpHandler());
                pcap.close();
            }
//            Results.printCDFOfNumBurstsOfAllFlows(FlowManager.getFlows());
//            Results.calculateCDFOfBurstsDurationOfFlows(FlowManager.getFlows());
//            Results.saveCDFBytesTraversedBursts(FlowManager.getFlows());
//            Results.printCDFBytesTraversedBurstsToFile(FlowManager.getFlows(), TransportLayerProtocols.UDP.getTransportLayerProtocol());
//            Results.printCDFBytesTraversedBurstsToFile(FlowManager.getFlows(), TransportLayerProtocols.TCP.getTransportLayerProtocol());
//            Results.saveGeneralResultsToFile(FlowManager.getFlows());
//            FlowManager.getNumberOfFlowsWithProtocol(IPV4.class,TCP.class);
//            Results.saveGeneralResultsToFile(FlowManager.getFlows());
//            Results.saveCDFOfFlowsThroughput(FlowManager.getFlows(), TraversedBytesUnits.KILOBYTES_PER_SECOND);
            Results.saveCDFOFInterBurstTime(FlowManager.getFlows());
//            for (RawFlow flow:FlowManager.getFlows()) {
//                if(flow.isBursty()){
//                    System.out.println(flow.getBurstEvents().getInterBurstTime());
//                }
//            }
//            System.out.println("");
//            for (RawFlow rawFlow:FlowManager.getFlows()) {
//                if(rawFlow.isBursty()){
//                    System.out.println(Utilities.getRoundedValue(rawFlow.getAverageThroughputInBursts()) +"  Bytes/second");
//                }
//            }

        }catch (Exception e){
            System.out.println(e);
        }

    }
}
