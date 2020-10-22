package com.test2019.tyapp.longhuan.presenter;

import android.app.Activity;
import android.content.Intent;
import android.os.Message;
import android.util.Log;

import com.test2019.tyapp.longhuan.R;
import com.test2019.tyapp.longhuan.activity.DeviceConnectionSettingActivity;
import com.test2019.tyapp.longhuan.activity.SelectWifiActivity;
import com.test2019.tyapp.longhuan.global.Global;
import com.test2019.tyapp.longhuan.model.DeviceBindModel;
import com.test2019.tyapp.longhuan.utils.ActivityUtils;
import com.test2019.tyapp.longhuan.utils.ProgressUtil;
import com.test2019.tyapp.longhuan.view.IDeviceAddView;
import com.tuya.smart.android.base.utils.PreferencesUtil;
import com.tuya.smart.android.common.utils.L;
import com.tuya.smart.android.mvp.bean.Result;
import com.tuya.smart.android.mvp.presenter.BasePresenter;
import com.tuya.smart.home.sdk.TuyaHomeSdk;
import com.tuya.smart.interior.device.bean.GwDevResp;
import com.tuya.smart.sdk.api.ITuyaActivatorGetToken;
import com.tuya.smart.sdk.bean.DeviceBean;

import static com.test2019.tyapp.longhuan.global.Global.CONFIG_PASSWORD;
import static com.test2019.tyapp.longhuan.global.Global.CONFIG_SSID;


public class DeviceAddPresenter extends BasePresenter {

    private static final String TAG = "DeviceAddPresenter";
    private static final int MESSAGE_SHOW_SUCCESS_PAGE = 1001;
    private Activity mContext;
    private IDeviceAddView mView;

    private static final int MESSAGE_CONFIG_WIFI_OUT_OF_TIME = 0x16;
//    private final int mConfigMode;
    private int mTime;
    private boolean mStop;
    private DeviceBindModel mModel;
    private final String mPassWord;
    private final String mSSId;
    private boolean mBindDeviceSuccess;

    public DeviceAddPresenter(Activity context, IDeviceAddView view) {
        super(context);
        this.mContext = context;
        this.mView = view;

        mModel = new DeviceBindModel(context, mHandler);
        mPassWord = mContext.getIntent().getStringExtra(CONFIG_PASSWORD);
        mSSId = mContext.getIntent().getStringExtra(CONFIG_SSID);
        showConfigDevicePage();
        getTokenForConfigDevice();
    }

    private void showConfigDevicePage() {
        mView.hideSubPage();
    }

    private void getTokenForConfigDevice() {
        ProgressUtil.showLoading(mContext, R.string.loading); // Showing progress
        long homeId = PreferencesUtil.getLong(Global.CURRENT_HOME); // get current home Id
        TuyaHomeSdk.getActivatorInstance().getActivatorToken(homeId, new ITuyaActivatorGetToken() {
            @Override
            public void onSuccess(String token) {
                ProgressUtil.hideLoading();
                initConfigDevice(token);
            }

            @Override
            public void onFailure(String s, String s1) {
                ProgressUtil.hideLoading();
                mView.showNetWorkFailurePage();

            }
        });
    }

    private void initConfigDevice(String token) {
        mModel.setEC(mSSId, mPassWord, token);
        startSearch();
    }

    private void startSearch() {    // start network config
        mModel.start();
        mView.showConnectPage();
        mBindDeviceSuccess = false;
        startLoop();            // start circle progress
    }

    private void startLoop() {
        mTime = 0;
        mStop = false;
        mHandler.sendEmptyMessage(MESSAGE_CONFIG_WIFI_OUT_OF_TIME);
    }

    /**
     * Restart configuration
     */
    public void reStartEZConfig() {
        goToEZActivity();
    }

    private void goToEZActivity() {
        Intent intent = new Intent(mContext, DeviceConnectionSettingActivity.class);
        ActivityUtils.startActivity(mContext, intent, ActivityUtils.ANIMATE_BACK, true);
    }

