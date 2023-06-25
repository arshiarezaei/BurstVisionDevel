package org.microburstdetection.BurstVision.utilities;

import org.microburstdetection.BurstVision.results.Results;

import java.io.File;
import java.io.FileWriter;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public interface ResultsProvider {
    public static Map<Integer,Double> calculateCDFInteger(ArrayList<Integer> data){
        Map<Integer,Long> sortedFrequencyOfData = (data.stream()
                .collect(Collectors.groupingBy(Function.identity(),
                        (Collectors.counting())))).entrySet().stream().
                sorted(Map.Entry.comparingByKey()).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
                        (oldValue, newValue) -> oldValue, LinkedHashMap::new));
        Double size = data.size()*1.0;
//        System.out.println(sortedFrequencyOfBursts);
        Map<Integer,Double> pdf = new HashMap<>();
        sortedFrequencyOfData.forEach((key, value) -> {
//            double probability = Utilities.getRoundedValue(( value/size)*100.0);
            double probability = ( value/size)*100.0;
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
//            sum = Utilities.getRoundedValue(sum);
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
    public static Map<Double,Double> calculateCDFDouble(ArrayList<Double> data){
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
    public static Map<Long,Double> calculateCDFLong(ArrayList<Long> data){
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
    public static void writeCDFDataToFile(String path,String header,Map sortedCDF,String datasetName){
        try{
            File file = new File(path);
            file.createNewFile();
            FileWriter fileWriter = new FileWriter(file,false);
            fileWriter.write(header+"\n");
            for (Object key:sortedCDF.keySet()) {
                fileWriter.write(datasetName.replace(".pcap","")+"\t\t"+key+"\t\t"+sortedCDF.get(key)+"\n");
                fileWriter.flush();
            }
            fileWriter.flush();
            fileWriter.close();
        }catch (Exception e){
            System.out.println(e);
        }
    }
}
