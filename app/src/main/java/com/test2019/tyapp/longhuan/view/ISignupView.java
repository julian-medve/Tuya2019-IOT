package com.test2019.tyapp.longhuan.view;


import com.tuya.smart.android.mvp.bean.Result;

public interface ISignupView {

    int getSignupType();

    String getCountryCode();
    String getUserInfo();
    String getCode();
    String getPassword();
    String getConfirmPass();

    void setCountdown(int sec);
    void enableGetValidateCode();
    void disableGetValidateCode();
    void checkValidateCode();

    void modelResult(int what, Result result);


}
