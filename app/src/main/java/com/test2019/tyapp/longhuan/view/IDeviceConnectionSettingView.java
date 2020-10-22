package com.test2019.tyapp.longhuan.view;

public interface IDeviceConnectionSettingView {

    void setWifiSSID(String ssId);
    void setWifiPass(String pass);
    String getWifiPass();
    String getWifiSsId();

    void showNoWifi();
    void show5gTip();
    void hide5gTip();
}
