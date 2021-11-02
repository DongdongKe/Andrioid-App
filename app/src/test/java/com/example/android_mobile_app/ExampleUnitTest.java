package com.example.android_mobile_app;

import com.example.android_mobile_app.measurement.MeasurementShimmer;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {

    private List<Integer> intervalList= new ArrayList<>();
    private List<Double> intervalSquareDifferenceList= new ArrayList<>();

    @Test
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void test_get_average_scl(){

    }


    @Test
    public void CheckRMSSDCalcualtion() throws Exception {
        AddData();
        for(int i=0 ; i< intervalList.size()-1;i++){
            int d= intervalList.get(i+1) - intervalList.get(i);
            intervalSquareDifferenceList.add(Math.pow(d,2));
        }

        double averageRmssd = Sum(intervalSquareDifferenceList)/intervalSquareDifferenceList.size();
        double squareRootRmssd=  Math.sqrt(averageRmssd);
        assertEquals(100,squareRootRmssd,0);
    }

    public double Sum(List<Double> list) {
        double sum = 0;
        for (double i : list)
            sum = sum + i;
        return sum;
    }

    public void AddData(){
        intervalList.addAll(Arrays.asList(
                796
                ,773
                ,773
                ,757
                ,679
                ,687
                ,710
                ,695
                ,656
                ,632
                ,625
                ,632
                ,617
                ,640
                ,664
                ,656
                ,632
                ,625
                ,632
                ,656
                ,679
                ,695
                ,679
                ,640
                ,656
                ,664
                ,679
                ,671
                ,664
                ,632
                ,617
                ,625
                ,625
                ,632
                ,648
                ,640
                ,695
                ,726
                ,742
                ,710
                ,695
                ,703
                ,695
                ,664

        ));
    }
}