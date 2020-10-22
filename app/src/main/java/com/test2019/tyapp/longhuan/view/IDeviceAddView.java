package com.test2019.tyapp.longhuan.view;

public interface IDeviceAddView {

    void showSuccessPage();

    void showFailurePage();

    void showConnectPage();

    void setConnectProgress(float progress, int animationDuration);

    void showNetWorkFailurePage();

    void showBindDeviceSuccessTip();

    void showDeviceFindTip(String gwId);

    void showConfigSuccessTip();

    void showBindDeviceSuccessFinalTip();

    void setAddDeviceName(String name);

    void showMainPage();

    void hideMainPage();

    void showSubPage();

    void hideSubPage();

    void setDevId(String devId);

}

