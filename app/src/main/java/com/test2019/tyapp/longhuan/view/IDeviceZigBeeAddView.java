package com.test2019.tyapp.longhuan.view;

public interface IDeviceZigBeeAddView {

    void showSpinKitView(int status);
    void showProgressBar(int status);

    void setStatusText(String status);
    void setProgressBar(int value);

    void showSuccessButton(boolean status);

}
