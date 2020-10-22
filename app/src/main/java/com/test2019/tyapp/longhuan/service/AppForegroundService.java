package com.test2019.tyapp.longhuan.service;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import android.util.Log;

import com.test2019.tyapp.longhuan.R;
import com.test2019.tyapp.longhuan.global.Global;
import com.test2019.tyapp.longhuan.login.SplashActivity;
import com.test2019.tyapp.longhuan.utils.ToastUtil;

public class AppForegroundService extends Service {

    private final String TAG = "AppForegroundService";
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        ToastUtil.showToast(this, "AppForegroundService onStartCommand");
        Log.d(TAG, "onStartCommand");

        startForeground(startId, getStartNotification());
        startService(new Intent(this, AppService.class));
        stopSelf();
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy");
        ToastUtil.showToast(this, "AppForegroundService onDestroy");
    }

    private Notification getStartNotification() {
        Intent intent = new Intent(this, SplashActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

        return new NotificationCompat.Builder(this.getApplicationContext(), Global.NOTIFICATION_CHANNEL)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("AppForegroundService")
                .setContentText("Starting...")
                .setAutoCancel(true)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setContentIntent(pendingIntent)
                .setPriority(NotificationManagerCompat.IMPORTANCE_HIGH)
                .build();
    }
}
