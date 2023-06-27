
package org.microburstdetection.BurstVision.results;

import java.io.File;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.microburstdetection.BurstVision.workingmodes.floworientedAnalysis.FiveTupleFlow;
import org.microburstdetection.BurstVision.workingmodes.floworientedAnalysis.FlowManager;
import org.microburstdetection.BurstVision.workingmodes.floworientedAnalysis.RawFlow;
import org.microburstdetection.BurstVision.cnfg.ConfigurationParameters;
import org.microburstdetection.BurstVision.cnfg.TrafficType;
import org.microburstdetection.BurstVision.utilities.TraversedBytesUnits;
import org.microburstdetection.BurstVision.utilities.Utilities;
import org.microburstdetection.networkstack.layer3.IPV4;
import org.microburstdetection.networkstack.layer4.TCP;
import org.microburstdetection.networkstack.layer4.TransportLayerProtocols;
import org.microburstdetection.networkstack.layer4.UDP;


public class Results {

    private static String resultsDir;
    private static String dataSetName;
    private static final Results results = new Results(); // make Results a singleton class


    private Results() {
    }

    public static void createDirsToStoreResults(String baseDir, String dataSetName){
        Results.resultsDir = baseDir;
        Results.dataSetName  = dataSetName;
        //TODO: add implementation of append=false
        baseDir = baseDir+"/"+"flow-oriented_analysis/"+ Results.dataSetName;
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
//
//        String generalResults = baseDir+"/generalResults";
//        boolean gr = new File(generalResults).mkdirs();
//        String cdfResults = baseDir+"/CDF";
//        Results.CDFReportsPath = cdfResults;
//        boolean cdf = new File(cdfResults).mkdirs();
//        if (!(gr || cdf)){
//            System.out.println("Error in Results->createDirsToStoreResults");
//        }
//        String dataset= dataSetName.replace(".pcap","");
//        boolean datasetFolder = new File(cdfResults+"/"+dataset).mkdirs();
    }

