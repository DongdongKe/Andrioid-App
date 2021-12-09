package com.example.android_mobile_app;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.OptionalDouble;

import static com.google.common.math.DoubleMath.mean;

public class SignalDetector {
    List<Double> data;
    int length=0;
    int lag;
    Double threshold;
    Double influence;
    List<Double> signals;
    List<Double> filteredData;
    List<Double> avgFilter;
    List<Double> stdFilter;
//    SummaryStatistics stats= new SummaryStatistics();



    public SignalDetector(List<Double> data,int lag, Double threshold, Double influence) {
        this.data=data;
        this.lag=lag;
        this.threshold=threshold;
        this.influence=influence;
        filteredData = new ArrayList<Double>(data);
        avgFilter = new ArrayList<Double>(Collections.nCopies(data.size(), 0.0d));
        stdFilter = new ArrayList<Double>(Collections.nCopies(data.size(), 0.0d));
        signals = new ArrayList<Double>(Collections.nCopies(data.size(), 0.0d));
    }

    public Double thresholding_algo(Double new_value){
        data.add(new_value);
        //-1?
        length= data.size()-1;
        if (length<=lag) return 0d;
        //Once the data length(lag) is enough
        else if (length > lag) {
//            for (int i = 0; i < lag; i++) {
//                stats.addValue(data.get(i));
//            }
            avgFilter.set(length-1,getMean(data));
//            stdFilter.set(lag,Math.sqrt(stats.getPopulationVariance()));
            stdFilter.set(length-1,getStdDeviation(data));
//            stats.clear();
//            return 0d;
        }

        // if the distance between the current value and average is enough standard deviations (threshold) away
        if (Math.abs((data.get(data.size()-1) - avgFilter.get(avgFilter.size()-1))) > (threshold * stdFilter.get(stdFilter.size()-1))) {
            // this is a signal (i.e. peak), determine if it is a positive or negative signal
            if (data.get(length) > avgFilter.get(length - 1)) {
//                signals.set(length-1, 1d);
                signals.add(1d);
            } else {
//                signals.set(length-1, -1d);
                signals.add(-1d);
            }

            // filter this signal out using influence
            filteredData.set(length-1, (influence * data.get(length)) + ((1 - influence) * filteredData.get(length - 1)));
//            filteredData.add((influence * data.get(length)) + ((1 - influence) * filteredData.get(length-1)));
            avgFilter.set(length-1,getMean(filteredData.subList(length-1-lag,length-1)));
//            avgFilter.add(getMean(filteredData.subList(length-1-lag,length-1)));
            stdFilter.set(length-1,getStdDeviation(filteredData.subList(length-1-lag,length-1)));
//            stdFilter.add(getStdDeviation(filteredData.subList(length-1-lag,length-1)));
        } else {
            // ensure this signal remains a zero
//            signals.set(length-1, 0d);
            signals.add(0d);
            // ensure this value is not filtered
            filteredData.set(length-1, data.get(length));
//            filteredData.add(data.get(length));
//            signals.remove(length);
            avgFilter.set(length-1,getMean(filteredData.subList(length-1-lag,length-1)));
//            avgFilter.add(getMean(filteredData.subList(length-1-lag,length-1)));
            stdFilter.set(length-1,getStdDeviation(filteredData.subList(length-1-lag,length-1)));
//            stdFilter.add(getStdDeviation(filteredData.subList(length-1-lag,length-1)));
        }
        return signals.get(length);
    }

    public double getMean (List<Double> list){
        OptionalDouble average = list.stream().mapToDouble(a -> a).average();
        double mean = average.isPresent()? average.getAsDouble():0;
        return mean;
    }


    public double getStdDeviation(List<Double> list){
        double sum = 0.0;
        OptionalDouble average = list.stream().mapToDouble(a -> a).average();
        double mean = average.isPresent()? average.getAsDouble():0;

        for (Double i : list)
            sum += Math.pow((i - mean), 2);
        return Math.sqrt(sum / list.size()); // sample
    }

    public void setList (List<Double> list){
        this.data = list;
        filteredData = new ArrayList<Double>(list);
        avgFilter = new ArrayList<Double>(Collections.nCopies(list.size(), 0.0d));
        stdFilter = new ArrayList<Double>(Collections.nCopies(list.size(), 0.0d));
        signals = new ArrayList<Double>(Collections.nCopies(list.size(), 0.0d));
    }

}
