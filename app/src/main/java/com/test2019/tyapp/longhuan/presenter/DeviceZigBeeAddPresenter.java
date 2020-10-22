package com.test2019.tyapp.longhuan.presenter;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;

import com.test2019.tyapp.longhuan.activity.DeviceSelectActivity;
import com.test2019.tyapp.longhuan.activity.RoomSelectActivity;
import com.test2019.tyapp.longhuan.global.Global;
import com.test2019.tyapp.longhuan.utils.ActivityUtils;
import com.test2019.tyapp.longhuan.utils.PreferenceUtils;
import com.test2019.tyapp.longhuan.utils.ProgressUtil;
import com.test2019.tyapp.longhuan.view.IDeviceZigBeeAddView;
import com.tuya.smart.android.base.utils.PreferencesUtil;
import com.tuya.smart.android.common.utils.L;
import com.tuya.smart.android.hardware.bean.HgwBean;
import com.tuya.smart.android.mvp.presenter.BasePresenter;
import com.tuya.smart.home.sdk.TuyaHomeSdk;
import com.tuya.smart.home.sdk.builder.TuyaGwActivatorBuilder;
import com.tuya.smart.home.sdk.builder.TuyaGwSubDevActivatorBuilder;
import com.tuya.smart.sdk.api.ITuyaActivator;
import com.tuya.smart.sdk.api.ITuyaActivatorGetToken;
import com.tuya.smart.sdk.api.ITuyaSmartActivatorListener;
import com.tuya.smart.sdk.bean.DeviceBean;

public class DeviceZigBeeAddPresenter extends BasePresenter {

    private final String TAG = "ZigBeeAddPresenter";

    public static final int VISIBLE = 0;
    public static final int INVISIBLE = 1;
    public static final int GONE = 2;

    private Activity mContext;
    private IDeviceZigBeeAddView mView;

    private Handler processHandler;
    private HgwBean mHgwBean = null;

    private ITuyaActivator mTuyaActivator;


    private String gatewayId;
    private boolean mStop;
    private String curDevId = null;

    private final Runnable searchRunnable = new Runnable() {
        int i = 0;

        @Override
        public void run() {
            i++;
            Log.e(TAG,  String.format("%d", i));
            processHandler.postDelayed(this, 1000);
            if (i > 120) {
                mView.showSpinKitView(INVISIBLE);
                mView.setStatusText("Not found, please try again");
                processHandler.removeCallbacks(this);
                mStop = true;
            }
        }
    };

    private final Runnable connectRunnable = new Runnable() {
        int i = 0;
        @Override
        public void run() {
            i++;
            Log.e(TAG,  String.format("%d", i));
            int value = i * 100 / 120;
            mView.setProgressBar(value);
            processHandler.postDelayed(this, 1000);
            if (i > 119) {
                mView.setStatusText("Connection Failed. please try again");
                processHandler.removeCallbacks(this);
            }
        }
    };

    public DeviceZigBeeAddPresenter(Activity context, IDeviceZigBeeAddView view) {
        this.mContext = context;
        this.mView = view;

        initData();
    }

    private void initData() {
        boolean isGateway = mContext.getIntent().getBooleanExtra(Global.GATEWAY_CHANNEL, false);

        processHandler = new Handler();

        if (isGateway) {
//            gatewayId = getSharedPreferences(Global.HAS_GATEWAY, Context.MODE_PRIVATE).getString("gwID", null);
            startGatewaySubDevConfig();
        } else {
            requestGWToken();
        }

        mStop = false;
    }

    private void startGatewaySubDevConfig() {

        if (TextUtils.isEmpty(gatewayId)) {
            mView.setStatusText("No Gateway\nPlease try to connect gateway");
            return;
        }

        mView.showSpinKitView(GONE);
        mView.showProgressBar(VISIBLE);
        mView.setStatusText("Connecting");

        processHandler.postDelayed(connectRunnable, 1000);
        TuyaGwSubDevActivatorBuilder builder = new TuyaGwSubDevActivatorBuilder()
                .setDevId(gatewayId)
                .setTimeOut(120)
                .setListener(new ITuyaSmartActivatorListener() {
                    @Override
                    public void onError(String s, String s1) {
                        mView.setProgressBar(0);
                        mView.setStatusText("Connection Failed. Please try again");
                        processHandler.removeCallbacks(connectRunnable);
                        Log.e(TAG, "subDevConfig onFailure");
                        mStop = true;
                        mView.showSuccessButton(false);
                    }

                    @Override
                    public void onActiveSuccess(DeviceBean deviceBean) {

                        mView.setProgressBar(100);
                        mView.setStatusText("Connected");

                        processHandler.removeCallbacks(connectRunnable);
                        Log.e(TAG, "config success：deviceResBean id:" + deviceBean.getDevId());
                        curDevId = deviceBean.getDevId();
                        mStop = true;
                        mView.showSuccessButton(true);
                    }

                    @Override
                    public void onStep(String s, Object o) {
                    }
                });

        mTuyaActivator = TuyaHomeSdk.getActivatorInstance().newTuyaGwActivator().newSubDevActivator(builder);
        mTuyaActivator.start();
    }

