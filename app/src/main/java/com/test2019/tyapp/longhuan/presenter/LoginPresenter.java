package com.test2019.tyapp.longhuan.presenter;

import android.app.Activity;
import android.content.Context;
import android.os.Message;

import com.test2019.tyapp.longhuan.app.Constant;
import com.test2019.tyapp.longhuan.utils.ActivityUtils;
import com.test2019.tyapp.longhuan.utils.MessageUtil;
import com.test2019.tyapp.longhuan.view.ILoginView;
import com.tuya.smart.android.common.utils.ValidatorUtil;
import com.tuya.smart.android.mvp.bean.Result;
import com.tuya.smart.android.mvp.presenter.BasePresenter;
import com.tuya.smart.android.user.api.ILoginCallback;
import com.tuya.smart.android.user.bean.User;
import com.tuya.smart.home.sdk.TuyaHomeSdk;

public class LoginPresenter extends BasePresenter {

    private Activity mContext;
    private ILoginView mView;

    public static final int MSG_LOGIN_SUCCESS = 15;
    public static final int MSG_LOGIN_FAILURE = 16;

    public LoginPresenter(Context context, ILoginView view) {
        this.mContext = (Activity) context;
        this.mView = view;
    }

    public void login(String countryCode, String userName, String password) {

        if (!ValidatorUtil.isEmail(userName)) {
            TuyaHomeSdk.getUserInstance().loginWithPhonePassword(countryCode, userName, password, mLoginCallback);
        } else {
            TuyaHomeSdk.getUserInstance().loginWithEmail(countryCode, userName, password, mLoginCallback);
        }
    }

    @Override
    public boolean handleMessage(Message msg) {
        switch (msg.what) {
            case MSG_LOGIN_SUCCESS:
                mView.modelResult(msg.what, null);
                Constant.finishActivity();
                ActivityUtils.gotoMainActivity(mContext);
                break;
            case MSG_LOGIN_FAILURE:
                mView.modelResult(msg.what, (Result) msg.obj);
                break;
            default:
                break;
        }

        return super.handleMessage(msg);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private ILoginCallback mLoginCallback = new ILoginCallback() {
        @Override
        public void onSuccess(User user) {
            mHandler.sendEmptyMessage(MSG_LOGIN_SUCCESS);
        }

        @Override
        public void onError(String s, String s1) {
            Message msg = MessageUtil.getCallFailMessage(MSG_LOGIN_FAILURE, s, s1);
            mHandler.sendMessage(msg);
        }
    };
}
