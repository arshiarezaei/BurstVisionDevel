package org.microburstdetection.framework.results;
import org.microburstdetection.Main;
import org.microburstdetection.framework.FiveTupleFlow;
import org.microburstdetection.framework.FlowManager;
import org.microburstdetection.framework.RawFlow;
import org.microburstdetection.framework.utilities.Utilities;
import org.microburstdetection.networkstack.layer3.IPV4;
import org.microburstdetection.networkstack.layer4.TCP;
import org.microburstdetection.networkstack.layer4.TransportLayerProtocols;
import org.microburstdetection.networkstack.layer4.UDP;

import java.io.File;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Results {

    private static String baseDir;
    private static String dataSetName;
    private static String generalResultsPath;
    private static String CDFReportsPath;
    private static final Results results = new Results();


    private Results() {
    }

    public static void createDirsToStoreResults(String baseDir, String dataSetName){
        Results.baseDir = baseDir;
        Results.dataSetName  = dataSetName;
        //TODO: add implementation of append=false
        baseDir = baseDir+"/results";
        Path path = Paths.get(baseDir);
        Path testPath = path;
        // The following if-else block checks for not existing of results' path
        if (Files.notExists(path)){
            new File(path.toString()).mkdirs();
        }
        //TODO: uncomment the following piece of code if you want to create a new dir in each run
//        else {
//            int counter = 1;
//            testPath = Paths.get(baseDir+counter);
//            while (Files.exists(testPath)){
//                counter++;
//                testPath = Paths.get(baseDir+counter);
//            }
//            new File(testPath.toString()).mkdirs();
//            baseDir = testPath.toString();
//        }

        String generalResults = baseDir+"/generalResults";
        Results.generalResultsPath = generalResults;
        boolean gr = new File(generalResults).mkdirs();
        String cdfResults = baseDir+"/CDF";
        Results.CDFReportsPath = cdfResults;
        boolean cdf = new File(cdfResults).mkdirs();
        //TODO: uncomment the following piece of code if you want to create a new dir in each run   
//        if (!(gr || cdf)){
//            System.out.println("Error in Results->createDirsToStoreResults");
//        }
        String dataset= dataSetName.replace(".pcap","");
        boolean datasetFolder = new File(cdfResults+"/"+dataset).mkdirs();
        Results.dataSetName = dataset;
    }

    public static void saveGeneralResultsToFile(ArrayList<RawFlow> flows){
        //TODO: implement function budy
        Integer numFlows = flows.size();
        int burstyFlows = flows.stream().filter(RawFlow::isBursty).toList().size();
        int numTCPFlows = FlowManager.getNumberOfFlowsWithProtocol(IPV4.class, TCP.class);
        int numUDPFlows = FlowManager.getNumberOfFlowsWithProtocol(IPV4.class, UDP.class);
        int avgNumPacketsInBursts=0;
        int packetsInBurstCounter = 0;
        double avgBurstDuration=0;
        int avgTraversedBytes = 0;
        double avgNumBursts=0;
        for (RawFlow flow:flows) {
            if(flow.isBursty()){
                packetsInBurstCounter += flow.getBurstEvents().getPacketsInBurst().size();
                avgBurstDuration += flow.getBurstEvents().getBurstsDuration().stream().mapToDouble(a->a).sum();
                avgTraversedBytes += flow.getBurstEvents().getBytesInEachBurst().stream().mapToInt(a->a).sum();
                avgNumBursts += flow.getBurstEvents().getNumberOfBurstEvents();

            }
        }
        avgNumPacketsInBursts = (int) (Math.ceil(packetsInBurstCounter/(burstyFlows*1.0)));
        avgBurstDuration = avgBurstDuration/(burstyFlows*1.0);
        avgTraversedBytes = (int) Math.ceil(avgTraversedBytes/(burstyFlows*1.0));
        avgNumBursts =(int) Math.ceil(avgNumBursts/(burstyFlows*1.0));

        try{
            File file = new File(baseDir+"/general_results.txt");
            file.createNewFile();
            FileWriter fileWriter = new FileWriter(file);
            fileWriter.write("dataset\t#flows\t#tcp_flows\t#udp_flows\t#bursty_flows\tavg_num_brsts\t" +
                    "avg_duration_burst\tavg_bytes_traversed\tavg_num_pkts_in_bursts\n");
            fileWriter.write(dataSetName+"\t"+numFlows+"\t"+numTCPFlows+"\t"+numUDPFlows+"\t"+burstyFlows+"\t"+
                    avgNumBursts+"\t"+avgBurstDuration+"\t"+avgTraversedBytes+"\t"+avgNumPacketsInBursts);
            fileWriter.flush();
            fileWriter.close();
        }catch (Exception e){
            System.out.println(e);
        }



    }
    public static void printNumberOfFlows(){
        System.out.println("Total number of Flows:   "+FlowManager.getNumberOfFlows());
    }
    public static void printNumberOfHeavyFlows(int numHeavyFlows){
        System.out.println("Number of Heavy Flows:   "+numHeavyFlows);
    }
    public static void printNumberOfBurstyFlows(int numBurstyFlows){
        System.out.println("Number of Bursty Flows:    "+numBurstyFlows);
    }
    public static void printCDFOfNumBurstsOfAllFlows(ArrayList<RawFlow> flowsList){
        /* This function calculates CDF of number of burst in flows and prints results in a file*/
        // TODO: implement body of function
        ArrayList<Integer> numberOfBursts = new ArrayList<>() ;
        for (RawFlow rawFlow:flowsList) {
            if(rawFlow.isBursty()){
                numberOfBursts.add(rawFlow.getBurstEvents().getNumberOfBurstEvents());
            }
        }
        Double totalNumberOfBursts = numberOfBursts.size()*1.0;
        Map<Integer,Long> sortedFrequencyOfBursts = (numberOfBursts.stream()
                .collect(Collectors.groupingBy(Function.identity(),
                        (Collectors.counting())))).entrySet().stream().
                sorted(Map.Entry.comparingByKey()).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
                (oldValue, newValue) -> oldValue, LinkedHashMap::new));
        Map<Integer,Double> pdf = new HashMap<>();
        sortedFrequencyOfBursts.forEach((key, value) -> {
            double probability = Utilities.getRoundedValue((value /totalNumberOfBursts)*100.0);
            pdf.put(key,probability);
        });
