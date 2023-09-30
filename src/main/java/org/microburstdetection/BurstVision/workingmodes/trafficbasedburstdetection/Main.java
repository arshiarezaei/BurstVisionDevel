package org.microburstdetection.BurstVision.workingmodes.trafficbasedburstdetection;

import io.pkts.Pcap;
import org.apache.commons.cli.*;
import org.microburstdetection.BurstVision.cnfg.BurstParameters;
import org.microburstdetection.BurstVision.cnfg.ConfigurationParameters;
import org.microburstdetection.BurstVision.cnfg.TrafficMonitoringParameters;
import org.microburstdetection.BurstVision.utilities.Utilities;

import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
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
        int burstRatio= 20;
        int samplingDuration=20;// in microseconds
        int samplingWindow=1200;// in microseconds
        ConfigurationParameters.setConfigurationParameters(new TrafficMonitoringParameters(samplingWindow,samplingDuration), new BurstParameters(burstRatio));
        // read pcap file
        try {
            for (String filePath : sourceFilePath) {
                Pcap pcap = Pcap.openStream(filePath);
                pcap.loop(new TrafficHandler());
                pcap.close();
            }
            ArrayList<TrafficSampleInfo> trafficSampleInfos = TrafficBasedAnalyser.getBurstEventHandler().getCapturedSamples();
            int sd = ConfigurationParameters.getTrafficMonitoringParameters().getSampleDuration();
            int aw = ConfigurationParameters.getTrafficMonitoringParameters().getSamplingWindowDuration();
            BurstDetector.getBursts(trafficSampleInfos,(double) TrafficHandler.bytes/TrafficBasedAnalyser.getCapturingTime(),sd,aw,burstRatio);
            ArrayList<Integer> bd = BurstEventHandler.getDetectedBursts().stream().map(DetectedBurst::getBurstDuration).collect(Collectors.toCollection(ArrayList::new));

            Results.createDirsToStoreResults(resultsPath, Utilities.getDatasetFileName(sourceFilePath[0]));
            Results.generateCDFOfBurstDuration(bd);
            ArrayList<Integer> numBurstyPkts = BurstEventHandler.getDetectedBursts().stream().map(DetectedBurst::getNumberOfBurstyPackets).collect(Collectors.toCollection(ArrayList::new));
            Results.generateCDFOfNumberOfPacketsInEachBurst(numBurstyPkts);
            ArrayList<Integer> traversedBytes = BurstEventHandler.getDetectedBursts().stream().map(DetectedBurst::getTraversedBytes).collect(Collectors.toCollection(ArrayList::new));
            Results.generateCDFOfTraversedBytesInEachBurst(traversedBytes);
            ArrayList<Double> avgPktSize = BurstEventHandler.getDetectedBursts().stream().map(DetectedBurst::getAveragePacketSize).collect(Collectors.toCollection(ArrayList::new));
            Results.generateCDFAveragePacketSize(avgPktSize);
            ArrayList<Double> br = BurstEventHandler.getDetectedBursts().stream().map(DetectedBurst::getBurstRatio).collect(Collectors.toCollection(ArrayList::new));
            Results.generateCDFOfBurstRatio(br);
            Results.generateCDFOfInterBurstTime(BurstEventHandler.getInterBurstTime(BurstEventHandler.getDetectedBursts(),samplingDuration));
            System.out.println("Num flows "+ FlowManager.getNumberOfFlows());
//
//            TrafficHandler.packetsSize.sort(Collections.reverseOrder());
//            System.out.println(TrafficHandler.packetsSize.subList(0,100));
//            System.out.println(TrafficBasedAnalyser.getBurstEventHandler().getCapturedSamples().size());
//            TrafficSampleInfo trafficSampleInfo = TrafficBasedAnalyser.getBurstEventHandler().getCapturedSamples().stream().max(Comparator.comparing(v -> v.getAverageThroughput(20))).get();
//            System.out.println(trafficSampleInfos.indexOf(trafficSampleInfo));
//            int sd = ConfigurationParameters.getTrafficMonitoringParameters().getSampleDuration();
//            int sw = ConfigurationParameters.getTrafficMonitoringParameters().getSamplingWindowDuration();
//            System.out.println(TrafficBasedAnalyser.getArrivalTimeOfLastPacket()-TrafficBasedAnalyser.getArrivalTimeOfFirstPacket());
//            long capturingTime= TrafficBasedAnalyser.getCapturingTime();
//            System.out.println("capturing time "+capturingTime);
//            System.out.println("bytes"+ TrafficBasedAnalyser.getCapturedBytes());
//            System.out.println(TrafficBasedAnalyser.getAvgThroughput());
//            System.out.println(TrafficBasedAnalyser.getBurstEventHandler().getCapturedSamples().size()+"\t"+sw/sd);
//            BurstDetector.getBursts(trafficSampleInfos);
//            System.out.println(TrafficBasedAnalyser.getCapturedBytes());// 475013266 475_013_266
//            System.out.println(TrafficBasedAnalyser.getCapturingTime());
//            System.out.println(TrafficHandler.counter);
//            System.out.println("th = \t"+((double) TrafficHandler.bytes/TrafficBasedAnalyser.getCapturingTime()));
//            System.out.println(TrafficHandler.bytes);
    }catch (Exception e){
            System.out.println("Error in Main class");
            System.out.println(Arrays.toString(e.getStackTrace()));
        }
    }
}
