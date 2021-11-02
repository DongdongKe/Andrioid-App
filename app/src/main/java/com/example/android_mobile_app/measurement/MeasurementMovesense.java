package com.example.android_mobile_app.measurement;

import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


import com.example.android_mobile_app.Movesense.CsvLogger;
import com.example.android_mobile_app.Movesense.FormatHelper;
import com.example.android_mobile_app.Movesense.MdsRx;
import com.example.android_mobile_app.R;
import com.example.android_mobile_app.model.EcgModel;
import com.example.android_mobile_app.model.EnergyGetModel;
import com.example.android_mobile_app.model.HeartRate;
import com.example.android_mobile_app.model.LinearAcceleration;
import com.google.gson.Gson;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.movesense.mds.Mds;
import com.movesense.mds.MdsException;
import com.movesense.mds.MdsNotificationListener;
import com.movesense.mds.MdsResponseListener;
import com.movesense.mds.MdsSubscription;
import com.movesense.mds.internal.connectivity.BleManager;
import com.movesense.mds.internal.connectivity.MovesenseConnectedDevices;
import com.polidea.rxandroidble2.RxBleDevice;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import butterknife.BindView;

public class MeasurementMovesense extends AppCompatActivity implements BleManager.IBleConnectionMonitor, AdapterView.OnItemSelectedListener {

    Button btn_event1;
    Button btn_event2;
    Button btn_event3;
    String eventText;
    int stress_amount;
    private String edit;
    private boolean event1;
    private boolean event2;
    private boolean event3;

    public static final int REQUEST_WRITE_EXTERNAL_STORAGE_PERMISSION = 99;

    //Energy API Path
    private final String BATTERY_PATH_GET = "/System/Energy/Level";
    //ECG graph path
    private final String ECG_VELOCITY_PATH = "Meas/ECG/";
    private final String ECG_VELOCITY_INFO_PATH = "/Meas/ECG/Info";
    private final String LINEAR_ACCELERATION_PATH = "Meas/Acc/";
    public static final String URI_EVENTLISTENER = "suunto://MDS/EventListener";

    @BindView(R.id.tv_battery) TextView tv_battery;
    @BindView(R.id.tv_device_name_stress_mesurment) TextView tv_deviceName;
    @BindView(R.id.ecg_lineChart_stressMesurment) GraphView ecgGraph;
    @BindView(R.id.tv_heartRate) TextView tv_heartRate;
    @BindView(R.id.Heart_rate_rssid) TextView tv_test;
    @BindView(R.id.tv_recording_time) TextView tv_timer;
    @BindView(R.id.btn_record) Button btnRecord;
    @BindView(R.id.spinner1) Spinner spinner;

    @BindView(R.id.imv1) ImageView img1;
    @BindView(R.id.imv2) ImageView img2;
    @BindView(R.id.imv3) ImageView img3;
    @BindView(R.id.imv4) ImageView img4;
    @BindView(R.id.imv5) ImageView img5;


    private long start_time =0;
    private final String TAG = MeasurementMovesense.class.getSimpleName();
    private Handler handler = new Handler();
    private MdsSubscription mdsSubscriptionHr;
    private MdsSubscription mdsSubscriptionEcg;
    private MdsSubscription mdsSubscriptionLinear;
    private final int MS_IN_SECOND = 1000;
    private final String HEART_RATE_PATH = "Meas/Hr";
    private int mDataPointsAppended;
    private boolean isLogSaved=false;
    private boolean ecgIsLogSaved=false;
    private boolean IMUIsLogSaved=false;
    //3 files logger
    private CsvLogger mCsvLogger;
    private CsvLogger ecgLogger;
    private CsvLogger IMULogger;
    //Graph
    private LineGraphSeries<DataPoint> mSeriesECG;
    //Timestamp
    private Timestamp timestamp;
    private Timestamp comparedTimestamp;
    Map.Entry<Timestamp,Integer> newPair;
    double averageRmssd;
    double squareRootRmssd;
    String rmssdString;
    //    public double minTime=0;
    //Time window of measurement
    public double selectedTimeWindow =30;
    public double maxTimeMillis =30;
    public double currentTimer;

