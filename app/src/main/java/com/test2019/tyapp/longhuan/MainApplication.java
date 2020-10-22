package com.test2019.tyapp.longhuan;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import androidx.core.app.NotificationCompat;

import com.test2019.tyapp.longhuan.global.Global;
import com.test2019.tyapp.longhuan.login.LoginActivity;
import com.test2019.tyapp.longhuan.utils.PreferenceUtils;
import com.tuya.smart.android.common.utils.L;
import com.tuya.smart.home.sdk.TuyaHomeSdk;

public class MainApplication extends Application {

    private final String TAG = "MainApplication";

    private static Context mContext;

    @Override
    public void onCreate() {
        super.onCreate();

        mContext = this;

        L.d(TAG, "onCreate " + getProcessName(this));
        L.setSendLogOn(true);
        createNotificationChannel();

        TuyaHomeSdk.init(this);
        TuyaHomeSdk.setOnNeedLoginListener((context -> {
            Intent intent = new Intent(context, LoginActivity.class);
            if (!(context instanceof Activity)) {
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            }
            startActivity(intent);
        }));

        TuyaHomeSdk.setDebugMode(true);
    }

    public static String getProcessName(Context context){
        int pid = android.os.Process.myPid();
        ActivityManager mActivityManager = (ActivityManager) context
                .getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningAppProcessInfo appProcess : mActivityManager
                .getRunningAppProcesses()) {
            if (appProcess.pid == pid) {
                return appProcess.processName;
            }
        }
        return "";
    }

    public static Context getAppContext() {
        return mContext;
    }

    public static String getDefaultEncoding(){
        String lang = PreferenceUtils.getString(getAppContext(), Global.LANGUAGE_SETTING);
        if (lang == null)
            return "en-US";
        else
            return lang;
//            return "pl-PL";
//        return "en-US";
    }
    public static String  getDefaultEncodingPrefix(){
        String lang_prefix = PreferenceUtils.getString(getAppContext(), Global.LANGUAGE_SETTING_PREFIX);
        if (lang_prefix == null)
            return "en";
        else
            return lang_prefix;
//        return "pl";
//        return "en";
    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= 26) {
            CharSequence name = getString(R.string.channel_name);
            String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(Global.NOTIFICATION_CHANNEL, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            channel.enableLights(true);
            channel.enableVibration(true);
            channel.setShowBadge(true);
            channel.setVibrationPattern(new long[] {300, 300});
            channel.setLockscreenVisibility(NotificationCompat.VISIBILITY_PUBLIC);

            notificationManager.createNotificationChannel(channel);
        }
    }
}
