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
        assertEquals(44.58,squareRootRmssd,0);
    }

    public double Sum(List<Double> list) {
        double sum = 0;
        for (double i : list)
            sum = sum + i;
        return sum;
    }

    public void AddData(){
        intervalList.addAll(Arrays.asList(
                812
                ,828
                ,820
                ,812
                ,757
                ,710
                ,742
                ,812
                ,828
                ,781
                ,804
                ,726
                ,687
                ,710
                ,765
                ,789
                ,820
                ,789
                ,765
                ,695
                ,679
                ,703
                ,750
                ,812
                ,765
                ,726
                ,796
                ,835
                ,828
                ,820
                ,726
                ,750
                ,750
                ,765
                ,781
                ,765
                ,695
                ,703
                ,734
                ,781
                ,679


        ));
    }
}