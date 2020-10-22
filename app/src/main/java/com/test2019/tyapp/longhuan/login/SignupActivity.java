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
import com.test2019.tyapp.longhuan.presenter.SignupPresenter;
import com.test2019.tyapp.longhuan.utils.ToastUtil;
import com.test2019.tyapp.longhuan.view.ISignupView;
import com.tuya.smart.android.common.utils.ValidatorUtil;
import com.tuya.smart.android.mvp.bean.Result;

public class SignupActivity extends BaseActivity implements View.OnClickListener, ISignupView, TextWatcher {

    private final String TAG = "SignupActivity";

    private final int PLATFORM_PHONE = 0x01;
    private final int PLATFORM_EMAIL = 0x10;

    private Button mBtnSignup;
    private Button mBtnGetCode;

    private ImageButton mBtnPassShow;
    private ImageButton mBtnConfirmPassShow;

    private EditText mTextCountyCode;
    private EditText mTextUser;
    private EditText mTextVerificationCode;
    private EditText mTextPass;
    private EditText mTextConfirmPass;

    private boolean isPassShow;
    private boolean isConfirmShow;
    private int mAccountType;

    private SignupPresenter mSignupPresenter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        initView();
        initData();
        initPresenter();
    }

    private void initView(){
        mTextCountyCode = findViewById(R.id.signup_country_code);
        mTextUser = findViewById(R.id.signup_user_info);
        mTextVerificationCode = findViewById(R.id.signup_verification_code);
        mTextPass = findViewById(R.id.signup_user_pass);
        mTextConfirmPass = findViewById(R.id.signup_confirm_pass);

        mTextCountyCode.addTextChangedListener(this);
        mTextUser.addTextChangedListener(this);
        mTextVerificationCode.addTextChangedListener(this);
        mTextPass.addTextChangedListener(this);
        mTextConfirmPass.addTextChangedListener(this);

        mBtnSignup = findViewById(R.id.btn_signup);
        mBtnGetCode = findViewById(R.id.btn_get_code);
        mBtnPassShow = findViewById(R.id.signup_password_switch);
        mBtnConfirmPassShow = findViewById(R.id.signup_confirm_password_switch);

        mBtnGetCode.setEnabled(false);
        mBtnSignup.setEnabled(false);

        mBtnSignup.setOnClickListener(this);
        mBtnGetCode.setOnClickListener(this);
        mBtnPassShow.setOnClickListener(this);
        mBtnConfirmPassShow.setOnClickListener(this);
    }

    private void initData(){
        isPassShow = false;
        isConfirmShow = false;

        showPassword(false);
        showConfirmPassword(false);
    }

    private void initPresenter() {
        mSignupPresenter = new SignupPresenter(this, this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        switch (id) {
            case R.id.btn_signup:
                signup();
                break;
            case R.id.btn_get_code:
                getVerificationCode();
                break;
            case R.id.signup_password_switch:
                isPassShow = !isPassShow;
                showPassword(isPassShow);
                break;
            case R.id.signup_confirm_password_switch:
                isConfirmShow = !isConfirmShow;
                showConfirmPassword(isConfirmShow);
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

    private void showConfirmPassword(boolean show){
        if (show) {
            mBtnConfirmPassShow.setImageResource(R.mipmap.password_on);
            mTextConfirmPass.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
        }
        else {
            mBtnConfirmPassShow.setImageResource(R.mipmap.password_off);
            mTextConfirmPass.setTransformationMethod(PasswordTransformationMethod.getInstance());
        }
    }

    public void signup() {

        String password = mTextPass.getText().toString();
        String confirm = mTextConfirmPass.getText().toString();

        if (confirm.equals(password))
            mSignupPresenter.confirm();
        else {
            ToastUtil.showToast(this, "ConfirmPassword is not matched");
        }
    }

    public void getVerificationCode() {
        hideIMM();
        disableGetValidateCode();
        mSignupPresenter.getValidateCode();
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        String userName = mTextUser.getText().toString();
        if (TextUtils.isEmpty(userName)) {
            mBtnGetCode.setEnabled(false);
        } else {
            mBtnGetCode.setEnabled(true);
            if (ValidatorUtil.isEmail(userName)) {
                // e-mail
                mAccountType = PLATFORM_EMAIL;
                mBtnSignup.setEnabled(true);
            } else {
                // phone
                try {
                    Long.valueOf(userName);
                    mAccountType = PLATFORM_PHONE;
                    mBtnSignup.setEnabled(true);
                } catch (Exception e) {
                    mBtnSignup.setEnabled(false);
                }
            }
        }
    }

    @Override
    public void disableGetValidateCode() {
        if (mBtnGetCode.isEnabled()) {
            mBtnGetCode.setEnabled(false);
        }
    }

    @Override
    public int getSignupType() {
        return mAccountType;
    }

    @Override
    public String getCountryCode() {
        return mTextCountyCode.getText().toString();
    }

    @Override
    public String getUserInfo() {
        return mTextUser.getText().toString();
    }

    @Override
    public String getCode() {
        return mTextVerificationCode.getText().toString();
    }

    @Override
    public String getPassword() {
        return mTextPass.getText().toString();
    }

    @Override
    public String getConfirmPass() {
        return mTextConfirmPass.getText().toString();
    }

    @Override
    public void setCountdown(int sec) {
        mBtnGetCode.setText(getString(R.string.reget_validation_second, sec));
    }

    @Override
    public void enableGetValidateCode() {
        mBtnGetCode.setText(R.string.login_reget_code);
    }

    @Override
    public void checkValidateCode() {
        if (!mSignupPresenter.isSended()) {
            resetGetValidateCode();
        }
    }

    private void resetGetValidateCode() {
        if (!mBtnGetCode.isEnabled()) {
            mBtnGetCode.setEnabled(true);
        }
    }

    @Override
    public void modelResult(int what, Result result) {
        switch (what) {
            case SignupPresenter.MSG_REGISTER_FAIL:
            case SignupPresenter.MSG_SEND_VALIDATE_CODE_ERROR:
                ToastUtil.shortToast(this, result.error);
                break;

            case SignupPresenter.MSG_SEND_VALIDATE_CODE_SUCCESS:
                disableGetValidateCode();
                mTextVerificationCode.requestFocus();
                break;
        }
    }

    @Override
    public boolean needLogin() {
        return false;
    }
}
