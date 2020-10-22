package com.test2019.tyapp.longhuan.presenter.device.common;

import android.app.Activity;
import androidx.fragment.app.Fragment;

import com.test2019.tyapp.longhuan.global.Global;
import com.test2019.tyapp.longhuan.view.device.common.ICommonView;
import com.tuya.smart.home.sdk.TuyaHomeSdk;
import com.tuya.smart.sdk.bean.DeviceBean;

public class CommonPresenter {

    private Activity mContext;
    private ICommonView mView;
    private Fragment mFragment;

    private DeviceBean mDevBean;
    private String mDevId;


    public CommonPresenter(Activity context, Fragment fragment, ICommonView view) {
        this.mContext = context;
        this.mFragment = fragment;
        this.mView = view;
        initData();
    }

    private void initData() {
        if (mFragment.getArguments() != null)
            mDevId = mFragment.getArguments().getString(Global.CURRENT_DEV);
        mDevBean = TuyaHomeSdk.getDataInstance().getDeviceBean(mDevId);
        if (mDevBean == null) {
            mContext.finish();
        } else {
            mView.updateTitle(mDevBean.getName());

            //====== my test device info ==========//
            mView.initDeviceInfo(mDevBean);
        }
    }
}
