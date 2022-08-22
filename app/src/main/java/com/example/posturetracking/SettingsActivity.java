package com.example.posturetracking;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

public class SettingsActivity extends AppCompatActivity {

    private EditText xPos;
    private EditText yPos;

    private SensorManager sensorManager;
    private Sensor sensor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    public void addItem(View view) {
        AlertDialog.Builder alertDialogExit = new AlertDialog.Builder(this);
        View viewDialog = LayoutInflater.from(SettingsActivity.this).inflate(
                R.layout.activity_dialog_add_item,
                (ConstraintLayout)findViewById(R.id.main_constraint)
        );
        alertDialogExit.setView(viewDialog);

        AlertDialog alertDialog = alertDialogExit.create();

        viewDialog.findViewById(R.id.cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.cancel();
            }
        });

        xPos = viewDialog.findViewById(R.id.editTextNumber);
        yPos = viewDialog.findViewById(R.id.editTextNumber3);

        SensorEventListener listener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent sensorEvent) {
                xPos.setText(String.format("%.3f", sensorEvent.values[0]));
                yPos.setText(String.format("%.3f", sensorEvent.values[1]));
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int i) {

            }
        };

        sensorManager.registerListener(listener, sensor, SensorManager.SENSOR_DELAY_NORMAL);
        alertDialog.show();
    }
}