package com.example.posturetracking;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import com.example.posturetracking.Service.AdminReceiver;
import com.example.posturetracking.Service.GyroscopeService;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.Objects;

public class Settings extends AppCompatActivity {

    private Context context = this;

    private CheckBox setPasswordCheckBox, setNotDeletedApp, verticalTrigger, horizontalTrigger;
    private TextView startTriggerPos, titleBottomDialog;
    private Button buttonSetInterval, closeModal;
    private BottomSheetDialog bottomSheetDialog;
    private TextView subTextBottomModal;

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private DevicePolicyManager devicePolicyManager;
    private ComponentName adminReceiver;
    private SensorManager sensorManager;
    private Sensor sensor;

    private String checkType = "";
    private String saveInterval = "start";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Настройки");

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);

        bottomSheetDialog = new BottomSheetDialog(this);
        bottomSheetDialog.setContentView(R.layout.activity_bottom_modal);
        bottomSheetDialog.setDismissWithAnimation(true);
        bottomSheetDialog.setCanceledOnTouchOutside(true);

        sharedPreferences = getSharedPreferences("SettingsStore", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        boolean pswd = sharedPreferences.getString("pswd", "").length() > 0;
        boolean isAdmin = sharedPreferences.getString("isAdmin", "").length() > 0;
        boolean vertTrigger = sharedPreferences.getString("PortraitFirstInterval", "").length() > 0
                && sharedPreferences.getString("PortraitSecondInterval", "").length() > 0;

        boolean horizTrigger = sharedPreferences.getString("LandscapeFirstInterval", "").length() > 0
                && sharedPreferences.getString("LandscapeSecondInterval", "").length() > 0;

        devicePolicyManager = (DevicePolicyManager)getSystemService(DEVICE_POLICY_SERVICE);
        adminReceiver = new ComponentName(this, AdminReceiver.class);

        setPasswordCheckBox = findViewById(R.id.settings_password_checkbox);
        setNotDeletedApp = findViewById(R.id.settings_admin_checkbox);
        verticalTrigger = findViewById(R.id.settings_portrait_checkbox);
        horizontalTrigger = findViewById(R.id.settings_landscape_checkbox);

        setPasswordCheckBox.setChecked(pswd);
        setNotDeletedApp.setChecked(isAdmin);
        verticalTrigger.setChecked(vertTrigger);
        horizontalTrigger.setChecked(horizTrigger);

        // Установка / Сброс пароля
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

        // Запрет блокировки приложения
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

        // Установка значения для портретного режима, когда будет действовать сервис
        verticalTrigger.setOnCheckedChangeListener((compoundButton, isChecked) -> {
            stopService(new Intent(this, GyroscopeService.class));

            if (sharedPreferences.getString("pswd", "").length() > 0) {
                Intent intentResult = new Intent(context, PasswordScreen.class);
                intentResult.putExtra("password", "check-activity");

                if (isChecked) {
                    checkType = "setVertical";
                } else {
                    checkType = "removeVertical";
                }

                activityResultLauncher.launch(intentResult);
            } else {
                gyroscopeIntervalOptions(isChecked, "portrait");
            }
        });

        // Установка значения для горизонтального режима, когда будет действовать сервис
        horizontalTrigger.setOnCheckedChangeListener((compoundButton, isChecked) -> {
            stopService(new Intent(this, GyroscopeService.class));

            if (sharedPreferences.getString("pswd", "").length() > 0) {
                Intent intentResult = new Intent(context, PasswordScreen.class);
                intentResult.putExtra("password", "check-activity");

                if (isChecked) {
                    checkType = "setHorizontal";
                } else {
                    checkType = "removeHorizontal";
                }

                activityResultLauncher.launch(intentResult);
            } else {
                gyroscopeIntervalOptions(isChecked, "landscape");
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
                        case "setVertical":
                            if (result.getResultCode() == Activity.RESULT_OK) {
                                gyroscopeIntervalOptions(true, "portrait");
                            }
                            break;
                        case "removeVertical":
                            if (result.getResultCode() == Activity.RESULT_OK) {
                                gyroscopeIntervalOptions(false, "portrait");
                            }
                            break;
                        case "setHorizontal":
                            if (result.getResultCode() == Activity.RESULT_OK) {
                                gyroscopeIntervalOptions(true, "landscape");
                            }
                            break;
                        case "removeHorizontal":
                            if (result.getResultCode() == Activity.RESULT_OK) {
                                gyroscopeIntervalOptions(false, "landscape");
                            }
                            break;
                    }
                }
            });


    private void gyroscopeIntervalOptions (boolean isChecked, String gyroscopePosition){
        if (isChecked){
            bottomSheetDialog.show();

            startTriggerPos = bottomSheetDialog.findViewById(R.id.startXRegim);
            buttonSetInterval = bottomSheetDialog.findViewById(R.id.settings_add_interval);
            titleBottomDialog = bottomSheetDialog.findViewById(R.id.title_bottom_sheet_dialog);
            subTextBottomModal = bottomSheetDialog.findViewById(R.id.subtext_bottom_modal);

            if (gyroscopePosition.equals("portrait")){
                Objects.requireNonNull(titleBottomDialog).setText("Портретный режим");
            } else {
                Objects.requireNonNull(titleBottomDialog).setText("Горизонтальный режим");
            }

            SensorEventListener listener = new SensorEventListener() {
                @Override
                public void onSensorChanged(SensorEvent sensorEvent) {
                    if (gyroscopePosition.equals("portrait")) {
                        startTriggerPos.setText(String.format("%.1f", sensorEvent.values[1]));
                    } else {
                        startTriggerPos.setText(String.format("%.1f", sensorEvent.values[2]));
                    }
                }

                @Override
                public void onAccuracyChanged(Sensor sensor, int i) {

                }
            };

            sensorManager.registerListener(listener, sensor, SensorManager.SENSOR_DELAY_NORMAL);

            buttonSetInterval.setOnClickListener(view -> {
                if (saveInterval.equals("end")) {
                    editor.putString(gyroscopePosition.equals("portrait") ? "PortraitFirstInterval" : "LandscapeFirstInterval", (String) startTriggerPos.getText());
                    sensorManager.unregisterListener(listener);

                    titleBottomDialog.setText(R.string.header_modal);
                    subTextBottomModal.setText(R.string.success_modal);
                    startTriggerPos.setVisibility(View.GONE);
                    buttonSetInterval.setText("Закрыть");

                    saveInterval = "close";
                } else if (saveInterval.equals("start")){
                    editor.putString(gyroscopePosition.equals("portrait") ? "PortraitSecondInterval" : "LandscapeSecondInterval", (String) startTriggerPos.getText());
                    buttonSetInterval.setText("Сохранить");
                    subTextBottomModal.setText(R.string.end_modal);
                    saveInterval = "end";
                } else {
                    bottomSheetDialog.cancel();
                    startTriggerPos.setVisibility(View.VISIBLE);
                    subTextBottomModal.setText(R.string.start_modal);
                    buttonSetInterval.setText("Добавить значение");
                    saveInterval = "start";
                }

                editor.apply();
            });
        } else {
            if (gyroscopePosition.equals("portrait")) {
                editor.putString("PortraitFirstInterval", "");
                editor.putString("PortraitSecondInterval", "");
            } else {
                editor.putString("LandscapeFirstInterval", "");
                editor.putString("LandscapeSecondInterval", "");
            }
            editor.apply();
        }
    }
}