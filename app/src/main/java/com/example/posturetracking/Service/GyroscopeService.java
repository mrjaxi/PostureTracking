package com.example.posturetracking.Service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.example.posturetracking.MainActivity;
import com.example.posturetracking.R;

public class GyroscopeService extends Service {
    public Context context = this;

    private SensorManager sensorManager;
    private Sensor sensor;
    private WindowManager wm;
    private SensorEventListener listener;

    private View dialogView;
    private TextView textView;

    private float y, y1;
    private float z, z1;
    private boolean showView = false;
    private int LAYOUT_FLAG;

    private NotificationChannel channel;
    private NotificationManager service;
    private SharedPreferences sharedPreferences;

    private Intent intentStop;
    private Notification notification;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        this.intentStop = intent;

        String SERVICE_NOTIFICATION_ID = "Background Service";
        String CHANNEL_ID = "Example";

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            channel = new NotificationChannel(CHANNEL_ID, SERVICE_NOTIFICATION_ID, NotificationManager.IMPORTANCE_NONE);
            channel.setLightColor(Color.BLUE);
            channel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);

            service = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
            service.createNotificationChannel(channel);
        }

        notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.notify_icons)
                .setShowWhen(false)
                .setChannelId(CHANNEL_ID)
                .setOngoing(true)
                .build();

        startForeground(101, notification);

        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return new Binder();
    }

    @Override
    public void onCreate() {
        Toast.makeText(this, "Сервис запущен", Toast.LENGTH_LONG).show();
        sharedPreferences = getSharedPreferences("SettingsStore", Context.MODE_PRIVATE);

        String firstStringPos = sharedPreferences.getString("PortraitFirstInterval", "");
        String secondStringPos = sharedPreferences.getString("PortraitSecondInterval", "");

        if (firstStringPos.length() > 0 && secondStringPos.length() > 0) {
            y = Float.parseFloat(sharedPreferences.getString("PortraitFirstInterval", "").replace(",", "."));
            y1 = Float.parseFloat(sharedPreferences.getString("PortraitSecondInterval", "").replace(",", "."));
        }

        String firstLndStringPos = sharedPreferences.getString("LandscapeFirstInterval", "");
        String secondLndStringPos = sharedPreferences.getString("LandscapeSecondInterval", "");

        if (firstLndStringPos.length() > 0 && secondLndStringPos.length() > 0) {
            z = Float.parseFloat(sharedPreferences.getString("LandscapeFirstInterval", "").replace(",", "."));
            z1 = Float.parseFloat(sharedPreferences.getString("LandscapeSecondInterval", "").replace(",", "."));
        }

        dialogView = LayoutInflater.from(context).inflate(R.layout.activity_overlay_dialog, null);
        wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        textView = dialogView.findViewById(R.id.textView);

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            LAYOUT_FLAG = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else {
            LAYOUT_FLAG = WindowManager.LayoutParams.TYPE_PHONE;
        }

        listener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent sensorEvent) {
                if (context.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT && (firstStringPos.length() > 0 && secondStringPos.length() > 0)) {
                    if (sensorEvent.values[1] > Math.min(y, y1) && sensorEvent.values[1] < Math.max(y, y1) && !showView) {
                        wm.addView(dialogView, new WindowManager.LayoutParams(
                            (int) (wm.getDefaultDisplay().getWidth() / 1.5),
                            160,
                                LAYOUT_FLAG,
                            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                            PixelFormat.TRANSLUCENT
                        ));
                        showView = true;
                    } else if (sensorEvent.values[1] < Math.min(y, y1) && showView) {
                        if (showView) {
                            wm.removeView(dialogView);
                            showView = false;
                        }
                    } else if (sensorEvent.values[1] > Math.max(y, y1)) {
                        if (showView) {
                            wm.removeView(dialogView);
                            showView = false;
                        }
                    }
                } else if (context.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE && (firstLndStringPos.length() > 0 && secondLndStringPos.length() > 0)) {
                    if (sensorEvent.values[2] > Math.min(z, z1) && sensorEvent.values[2] < Math.max(z, z1) && !showView) {
                        wm.addView(dialogView, new WindowManager.LayoutParams(
                            (int) (wm.getDefaultDisplay().getWidth() / 1.5),
                            160,
                                LAYOUT_FLAG,
                            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                            PixelFormat.TRANSLUCENT
                        ));
                        showView = true;
                    } else if (sensorEvent.values[2] < Math.min(z, z1) && showView) {
                        if (showView) {
                            wm.removeView(dialogView);
                            showView = false;
                        }
                    } else if (sensorEvent.values[2] > Math.max(z, z1) && showView) {
                        if (showView) {
                            wm.removeView(dialogView);
                            showView = false;
                        }
                    }
                }
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int i) {

            }
        };
        sensorManager.registerListener(listener, sensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        sensorManager.unregisterListener(listener);
        if (showView) {
            wm.removeView(dialogView);
        }
        showView = false;
        stopSelf();
        Toast.makeText(this, "Сервис завершил работу", Toast.LENGTH_LONG).show();
    }
}