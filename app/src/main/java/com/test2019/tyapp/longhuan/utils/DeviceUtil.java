package com.test2019.tyapp.longhuan.utils;

import com.tuya.smart.home.sdk.TuyaHomeSdk;
import com.tuya.smart.sdk.bean.DeviceBean;

public class DeviceUtil {
    public static boolean isOnline(String devId){
        //===== Check for device is online ================//
        DeviceBean bean = TuyaHomeSdk.getDataInstance().getDeviceBean(devId);
        return bean.getIsOnline();
    }
}
