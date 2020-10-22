package com.test2019.tyapp.longhuan.utils;

import android.app.Activity;
import android.content.Context;

import com.test2019.tyapp.longhuan.R;
import com.test2019.tyapp.longhuan.app.Constant;
import com.test2019.tyapp.longhuan.login.LoginActivity;
import com.tuya.smart.home.sdk.TuyaHomeSdk;


/**
 * Created by letian on 16/7/15.
 */
public class LoginHelper {


    public static void afterLogin() {

        //there is the somethings that need to set.For example the lat and lon;
        //   TuyaSdk.setLatAndLong();
    }


    /**
     * Re-Login
     *
     * @param context context
     */
    public static void reLogin(Context context) {
        reLogin(context, true);
    }

    public static void reLogin(Context context, boolean tip) {
        onLogout(context);
        if (tip) {
            ToastUtil.shortToast(context, R.string.login_session_expired);
        }
        ActivityUtils.gotoActivity((Activity) context, LoginActivity.class, ActivityUtils.ANIMATE_BACK, true);
    }

    private static void onLogout(Context context) {
        exit(context);
    }

    /**
     * Exit the app
     *
     * @param context context
     */
    public static void exit(Context context) {
        Constant.finishActivity();
        TuyaHomeSdk.onDestroy();
    }
}
