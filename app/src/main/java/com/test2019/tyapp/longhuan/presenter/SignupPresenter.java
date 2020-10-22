package com.test2019.tyapp.longhuan.presenter;

import android.app.Activity;
import android.os.CountDownTimer;
import android.os.Message;

import com.test2019.tyapp.longhuan.R;
import com.test2019.tyapp.longhuan.utils.ActivityUtils;
import com.test2019.tyapp.longhuan.utils.DialogUtil;
import com.test2019.tyapp.longhuan.utils.LoginHelper;
import com.test2019.tyapp.longhuan.utils.MessageUtil;
import com.test2019.tyapp.longhuan.view.ISignupView;
import com.tuya.smart.android.mvp.bean.Result;
import com.tuya.smart.android.mvp.presenter.BasePresenter;
import com.tuya.smart.android.user.api.IRegisterCallback;
import com.tuya.smart.android.user.api.IValidateCallback;
import com.tuya.smart.android.user.bean.User;
import com.tuya.smart.home.sdk.TuyaHomeSdk;
import com.tuya.smart.sdk.api.IResultCallback;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SignupPresenter extends BasePresenter {

    private Activity mContext;
    private ISignupView mView;

    private final int PLATFORM_PHONE = 0x01;
    private final int PLATFORM_EMAIL = 0x10;

    public static final int MSG_SEND_VALIDATE_CODE_SUCCESS = 12;
    public static final int MSG_SEND_VALIDATE_CODE_ERROR = 13;
    public static final int MSG_REGISTER_SUCC = 16;
    public static final int MSG_REGISTER_FAIL = 17;
    public static final int MSG_LOGIN_FAIL = 18;

    private static final int GET_VALIDATE_CODE_PERIOD = 60 * 1000;

    private boolean mSend;
    private CountDownTimer mCountDownTimer;

    private IResultCallback iResultCallback = new IResultCallback() {
        @Override
        public void onError(String s, String s1) {
            getValidateCodeFail(s, s1);
        }

        @Override
        public void onSuccess() {
            mHandler.sendEmptyMessage(MSG_SEND_VALIDATE_CODE_SUCCESS);
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

//    private IResetPasswordCallback mIResetPasswordCallback = new IResetPasswordCallback() {
//        @Override
//        public void onSuccess() {
//            mHandler.sendEmptyMessage(MSG_RESET_PASSWORD_SUCC);
//        }
//
//        @Override
//        public void onError(String errorCode, String errorMsg) {
//            Message msg = MessageUtil.getCallFailMessage(MSG_RESET_PASSWORD_FAIL, errorCode, errorMsg);
//            mHandler.sendMessage(msg);
//        }
//    };

    private IRegisterCallback mIRegisterCallback = new IRegisterCallback() {
        @Override
        public void onSuccess(User user) {
            mHandler.sendEmptyMessage(MSG_REGISTER_SUCC);
        }

        @Override
        public void onError(String errorCode, String errorMsg) {
            Message msg = MessageUtil.getCallFailMessage(MSG_REGISTER_FAIL, errorCode, errorMsg);
            mHandler.sendMessage(msg);
        }
    };

    public SignupPresenter(Activity context, ISignupView view) {
        this.mContext = context;
        this.mView = view;
    }

    public void getValidateCode() {
        mSend = true;
        buildCountDown();

        if (mView == null) {
            return;
        }
        String countryCode = mView.getCountryCode();
        String userName = mView.getUserInfo();

        switch (mView.getSignupType()) {
            case PLATFORM_EMAIL:
                TuyaHomeSdk.getUserInstance().getRegisterEmailValidateCode(countryCode,userName,iResultCallback);
                break;

            case PLATFORM_PHONE:
                TuyaHomeSdk.getUserInstance().getValidateCode(countryCode, userName, mIValidateCallback);
                break;
        }

    }

    private void buildCountDown() {
        mCountDownTimer = new Countdown(GET_VALIDATE_CODE_PERIOD, 1000);
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

    private void SignupSuccess() {
        LoginHelper.afterLogin();
        ActivityUtils.back(mContext);
    }

    @Override
    public boolean handleMessage(Message msg) {
        switch (msg.what) {
            case MSG_SEND_VALIDATE_CODE_SUCCESS:
                mView.modelResult(msg.what, null);
                break;
            case MSG_SEND_VALIDATE_CODE_ERROR:
            case MSG_REGISTER_FAIL:
                mView.modelResult(msg.what, (Result) msg.obj);
                break;

            case MSG_REGISTER_SUCC:
                SignupSuccess();
                break;
        }
        return super.handleMessage(msg);
    }

    private void getValidateCodeFail(String errorCode, String errorMsg) {
        Message msg = MessageUtil.getCallFailMessage(MSG_SEND_VALIDATE_CODE_ERROR, errorCode, errorMsg);
        mHandler.sendMessage(msg);
        mSend = false;
    }

    private void register() {
        String countryCode = mView.getCountryCode();
        String userInfo = mView.getUserInfo();
        String verificationCode = mView.getCode();

        switch (mView.getSignupType()) {
            case PLATFORM_EMAIL:
                TuyaHomeSdk.getUserInstance().registerAccountWithEmail(countryCode, userInfo, mView.getPassword(), verificationCode,mIRegisterCallback);
                break;

            case PLATFORM_PHONE:
                TuyaHomeSdk.getUserInstance().registerAccountWithPhone(countryCode, userInfo, mView.getPassword(),verificationCode, mIRegisterCallback);
                break;
        }
    }

    public void confirm() {
        // Increase password rule judgment (consistent with the developer background, 6~20 characters, letters/numbers/symbols)
        if (mView.getPassword().length() < 6 || mView.getPassword().length() > 20) {
            DialogUtil.simpleSmartDialog(mContext, mContext.getString(R.string.enter_keyword_tip), null);
            return;
        } else {
            final Pattern PASS_PATTERN = Pattern.compile("^[A-Za-z\\d!@#$%*&_\\-.,:;+=\\[\\]{}~()^]{6,20}$");
            Matcher matcher = PASS_PATTERN.matcher(mView.getPassword());
            if (!matcher.matches()) {
                DialogUtil.simpleSmartDialog(mContext, mContext.getString(R.string.enter_keyword_tip), null);
                return;
            }
        }

        register();
    }
}