    //List of pairs value
    List<Map.Entry<Timestamp,Integer>> pairList= new ArrayList<>();
    int endIndexRR =0;
    //Result
    private List<Double> intervalSquareDifferenceList;

    enum recordState{
        idle,
        running,
        stop
    }

    recordState myState=recordState.idle;

    //timer handler
    Handler timerHandler = new Handler();
    Runnable timerRunnable = new Runnable() {
        @Override
        public void run() {
            long millis = System.currentTimeMillis() - start_time;
            int totalSeconds = (int) (millis / 1000);
            int minutes = totalSeconds / 60;
            int hours = minutes / 60;
            int seconds = totalSeconds % 60;
            tv_timer.setText(String.format("%d:%d:%02d", hours, minutes, seconds));
            timerHandler.postDelayed(this, 500);
        }
    };


    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        String item= adapterView.getItemAtPosition(i).toString();
        selectedTimeWindow =Double.parseDouble(item);
        maxTimeMillis = selectedTimeWindow *1000;
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_measurement_movesense);

        edit = "";
        event1 = false;
        event2 = false;
        event3 = false;

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Stress Measurement App");
        }
        mCsvLogger=new CsvLogger();
        ecgLogger= new CsvLogger();
        IMULogger= new CsvLogger();
        tv_deviceName.setText(MovesenseConnectedDevices.getConnectedDevice(0)
                .getSerial());

        intervalSquareDifferenceList=new ArrayList<>();


        //Init empty chart
        mSeriesECG = new LineGraphSeries<>();
        ecgGraph.addSeries(mSeriesECG);
        ecgGraph.getViewport().setXAxisBoundsManual(true);
        ecgGraph.getViewport().setMinX(0);
        ecgGraph.getViewport().setMaxX(500);

        ecgGraph.getViewport().setYAxisBoundsManual(true);
        ecgGraph.getViewport().setMinY(-15000);
        ecgGraph.getViewport().setMaxY(15000);

        ecgGraph.getViewport().setScrollable(false);
        ecgGraph.getViewport().setScrollableY(false);

        ecgGraph.setTitleColor(Color.WHITE);

        ecgGraph.getGridLabelRenderer().setHorizontalLabelsVisible(false);
        ecgGraph.getGridLabelRenderer().setVerticalLabelsVisible(false);
        ecgGraph.getGridLabelRenderer().setHighlightZeroLines(false);
        mSeriesECG.setColor(Color.GREEN);
        getECGGraph();
        //Update battery status every 1 minute
        getBattery();
        handler.postDelayed(updateBatteryThread, 60000);

        ArrayAdapter<CharSequence> spinnerAdapter =ArrayAdapter.createFromResource(this,R.array.fixedNumber, android.R.layout.simple_spinner_item);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(spinnerAdapter);
        spinner.setOnItemSelectedListener(this);



        btnRecord.setText("start");
        btnRecord.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                Button b = (Button) v;
                if (b.getText().equals("stop")) {
                    spinner.setEnabled(true);
                    myState = recordState.stop;
                    timerHandler.removeCallbacks(timerRunnable);
                    b.setText("start");

                } else {
                    spinner.setEnabled(false);
                    start_time = System.currentTimeMillis();
                    timerHandler.postDelayed(timerRunnable, 0);

                    b.setText("stop");
                    myState= recordState.running;
                }
            }
        });

        // events buttons handled.
        btn_event1 =  findViewById(R.id.btn_event1);
        btn_event1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                eventText = (String) btn_event1.getText();
            }
        });

        // long clicked for event name change
        btn_event1.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                event1 =  true;
                event2 =false;
                event3 = false;
                showDialog(edit);

                return true;
            }
        });



        btn_event2 =  findViewById(R.id.btn_event2);
        btn_event2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                eventText = (String) btn_event2.getText();
            }
        });

        btn_event2.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                event1 = false;
                event2 =true;
                event3 = false;
                showDialog(edit);

                return true;
            }
        });


        btn_event3 =  findViewById(R.id.notes_btn);
        btn_event3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                eventText = (String) btn_event3.getText();
            }
        });

        btn_event3.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                event1 =  false;
                event2 =false;
                event3 = true;
                showDialog(edit);

                return true;
            }
        });
        // faces icons handled

        img1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {stress_amount = 1; }
        });
        img2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {stress_amount = 2; }
        });
        img3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {stress_amount = 3; }
        });
        img4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {stress_amount = 4; }
        });
        img5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {stress_amount = 5; }
        });
    }

    private Runnable updateBatteryThread = new Runnable() {
        @Override
        public void run() {
            getBattery();
            handler.postDelayed(this, 60000);
        }
    };
    //Get Battery API Call
    public void getBattery() {

        Mds.builder().build(this).get(MdsRx.SCHEME_PREFIX +
                        MovesenseConnectedDevices.getConnectedDevice(0).getSerial() + BATTERY_PATH_GET,
                null, new MdsResponseListener() {
                    @Override
                    public void onSuccess(String s) {

                        EnergyGetModel energyGetModel = new Gson().fromJson(s, EnergyGetModel.class);

                        tv_battery.setText(String.format(Locale.getDefault(), "%d", energyGetModel.content));
                    }

                    @Override
                    public void onError(MdsException e) {
                        Log.e(TAG, "onError: ", e);
                    }
                });
    }

    public double sum(List<Double> list) {
        double sum = 0;

        for (double i : list)
            sum = sum + i;

        return sum;
    }

    public double getSeconds(String time) throws Exception {
        DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        Date date;
        try {
            date = dateFormat.parse(time);
        }catch (Exception e){
            throw new Exception("Error");
        }
        double seconds = date.getTime() / 1000L;
        return seconds;
    }

    public void getECGGraph() {
        mDataPointsAppended = 0;
        mCsvLogger.checkRuntimeWriteExternalStoragePermission(this, this);
        int width = 128 * 3;
        ecgGraph.getViewport().setMaxX(width);

        mSeriesECG.resetData(new DataPoint[0]);
        //HRV logger
        mdsSubscriptionHr = Mds.builder().build(this).subscribe(URI_EVENTLISTENER,
                FormatHelper.formatContractToJson(MovesenseConnectedDevices.getConnectedDevice(0).getSerial(),
                        HEART_RATE_PATH)
                , new MdsNotificationListener() {
                    @RequiresApi(api = Build.VERSION_CODES.N)
                    @Override
                    public void onNotification(String data) {
                        Log.e(TAG, "Heart rate onNotification() : " + data);
                        HeartRate heartRate = new Gson().fromJson(data, HeartRate.class);
                        if (heartRate != null) {
                            double heart=(60.0 / heartRate.body.rrData[0]) * 1000;
                            tv_heartRate.setText(String.format(Locale.getDefault(),
                                    "Heart rate: %.0f [bpm]", heart));
                            int rr= heartRate.body.rrData[0];

                            //If the record btn is pressed, start to save raw data and calculate RMSSD(HRV)
                            switch (myState){
                                case stop:
                                    unSubscribe();
                                    break;
                                case running:
                                    //Post-Processing data
                                    timestamp=new Timestamp(System.currentTimeMillis());
                                    try {
                                        currentTimer = getSeconds(tv_timer.getText().toString());
                                    } catch (Exception e) {

                                    }
                                    newPair=new AbstractMap.SimpleEntry<>(timestamp,rr);
                                    if (currentTimer> selectedTimeWindow){
                                        comparedTimestamp = new Timestamp((long) (timestamp.getTime() - maxTimeMillis));
                                        pairList.removeIf(n -> n.getKey().before(comparedTimestamp));
//                                        for (Map.Entry<Timestamp, Integer> rawRR : pairList) {
//                                            if (rawRR.getKey().before(comparedTimestamp)){
//                                                pairList.remove(rawRR);
//                                            }else {
//                                                break;
//                                            }
//                                        }
                                    }


                                    //timestampList.add(tv_timer.getText().toString());
                                    //Add a few intervals without filtering out the interval
                                    if (pairList.size() <5) {
                                        pairList.add(newPair);
                                    }
                                    else {
                                        endIndexRR = pairList.get(pairList.size()-1).getValue();
                                        if (endIndexRR > (rr*0.8) && endIndexRR <(rr*1.2)) {
                                            pairList.add(newPair);
                                            double last=pairList.get(pairList.size()-1).getValue();
                                            double secondLast=pairList.get(pairList.size()-2).getValue();
                                            double t= Math.pow(secondLast-last,2);
                                            intervalSquareDifferenceList.add(t);
                                        }
                                    }
                                    if (intervalSquareDifferenceList.size()!=0){
                                        averageRmssd = sum(intervalSquareDifferenceList)/intervalSquareDifferenceList.size();
                                        squareRootRmssd=  Math.sqrt(averageRmssd);
                                        rmssdString= Double.toString(Math.round(squareRootRmssd*100.0)/100.0);
                                    }
                                    tv_test.setText("RMSSD: "+rmssdString);
                                    isLogSaved=false;
                                    //Saving data
                                    mCsvLogger.appendHeader("Timestamp,Heart,interval,RMSSD,Event,StressAmount");
                                    mCsvLogger.appendLine(String.format(Locale.getDefault(),
                                            "%s,%.0f,%d,%s,%s,%d",timestamp,(60.0 / heartRate.body.rrData[0]) * 1000,rr,rmssdString,eventText,stress_amount));
                                    break;
                                default:
                                    break;
                            }
                        }
                    }

                    @Override
                    public void onError(MdsException error) {
                        Log.e(TAG, "Heart rate error", error);
                    }
                });
        //ECG logger
        ecgLogger.checkRuntimeWriteExternalStoragePermission(this,this);
        mdsSubscriptionEcg = Mds.builder().build(this).subscribe(URI_EVENTLISTENER,
                FormatHelper.formatContractToJson(MovesenseConnectedDevices.getConnectedDevice(0)
                        .getSerial(), ECG_VELOCITY_PATH + 128), new MdsNotificationListener() {
                    @Override
                    public void onNotification(String data) {
                        Log.d(TAG, "onSuccess(): " + data);

                        final EcgModel ecgModel = new Gson().fromJson(
                                data, EcgModel.class);

                        final int[] ecgSamples = ecgModel.getBody().getData();
                        final int sampleCount = ecgSamples.length;
                        final float sampleInterval = (float) MS_IN_SECOND / 128;
                        if (ecgModel.getBody() != null) {
                            for (int i = 0; i < sampleCount; i++) {
                                //show ECG anytime
                                mSeriesECG.appendData(
                                        new DataPoint(mDataPointsAppended, ecgSamples[i]), false,
                                        width);
                                //check state
                                switch (myState){
                                    case running:
                                        isLogSaved=false;
                                        try {
                                            ecgLogger.appendHeader("Timestamp,Count");
                                            if (ecgModel.mBody.timestamp != null) {
                                                ecgLogger.appendLine(String.format(Locale.getDefault(),
                                                        "%d,%s", ecgModel.mBody.timestamp + Math.round(sampleInterval * i),
                                                        String.valueOf(ecgSamples[i])));
                                            } else {
                                                ecgLogger.appendLine("," + String.valueOf(ecgSamples[i]));
                                            }

                                        } catch (IllegalArgumentException e) {
                                            Log.e(TAG, "GraphView error ", e);
                                        }
                                        break;
                                }

                                mDataPointsAppended++;
                                if (mDataPointsAppended == 400) {
                                    mDataPointsAppended = 0;
                                    mSeriesECG.resetData(new DataPoint[0]);
                                }
                            }
                        }
                        switch (myState){
                            case stop:
                                ecgUnSubscribe();
                                break;
                        }
                    }

                    @Override
                    public void onError(MdsException error) {
                        Log.e(TAG, "onError(): ", error);
                        Toast.makeText(MeasurementMovesense.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
        //IMU logger
        IMULogger.checkRuntimeWriteExternalStoragePermission(this,this);
        mdsSubscriptionLinear = Mds.builder().build(this).subscribe(URI_EVENTLISTENER,
                FormatHelper.formatContractToJson(MovesenseConnectedDevices.getConnectedDevice(0)
                        .getSerial(), LINEAR_ACCELERATION_PATH + 13), new MdsNotificationListener() {
                    @Override
                    public void onNotification(String data) {
                        LinearAcceleration linearAccelerationData = new Gson().fromJson(
                                data, LinearAcceleration.class);
                        if (linearAccelerationData != null) {
                            switch (myState){
                                case running:
                                    isLogSaved=false;
                                    LinearAcceleration.Array arrayData = linearAccelerationData.body.array[0];

                                    IMULogger.appendHeader("Timestamp (ms),X: (m/s^2),Y: (m/s^2),Z: (m/s^2)");

                                    IMULogger.appendLine(String.format(Locale.getDefault(),
                                            "%d,%.6f,%.6f,%.6f, ", linearAccelerationData.body.timestamp,
                                            arrayData.x, arrayData.y, arrayData.z));
                                    break;
                                case stop:
                                    IMUUnSubscribe();
                                    break;
                            }

                        }

                    }

                    @Override
                    public void onError(MdsException error) {
                        Log.e(TAG, "onError(): ", error);

                    }
                });
    }

    @Override
    public void onDisconnect(String s) {

    }

    @Override
    public void onConnect(RxBleDevice rxBleDevice) {

    }

    @Override
    public void onConnectError(String s, Throwable throwable) {

    }

    @Override
    protected void onPause() {
        super.onPause();
        timerHandler.removeCallbacks(timerRunnable);
        btnRecord.setText("start");
    }

    private void unSubscribe() {
//        if (mdsSubscriptionHr != null) {
//            mdsSubscriptionHr.unsubscribe();
//            mdsSubscriptionHr = null;
//        }


        if (!isLogSaved) {
            mCsvLogger.finishSavingLogs(this, TAG);
            isLogSaved = true;
        }
    }

    private void ecgUnSubscribe() {
//        if (mdsSubscriptionEcg != null) {
//            mdsSubscriptionEcg.unsubscribe();
//            mdsSubscriptionEcg = null;
//        }
        if (!ecgIsLogSaved) {
            ecgLogger.finishSavingLogs(this, TAG+"_ECG");
            ecgIsLogSaved = true;
        }
    }

    private void IMUUnSubscribe() {

//        if (mdsSubscriptionLinear != null) {
//            mdsSubscriptionLinear.unsubscribe();
//            mdsSubscriptionLinear = null;
//        }
        if (!IMUIsLogSaved) {
            IMULogger.finishSavingLogs(this, TAG+"_Linear");
            IMUIsLogSaved = true;
        }
    }



    // showing dialog
    private void showDialog(String str) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("input text");
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_view, null);
        final EditText edit_dialog = (EditText) view.findViewById(R.id.edit_dialog);
        edit_dialog.setText(str);
        builder.setView(view);
        builder.setNegativeButton("cancel",null);
        builder.setPositiveButton("confirm", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                if (event1){ btn_event1.setText(edit_dialog.getText().toString());}
                if (event2){ btn_event2.setText(edit_dialog.getText().toString());}
                if (event3){ btn_event3.setText(edit_dialog.getText().toString());}

            }
        });
        builder.show();
    }
}