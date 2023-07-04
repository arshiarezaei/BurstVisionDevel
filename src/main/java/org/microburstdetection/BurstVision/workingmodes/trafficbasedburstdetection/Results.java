package org.microburstdetection.BurstVision.workingmodes.trafficbasedburstdetection;

import java.io.File;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

import org.microburstdetection.BurstVision.utilities.ResultsProvider;




class Results {
    private static String resultsDir;
    private static String dataSetName;
    private static final Results results = new Results(); // make Results a singleton class
    private Results() {
    }
    public static void createDirsToStoreResults(String baseDir, String dataSetName){
        Results.resultsDir = baseDir;
        Results.dataSetName  = dataSetName;
        //TODO: add implementation of append=false
        baseDir = baseDir+"/"+"traffic_based_analysis/"+Results.dataSetName;
        Path path = Paths.get(baseDir);
        Path testPath;
        // The following if-else block checks for not existing of results' path
        if (Files.notExists(path)){
            new File(path.toString()).mkdirs();
        }
        else {
            int counter = 1;
            testPath = Paths.get(baseDir+"("+counter+")");
            while (Files.exists(testPath)){
                counter++;
                testPath = Paths.get(baseDir+"("+counter+")");
            }
            new File(testPath.toString()).mkdirs();
            baseDir = testPath.toString();
        }
        Results.resultsDir = baseDir;
    }
    private static double getXPercentile(ArrayList listOfValues, int xPercentile){
        Collections.sort(listOfValues);
        int index = (int) Math.ceil((xPercentile / 100.0) * listOfValues.size());
        return Double.parseDouble( listOfValues.get(index-1).toString());
    }
    private static <T extends Number>  double getAverage(ArrayList<T> listOfValues){
        double sum = listOfValues.stream().mapToDouble(Number::doubleValue).sum();
        return sum/(listOfValues.size()*1.0);
    }

    public static void saveGeneralResultsToFile(){
        ArrayList<Integer> traversedBytesInEachBurst = TrafficBasedAnalyser.getBurstEventHandler().getTraversedBytesInEachBurst();
        ArrayList<Long> bd = TrafficBasedAnalyser.getBurstEventHandler().getBurstsDuration();
        ArrayList<Long> interBurstTime = TrafficBasedAnalyser.getBurstEventHandler().getBurstInterBurstTime();
        double avgThroughput= TrafficBasedAnalyser.getAvgThroughput();
        double avgBurstyThroughput = TrafficBasedAnalyser.getBurstEventHandler().getAverageBurstThroughput();
        double burstRatio = avgBurstyThroughput/avgThroughput;
        //FIXME: burst ration could not be defined for traffic based analysis
//        System.out.println(avgThroughput);
//        System.out.println(avgBurstyThroughput);
//        System.out.println(burstRatio);
        try{
            File file = new File(resultsDir +"/general.txt");
            file.createNewFile();
            FileWriter fileWriter = new FileWriter(file);
            fileWriter.write(
                    "Dataset\t "+dataSetName+"\n"+
                            "Threshold\t"+" micro-seconds\n"+
                            "Minimum number of packets to detect a burst\t"+"\n"+
                            "Maximum number of packets in a burst event\t"+"\n"+
                            "Traffic Capturing Time\t"+ TrafficBasedAnalyser.getCapturingTime()*Math.pow(10,-6)+" (seconds) \n"+
                            "Number of Captured Packets\t"+ TrafficBasedAnalyser.getNumberOfCapturedPackets()+"\n"+
                            "Number of bursts\t"+TrafficBasedAnalyser.getBurstEventHandler().getNumberOfBursts()+"\n"+
                            "Number of Bursty Packets\t"+TrafficBasedAnalyser.getBurstEventHandler().getTotalNumberOfPacketsInBursts()+"\n"+
                            "Average Number Of packets in burst\t"+ getXPercentile(TrafficBasedAnalyser.getBurstEventHandler()
                            .getNumberOfPacketsInEachBurst(), 50)+"\n"+
                            "Number of packets in 90% of bursts\t"+ getXPercentile(TrafficBasedAnalyser.getBurstEventHandler()
                            .getTraversedBytesInEachBurst(), 90)+"\n"+
                            "Average Traversed Bytes in bursts\t"+getAverage(traversedBytesInEachBurst)+"\n"+
                            "Traversed Bytes in 90% of bursts\t"+getXPercentile(traversedBytesInEachBurst,90)+"\n"+
                            "Average Burst Duration\t"+getAverage(bd)+"\n"+
                            "Burst duration of 90th \t"+getXPercentile(bd,90)+" micro-seconds \n"+
                            "Average inter-burst time \t"+getAverage(interBurstTime)+" micro-seconds \n"+
                            "90th Inter-burst time\t"+getXPercentile(interBurstTime,90)+" micro-seconds \n"
//                            "Burst Ratio\t"+burstRatio+"\n"
            );
            fileWriter.flush();
            fileWriter.close();
        }catch (Exception e){
            System.out.println(e);
        }
    }

