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
import com.test2019.tyapp.longhuan.presenter.DeviceTipPresent;
import com.test2019.tyapp.longhuan.utils.ActivityUtils;
import com.test2019.tyapp.longhuan.view.IDeviceTipView;

public class DeviceTipActivity extends BaseActivity implements IDeviceTipView {

    private final String TAG = "DeviceTipActivity";

    private DeviceTipPresent mDeviceTipPresenter;
    private AnimationDrawable mStatusLightAnimation;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_tip);

        initView();
        initPresenter();
    }

    private void initView() {
        Button btnNext = findViewById(R.id.btn_device_tip);
        initTipImageView();
        btnNext.setOnClickListener((v)->mDeviceTipPresenter.next());
        ImageButton btnBack = findViewById(R.id.btn_back);
        btnBack.setOnClickListener((v)->ActivityUtils.gotoActivity(DeviceTipActivity.this, DeviceSelectActivity.class, ActivityUtils.ANIMATE_BACK, true));
    }

    private void initTipImageView() {
        ImageView mStatusLightImageView = (ImageView) findViewById(R.id.img_device_tip);
        mStatusLightAnimation = new AnimationDrawable();
        mStatusLightAnimation.addFrame(ContextCompat.getDrawable(this, R.mipmap.device_light_on), 250);
        mStatusLightAnimation.addFrame(ContextCompat.getDrawable(this, R.mipmap.device_light_off), 250);
        mStatusLightAnimation.setOneShot(false);
        mStatusLightImageView.setImageDrawable(mStatusLightAnimation);
        mStatusLightAnimation.start();
    }

    private void initPresenter() {
        mDeviceTipPresenter = new DeviceTipPresent(this, this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mStatusLightAnimation != null && mStatusLightAnimation.isRunning()) {
            mStatusLightAnimation.stop();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mStatusLightAnimation != null && !mStatusLightAnimation.isRunning()) {
            mStatusLightAnimation.start();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mStatusLightAnimation != null && mStatusLightAnimation.isRunning()) {
            mStatusLightAnimation.stop();
        }
    }

    @Override
    public void onBackPressed() {
        ActivityUtils.gotoActivity(this, DeviceSelectActivity.class, ActivityUtils.ANIMATE_BACK, true);
    }
}
