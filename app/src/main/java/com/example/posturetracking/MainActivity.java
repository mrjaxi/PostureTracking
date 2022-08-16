package com.example.posturetracking;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private TextView xPos;
    private TextView yPos;
    private TextView zPos;

    private SensorManager sensorManager;
    private Sensor sensor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName()));
        startActivityForResult(intent, 3);

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
        sensorManager.registerListener(listener, sensor, SensorManager.SENSOR_DELAY_NORMAL);

        xPos = findViewById(R.id.xVIew);
        yPos = findViewById(R.id.yView);
        zPos = findViewById(R.id.zView);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    SensorEventListener listener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent sensorEvent) {
            xPos.setText("X angle: " + String.format("%.3f", sensorEvent.values[0]));
            yPos.setText("Y angle: " + String.format("%.3f", sensorEvent.values[1]));
            zPos.setText("Z: " + String.format("%.3f", sensorEvent.values[2]));
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int i) {

        }
    };

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void startBackService(View view) {
        startForegroundService(new Intent(this, GyroscopeService.class));
    }
}