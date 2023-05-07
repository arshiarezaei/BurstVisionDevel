package org.microburstdetection.framework.results;

import java.util.ArrayList;

public class Results {
    public static void printNumberOfFlows(int numFlows){
        System.out.println("Total number of Flows:   "+numFlows);
    }
    public static void printNumberOfHeavyFlows(int numHeavyFlows){
        System.out.println("Number of Heavy Flows:   "+numHeavyFlows);
    }
    public static void printNumberOfBurstyFlows(int numBurstyFlows){
        System.out.println("Number of Bursty Flows:    "+numBurstyFlows);
    }
    public static void CDFOfBurstInBurstyFlows(ArrayList<Integer> numberOfBurstsInEachFlow){
        /* This function calculates CDF of number of burst in flows and prints results in a file*/
        // TODO: implement body of function
    }
    public static void CDFOfIntraBurstTime(){
        // TODO: implement body of function, print results in results folder
    }
    public static void CDFOfBurstsLength(){
        // TODO: implement body of function, print results in results folder
    }
    public static void CDFOfNumberOfPacketsInBursts(){
        // TODO: implement body of function, print results in results folder
    }
}
