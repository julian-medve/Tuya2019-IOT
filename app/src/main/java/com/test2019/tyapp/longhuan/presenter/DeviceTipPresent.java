package com.test2019.tyapp.longhuan.presenter;

import android.app.Activity;
import android.content.Intent;

import com.test2019.tyapp.longhuan.activity.DeviceConnectionSettingActivity;
import com.test2019.tyapp.longhuan.utils.ActivityUtils;
import com.test2019.tyapp.longhuan.view.IDeviceTipView;

public class DeviceTipPresent {

    private Activity mContext;
    private IDeviceTipView mView;

//    private int mType;

    public DeviceTipPresent(Activity context, IDeviceTipView view) {
        this.mContext = context;
        this.mView = view;
        initData();
    }

    private void initData() {
//        mType = mContext.getIntent().getIntExtra(Global.DEVICE_TYPE_CHANNEL, Global.DEVICE_TYPE_COMMON);
//        Log.d("DeviceTip", "initData: " + String.format("%d", mType));
    }

    public void next() {
        Intent intent = new Intent(mContext, DeviceConnectionSettingActivity.class);
//        intent.putExtra(Global.DEVICE_TYPE_CHANNEL, mType);
        ActivityUtils.startActivity(mContext, intent, ActivityUtils.ANIMATE_FORWARD, true);
    }
}
