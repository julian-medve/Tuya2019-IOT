package com.test2019.tyapp.longhuan.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.test2019.tyapp.longhuan.global.Global;
import com.test2019.tyapp.longhuan.login.SplashActivity;

public class AutoStartReceiver extends BroadcastReceiver {

    private final String TAG = "AutoStartReceiver";
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "onReceive: " + intent.getAction());
//        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
//            context.startForegroundService(new Intent(context, AppForegroundService.class));
//        } else {
//            context.startService(new Intent(context, AppService.class));
//        }
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            Intent bootActivity = new Intent(context, SplashActivity.class);
            bootActivity.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            bootActivity.putExtra(Global.AUTOSTART_CHANNEL, true);
            context.startActivity(bootActivity);
        }
    }
}