    public static void generateCDFOfBurstDuration(ArrayList<Long> listOfBurstDuration){
        Map<Long,Double> cdf = ResultsProvider.calculateCDFLong(listOfBurstDuration);
        String path = resultsDir+"/"+"cdf_bursts_duration.txt";
        ResultsProvider.writeCDFDataToFile(path,"dataset"+"\t\t"+"burst duration(microseconds)"+"\t\t\t"+"X%",cdf,dataSetName);
    }
    public static void generateCDFOfNumberOfPacketsInEachBurst(ArrayList<BurstEvent> burstEvents){
        ArrayList<Integer> bs= burstEvents.stream().map(BurstEvent::getNumberOfPackets).collect(Collectors.toCollection(ArrayList::new));
        Map<Integer,Double> r= ResultsProvider.calculateCDFInteger(bs);
        String path = resultsDir+"/"+"cdf_num_packets.txt";
        ResultsProvider.writeCDFDataToFile(path,"dataset"+"\t\t"+"#Packets"+"\t\t\t"+"X%",r,dataSetName);
    }
    public static void generateCDFOfTraversedBytesInEachBurst(ArrayList<Integer> listOfTraversedBytes){
        Map<Integer,Double> r= ResultsProvider.calculateCDFInteger(listOfTraversedBytes);
        String path = resultsDir+"/"+"cdf_traversed_bytes.txt";
        ResultsProvider.writeCDFDataToFile(path,"dataset"+"\t\t"+"bytes"+"\t\t\t"+"X%",r,dataSetName);
    }
    public static void generateCDFOfInterBurstTime(ArrayList<Long> interBurstTimes){
        Map<Long,Double> cdf = ResultsProvider.calculateCDFLong(interBurstTimes);
        String path = resultsDir+"/"+"cdf_inter_burst_time.txt";
        ResultsProvider.writeCDFDataToFile(path,"dataset"+"\t\t"+"inter_burst Time (microseconds)"+"\t\t\t"+"X%",cdf,dataSetName);
    }

    public static void generateCDFOfNumberOfFlowsContributingToBursts(ArrayList<Integer> listOfNumberOfFlowsContributingToBursts){
        Map<Integer,Double> cdf = ResultsProvider.calculateCDFInteger(listOfNumberOfFlowsContributingToBursts);
        String path = resultsDir+"/"+"cdf_num_flows_contribute_to_bursts.txt";
        ResultsProvider.writeCDFDataToFile(path,"dataset"+"\t\t"+"#flows"+"\t\t\t"+"X%",cdf,dataSetName);
    }
    public static void generateCDFAveragePacketSize(ArrayList<Double> averagePacketSizeList){
        Map<Double,Double> cdf = ResultsProvider.calculateCDFDouble(averagePacketSizeList);
        String path = resultsDir+"/"+"cdf_avg_packet_size.txt";
        ResultsProvider.writeCDFDataToFile(path,"dataset"+"\t\t"+"avg_pkt_size (bytes)"+"\t\t\t"+"X%",cdf,dataSetName);
    }
    public static void generateCDFOfPacketSize(ArrayList<Integer> burstyPacketsSizeList){
        Map<Integer,Double> cdf = ResultsProvider.calculateCDFInteger(burstyPacketsSizeList);
        String path = resultsDir+"/"+"cdf_packet_size.txt";
        ResultsProvider.writeCDFDataToFile(path,"dataset"+"\t\t"+"packetSize (bytes)"+"\t\t\t"+"X%",cdf,dataSetName);
    }

}

