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
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;


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

    TextView tv_battery,tv_deviceName,tv_heartRate,tv_test,tv_timer,btnRecord;
    ImageView img1,img2,img3,img4,img5;
    GraphView ecgGraph;
    Spinner spinner;




    Boolean startProcessing;
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
    Map.Entry<Timestamp,Double> tempLast;
    double sumOfSquare;
    double meanSquare;
    double squareRootRmssd;
    String rmssdString;

    //Time window of measurement
    public double selectedTimeWindow =30;
    public double maxTimeMillis =30;
    public double currentTimer;


    //List of raw data
    List<Map.Entry<Timestamp,Integer>> pairList= new ArrayList<>();
    int endIndexRR =0;
    //List of 30seconds data
    private List<Map.Entry<Timestamp,Double>> intervalSquareDifferenceList= new ArrayList<>();

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
        tv_deviceName = findViewById(R.id.tv_device_name_stress_mesurment);
        tv_battery=findViewById(R.id.tv_battery);
        ecgGraph=findViewById(R.id.ecg_lineChart_stressMesurment);
        tv_heartRate=findViewById(R.id.tv_heartRate);
        tv_test=findViewById(R.id.Heart_rate_rssid);
        tv_timer=findViewById(R.id.tv_recording_time);
        btnRecord=findViewById(R.id.btn_record);
        spinner=findViewById(R.id.spinner1);

        img1=findViewById(R.id.imv1);
        img2=findViewById(R.id.imv2);
        img3=findViewById(R.id.imv3);
        img4=findViewById(R.id.imv4);
        img5=findViewById(R.id.imv5);

        intervalSquareDifferenceList=new ArrayList<>();


        tv_deviceName.setText(MovesenseConnectedDevices.getConnectedDevice(0).getSerial());

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
                    b.setBackgroundResource(R.drawable.recording_button);

                } else {
                    spinner.setEnabled(false);
                    start_time = System.currentTimeMillis();
                    timerHandler.postDelayed(timerRunnable, 0);

                    b.setText("stop");
                    myState= recordState.running;
                    b.setBackgroundResource(R.drawable.start_button);

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

    public double sum(List<Map.Entry<Timestamp,Double>> list) {
        double sum = 0;

        for (Map.Entry<Timestamp,Double> i : list)
            try {
                sum = sum + i.getValue();
            }catch(Exception e){

            }
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
                                    //New rr
                                    timestamp=new Timestamp(System.currentTimeMillis());
                                    newPair=new AbstractMap.SimpleEntry<>(timestamp,rr);
                                    //Post-Processing data
                                    try {
                                        currentTimer = getSeconds(tv_timer.getText().toString());
                                    } catch (Exception e) {

                                    }
                                    startProcessing= currentTimer> selectedTimeWindow;
                                    //Add a few intervals without filtering out the interval
                                    if (startProcessing){
                                        comparedTimestamp = new Timestamp((long) (timestamp.getTime() - maxTimeMillis));
                                        //Lambada
                                        //pairList.removeIf(n -> n.getKey().before(comparedTimestamp));
                                        Iterator<Map.Entry<Timestamp,Double>> iterator= intervalSquareDifferenceList.iterator();
                                        while (iterator.hasNext()){
                                            Map.Entry<Timestamp,Double> t= iterator.next();
                                            Timestamp s= t.getKey();
                                            if (t.getKey().before(comparedTimestamp)){
                                                tempLast= t;
                                                iterator.remove();
                                            }else{
                                                if (tempLast!=null){
                                                    intervalSquareDifferenceList.add(0,tempLast);
                                                }
                                                break;
                                            }
                                        }
                                    }

                                    if (pairList.size() <2) {
                                        pairList.add(newPair);
                                    }
                                    else {
                                        endIndexRR = pairList.get(pairList.size()-1).getValue();
//                                        if (endIndexRR > (rr*0.8) && endIndexRR <(rr*1.2)) {
                                        pairList.add(newPair);
                                        double last=pairList.get(pairList.size()-1).getValue();
                                        double secondLast=pairList.get(pairList.size()-2).getValue();
                                        double t= Math.pow(secondLast-last,2);
                                        intervalSquareDifferenceList.add(new AbstractMap.SimpleEntry<>(timestamp,t));
//                                        }
                                    }

                                    //Calculation part
                                    sumOfSquare = sum(intervalSquareDifferenceList);
                                    meanSquare = sumOfSquare/intervalSquareDifferenceList.size();
                                    squareRootRmssd=  Math.sqrt(meanSquare);
                                    rmssdString= Double.toString(Math.round(squareRootRmssd*100.0)/100.0);
                                    if (!startProcessing){
                                        rmssdString ="waiting";
                                    }


                                    tv_test.setText("HRV(RMSSD): "+rmssdString);
                                    isLogSaved=false;
                                    //Saving data
                                    mCsvLogger.appendHeader("Timestamp,Heart,interval,RMSSD,Event,StressAmount");
                                    mCsvLogger.appendLine(String.format(Locale.getDefault(),
                                            "%s,%.0f,%d,%s,%s,%s",timestamp,(60.0 / heartRate.body.rrData[0]) * 1000,rr,rmssdString,sumOfSquare,meanSquare));
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