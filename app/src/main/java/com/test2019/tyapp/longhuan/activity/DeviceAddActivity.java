package com.test2019.tyapp.longhuan.activity;

import com.test2019.tyapp.longhuan.global.Global;
import com.test2019.tyapp.longhuan.utils.ActivityUtils;
import com.wnafee.vector.compat.VectorDrawable;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import androidx.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.test2019.tyapp.longhuan.R;
import com.test2019.tyapp.longhuan.activity.base.BaseActivity;
import com.test2019.tyapp.longhuan.presenter.DeviceAddPresenter;
import com.test2019.tyapp.longhuan.utils.ViewUtils;
import com.test2019.tyapp.longhuan.view.IDeviceAddView;
import com.test2019.tyapp.longhuan.widget.CircularProgressBar;
import com.tuya.smart.sdk.TuyaSdk;

public class DeviceAddActivity extends BaseActivity implements IDeviceAddView, View.OnClickListener {

    private final String TAG = "DeviceAddActivity";
    public static final String DEFAULT_COMMON_AP_SSID = "SmartLife";

    CircularProgressBar mCircleView;
    TextView mEcConnectTip;
    LinearLayout mEcConnecting;
    Button btn_next;
    Button mTvShareButton;
    Button mTvRetryButton;
    TextView mTvAddDeviceSuccess;
    LinearLayout mLlFailureView;
    TextView mNetworkTip;
    private View mRetryContactTip;
    private TextView mHelp;
    private TextView mDeviceFindTip;
    private TextView mDeviceBindSussessTip;
    private TextView mDeviceInitTip;
    private TextView mDeviceInitFailureTip;
    private RelativeLayout mSwitchWifiLayout;
    private DeviceAddPresenter mPresenter;