//        System.out.println(pdf);
        Map<Integer,Double> cdf = new HashMap<>();
        for (Integer key: pdf.keySet()) {
            Double sum = 0.0;
            for (Integer key2: sortedFrequencyOfBursts.keySet()) {
                if(key2<=key){
                    sum+=pdf.get(key2);
                }
            }
            sum = Utilities.getRoundedValue(sum);
            cdf.put(key,sum);
        }
//        System.out.println(cdf);
        cdf = cdf.entrySet().stream().
                sorted(Map.Entry.comparingByKey()).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
                        (oldValue, newValue) -> oldValue, LinkedHashMap::new));
//        System.out.println(cdf);
        try{
            File file = new File(CDFReportsPath+"/"+Results.dataSetName+"/cdf_number_bursts.txt");
            file.createNewFile();
            FileWriter fileWriter = new FileWriter(file,false);
            fileWriter.write("dataset"+"\t\t"+"#Bursts"+"\t\t"+"X%"+"\n");
            for (Integer key:cdf.keySet()) {
                fileWriter.write(Results.dataSetName.replace(".pcap","")+"\t\t"+key+"\t\t"+cdf.get(key)+"\n");
                fileWriter.flush();
            }
            fileWriter.flush();
            fileWriter.close();
        }catch (Exception e){
            System.out.println(e);
        }
    }
    public static void CDFOfIntraBurstTime(){
        // TODO: implement body of function, print results in results folder
    }
    public static void calculateCDFOfBurstsDurationOfFlows(ArrayList<RawFlow> flows){
        // TODO: implement body of function, print results in results folder

        ArrayList<Long> burstDurationOfallFlows = new ArrayList<>();
        for (RawFlow flow:flows) {
            if(flow.isBursty()){
                burstDurationOfallFlows.addAll(flow.getBurstEvents().getBurstsDuration());
            }
        }
        Map<Long, Long> sortedFrequencyOfBursts = (burstDurationOfallFlows.stream()
                .collect(Collectors.groupingBy(Function.identity(),
                        (Collectors.counting())))).entrySet().stream().
                sorted(Map.Entry.comparingByKey()).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
                        (oldValue, newValue) -> oldValue, LinkedHashMap::new));
        Double totalNumberOfBursts = burstDurationOfallFlows.size()*1.0;
