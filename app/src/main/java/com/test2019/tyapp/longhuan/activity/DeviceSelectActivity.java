package com.test2019.tyapp.longhuan.activity;

import android.os.Bundle;
import androidx.annotation.Nullable;
import android.util.Log;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;

import com.test2019.tyapp.longhuan.R;
import com.test2019.tyapp.longhuan.activity.base.BaseActivity;
import com.test2019.tyapp.longhuan.adapter.DeviceTypeAdapter;
import com.test2019.tyapp.longhuan.presenter.DeviceSelectPresenter;
import com.test2019.tyapp.longhuan.utils.ActivityUtils;
import com.test2019.tyapp.longhuan.utils.ToastUtil;
import com.test2019.tyapp.longhuan.view.IDeviceSelectView;

public class DeviceSelectActivity extends BaseActivity implements IDeviceSelectView {

    private final String TAG = "DeviceSelectActivity";

    private final int NONE_SELECTED = -1;
    private GridView gView;

    private DeviceSelectPresenter mDeviceSelectPresenter;
    private int curDeviceType = -1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_select);
        Log.d(TAG, "onCreate");
        initView();
        initPresenter();
    }

    private void initView() {
        Log.d(TAG, "initView");
        gView = findViewById(R.id.grid_device_type);
        gView.setOnItemClickListener(((parent, view, position, id) -> {
            if (curDeviceType == position)
                curDeviceType = NONE_SELECTED;
            else
                curDeviceType = position;
            DeviceTypeAdapter adapter = (DeviceTypeAdapter) parent.getAdapter();
            adapter.selectDeviceType(curDeviceType);
        }));

        Button btnNext = findViewById(R.id.btn_device_select);
        btnNext.setOnClickListener((v)-> {
                if (curDeviceType != NONE_SELECTED)
                    mDeviceSelectPresenter.next(curDeviceType);
                else
                    ToastUtil.showToast(getApplicationContext(), "Please select device type");
        });
        ImageButton btnBack = findViewById(R.id.btn_back);
        btnBack.setOnClickListener(v -> ActivityUtils.gotoMainActivity(DeviceSelectActivity.this));
    }

    private void initPresenter() {
        mDeviceSelectPresenter = new DeviceSelectPresenter(this, this);
    }

    @Override
    public void LoadDeviceType(DeviceTypeAdapter adapter) {
        gView.setAdapter(adapter);
    }

    @Override
    public void onBackPressed() {
        ActivityUtils.gotoMainActivity(DeviceSelectActivity.this);
    }
}