    @Override
    public boolean handleMessage(Message msg) {
        switch (msg.what) {
            case MESSAGE_SHOW_SUCCESS_PAGE:
                mView.showSuccessPage();
                break;
            case MESSAGE_CONFIG_WIFI_OUT_OF_TIME:
                checkLoop();
                break;
            case DeviceBindModel.WHAT_EC_GET_TOKEN_ERROR:
                stopSearch();
                mView.showNetWorkFailurePage();
                break;
            case DeviceBindModel.WHAT_EC_ACTIVE_ERROR:
                L.d(TAG, "ec_active_error");
                stopSearch();
                if (mBindDeviceSuccess) {
                    mView.showBindDeviceSuccessFinalTip();
                    break;
                }
                mView.showFailurePage();
                break;

            case DeviceBindModel.WHAT_EC_ACTIVE_SUCCESS:
                L.d(TAG, "active_success");
                DeviceBean configDev = (DeviceBean) ((Result)msg.obj).getObj();
                stopSearch();
                configSuccess(configDev);
                break;

            case DeviceBindModel.WHAT_DEVICE_FIND:
                L.d(TAG, "device_find");
                deviceFind((String) ((Result) (msg.obj)).getObj());
                break;
            case DeviceBindModel.WHAT_BIND_DEVICE_SUCCESS:
                L.d(TAG, "bind_device_success");
                bindDeviceSuccess(((GwDevResp) ((Result) (msg.obj)).getObj()).getName());
                break;

        }
        return super.handleMessage(msg);
    }

    private void bindDeviceSuccess(String name) {
        if (!mStop) {
//            mBindDeviceSuccess = true;
            mView.setAddDeviceName(name);
            mView.showBindDeviceSuccessTip();
        }
    }

    private void deviceFind(String gwId) {
        if (!mStop) {
            mView.showDeviceFindTip(gwId);
        }
    }

    private void checkLoop() {

        if (mStop) return;
        if (mTime >= 100) {
            stopSearch();
            mModel.configFailure();
        } else {
            mView.setConnectProgress(mTime++, 1000);
            mHandler.sendEmptyMessageDelayed(MESSAGE_CONFIG_WIFI_OUT_OF_TIME, 1000);
        }
    }

    private void configSuccess(DeviceBean deviceBean) {
        if (deviceBean != null){
            Log.d(TAG, "configSuccess: " + deviceBean.getDevId());
//            Toast.makeText(mContext,"the device id is: " + deviceBean.getDevId(), Toast.LENGTH_SHORT).show();
        }
        stopSearch();
        mView.showConfigSuccessTip();
//        Log.d(TAG, "configSuccess: " + deviceBean.getDevId());
//        PreferenceUtils.setInt(mContext, deviceBean.getDevId(), mType);
        if (deviceBean != null)
            mView.setDevId(deviceBean.getDevId());
        mBindDeviceSuccess = true;
        mView.setConnectProgress(100, 800);
        mHandler.sendEmptyMessageDelayed(MESSAGE_SHOW_SUCCESS_PAGE, 1000);
    }

    private void stopSearch() {
        mStop = true;
        mHandler.removeMessages(MESSAGE_CONFIG_WIFI_OUT_OF_TIME);
        mModel.cancel();
    }

    public void gotoShareActivity() {
//        ActivityUtils.gotoActivity((Activity) mContext, SharedActivity.class, ActivityUtils.ANIMATE_FORWARD, true);
    }

    public void goForHelp() {

    }

    public void onBack() {
        if (mStop && !mBindDeviceSuccess) {
//            ActivityUtils.gotoActivity(mContext, DeviceSelectActivity.class, ActivityUtils.ANIMATE_BACK, true);   // old version
            ActivityUtils.gotoActivity(mContext, SelectWifiActivity.class, ActivityUtils.ANIMATE_BACK, true);
        }
    }

    @Override
    public void onDestroy() {
        mHandler.removeMessages(MESSAGE_CONFIG_WIFI_OUT_OF_TIME);
        mHandler.removeMessages(MESSAGE_SHOW_SUCCESS_PAGE);
        mModel.onDestroy();
        super.onDestroy();
    }
}
