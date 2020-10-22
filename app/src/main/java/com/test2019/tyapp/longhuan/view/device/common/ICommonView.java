package com.test2019.tyapp.longhuan.view.device.common;

import com.tuya.smart.sdk.bean.DeviceBean;

public interface ICommonView {
    void updateTitle(String devName);

    void initDeviceInfo(DeviceBean deviceBean);
    void setDevImage(DeviceBean deviceBean);
}
