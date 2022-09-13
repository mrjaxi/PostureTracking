package com.example.posturetracking;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.posturetracking.Service.GyroscopeService;

public class MainActivity extends AppCompatActivity {

    private SharedPreferences sharedPreferences;
    private boolean serviceIsStarted = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sharedPreferences = getSharedPreferences("SettingsStore", Context.MODE_PRIVATE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(this)) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName()));
                startActivityForResult(intent, 3);
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void startBackService(View view) {
        String firstStringPos = sharedPreferences.getString("PortraitFirstInterval", "");
        String secondStringPos = sharedPreferences.getString("PortraitSecondInterval", "");

        String firstLndStringPos = sharedPreferences.getString("LandscapeFirstInterval", "");
        String secondLndStringPos = sharedPreferences.getString("LandscapeSecondInterval", "");

        if ((firstStringPos.length() > 0 && secondStringPos.length() > 0) || (firstLndStringPos.length() > 0 && secondLndStringPos.length() > 0)) {
            if (!serviceIsStarted) {
                startService(new Intent(this, GyroscopeService.class));
                serviceIsStarted = true;
            } else {
                Toast.makeText(this, "Сервис уже запущен!", Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(this, "Вы еще не задали значения. Перейдите: Найстройки ➝ Триггеры", Toast.LENGTH_LONG).show();
        }
    }

    public void stopService(View view) {
        if (serviceIsStarted) {
            stopService(new Intent(this, GyroscopeService.class));
            serviceIsStarted = false;
        } else {
            Toast.makeText(this, "Сервис уже выключен!", Toast.LENGTH_LONG).show();
        }
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