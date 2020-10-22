package com.test2019.tyapp.longhuan.login;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.text.TextUtils;

import com.test2019.tyapp.longhuan.global.Global;
import com.test2019.tyapp.longhuan.utils.ActivityUtils;
import com.test2019.tyapp.longhuan.utils.DialogUtil;
import com.tuya.smart.android.common.utils.L;
import com.tuya.smart.android.common.utils.TuyaUtil;
import com.tuya.smart.home.sdk.TuyaHomeSdk;

public class SplashActivity extends AppCompatActivity {

    private boolean auto_start;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        L.d("splash", "TuyaTime: " + TuyaUtil.formatDate(System.currentTimeMillis(), "yyyy-mm-dd hh:mm:ss"));
        auto_start = false;
        if (getIntent() != null)
            auto_start = getIntent().getBooleanExtra(Global.AUTOSTART_CHANNEL, false);
        if (isInitAppkey()) {
            gotoLogin();
        } else {
            showTipDialog();
        }
    }

    public void gotoLogin() {
        if (TuyaHomeSdk.getUserInstance().isLogin()) {
            ActivityUtils.gotoMainActivity(this, auto_start);
        } else {
            ActivityUtils.gotoActivity(this, LoginActivity.class, ActivityUtils.ANIMATE_FORWARD, true);
        }
    }


    private void showTipDialog() {
        DialogUtil.simpleConfirmDialog(this, "Appkey or Appsecret is empty. \nPlease check your configuration", ((dialog, which) -> finish()));
    }

    private boolean isInitAppkey() {
        String appkey = getInfo("TUYA_SMART_APPKEY", this);
        String appSecret = getInfo("TUYA_SMART_SECRET", this);
        if (TextUtils.equals("null", appkey) || TextUtils.equals("null", appSecret)) return false;
        return !TextUtils.isEmpty(appkey) && !TextUtils.isEmpty(appSecret);
    }

    public static String getInfo(String infoName, Context context) {
        ApplicationInfo e;
        try {
            e = context.getPackageManager().getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
            return e.metaData.getString(infoName);
        } catch (PackageManager.NameNotFoundException e1) {
            e1.printStackTrace();
        }
        return "";
    }
}
