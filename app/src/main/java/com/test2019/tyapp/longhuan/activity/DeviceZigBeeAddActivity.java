package com.test2019.tyapp.longhuan.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.github.ybq.android.spinkit.SpinKitView;
import com.test2019.tyapp.longhuan.R;
import com.test2019.tyapp.longhuan.activity.base.BaseActivity;
import com.test2019.tyapp.longhuan.presenter.DeviceZigBeeAddPresenter;
import com.test2019.tyapp.longhuan.utils.ViewUtils;
import com.test2019.tyapp.longhuan.view.IDeviceZigBeeAddView;
import com.test2019.tyapp.longhuan.widget.CircularProgressBar;

public class DeviceZigBeeAddActivity extends BaseActivity implements IDeviceZigBeeAddView {
    private static final String TAG = "DeviceZigBeeAddActivity";

    private TextView txtGatewayStatus;
    private SpinKitView spinKitView;
    private CircularProgressBar progressBar;
    private Button btnFinish;
    private Button btnRetry;

    private DeviceZigBeeAddPresenter mPresenter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_device_zigbee_add);
        initView();
        initPresenter();
    }

    private void initView() {
        spinKitView = findViewById(R.id.spin_kit_gateway);
        txtGatewayStatus = findViewById(R.id.txt_gateway_status);
        progressBar = findViewById(R.id.gateway_progressbar);
        progressBar.setProgress(0);
        txtGatewayStatus.setText("");

        int color = ViewUtils.getColor(this, R.color.text_light_blue);
        spinKitView.setColor(color);
        spinKitView.setVisibility(View.INVISIBLE);
        progressBar.setProgressColor(color);
        progressBar.setProgressWidth(3);
        progressBar.setTextColor(color);

        btnFinish = findViewById(R.id.btn_zigbee_finish);
        btnFinish.setOnClickListener((v)->mPresenter.onClickFinish());

        btnRetry = findViewById(R.id.btn_zigbee_retry);
        btnRetry.setOnClickListener((v)->mPresenter.onBack());
    }

    private void initPresenter() {
        mPresenter = new DeviceZigBeeAddPresenter(this, this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mPresenter.onDestroy();
    }

    @Override
    public void showSpinKitView(int status) {
        switch (status) {
            case DeviceZigBeeAddPresenter.VISIBLE:
                spinKitView.setVisibility(View.VISIBLE);
                break;
            case DeviceZigBeeAddPresenter.INVISIBLE:
                spinKitView.setVisibility(View.INVISIBLE);
                break;
            case DeviceZigBeeAddPresenter.GONE:
                spinKitView.setVisibility(View.GONE);
                break;
        }
    }

    @Override
    public void showProgressBar(int status) {
        switch (status) {
            case DeviceZigBeeAddPresenter.VISIBLE:
                progressBar.setVisibility(View.VISIBLE);
                break;
            case DeviceZigBeeAddPresenter.INVISIBLE:
                progressBar.setVisibility(View.INVISIBLE);
                break;
            case DeviceZigBeeAddPresenter.GONE:
                progressBar.setVisibility(View.GONE);
                break;
        }
    }

    @Override
    public void setStatusText(String status) {
        txtGatewayStatus.setText(status);
    }

    @Override
    public void setProgressBar(int value) {
        progressBar.setProgress(value);
    }

    @Override
    public void showSuccessButton(boolean status) {
        if (status) {
            btnFinish.setVisibility(View.VISIBLE);
            btnRetry.setVisibility(View.GONE);
        } else {
            btnFinish.setVisibility(View.GONE);
            btnRetry.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onBackPressed() {
        mPresenter.onBack();
    }
}