    public static void saveGeneralResultsToFile(ArrayList<RawFlow> flows){
        //TODO: move calculation of each parameter to a separate function

        Integer numFlows = flows.size();
        int numBurstyFlows = flows.stream().filter(RawFlow::isBursty).toList().size();
        int numTCPFlows = FlowManager.getNumberOfFlowsWithProtocol( IPV4.class,TCP.class);
        int numUDPFlows = FlowManager.getNumberOfFlowsWithProtocol(IPV4.class,UDP.class);
        int avgNumPacketsInBursts=0;
        int packetsInBurstCounter = 0;
        double avgBurstDuration=0;
        int avgTraversedBytes = 0;
        double avgNumBurstsInAllBurstyFlows=0;
        double avgThroughputBurstyFlows = 0.0;
        double avgThroughput=0;
        double avgThroughputHeavy=0;
        double counterAvgThroughputHeavy=0;
        double counter=0;// this counter tracks the number of flows that has more than one packet
        for (RawFlow flow:flows) {
            if(flow.isBursty()){
                packetsInBurstCounter += flow.getBurstEvents().getTotalNumberOfPacketsInBursts();
                avgBurstDuration += flow.getBurstEvents().getBurstsDuration().stream().mapToLong(a->a).sum();
                avgTraversedBytes += flow.getBurstEvents().getTraversedBytesInEachBurst().stream().mapToInt(a->a).sum();
                avgNumBurstsInAllBurstyFlows += flow.getBurstEvents().getNumberOfBursts();
                if(flow.getNumberOfPackets()>1){
                    avgThroughputBurstyFlows+=flow.getAverageThroughputInBursts(TraversedBytesUnits.BYTES_PER_SECONDS);
                    counter++;
                }
            }
            avgThroughput += flow.getAverageThroughput(TraversedBytesUnits.BYTES_PER_SECONDS);
        }
        avgNumPacketsInBursts = (int) (Math.ceil(packetsInBurstCounter/(numBurstyFlows*1.0)));
        int totalNumBursts = flows.stream().mapToInt(a-> a.getBurstEvents().getNumberOfBursts()).sum();
        avgBurstDuration = Utilities.getRoundedValue(avgBurstDuration/(totalNumBursts*1.0));
        avgTraversedBytes = (int) Math.ceil(avgTraversedBytes/(numBurstyFlows*1.0));
        avgNumBurstsInAllBurstyFlows =(int) Math.floor(avgNumBurstsInAllBurstyFlows/(numBurstyFlows*1.0));
        avgThroughputBurstyFlows = Utilities.getRoundedValue(avgThroughputBurstyFlows/(counter*1.0));
        avgThroughput = Utilities.getRoundedValue(avgThroughput/(flows.size()*1.0));
        for (RawFlow rawFlow: flows) {
            if(rawFlow.getAverageThroughput(TraversedBytesUnits.BYTES_PER_SECONDS)>avgThroughput){
                avgThroughputHeavy += rawFlow.getAverageThroughput(TraversedBytesUnits.BYTES_PER_SECONDS);
                counterAvgThroughputHeavy++;
            }
        }
        avgThroughputHeavy = Utilities.getRoundedValue((avgThroughputHeavy/(counterAvgThroughputHeavy))*
                Math.pow(10,TraversedBytesUnits.BYTES_PER_SECONDS.getTraversedBytesUnits()));
        try{
            File file = new File(resultsDir +"/general.txt");
            file.createNewFile();
            FileWriter fileWriter = new FileWriter(file);
            fileWriter.write(
                    "Dataset\t "+dataSetName+"\n"+
                            "Threshold\t"+ ConfigurationParameters.getBurstParameters().getTHRESHOLD()+" micro-seconds\n"+
                            "Minimum number of packets to detect a burst\t"+ConfigurationParameters.getBurstParameters().getMINIMUM_NUMBER_OF_PACKETS_IN_BURST()+"\n"+
                            "Maximum number of packets in a burst event\t"+ ConfigurationParameters.getBurstParameters().getMAXIMUM_NUMBER_OF_PACKETS_IN_BURST()+"\n"+
                            "Number of flows\t" + numFlows + "\n"+
                            "Number of TCP flows\t" + numTCPFlows + "\n"+
                            "Number of UDP flows\t" + numUDPFlows + "\n"+
                            "Number of Heavy Flows\t" + FlowManager.getFlowsByTrafficType(TrafficType.HEAVY).size() +"\n"+
                            "Number of Heavy TCP flows\t"+ FlowManager.getNumberOfFlowsByType(FiveTupleFlow.class,
                            TrafficType.HEAVY,IPV4.class,TCP.class)+"\n"+
                            "Number of Heavy UDP flows\t"+ FlowManager.getNumberOfFlowsByType(FiveTupleFlow.class,
                            TrafficType.HEAVY,IPV4.class,UDP.class)+"\n"+
                            "Number of bursty flows\t" + numBurstyFlows + "\n"+
                            "Number of TCP bursty flows\t" + FlowManager.getNumberOfFlowsByType(FiveTupleFlow.class,
                            TrafficType.BURSTY,IPV4.class,TCP.class) + "\n"+
                            "Number of UDP bursty flows\t" + FlowManager.getNumberOfFlowsByType(FiveTupleFlow.class,
                            TrafficType.BURSTY,IPV4.class,UDP.class) + "\n"+

                            "Average throughput of all flows\t"+ avgThroughput+"\n"+
                            "Average throughput of heavy flows\t"+ avgThroughputHeavy +"\n"+
                            "Average throughput of bursty Flows\t" + avgThroughputBurstyFlows+"\n"+

                            "Average number of bursts\t" + avgNumBurstsInAllBurstyFlows + "\n"+
                            "Average burst duration (us)\t" + Utilities.getRoundedValue(avgBurstDuration) + "\n"+
                            "Average traversed bytes in bursts\t" +  avgTraversedBytes + "\n"+
                            "Average number of packets in Bursts\t"+ avgNumPacketsInBursts+ "\n");
//            fileWriter.write(dataSetName+"\t"+numFlows+"\t"+numTCPFlows+"\t"+numUDPFlows+"\t"+numBurstyFlows+"\t"+
//                    avgNumBurstsInAllBurstyFlows+"\t"+avgBurstDuration+"\t"+avgTraversedBytes+"\t"+avgNumPacketsInBursts);
            fileWriter.flush();
            fileWriter.close();
        }catch (Exception e){
            System.out.println(e);
        }



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
    private static <T extends Comparable<T> & Comparator<T>> Map<T,T> calculateCDFUsingGenerics(ArrayList<T> data){
        Map sortedFrequencyOfData = (data.stream()
                .collect(Collectors.groupingBy(Function.identity(),
                        (Collectors.counting())))).entrySet().stream().
                sorted(Map.Entry.comparingByKey()).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
                        (oldValue, newValue) -> oldValue, LinkedHashMap::new));
        Double size = data.size()*1.0;
//        System.out.println(sortedFrequencyOfBursts);
        Map pdf = new HashMap<>();
        sortedFrequencyOfData.forEach((key, value) -> {
            double probability = Utilities.getRoundedValue((Double.parseDouble(key.toString())/size)*100.0);
            pdf.put(key,probability);
        });
        System.out.println("-------");
        System.out.println(pdf);
        Map cdf = new HashMap<>();
        for (Object key: pdf.keySet()) {
            Double sum = 0.0;
            for (Object key2: sortedFrequencyOfData.keySet()) {
                if(Double.parseDouble(key2.toString()) <= Double.parseDouble(key.toString())){
                    sum+=Double.parseDouble(pdf.get(key2).toString() );
                }
            }
            sum = Utilities.getRoundedValue(sum);
            cdf.put(key,sum);
        }
//        System.out.println(cdf);
        cdf.entrySet().stream().sorted(Map.Entry.comparingByValue()).collect(Collectors.toList());
        return cdf;
    }
    private static Map<Double,Double> calculateCDFDouble(ArrayList<Double> data){
        Map<Double,Long> sortedFrequencyOfData = (data.stream()
                .collect(Collectors.groupingBy(Function.identity(),
                        (Collectors.counting())))).entrySet().stream().
                sorted(Map.Entry.comparingByKey()).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
                        (oldValue, newValue) -> oldValue, LinkedHashMap::new));
        Double size = data.size()*1.0;
