package com.example.posturetracking;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private TextView xPos;
    private TextView yPos;
    private TextView zPos;

    private SensorManager sensorManager;
    private Sensor sensor;

    private SensorEventListener listener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent sensorEvent) {
            xPos.setText("X angle: " + String.format("%.3f", sensorEvent.values[0]));
            yPos.setText("Y angle: " + String.format("%.3f", sensorEvent.values[1]));
            zPos.setText("Z angle: " + String.format("%.3f", sensorEvent.values[2]));
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int i) {

        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (!Settings.canDrawOverlays(this)) {
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName()));
            startActivityForResult(intent, 3);
        }

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

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void startBackService(View view) {
        startForegroundService(new Intent(this, GyroscopeService.class));
    }

    public void goSettingsScreen(View view) {
        Intent intent = new Intent(this, PasswordScreen.class);
        intent.putExtra("password", "write");
        startActivity(intent);
    }

    public void stopService(View view) {
        Intent intent = new Intent(this, GyroscopeService.class);
        intent.putExtra("service", "stop");
        stopService(intent);
    }
}