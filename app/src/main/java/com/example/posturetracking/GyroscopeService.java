package com.example.posturetracking;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Binder;
import android.os.IBinder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

public class GyroscopeService extends Service {
    public Context context = this;

    private SensorManager sensorManager;
    private Sensor sensor;
    private WindowManager wm;
    private SensorEventListener listener;

    private View dialogView;
    private TextView textView;

    private boolean showView = false;
    private NotificationChannel channel;
    private NotificationManager service;

    private Intent intentStop;

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

        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE);
        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentIntent(contentIntent)
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
        dialogView = LayoutInflater.from(context).inflate(R.layout.activity_overlay_dialog, null);
        wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        textView = dialogView.findViewById(R.id.textView);

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);

        listener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent sensorEvent) {
                if (context.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                    if (sensorEvent.values[1] > -45.0f && sensorEvent.values[1] < -3.5f && !showView) {
                        wm.addView(dialogView, new WindowManager.LayoutParams(
                                (int) (wm.getDefaultDisplay().getWidth() / 1.5),
                                160,
                                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                                PixelFormat.TRANSLUCENT
                        ));
                        showView = true;
                    } else if (sensorEvent.values[1] < -45.0f && showView) {
                        if (showView) {
                            wm.removeView(dialogView);
                            showView = false;
                        }
                    } else if (sensorEvent.values[1] > -3.5f) {
                        if (showView) {
                            wm.removeView(dialogView);
                            showView = false;
                        }
                    }
                } else {
                    if (sensorEvent.values[2] > -33.0f && sensorEvent.values[2] < -8.0f && !showView) {
                        wm.addView(dialogView, new WindowManager.LayoutParams(
                                (int) (wm.getDefaultDisplay().getWidth() / 1.5),
                                160,
                                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                                PixelFormat.TRANSLUCENT
                        ));
                        showView = true;
                    } else if (sensorEvent.values[2] < -33.0f && showView) {
                        if (showView) {
                            wm.removeView(dialogView);
                            showView = false;
                        }
                    } else if (sensorEvent.values[2] > -8.0f && showView) {
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
    }
}