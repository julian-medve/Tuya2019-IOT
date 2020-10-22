package com.test2019.tyapp.longhuan.fragment.device.common;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.test2019.tyapp.longhuan.R;
import com.test2019.tyapp.longhuan.presenter.device.common.CommonPresenter;
import com.test2019.tyapp.longhuan.view.device.common.ICommonView;
import com.tuya.smart.sdk.bean.DeviceBean;

public class CommonDeviceFragment extends Fragment implements ICommonView {

    private final String TAG = "CommonDeviceFragment";

    private volatile static CommonDeviceFragment mSelfFragment;

    private CommonPresenter mPresenter;
    private TextView txtDeviceName;

    private TextView tv_devName, tv_devId, tv_devCategory, tv_gwType, tv_productId, tv_prodCategory, tv_iconUrl;

    public static Fragment newInstance() {
        if (mSelfFragment != null)
            mSelfFragment = null;
        synchronized (CommonDeviceFragment.class) {
            if (mSelfFragment == null) {
                mSelfFragment = new CommonDeviceFragment();
            }
        }
        return mSelfFragment;
    }

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_common, container, false);
        txtDeviceName = v.findViewById(R.id.txt_device_name);

        //======== my test =========//
        tv_devName = v.findViewById(R.id.tv_devName);
        tv_devId = v.findViewById(R.id.tv_devId);
        tv_devCategory = v.findViewById(R.id.tv_devCategory);
        tv_gwType = v.findViewById(R.id.tv_gwType);
        tv_productId = v.findViewById(R.id.tv_productId);
        tv_prodCategory = v.findViewById(R.id.tv_prodCategory);
        tv_iconUrl = v.findViewById(R.id.tv_iconUrl);

        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        initPresenter();
    }

    public void initPresenter() {
        mPresenter = new CommonPresenter(getActivity(), this, this);
    }

    @Override
    public void updateTitle(String devName) {
        txtDeviceName.setText(devName);
    }

    @Override
    public void initDeviceInfo(DeviceBean deviceBean) {
        tv_devName.setText(deviceBean.getName());
        tv_devId.setText(deviceBean.getDevId());
        tv_devCategory.setText(deviceBean.getCategory());
        tv_gwType.setText(deviceBean.getGwType());
        tv_productId.setText(deviceBean.getProductId());
        tv_prodCategory.setText(deviceBean.getProductBean().getCategory());
        tv_iconUrl.setText(deviceBean.getIconUrl());
    }

    @Override
    public void setDevImage(DeviceBean deviceBean) {

    }


}