    private void startBindGatewayDevice(String token) {

        mView.setStatusText("Connecting");
        processHandler.postDelayed(connectRunnable, 1000);
        TuyaGwActivatorBuilder builder = new TuyaGwActivatorBuilder()
                .setContext(mContext)
                .setHgwBean(mHgwBean)
                .setToken(token)
                .setTimeOut(120)
                .setListener(new ITuyaSmartActivatorListener() {
                    @Override
                    public void onError(String errorCode, String errorMsg) {

                        mView.setProgressBar(0);
                        mView.setStatusText("Connection Failed. Please try again");

                        processHandler.removeCallbacks(connectRunnable);

                        Log.e(TAG, "error:" + errorCode + " msg:" + errorMsg);
                        mStop = true;
                        mView.showSuccessButton(false);
                    }

                    @Override
                    public void onActiveSuccess(DeviceBean deviceBean) {
                        mView.setProgressBar(100);
                        mView.setStatusText("Connected");
                        processHandler.removeCallbacks(connectRunnable);
                        Log.e(TAG, "onActiveSuccess");
                        gatewayId = deviceBean.getDevId();
                        curDevId = gatewayId;

                        PreferenceUtils.set(mContext, Global.GATEWAY_CHANNEL, true);

//                        SharedPreferences sharedPreferences = getSharedPreferences("Longhuan",  Context.MODE_PRIVATE);
//                        SharedPreferences.Editor editor = sharedPreferences.edit();
//                        editor.putString("gwID", gwId);
//                        editor.putBoolean("HasGWID", true);
//                        editor.commit();
//                        Log.d(TAG, "gwId:" + gwId);

                        mStop = true;
                        mView.showSuccessButton(true);
                    }

                    @Override
                    public void onStep(String s, Object o) {
                        L.e(TAG, "s=" + s);
                    }
                });
        mTuyaActivator = TuyaHomeSdk.getActivatorInstance().newGwActivator(builder);
        mTuyaActivator.start();
    }

    private void requestGWToken() {

        ProgressUtil.showLoading(mContext, "Getting Token");
        long homeId = PreferencesUtil.getLong(Global.CURRENT_HOME);
        TuyaHomeSdk.getActivatorInstance().getActivatorToken(homeId, new ITuyaActivatorGetToken() {
            @Override
            public void onSuccess(String token) {
                ProgressUtil.hideLoading();
                Log.e(TAG, token);
                findGateWayDevice(token);
            }

            @Override
            public void onFailure(String s, String s1) {
                ProgressUtil.hideLoading();
                L.d(TAG, "get token error:::" + s + " ++ " + s1);
                mStop = true;
                mView.setStatusText("Falied Getting Token\nerror: " + s +"++" + s1);
            }
        });
    }

    private void findGateWayDevice(final String token){
        mView.showSpinKitView(VISIBLE);
        mView.setStatusText("Searching");
        processHandler.postDelayed(searchRunnable, 1000);

        TuyaHomeSdk.getActivatorInstance().newTuyaGwActivator().newSearcher().registerGwSearchListener((event) -> {
            mView.showSpinKitView(GONE);
            mView.showProgressBar(VISIBLE);

            processHandler.removeCallbacks(searchRunnable);
            mHgwBean = event;
            startBindGatewayDevice(token);
        });
    }

    public void onDestroy() {
        if (mTuyaActivator != null) {
            mTuyaActivator.onDestroy();
        }
    }

    public void onBack() {
        if (mStop) {
            ActivityUtils.gotoActivity(mContext, DeviceSelectActivity.class, ActivityUtils.ANIMATE_BACK, true);
        }
    }

    public void onClickFinish() {
        Intent intent = new Intent(mContext, RoomSelectActivity.class);
        intent.putExtra(Global.CURRENT_DEV, curDevId);
        ActivityUtils.startActivity(mContext, intent, ActivityUtils.ANIMATE_FORWARD, true);
    }
}