//        System.out.println(sortedFrequencyOfBursts);
        Map<Long,Double> pdf = new HashMap<>();
        sortedFrequencyOfBursts.forEach((key, value) -> {
            double probability = Utilities.getRoundedValue(( value/totalNumberOfBursts)*100.0);
            pdf.put(key,probability);
        });
//        System.out.println(pdf);
        Map<Long,Double> cdf = new HashMap<>();
        for (Long key: pdf.keySet()) {
            Double sum = 0.0;
            for (Long key2: sortedFrequencyOfBursts.keySet()) {
                if(key2<=key){
                    sum+=pdf.get(key2);
                }
            }
            sum = Utilities.getRoundedValue(sum);
            cdf.put(key,sum);
        }
//        System.out.println(cdf);
        cdf = cdf.entrySet().stream().
                sorted(Map.Entry.comparingByKey()).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
                        (oldValue, newValue) -> oldValue, LinkedHashMap::new));
        try{
            File file = new File(CDFReportsPath+"/"+Results.dataSetName+"/cdf_burst_duration.txt");
            file.createNewFile();
            FileWriter fileWriter = new FileWriter(file,false);
            fileWriter.write("dataset"+"\t\t"+"Duration(us)"+"\t\t\t"+"X%"+"\n");
            for (Long key:cdf.keySet()) {
                fileWriter.write(Results.dataSetName.replace(".pcap","")+"\t\t"+key+"\t\t"+cdf.get(key)+"\n");
                fileWriter.flush();
            }
            fileWriter.flush();
            fileWriter.close();
        }catch (Exception e){
            System.out.println(e);
        }
    }
    public static void CDFOfNumberOfPacketsInBursts(){
        // TODO: implement body of function, print results in results folder
    }
    public static void saveCDFBytesTraversedBursts(ArrayList<RawFlow> flows){
        ArrayList<Integer> traversedBytesOfAllFlows = new ArrayList<>();
        for (RawFlow flow:flows) {
            if(flow.isBursty()){
                traversedBytesOfAllFlows.addAll(flow.getBurstEvents().getBytesInEachBurst());
            }
        }
        Map<Integer,Long> sortedFrequencyOfBursts = (traversedBytesOfAllFlows.stream()
                .collect(Collectors.groupingBy(Function.identity(),
                        (Collectors.counting())))).entrySet().stream().
                sorted(Map.Entry.comparingByKey()).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
                        (oldValue, newValue) -> oldValue, LinkedHashMap::new));
        Double  traversedBytesOfAllFlowsSize = traversedBytesOfAllFlows.size()*1.0;
        Map<Integer,Double> pdf = new HashMap<>();
        sortedFrequencyOfBursts.forEach((key, value) -> {
            double probability = Utilities.getRoundedValue(( value/traversedBytesOfAllFlowsSize)*100.0);
            pdf.put(key,probability);
        });
//        System.out.println(pdf);
        Map<Integer,Double> cdf = new HashMap<>();
        for (Integer key: pdf.keySet()) {
            Double sum = 0.0;
            for (Integer key2: sortedFrequencyOfBursts.keySet()) {
                if(key2<=key){
                    sum+=pdf.get(key2);
                }
            }
            sum = Utilities.getRoundedValue(sum);
            cdf.put(key,sum);
        }
