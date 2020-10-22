package com.test2019.tyapp.longhuan.activity;

import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.test2019.tyapp.longhuan.R;
import com.test2019.tyapp.longhuan.activity.base.BaseActivity;
import com.test2019.tyapp.longhuan.presenter.DeviceZigBeeTipPresenter;
import com.test2019.tyapp.longhuan.utils.ActivityUtils;
import com.test2019.tyapp.longhuan.view.IDeviceZigbeeTipView;

public class DeviceZigBeeTipActivity extends BaseActivity implements IDeviceZigbeeTipView {

    private final String TAG = "DeviceZigBeeTipActivity";

    private Button btnNext;
    private ImageButton btnBack;
    private ImageView mStatusLightImageView;

    private AnimationDrawable mStatusLightAnimation;

    private DeviceZigBeeTipPresenter mDeviceZigBeeTipPresenter;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_gateway);

        initView();
        initPresenter();
    }

    private void initView() {
        mStatusLightImageView = findViewById(R.id.img_device_gateway_tip);
        btnNext = findViewById(R.id.btn_device_gateway_tip);
        btnNext.setOnClickListener((v)->mDeviceZigBeeTipPresenter.gotoZigBeeAddActivity());

        btnBack = findViewById(R.id.btn_back);
        btnBack.setOnClickListener((v)->ActivityUtils.gotoActivity(DeviceZigBeeTipActivity.this, DeviceSelectActivity.class, ActivityUtils.ANIMATE_BACK, true));
    }

    private void initPresenter() {
        mDeviceZigBeeTipPresenter = new DeviceZigBeeTipPresenter(this, this);
    }

    @Override
    public void setButtonText(boolean isGateway) {
        if (isGateway)
            btnNext.setText(R.string.confirm_add_subdevice_state);
        else
            btnNext.setText(R.string.confirm_add_gateway_state);
    }

    @Override
    public void initTipImageView() {

        mStatusLightAnimation = new AnimationDrawable();
        mStatusLightAnimation.addFrame(ContextCompat.getDrawable(this, R.mipmap.device_light_on), 250);
        mStatusLightAnimation.addFrame(ContextCompat.getDrawable(this, R.mipmap.device_light_off), 250);
        mStatusLightAnimation.setOneShot(false);
        mStatusLightImageView.setImageDrawable(mStatusLightAnimation);
        mStatusLightAnimation.start();
    }
}
