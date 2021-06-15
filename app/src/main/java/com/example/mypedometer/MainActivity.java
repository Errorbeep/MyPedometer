package com.example.mypedometer;

import androidx.appcompat.app.AppCompatActivity;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.ImageButton;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;

import antonkozyriatskyi.circularprogressindicator.CircularProgressIndicator;

public class MainActivity extends AppCompatActivity implements SensorEventListener, StepListener {
    private TextView textView, tvSteps, dateText, milesText, calorieText, speedText;
    private ImageButton btnStart, btnStop;
    private CircularProgressIndicator circularProgress, miles, calorie, speed, time;
    private StepDetector simpleStepDetector;
    private SensorManager sensorManager;
    private Chronometer chronometer;
    private Sensor accel;
    private static final String TEXT_NUM_STEPS = "Number of Steps: ";
    private int numSteps;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MainActivity.this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        // Get an instance of the SensorManager
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        accel = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        simpleStepDetector = new StepDetector();
        simpleStepDetector.registerListener(this);

        tvSteps = (TextView) findViewById(R.id.tv_steps);
        dateText = findViewById(R.id.date);
        SimpleDateFormat f4= new SimpleDateFormat("MM月dd日 E");
        dateText.setText(f4.format(new Date()));
        btnStart = findViewById(R.id.btn_start);
        btnStop = findViewById(R.id.btn_stop);

        circularProgress = findViewById(R.id.circular_progress);
        chronometer = findViewById(R.id.chronometer);

        // or all at once
        circularProgress.setProgress(0, 10000);
        circularProgress.setDirection(CircularProgressIndicator.DIRECTION_CLOCKWISE);
        Log.e("DIR", String.valueOf(circularProgress.getDirection()));
        circularProgress.setAnimationEnabled(true);
        circularProgress.setProgressStrokeWidthDp(20);
        circularProgress.setTextSizeSp(80);
        circularProgress.setShouldDrawDot(true);

        miles = findViewById(R.id.miles);
        miles.setProgress(0, 10000);

        calorie = findViewById(R.id.calorie);
        calorie.setProgress(0, 10000);

        speed = findViewById(R.id.speed);
        speed.setProgress(0, 10000);

        time = findViewById(R.id.time);
        time.setProgress(0, 10000);

        milesText = findViewById(R.id.milesText);
        calorieText = findViewById(R.id.calorieText);
        speedText = findViewById(R.id.speedText);

        milesText.setText("0");
        calorieText.setText("0");
        speedText.setText("0");

        btnStart.setVisibility(View.VISIBLE);
        btnStop.setVisibility(View.GONE);
        btnStart.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                numSteps = 0;
                sensorManager.registerListener(MainActivity.this, accel, SensorManager.SENSOR_DELAY_FASTEST);
                chronometer.setBase(SystemClock.elapsedRealtime());
                chronometer.start();
                circularProgress.setProgress(0, 10000);
                btnStart.setVisibility(View.GONE);
                btnStop.setVisibility(View.VISIBLE);

            }
        });


        btnStop.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                sensorManager.unregisterListener(MainActivity.this);
                chronometer.stop();
                btnStart.setVisibility(View.VISIBLE);

            }
        });



    }



    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            simpleStepDetector.updateAccel(
                    event.timestamp, event.values[0], event.values[1], event.values[2]);
        }
    }

    @Override
    public void step(long timeNs) {
        numSteps++;
        double m = numSteps * 0.6 / 1000;
        double c = numSteps / 20.0;
        double s = m / 60;

        milesText.setText(String.format("%.2f", m));
        calorieText.setText(String.format("%.2f", c));
        speedText.setText(String.format("%.3f", s));
        circularProgress.setProgress(numSteps, 10000);
        miles.setProgress(numSteps, 10000);
        calorie.setProgress(numSteps, 10000);
        speed.setProgress(s, 36);
        time.setProgress(10000, 10000);

    }

}

