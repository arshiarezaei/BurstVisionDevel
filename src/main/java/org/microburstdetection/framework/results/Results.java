package org.microburstdetection.framework.results;

import org.microburstdetection.framework.Flow;
import org.microburstdetection.framework.FlowManager;
import org.microburstdetection.framework.RawFlow;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;

public class Results {

    private static String baseDir;
    private static String dataSetName;
    private static String generalResultsPath;
    private static String CDFReportsPath;
    private static Results results = new Results();


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
        }else {
            int counter = 1;
            testPath = Paths.get(baseDir+counter);
            while (Files.exists(testPath)){
                counter++;
                testPath = Paths.get(baseDir+counter);
            }
            new File(testPath.toString()).mkdirs();
            baseDir = testPath.toString();
        }

        String generalResults = baseDir+"/generalResults";
        Results.generalResultsPath = generalResults;
        boolean gr = new File(generalResults).mkdirs();
        String cdfResults = baseDir+"/CDF";
        Results.CDFReportsPath = cdfResults;
        boolean cdf = new File(cdfResults).mkdirs();
        // FIXME:
        if (!(gr || cdf)){
            System.out.println("Error in Results->createDirsToStoreResults");
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
    public static void printCDFOfNumBurstsOfAllFlows(ArrayList<Integer> numberOfBurstsInEachFlow){
        /* This function calculates CDF of number of burst in flows and prints results in a file*/
        // TODO: implement body of function
    }
    public static void CDFOfIntraBurstTime(){
        // TODO: implement body of function, print results in results folder
    }
    public static void CDFOfBurstsDurationOfAllFlows(ArrayList<RawFlow> flows){
        // TODO: implement body of function, print results in results folder

        ArrayList<Long> burstDurationOfallFlows = new ArrayList<>();
        for (RawFlow flow:flows) {
            burstDurationOfallFlows.addAll(( (Flow) flow).getBurstEvents().getBurstsDuration());
        }
        burstDurationOfallFlows.removeIf(n-> Objects.equals(n,0));
        Collections.sort(burstDurationOfallFlows);
        ArrayList<Integer> steps=new ArrayList<>();
        ArrayList<Double> value=new ArrayList<>();
        for (int i =5; i <= 100 ; i+=5) {
            steps.add(i);
        }
        for (Integer step:steps) {
            int XPercentile = (int) Math.ceil(burstDurationOfallFlows.size() * (step)/100.0);
            Long max = Collections.max(burstDurationOfallFlows.subList(0,XPercentile));
            value.add(((burstDurationOfallFlows.stream().filter(i-> i<= max).count())/(burstDurationOfallFlows.size()*1.0))*100.0);
        }
        for (int i = 0; i < steps.size(); i++) {
            System.out.println(steps.get(i)+"\t"+value.get(i));
        }

    }
    public static void CDFOfNumberOfPacketsInBursts(){
        // TODO: implement body of function, print results in results folder
    }
}
