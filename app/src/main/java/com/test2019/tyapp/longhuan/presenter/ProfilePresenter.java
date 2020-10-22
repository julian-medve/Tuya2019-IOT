package com.test2019.tyapp.longhuan.presenter;

import android.app.Activity;
import android.os.CountDownTimer;
import android.os.Message;
import android.text.TextUtils;

import com.test2019.tyapp.longhuan.R;
import com.test2019.tyapp.longhuan.utils.DialogUtil;
import com.test2019.tyapp.longhuan.utils.LoginHelper;
import com.test2019.tyapp.longhuan.utils.MessageUtil;
import com.test2019.tyapp.longhuan.utils.ProgressUtil;
import com.test2019.tyapp.longhuan.utils.ToastUtil;
import com.test2019.tyapp.longhuan.view.IProfileView;
import com.tuya.smart.android.mvp.bean.Result;
import com.tuya.smart.android.mvp.presenter.BasePresenter;
import com.tuya.smart.android.user.api.IReNickNameCallback;
import com.tuya.smart.android.user.api.IResetPasswordCallback;
import com.tuya.smart.android.user.api.IValidateCallback;
import com.tuya.smart.android.user.bean.User;
import com.tuya.smart.home.sdk.TuyaHomeSdk;
import com.tuya.smart.sdk.api.IResultCallback;

public class ProfilePresenter extends BasePresenter {

    private final String TAG = "ProfilePresenter";

    private static final int PLATFORM_EMAIL = 0;
    private static final int PLATFORM_PHONE = 1;

    private static final int GET_VALIDATE_CODE_PERIOD = 60 * 1000;
    public static final int MSG_SEND_VALIDATE_CODE_SUCCESS = 12;
    public static final int MSG_SEND_VALIDATE_CODE_ERROR = 13;
    private static final int MSG_RESET_PASSWORD_SUCC = 14;
    public static final int MSG_RESET_PASSWORD_FAIL = 15;

    private Activity mContext;
    private IProfileView mView;

    private User user;
    private int accountType;
    private boolean mSend;

    private CountDownTimer mCountDownTimer;

    private IResultCallback iResultCallback = new IResultCallback() {
        @Override
        public void onError(String s, String s1) {
            if (ProgressUtil.isShowLoading())
                ProgressUtil.hideLoading();
            getValidateCodeFail(s, s1);
        }

        @Override
        public void onSuccess() {
            if (ProgressUtil.isShowLoading())
                ProgressUtil.hideLoading();
            mHandler.sendEmptyMessage(MSG_SEND_VALIDATE_CODE_SUCCESS);
        }
    };

    private IResetPasswordCallback mIResetPasswordCallback = new IResetPasswordCallback() {
        @Override
        public void onSuccess() {
            if (ProgressUtil.isShowLoading())
                ProgressUtil.hideLoading();
            mHandler.sendEmptyMessage(MSG_RESET_PASSWORD_SUCC);
        }

        @Override
        public void onError(String errorCode, String errorMsg) {
            if (ProgressUtil.isShowLoading())
                ProgressUtil.hideLoading();
            Message msg = MessageUtil.getCallFailMessage(MSG_RESET_PASSWORD_FAIL, errorCode, errorMsg);
            mHandler.sendMessage(msg);
        }
    };

    private IValidateCallback mIValidateCallback = new IValidateCallback() {
        @Override
        public void onSuccess() {
            mHandler.sendEmptyMessage(MSG_SEND_VALIDATE_CODE_SUCCESS);
        }

        @Override
        public void onError(String s, String s1) {
            getValidateCodeFail(s, s1);
        }
    };

    public ProfilePresenter(Activity context, IProfileView view) {
        this.mContext = context;
        this.mView = view;
        user = TuyaHomeSdk.getUserInstance().getUser();
        mView.setUserName(user.getNickName());
        if (TextUtils.isEmpty(user.getEmail())) {
            mView.setUserInfo(user.getMobile());
            accountType = PLATFORM_PHONE;
        } else if (TextUtils.isEmpty(user.getMobile())) {
            mView.setUserInfo(user.getEmail());
            accountType = PLATFORM_EMAIL;
        }
    }