//        System.out.println(sortedFrequencyOfBursts);
        Map<Double,Double> pdf = new HashMap<>();
        sortedFrequencyOfData.forEach((key, value) -> {
            double probability = Utilities.getRoundedValue(( value/size)*100.0);
            pdf.put(key,probability);
        });
//        System.out.println(pdf);
        Map<Double,Double> cdf = new HashMap<>();
        for (Double key: pdf.keySet()) {
            Double sum = 0.0;
            for (Double key2: sortedFrequencyOfData.keySet()) {
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
    private static Map<Long,Double> calculateCDFLong(ArrayList<Long> data){
        Map<Long,Long> sortedFrequencyOfData = (data.stream()
                .collect(Collectors.groupingBy(Function.identity(),
                        (Collectors.counting())))).entrySet().stream().
                sorted(Map.Entry.comparingByKey()).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
                        (oldValue, newValue) -> oldValue, LinkedHashMap::new));
        Double size = data.size()*1.0;
//        System.out.println(sortedFrequencyOfBursts);
        Map<Long,Double> pdf = new HashMap<>();
        sortedFrequencyOfData.forEach((key, value) -> {
            double probability = Utilities.getRoundedValue(( value/size)*100.0);
            pdf.put(key,probability);
        });
//        System.out.println(pdf);
        Map<Long,Double> cdf = new HashMap<>();
        for (Long key: pdf.keySet()) {
            Double sum = 0.0;
            for (Long key2: sortedFrequencyOfData.keySet()) {
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
    private static void writeCDFDataToFile(String path,String header,Map sortedCDF){
        try{
            File file = new File(path);
            file.createNewFile();
            FileWriter fileWriter = new FileWriter(file,false);
            fileWriter.write(header+"\n");
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
    public static void printCDFOfNumBurstsOfAllFlows(ArrayList<RawFlow> flowsList){
        /* This function calculates CDF of number of burst in flows and prints results in a file*/
        // TODO: implement body of function
        ArrayList<Integer> numberOfBursts = new ArrayList<>() ;
        for (RawFlow rawFlow:flowsList) {
            if(rawFlow.isBursty()){
                numberOfBursts.add(rawFlow.getBurstEvents().getNumberOfBursts());
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
            File file = new File(Results.resultsDir+"/cdf_number_bursts.txt");
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
            File file = new File(Results.resultsDir+"/cdf_burst_duration.txt");
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
                traversedBytesOfAllFlows.addAll(flow.getBurstEvents().getTraversedBytesInEachBurst());
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
            File file = new File(Results.resultsDir+"/cdf_bytes_traversed_bursts.txt");
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
                traversedBytesOfAllFlows.addAll(flow.getBurstEvents().getTraversedBytesInEachBurst());
            }
        }
        Map<Integer, Double> sortedCDf = calculateCDF(traversedBytesOfAllFlows);
        String path = Results.resultsDir+"/"+TransportLayerProtocols.getTransportLayerProtocol(transportLayerProtocol)
                +"_cdf_bytes_traversed_bursts.txt";
        writeCDFDataToFile(path,"dataset"+"\t\t"+"Bytes"+"\t\t\t"+"X%",sortedCDf);
    }

    public static void saveCDFOfFlowsThroughput(ArrayList<RawFlow> flows,TraversedBytesUnits T){
       ArrayList<Double> arrayList = flows.stream().map(rawFlow -> rawFlow.getAverageThroughput(T)).collect(Collectors.toCollection(ArrayList::new));
       arrayList.removeIf(aDouble -> aDouble.equals(0));
       // calculate sorted cdf of flows' throughput
        String path = Results.resultsDir+"/"+"cdf_flow_throughput.txt";
        Map sortedCDF = Results.calculateCDFDouble(arrayList);
        Results.writeCDFDataToFile(path,"dataset"+"\t\t"+T.toString()+"\t\t\t"+"X%",sortedCDF);
    }
    public static void saveCDFOFInterBurstTime(ArrayList<RawFlow> flows){
        ArrayList<Long> allFlowInterBurstTime = new ArrayList<>();
        for (RawFlow rawFlow: flows ) {
            if(rawFlow.isBursty()){
                allFlowInterBurstTime.addAll(rawFlow.getBurstEvents().getBurstInterBurstTime());
            }
        }
        Map sortedCDF = Results.calculateCDFLong(allFlowInterBurstTime);
        String path = Results.resultsDir+"/"+"cdf_inter-burst_time.txt";
        String header = "dataset"+"\t\t"+"inter-burst(us)"+"\t\t\t"+"X%";
        Results.writeCDFDataToFile(path,header,sortedCDF);
    }
    public static void saveCDFOfBurstRatio(ArrayList<RawFlow> flows){
        ArrayList<Double> burstRatioOfAllFlows = new ArrayList<>();
        for (RawFlow rawFlow: flows ) {
            if(rawFlow.isBursty()){
               burstRatioOfAllFlows.addAll(rawFlow.getListOfBurstsRatio());
            }
        }
        Map sortedCDF = Results.calculateCDFDouble(burstRatioOfAllFlows);
        String path = Results.resultsDir+"/"+"cdf_burst_ratio.txt";
        String header = "dataset"+"\t\t"+"burst ratio"+"\t\t\t"+"X%";
        Results.writeCDFDataToFile(path,header,sortedCDF);
    }
}
