package com.test2019.tyapp.longhuan.presenter;

import android.app.Activity;
import android.content.Intent;

import com.test2019.tyapp.longhuan.activity.DeviceZigBeeAddActivity;
import com.test2019.tyapp.longhuan.global.Global;
import com.test2019.tyapp.longhuan.utils.ActivityUtils;
import com.test2019.tyapp.longhuan.view.IDeviceZigbeeTipView;

public class DeviceZigBeeTipPresenter {

    private Activity mContext;
    private IDeviceZigbeeTipView mView;

    private boolean isGateway;

    public DeviceZigBeeTipPresenter(Activity context, IDeviceZigbeeTipView view) {
        this.mContext = context;
        this.mView = view;

        isGateway = mContext.getIntent().getBooleanExtra(Global.GATEWAY_CHANNEL, false);

        if (isGateway) {
            mView.setButtonText(true);
            mView.initTipImageView();
        } else {
            mView.setButtonText(false);
        }
    }

    public void gotoZigBeeAddActivity() {
        Intent intent = new Intent(mContext, DeviceZigBeeAddActivity.class);
        intent.putExtra(Global.GATEWAY_CHANNEL, isGateway);
        ActivityUtils.startActivity(mContext, intent, ActivityUtils.ANIMATE_FORWARD, true);
    }
}
