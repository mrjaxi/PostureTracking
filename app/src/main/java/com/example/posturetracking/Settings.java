package com.example.posturetracking;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RadioGroup;

import com.example.posturetracking.Service.AdminReceiver;

import java.util.Objects;

public class Settings extends AppCompatActivity {

    private Context context = this;

    private CheckBox setPasswordCheckBox, setNotDeletedApp;

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private DevicePolicyManager devicePolicyManager;
    private ComponentName adminReceiver;

    private String checkType = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Настройки");

        sharedPreferences = getSharedPreferences("SettingsStore", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        boolean pswd = sharedPreferences.getString("pswd", "").length() > 0;
        boolean isAdmin = sharedPreferences.getString("isAdmin", "").length() > 0;

        devicePolicyManager = (DevicePolicyManager)getSystemService(DEVICE_POLICY_SERVICE);
        adminReceiver = new ComponentName(this, AdminReceiver.class);

        setPasswordCheckBox = findViewById(R.id.settings_password_checkbox);
        setNotDeletedApp = findViewById(R.id.settings_admin_checkbox);

        setPasswordCheckBox.setChecked(pswd);
        setNotDeletedApp.setChecked(isAdmin);

        setPasswordCheckBox.setOnCheckedChangeListener((compoundButton, isChecked) -> {
                Intent intent = new Intent(context, PasswordScreen.class);
                if (isChecked) {
                    intent.putExtra("password", "write");
                    startActivity(intent);
                } else {
                    if (sharedPreferences.getString("pswd", "").length() > 0) {
                        intent.putExtra("password", "remove");
                        startActivity(intent);
                    }
                }
        });


        setNotDeletedApp.setOnCheckedChangeListener((compoundButton, isChecked) -> {
            if (sharedPreferences.getString("pswd", "").length() > 0) {
                Intent intentResult = new Intent(context, PasswordScreen.class);
                intentResult.putExtra("password", "check-activity");

                if (isChecked) {
                    checkType = "setAdmin";
                } else {
                    checkType = "removeAdmin";
                }

                activityResultLauncher.launch(intentResult);
            } else {
                if (isChecked) {
                    Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
                    intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, adminReceiver);
                    intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, "AdminBlockDelete");
                    editor.putString("isAdmin", "admin");
                    editor.apply();
                    startActivity(intent);
                } else {
                    devicePolicyManager.removeActiveAdmin(adminReceiver);
                    editor.putString("isAdmin", "");
                    editor.apply();
                }
            }
        });
    }

    ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    switch (checkType) {
                        case "setAdmin":
                            if (result.getResultCode() == Activity.RESULT_OK) {
                                Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
                                intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, adminReceiver);
                                intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, "AdminBlockDelete");
                                editor.putString("isAdmin", "admin");
                                editor.apply();
                                startActivity(intent);
                            }
                            break;
                        case "removeAdmin":
                            if (result.getResultCode() == Activity.RESULT_OK) {
                                devicePolicyManager.removeActiveAdmin(adminReceiver);
                                editor.putString("isAdmin", "");
                                editor.apply();
                            }
                            break;
                    }
                }
            });
}