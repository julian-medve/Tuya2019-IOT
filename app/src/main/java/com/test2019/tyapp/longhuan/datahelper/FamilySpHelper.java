package com.test2019.tyapp.longhuan.datahelper;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.test2019.tyapp.longhuan.MainApplication;
import com.tuya.smart.android.user.bean.User;
import com.tuya.smart.home.sdk.TuyaHomeSdk;
import com.tuya.smart.home.sdk.bean.HomeBean;

public class FamilySpHelper {

    private SharedPreferences mPreferences;

    private static final String PREFERENCE_NAME = "ll_home";

    private static final String CURRENT_FAMILY_SUFFIX = "ll_currentHome_";


    public static final String TAG = FamilySpHelper.class.getSimpleName();

    public FamilySpHelper() {
        mPreferences = MainApplication.getAppContext().getSharedPreferences(
                PREFERENCE_NAME, Context.MODE_PRIVATE);
    }

    public void putCurrentHome(HomeBean homeBean) {
        if (null == homeBean) {
            Log.d(TAG, "putCurrentHome: null");
            return;
        }
        SharedPreferences.Editor editor = mPreferences.edit();
        String userId = null;
        User user = TuyaHomeSdk.getUserInstance().getUser();
        if (null != user) {
            userId = user.getUid();
        }
        editor.putString(CURRENT_FAMILY_SUFFIX + userId, JSON.toJSONString(homeBean));
        editor.apply();
    }


    public HomeBean getCurrentHome() {
        String userId = null;
        User user = TuyaHomeSdk.getUserInstance().getUser();
        if (null != user) {
            userId = user.getUid();
        }

        String currentFamilyStr = mPreferences.getString(CURRENT_FAMILY_SUFFIX + userId, "");
        if (TextUtils.isEmpty(currentFamilyStr)) {
            return null;
        }
        return JSON.parseObject(currentFamilyStr, HomeBean.class);
    }


}