    private String curDevId = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_add);

        initView();
        initPresenter();
    }

    private void initView() {
        mCircleView = findViewById(R.id.circleView);
        mEcConnectTip = findViewById(R.id.ec_connect_tip);
        mEcConnecting = findViewById(R.id.ec_connecting);
        btn_next = findViewById(R.id.btn_next);
        btn_next.setOnClickListener(this);
        mTvShareButton = findViewById(R.id.tv_share_button);
        mTvShareButton.setOnClickListener(this);
        mTvRetryButton = findViewById(R.id.tv_retry_button);
        mTvRetryButton.setOnClickListener(this);
        mTvAddDeviceSuccess = findViewById(R.id.tv_add_device_success);
        mLlFailureView = findViewById(R.id.ll_failure_view);
        mNetworkTip = findViewById(R.id.network_tip);
        mRetryContactTip = findViewById(R.id.tv_add_device_contact_tip);
        mHelp = findViewById(R.id.tv_ec_find_search_help);
        mHelp.setOnClickListener(this);
        int color = ViewUtils.getColor(this, R.color.navbar_font_color);
        mCircleView.setProgressColor(color);
        mCircleView.setProgressWidth(3);
        mCircleView.setTextColor(color);

        ImageView failIv = findViewById(R.id.iv_add_device_fail);
        VectorDrawable drawable = VectorDrawable.getDrawable(TuyaSdk.getApplication(), R.drawable.add_device_fail_icon);
        failIv.setImageDrawable(drawable);

        mDeviceFindTip = findViewById(R.id.tv_dev_find);
        mDeviceBindSussessTip = findViewById(R.id.tv_bind_success);
        mDeviceInitTip = findViewById(R.id.tv_device_init);
        mDeviceInitFailureTip = findViewById(R.id.tv_device_init_tip);

        // Ap distribution network auxiliary pageshowConfigSuccessTip
        mSwitchWifiLayout = findViewById(R.id.switch_wifi_layout);
        ((TextView) findViewById(R.id.tv_ap_ssid)).setText(DEFAULT_COMMON_AP_SSID.concat("_XXX"));
    }

    private void initPresenter() {
        mPresenter = new DeviceAddPresenter(this, this);
    }

    public void onClickSettings() {
        Intent wifiSettingsIntent = new Intent("android.settings.WIFI_SETTINGS");
        if (null != wifiSettingsIntent.resolveActivity(getPackageManager())) {
            startActivity(wifiSettingsIntent);
        } else {
            wifiSettingsIntent = new Intent(Settings.ACTION_WIFI_SETTINGS);
            if (null != wifiSettingsIntent.resolveActivity(getPackageManager())) {
                startActivity(wifiSettingsIntent);
            }
        }
    }

    public void onClickRetry() {
        mCircleView.setProgress(0);
//        mPresenter.reStartEZConfig(); // old version
        mPresenter.onBack();
    }

    public void onClickConnect() {
        mPresenter.gotoShareActivity();
    }

    @Override
    public void onBackPressed() {
        mPresenter.onBack();
    }

    public void onClickNext() {
//        onBackPressed();
//        Intent intent = new Intent(this, RoomSelectActivity.class);
        Intent intent = new Intent(this, SelectCategoryActivity.class);
        intent.putExtra(Global.CURRENT_DEV, curDevId);
        ActivityUtils.startActivity(this, intent, ActivityUtils.ANIMATE_FORWARD, true);
    }

    public void onClickFins() {
        mPresenter.goForHelp();
    }

    private void setAddDeviceTipGone() {
        setViewGone(mDeviceBindSussessTip);
        setViewGone(mDeviceFindTip);
        setViewGone(mDeviceInitTip);
    }

    @Override
    public void showSuccessPage() {
        setAddDeviceTipGone();
        setViewVisible(mTvAddDeviceSuccess);
        setViewVisible(btn_next);
//        setViewVisible(mTvShareButton);
        setViewGone(mEcConnecting);

    }

    @Override
    public void showFailurePage() {
        setAddDeviceTipGone();
        setTitle(getString(R.string.ap_error_title));
        setViewGone(mEcConnecting);
        setViewVisible(mLlFailureView);
        setViewVisible(mTvRetryButton);
//        setViewVisible(mRetryContactTip);     // old version
        setViewGone(mRetryContactTip);
        mHelp.setText(R.string.ap_error_description);
    }

    @Override
    public void showConnectPage() {
        setTitle(getString(R.string.ez_connecting_device_title));
        setViewVisible(mEcConnecting);
        setViewGone(mLlFailureView);
        setViewGone(mTvRetryButton);
        setViewGone(mRetryContactTip);
    }

    @Override
    public void setConnectProgress(float progress, int animationDuration) {
        mCircleView.setProgress((int)progress);
    }

    @Override
    public void showNetWorkFailurePage() {
        showFailurePage();
        setViewGone(mRetryContactTip);
        mHelp.setText(R.string.network_time_out);
    }

    @Override
    public void showBindDeviceSuccessTip() {
        ViewUtils.setTextViewDrawableLeft(TuyaSdk.getApplication(), mDeviceBindSussessTip, R.mipmap.add_device_ok_tip);
    }

    @Override
    public void showDeviceFindTip(String gwId) {
        ViewUtils.setTextViewDrawableLeft(TuyaSdk.getApplication(), mDeviceFindTip, R.mipmap.add_device_ok_tip);
    }

    @Override
    public void showConfigSuccessTip() {
        ViewUtils.setTextViewDrawableLeft(TuyaSdk.getApplication(), mDeviceInitTip, R.mipmap.add_device_ok_tip);
    }

    @Override
    public void showBindDeviceSuccessFinalTip() {
        showSuccessPage();
        setViewVisible(mDeviceInitFailureTip);
    }

    @Override
    public void setAddDeviceName(String name) {
        mTvAddDeviceSuccess.setText(String.format("%s%s", name, mTvAddDeviceSuccess.getText().toString()));
    }

    @Override
    public void showMainPage() {
        setViewVisible(mDeviceFindTip);
        setViewVisible(mDeviceBindSussessTip);
        setViewVisible(mDeviceInitTip);
    }

    @Override
    public void hideMainPage() {
        setViewGone(mEcConnecting);
        setViewGone(btn_next);
        setViewGone(mTvShareButton);
        setViewGone(mTvRetryButton);
        setViewGone(mRetryContactTip);
        setViewGone(mTvAddDeviceSuccess);
        setViewGone(mDeviceInitFailureTip);
        setViewGone(mLlFailureView);
        setViewGone(mDeviceFindTip);
        setViewGone(mDeviceBindSussessTip);
        setViewGone(mDeviceInitTip);
    }

    @Override
    public void showSubPage() {
        setViewVisible(mSwitchWifiLayout);
    }

    @Override
    public void hideSubPage() {
        setViewGone(mSwitchWifiLayout);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.add_device_tip_help:
                onClickSettings();
                break;
            case R.id.tv_retry_button:
                onClickRetry();
                break;
            case R.id.tv_share_button:
                onClickConnect();
                break;
            case R.id.btn_next:
                onClickNext();
                break;
            case R.id.tv_ec_find_search_help:
                onClickFins();
                break;
        }
    }

    @Override
    public void setDevId(String devId) {
        curDevId = devId;
    }
}
