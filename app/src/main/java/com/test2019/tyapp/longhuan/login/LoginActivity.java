package com.test2019.tyapp.longhuan.login;

import android.os.Bundle;
import androidx.annotation.Nullable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.test2019.tyapp.longhuan.R;
import com.test2019.tyapp.longhuan.activity.base.BaseActivity;
import com.test2019.tyapp.longhuan.presenter.LoginPresenter;
import com.test2019.tyapp.longhuan.utils.ActivityUtils;
import com.test2019.tyapp.longhuan.utils.ProgressUtil;
import com.test2019.tyapp.longhuan.utils.ToastUtil;
import com.test2019.tyapp.longhuan.view.ILoginView;
import com.tuya.smart.android.common.utils.ValidatorUtil;
import com.tuya.smart.android.mvp.bean.Result;

public class LoginActivity extends BaseActivity implements View.OnClickListener, ILoginView, TextWatcher {

    private final String TAG = "LoginActivity";

    private Button mBtnSignup;
    private Button mBtnLogin;
    private ImageButton mBtnPassShow;
    private EditText mTextCountryCode;
    private EditText mTextUser;
    private EditText mTextPass;

    private LoginPresenter mLoginPresenter;

    private boolean isPassShow;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initView();
        initData();
        initPresenter();
    }

    private void initView(){

        mTextCountryCode = findViewById(R.id.login_country_code);
        mTextUser = findViewById(R.id.login_user_info);
        mTextPass = findViewById(R.id.login_user_pass);
        mBtnLogin = findViewById(R.id.btn_login);
        mBtnSignup = findViewById(R.id.go_to_signup);
        mBtnPassShow = findViewById(R.id.login_password_switch);

//        mTextCountryCode.addTextChangedListener(this);
//        mTextUser.addTextChangedListener(this);
//        mTextPass.addTextChangedListener(this);

        mBtnLogin.setOnClickListener(this);
        mBtnSignup.setOnClickListener(this);
        mBtnPassShow.setOnClickListener(this);
    }

    public void initData(){
        isPassShow = false;
        showPassword(false);
    }

    private void initPresenter() {
        mLoginPresenter = new LoginPresenter(this, this);
    }
    @Override
    public void onClick(View v) {
        int id = v.getId();

        switch (id) {
            case R.id.btn_login:
                login();
                break;
            case R.id.go_to_signup:
                ActivityUtils.gotoActivity(this, SignupActivity.class, ActivityUtils.ANIMATE_FORWARD, false);
                break;
            case R.id.login_password_switch:
                isPassShow = !isPassShow;
                showPassword(isPassShow);
                break;
        }
    }

    private void showPassword(boolean show){
        if (show) {
            mBtnPassShow.setImageResource(R.mipmap.password_on);
            mTextPass.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
        }
        else {
            mBtnPassShow.setImageResource(R.mipmap.password_off);
            mTextPass.setTransformationMethod(PasswordTransformationMethod.getInstance());
        }
    }

    @Override
    public void modelResult(int what, Result result) {
        switch (what) {
            case LoginPresenter.MSG_LOGIN_SUCCESS:
                ProgressUtil.hideLoading();
                break;
            case LoginPresenter.MSG_LOGIN_FAILURE:
                ProgressUtil.hideLoading();
                ToastUtil.shortToast(this, result.error);
                enableLogin();
                break;
            default:
                break;
        }
    }

    public void enableLogin() {
        if (!mBtnLogin.isEnabled()) mBtnLogin.setEnabled(true);
    }

    public void disableLogin() {
        if (mBtnLogin.isEnabled()) mBtnLogin.setEnabled(false);
    }

    public boolean needLogin() {
        return false;
    }

    public void login(){
        if (mBtnLogin.isEnabled()) {
            String userName = mTextUser.getText().toString();
            if (!ValidatorUtil.isEmail(userName) && mTextCountryCode.getText().toString().contains("86") && mTextUser.getText().length() != 11) {
                ToastUtil.shortToast(LoginActivity.this, getString(R.string.phone_num_error));
                return;
            }
            hideIMM();
            disableLogin();
            ProgressUtil.showLoading(LoginActivity.this, R.string.logining);
            mLoginPresenter.login(mTextCountryCode.getText().toString(), userName, mTextPass.getText().toString());
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        String countryCode = mTextCountryCode.getText().toString();
        String userName = mTextUser.getText().toString();
        String password = mTextPass.getText().toString();
        if (TextUtils.isEmpty(userName) || TextUtils.isEmpty(password) || TextUtils.isEmpty(countryCode)) {
            disableLogin();
        } else {
            if (ValidatorUtil.isEmail(userName)) {
                // e-mail
                enableLogin();
            } else {
                // phone
                try {
                    Long.valueOf(userName);
                    enableLogin();
                } catch (Exception e) {
                    disableLogin();
                }
            }
        }
    }
}
