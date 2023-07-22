package org.microburstdetection.BurstVision.workingmodes.trafficbasedburstdetection;

import com.github.psambit9791.jdsp.misc.UtilMethods;
import com.github.psambit9791.jdsp.signal.peaks.FindPeak;
import com.github.psambit9791.jdsp.signal.peaks.Peak;

import java.util.ArrayList;


public class BurstDetector {
    public static void getBursts(ArrayList<TrafficSampleInfo> capturedSamples, double threshold, int samplingDuration, int avgWindow,int burstRatio){
        System.out.println("here");
        double[] bitrate = capturedSamples.stream().mapToDouble(c -> c.getAverageThroughput(samplingDuration)).toArray();
        double[] data = UtilMethods.splitByIndex(bitrate, 0, bitrate.length);
        FindPeak fp = new FindPeak(data);
        Peak out = fp.detectPeaks();

        int[] indexOfPeaks = out.getPeaks();
//        System.out.println(Arrays.toString(out.getPeaks()));
//        int[] indexOfBursts = getIndexOfBursts(capturedSamples,indexOfPeaks,avgWindow,samplingDuration,10);
        int aw = avgWindow/2;
        int numberOfSamplesToMeasure = aw/samplingDuration;
        ArrayList<Integer> indexOfBursts = new ArrayList<>();
//        ArrayList<Double> calbr = new ArrayList<>();
        ArrayList<DetectedBurst> detectedBursts = new ArrayList<>();
        for (int i = 0; i < indexOfPeaks.length; i++) {
            // to prevent indexOutOfBoundException
            int lowerBound = Math.max(indexOfPeaks[i]-numberOfSamplesToMeasure,0);
            int upperBound = Math.min(indexOfPeaks[i]+numberOfSamplesToMeasure, capturedSamples.size());

            int sumBytes = capturedSamples.subList(lowerBound,upperBound).stream().mapToInt(TrafficSampleInfo::traversedBytes).sum();
            double throughputInWindow = (double) sumBytes/avgWindow;
            double throughputInSample = capturedSamples.get(indexOfPeaks[i]).getAverageThroughput(samplingDuration);
            double br = throughputInSample/throughputInWindow;
//            System.out.println(throughputInWindow+"\t"+throughputInSample+"\t"+br);\

            if(br>=burstRatio){
                indexOfBursts.add(indexOfPeaks[i]);
//                calbr.add(Utilities.getRoundedValue(br));
                // burst duration right side of the peak
                int indexOfRightSideOfPeak=0;
                int indexOfBurst = indexOfPeaks[i];
//                if(indexOfBurst==823431){
//                    System.out.println("break");
//                }
                for (int j = indexOfBurst+1; j <= bitrate.length; j++) {
                    if(bitrate[j-1]<throughputInWindow){
                        indexOfRightSideOfPeak = j;
                        break;
                    } else if (j==bitrate.length) {
                        indexOfRightSideOfPeak = bitrate.length;
                        break;
                    }
                }
                int indexOfLeftSideOfPeak=0;
                for (int j = indexOfBurst-1; j >= 0 ; j--) {
                    if(bitrate[j]<throughputInWindow){
                        indexOfLeftSideOfPeak = j;
                        break;
                    }
                }
                int bd = indexOfRightSideOfPeak-indexOfLeftSideOfPeak;
                bd = (bd+1)*samplingDuration;
                int burstyPackets = capturedSamples.subList(indexOfLeftSideOfPeak,indexOfRightSideOfPeak).stream().
                        mapToInt(TrafficSampleInfo::traversedPackets).sum();
                int flowsContributedToBurst = capturedSamples.subList(indexOfLeftSideOfPeak,indexOfRightSideOfPeak).stream().mapToInt(TrafficSampleInfo::getNumFlows).sum();
                DetectedBurst detectedBurst = new DetectedBurst(indexOfBurst,burstyPackets,sumBytes,bd,br,flowsContributedToBurst);
//                    detectedBursts.add(detectedBurst);
                BurstEventHandler.addNewBurst(detectedBurst);

            }
        }
//        System.out.println("burst size"+detectedBursts.size());
//        System.out.println("Bitrate check"+(capturedSamples.size()==bitrate.length));
//        System.out.println("length of peaks"+indexOfPeaks.length);
//        System.out.println("length of bursts"+indexOfBursts.length);
//        System.out.println("Check\t"+ (indexOfPeaks.length>indexOfBursts.length));
//        System.out.println(Arrays.toString(a));
//        double[][] aa  = out.getWidthData();
//        System.out.println(aa);
    }
//    private static int[] getIndexOfBursts(ArrayList<TrafficSampleInfo> capturedSamples, int[] indexOfPeaks, int avgWindow, int samplingDuration, int burstRatio){
//        int aw = avgWindow/2;
//        int numberOfSamplesToMeasure = aw/samplingDuration;
//        ArrayList<Integer> indexOfBursts = new ArrayList<>();
////        ArrayList<Double> calbr = new ArrayList<>();
//        for (int i = 0; i < indexOfPeaks.length; i++) {
//            // to prevent indexOutOfBoundException
//            int lowerBound = Math.max(indexOfPeaks[i]-numberOfSamplesToMeasure,0);
//            int upperBound = Math.min(indexOfPeaks[i]+numberOfSamplesToMeasure, capturedSamples.size());
//
//            int sumBytes = capturedSamples.subList(lowerBound,upperBound).stream().mapToInt(TrafficSampleInfo::traversedBytes).sum();
//            double throughputInWindow = (double) sumBytes/avgWindow;
//            double throughputInSample = capturedSamples.get(indexOfPeaks[i]).getAverageThroughput(samplingDuration);
//            double br = throughputInSample/throughputInWindow;
////            System.out.println(throughputInWindow+"\t"+throughputInSample+"\t"+br);\
//
//            if(br>=burstRatio){
//                indexOfBursts.add(indexOfPeaks[i]);
////                calbr.add(Utilities.getRoundedValue(br));
//            }
//        }
////        System.out.println(calbr);
////        System.out.println(indexOfBursts);
////        System.out.println(indexOfBursts.size()==calbr.size());
//        return indexOfBursts.stream().mapToInt(i -> i).toArray();
//    }
//    private static int[] getBurstDuration(ArrayList<TrafficSampleInfo> capturedSamples, int[] indexOfBursts,int avgWindow,int samplingDuration,int burstRatio){
//        int aw = avgWindow/2;
//        int numberOfSamplesToMeasure = aw/samplingDuration;
//        ArrayList<Integer> indexOfBursts = new ArrayList<>();
////        ArrayList<Double> calbr = new ArrayList<>();
//        for (int i = 0; i < indexOfBursts.length; i++) {
//            // to prevent indexOutOfBoundException
//            int lowerBound = Math.max(indexOfBursts[i]-numberOfSamplesToMeasure,0);
//            int upperBound = Math.min(indexOfBursts[i]+numberOfSamplesToMeasure, capturedSamples.size());
//
//            int sumBytes = capturedSamples.subList(lowerBound,upperBound).stream().mapToInt(TrafficSampleInfo::traversedBytes).sum();
//            double throughputInWindow = (double) sumBytes/avgWindow;
//            double throughputInSample = capturedSamples.get(indexOfBursts[i]).getAverageThroughput(samplingDuration);
//            double br = throughputInSample/throughputInWindow;
////            System.out.println(throughputInWindow+"\t"+throughputInSample+"\t"+br);\
//
//            if(br>=burstRatio){
//                indexOfBursts.add(indexOfBursts[i]);
////                calbr.add(Utilities.getRoundedValue(br));
//            }
//        }
////        System.out.println(calbr.stream().sorted(Collections.reverseOrder()).collect(Collectors.toList()));
//        return indexOfBursts.stream().mapToInt(i -> i).toArray();
//    }
}
