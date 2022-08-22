package com.example.posturetracking;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

public class SplashScreen extends AppCompatActivity {

    private SharedPreferences sharedPreferences;
    private String pswd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        sharedPreferences = getSharedPreferences("SettingsStore", Context.MODE_PRIVATE);
        if (sharedPreferences.contains("pswd")){
            pswd = sharedPreferences.getString("pswd", "");

            if (pswd.length() > 0) {
                Intent intent = new Intent(this, PasswordScreen.class);
                intent.putExtra("password", "check");
                startActivity(intent);
            } else {
                startActivity(new Intent(this, MainActivity.class));
            }
            finish();
        } else {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }
    }
}