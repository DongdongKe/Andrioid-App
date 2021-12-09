package com.example.android_mobile_app;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.icu.util.Measure;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.example.android_mobile_app.domain.Measurement;
import com.example.android_mobile_app.domain.MeasurementValue;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.jjoe64.graphview.series.PointsGraphSeries;

import java.util.List;

public class MeasurementGraph extends AppCompatActivity {

    private Measurement measurement;
    private TextView tv_min,tv_max,tv_avg;

    private GraphView graph;

    LineGraphSeries<DataPoint> signalGraphSeries, SCLGraphSeries,HRVGraphSeries;
    PointsGraphSeries<DataPoint> SCRGraphSeries, FinalSCRGraphSeries;

    private int index = 0;

    private int datasetSize;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_measurement_graph);
        tv_min=findViewById(R.id.tv_Min);
        tv_max=findViewById(R.id.tv_Max);
        tv_avg=findViewById(R.id.tv_Avg);

        measurement = (Measurement) getIntent().getSerializableExtra("measurement");

        final List<MeasurementValue> values = measurement.getMeasurementValues();

        graph = (GraphView) findViewById(R.id.dataGraph);

        datasetSize = measurement.getMeasurementValues().size();

        signalGraphSeries = new LineGraphSeries<>();
        signalGraphSeries.setColor(Color.BLACK);

        SCLGraphSeries = new LineGraphSeries<>();
        SCLGraphSeries.setColor(Color.BLUE);

        SCRGraphSeries = new PointsGraphSeries<>();
        SCRGraphSeries.setColor(Color.RED);
        SCRGraphSeries.setSize(5);

        HRVGraphSeries = new LineGraphSeries<>();
        HRVGraphSeries.setColor(Color.BLACK);

        FinalSCRGraphSeries = new PointsGraphSeries<>();
        FinalSCRGraphSeries.setColor(Color.YELLOW);
        FinalSCRGraphSeries.setSize(6);

        graph.addSeries(signalGraphSeries);
        graph.addSeries(SCLGraphSeries);
        graph.addSeries(SCRGraphSeries);
        graph.addSeries(FinalSCRGraphSeries);
        graph.addSeries(HRVGraphSeries);


        runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {

                for(MeasurementValue value : values){
                    signalGraphSeries.appendData(new DataPoint(index, value.getGSR()), false, datasetSize);

                    if (value.getSCL() > 0.0){
                        SCLGraphSeries.appendData(new DataPoint(index, value.getSCL()), false, datasetSize);
                    }

                    if (value.getSCR() > 0.0){

                        //double y = value.getGSR() + 0.1;
                        double y = value.getSCR();
                        SCRGraphSeries.appendData(new DataPoint(index, y), false, datasetSize);
                    }

                    if(value.getFinalSCR() > 0.0){
                        double y2 = value.getFinalSCR();
                        FinalSCRGraphSeries.appendData(new DataPoint(index, y2), false, datasetSize);
                    }

                    if (value.getHRV()>0.0){
                        HRVGraphSeries.appendData(new DataPoint(index, value.getHRV()), false, datasetSize);
                        tv_max.setText("Max:"+String.valueOf(HRVGraphSeries.getHighestValueY()));
                        tv_min.setText("Min:"+String.valueOf(HRVGraphSeries.getLowestValueY()));
                    }

                    index++;
                }
            }
        });

    }
}