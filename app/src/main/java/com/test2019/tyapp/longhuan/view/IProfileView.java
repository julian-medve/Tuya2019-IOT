package com.test2019.tyapp.longhuan.view;

import com.tuya.smart.android.mvp.bean.Result;

public interface IProfileView {

    String getUserInfo();

    String getUserPass();

    String getVerificationCode();

    void setUserName(String nickname);

    void setUserInfo(String email);

    void disableGetValidateCode();

    void setCountdown(int sec);

    void enableGetValidateCode();

    void checkValidateCode();

    void modelResult(int what, Result result);
}
