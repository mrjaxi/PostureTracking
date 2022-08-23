package com.example.posturetracking;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.example.posturetracking.Service.GyroscopeService;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (!Settings.canDrawOverlays(this)) {
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName()));
            startActivityForResult(intent, 3);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void startBackService(View view) {
        startForegroundService(new Intent(this, GyroscopeService.class));
    }

    public void stopService(View view) {
        Intent intent = new Intent(this, GyroscopeService.class);
        intent.putExtra("service", "stop");
        stopService(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(@NonNull Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.settings:
                startActivity(new Intent(this, com.example.posturetracking.Settings.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}