    public void updateUserInfo() {

        String strInfo = mView.getUserInfo();
        String strPass = mView.getUserPass();
        String strCode = mView.getVerificationCode();

        if (strInfo.isEmpty()) {
            ToastUtil.showToast(mContext, "User Mobile or Email is empty");
            return;
        }

        if (strPass.isEmpty() || strPass.length() < 6) {
            ToastUtil.showToast(mContext, "Password length is longer than 6 letters");
            return;
        }

        if (strCode.isEmpty()) {
            ToastUtil.showToast(mContext, "You get Verification Code at first");
            return;
        }

        ProgressUtil.showLoading(mContext, "Updating...");
        resetPassword();
    }

    private void resetPassword() {
        User user = TuyaHomeSdk.getUserInstance().getUser();

        switch (accountType) {
            case PLATFORM_EMAIL:
                TuyaHomeSdk.getUserInstance().resetEmailPassword(user.getPhoneCode(), mView.getUserInfo(), mView.getVerificationCode(), mView.getUserPass(), mIResetPasswordCallback);
                break;
            case PLATFORM_PHONE:
                TuyaHomeSdk.getUserInstance().resetPhonePassword(user.getPhoneCode(), mView.getUserInfo(), mView.getVerificationCode(), mView.getUserPass(), mIResetPasswordCallback);
                break;
        }
    }

    public void reNickName(final String nickName) {
        ProgressUtil.showLoading(mContext, "Changing...");
        TuyaHomeSdk.getUserInstance().reRickName(nickName, new IReNickNameCallback() {
            @Override
            public void onSuccess() {
                ProgressUtil.hideLoading();
                mView.setUserName(nickName);
//                resultSuccess(RENAME_NICKNAME_SUCCESS, nickName);
            }

            @Override
            public void onError(String s, String s1) {
                ProgressUtil.hideLoading();
                ToastUtil.showToast(mContext, "Error! Please try it again");
//                resultError(RENAME_NICKNAME_ERROR, s, s1);
            }
        });
    }

    public void getValidateCode() {
        User user = TuyaHomeSdk.getUserInstance().getUser();
        mSend = true;
        buildCountDown();

        if (mView == null) {
            return;
        }
        String countryCode = user.getPhoneCode();
        String userName = mView.getUserInfo();

        switch (accountType) {
            case PLATFORM_EMAIL:
                TuyaHomeSdk.getUserInstance().getEmailValidateCode(countryCode, userName, mIValidateCallback);
                break;

            case PLATFORM_PHONE:
                TuyaHomeSdk.getUserInstance().getValidateCode(countryCode, userName, mIValidateCallback);
                break;
        }

    }

    private void getValidateCodeFail(String errorCode, String errorMsg) {
        Message msg = MessageUtil.getCallFailMessage(MSG_SEND_VALIDATE_CODE_ERROR, errorCode, errorMsg);
        mHandler.sendMessage(msg);
        mSend = false;
    }

    private void buildCountDown() {
        mCountDownTimer = new ProfilePresenter.Countdown(GET_VALIDATE_CODE_PERIOD, 1000);
        mCountDownTimer.start();
        mView.disableGetValidateCode();
    }

    private class Countdown extends CountDownTimer {

        private Countdown(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long millisUntilFinished) {
            mView.setCountdown((int) (millisUntilFinished / 1000));
        }

        @Override
        public void onFinish() {
            mView.enableGetValidateCode();
            mSend = false;
            mView.checkValidateCode();
        }
    }

    public boolean isSended() {
        return mSend;
    }

    @Override
    public boolean handleMessage(Message msg) {
        switch (msg.what) {
            case MSG_SEND_VALIDATE_CODE_SUCCESS:
                mView.modelResult(msg.what, null);
                break;
            case MSG_SEND_VALIDATE_CODE_ERROR:
            case MSG_RESET_PASSWORD_FAIL:
                mView.modelResult(msg.what, (Result) msg.obj);
                break;
            case MSG_RESET_PASSWORD_SUCC:
                DialogUtil.simpleSmartDialog(mContext, R.string.modify_password_success, ((dialog, which) -> LoginHelper.reLogin(mContext, false)));
                break;
        }
        return super.handleMessage(msg);
    }
}