//        System.out.println(cdf);
        cdf = cdf.entrySet().stream().
                sorted(Map.Entry.comparingByKey()).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
                        (oldValue, newValue) -> oldValue, LinkedHashMap::new));
//        for (Integer key: cdf.keySet()) {
//            System.out.println(key+"\t"+ cdf.get(key));
//        }
        try{
            File file = new File(CDFReportsPath+"/"+Results.dataSetName+"/cdf_bytes_traversed_bursts.txt");
            file.createNewFile();
            FileWriter fileWriter = new FileWriter(file,false);
            fileWriter.write("dataset"+"\t\t"+"Bytes"+"\t\t\t"+"X%"+"\n");
            for (Integer key:cdf.keySet()) {
                fileWriter.write(Results.dataSetName.replace(".pcap","")+"\t\t"+key+"\t\t"+cdf.get(key)+"\n");
                fileWriter.flush();
            }
            fileWriter.flush();
            fileWriter.close();
        }catch (Exception e){
            System.out.println(e);
        }
    }

    public static void printCDFBytesTraversedBurstsToFile(ArrayList<RawFlow> flows, int transportLayerProtocol){
        // this function print cdf of traversed Bytes in bursts seperated by protocol
        ArrayList<Integer> traversedBytesOfAllFlows = new ArrayList<>();
        for (RawFlow flow:flows) {
            if(flow.isBursty()&&((FiveTupleFlow) flow).getLayer4().getTransportProtocol()==transportLayerProtocol){
                traversedBytesOfAllFlows.addAll(flow.getBurstEvents().getBytesInEachBurst());
            }
        }
        Map<Integer, Double> sortedCDf = calculateCDF(traversedBytesOfAllFlows);
        String path = CDFReportsPath+"/"+Results.dataSetName+"/"+TransportLayerProtocols.getTransportLayerProtocol(transportLayerProtocol)
                +"_cdf_bytes_traversed_bursts.txt";
        writeCDFDataToFile(path,sortedCDf);
    }
    private static Map<Integer,Double> calculateCDF(ArrayList<Integer> data){
        Map<Integer,Long> sortedFrequencyOfData = (data.stream()
                .collect(Collectors.groupingBy(Function.identity(),
                        (Collectors.counting())))).entrySet().stream().
                sorted(Map.Entry.comparingByKey()).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
                        (oldValue, newValue) -> oldValue, LinkedHashMap::new));
        Double size = data.size()*1.0;
//        System.out.println(sortedFrequencyOfBursts);
        Map<Integer,Double> pdf = new HashMap<>();
        sortedFrequencyOfData.forEach((key, value) -> {
            double probability = Utilities.getRoundedValue(( value/size)*100.0);
            pdf.put(key,probability);
        });
//        System.out.println(pdf);
        Map<Integer,Double> cdf = new HashMap<>();
        for (Integer key: pdf.keySet()) {
            Double sum = 0.0;
            for (Integer key2: sortedFrequencyOfData.keySet()) {
                if(key2<=key){
                    sum+=pdf.get(key2);
                }
            }
            sum = Utilities.getRoundedValue(sum);
            cdf.put(key,sum);
        }
//        System.out.println(cdf);
        cdf = cdf.entrySet().stream().
                sorted(Map.Entry.comparingByKey()).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
                        (oldValue, newValue) -> oldValue, LinkedHashMap::new));
        return cdf;
    }
    private static void writeCDFDataToFile(String path,Map sortedCDF){
        try{
            File file = new File(path);
            file.createNewFile();
            FileWriter fileWriter = new FileWriter(file,false);
            fileWriter.write("dataset"+"\t\t"+"Bytes"+"\t\t\t"+"X%"+"\n");
            for (Object key:sortedCDF.keySet()) {
                fileWriter.write(Results.dataSetName.replace(".pcap","")+"\t\t"+key+"\t\t"+sortedCDF.get(key)+"\n");
                fileWriter.flush();
            }
            fileWriter.flush();
            fileWriter.close();
        }catch (Exception e){
            System.out.println(e);
        }
    }
}
