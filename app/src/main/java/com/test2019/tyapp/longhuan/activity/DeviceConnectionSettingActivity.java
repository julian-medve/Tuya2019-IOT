package com.test2019.tyapp.longhuan.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;
import androidx.annotation.Nullable;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.test2019.tyapp.longhuan.R;
import com.test2019.tyapp.longhuan.activity.base.BaseActivity;
import com.test2019.tyapp.longhuan.presenter.DeviceConnectionSettingPresenter;
import com.test2019.tyapp.longhuan.utils.ActivityUtils;
import com.test2019.tyapp.longhuan.view.IDeviceConnectionSettingView;

import static com.test2019.tyapp.longhuan.presenter.DeviceConnectionSettingPresenter.CODE_FOR_LOCATION_PERMISSION;
import static com.test2019.tyapp.longhuan.presenter.DeviceConnectionSettingPresenter.PRIVATE_CODE;

public class DeviceConnectionSettingActivity extends BaseActivity implements IDeviceConnectionSettingView, View.OnClickListener {

    private static final String TAG = "DeviceConnectionSettingActivity";


    private TextView txtSSID;
    private Button btnOtherWifi;
    private EditText txtWifiPassword;
    private ImageButton btnWifiSwitch;
    private Button btnNext;
    private ImageButton btnBack;

    private DeviceConnectionSettingPresenter mPresenter;

    private boolean passwordOn = true;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_connect_setting);

        initView();
        initPresenter();
    }

    private void initView() {
        txtSSID = findViewById(R.id.txt_wifi_status);
        btnOtherWifi = findViewById(R.id.btn_other_wifi);
        btnOtherWifi.setOnClickListener(this);
        txtWifiPassword = findViewById(R.id.wifi_password);
        btnWifiSwitch = findViewById(R.id.wifi_password_switch);
        btnWifiSwitch.setOnClickListener(this);
        btnNext = findViewById(R.id.btn_device_connect_setting);
        btnNext.setOnClickListener(this);
        btnBack = findViewById(R.id.btn_back);
        btnBack.setOnClickListener((v)->ActivityUtils.gotoActivity(DeviceConnectionSettingActivity.this, DeviceTipActivity.class, ActivityUtils.ANIMATE_BACK, true));
        clickPasswordSwitch();
    }

    private void initPresenter() {
        mPresenter = new DeviceConnectionSettingPresenter(this, this);
        mPresenter.showLocationError();
    }

    @Override
    public void setWifiSSID(String ssId) {
        txtSSID.setText(String.format("Wi-Fi at present: %s", ssId));
    }

    @Override
    public void setWifiPass(String pass) {
        txtWifiPassword.setText(pass);
    }

    @Override
    public String getWifiPass() {
        return txtWifiPassword.getText().toString();
    }

    @Override
    public String getWifiSsId() {
        return txtSSID.getText().toString();
    }

    @Override
    public void showNoWifi() {
        hide5gTip();
        setWifiPass("");
        txtSSID.setText(getString(R.string.ez_current_no_wifi));
    }

    @Override
    public void show5gTip() {

    }

    @Override
    public void hide5gTip() {

    }

    @Override
    protected void onResume() {
        super.onResume();
        mPresenter.checkWifiNetworkStatus();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mPresenter.onDestroy();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 0:
            case PRIVATE_CODE:
                LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                boolean isOpen = locationManager.isProviderEnabled(android.location.LocationManager.GPS_PROVIDER);
                if (isOpen) {
                    mPresenter.checkSinglePermission(Manifest.permission.ACCESS_FINE_LOCATION, CODE_FOR_LOCATION_PERMISSION);
                }
                break;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_other_wifi:
                mPresenter.userOtherWifi();
                break;
            case R.id.wifi_password_switch:
                clickPasswordSwitch();
                break;
            case R.id.btn_device_connect_setting:
                mPresenter.goNextStep();
                break;
        }
    }

    public void clickPasswordSwitch() {
        passwordOn = !passwordOn;
        if (passwordOn) {
            btnWifiSwitch.setImageResource(R.mipmap.password_on);
            txtWifiPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
        } else {
            btnWifiSwitch.setImageResource(R.mipmap.password_off);
            txtWifiPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
        }
    }

    @Override
    public void onBackPressed() {
        ActivityUtils.gotoActivity(this, DeviceTipActivity.class, ActivityUtils.ANIMATE_BACK, true);
    }
}
