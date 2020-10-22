package com.test2019.tyapp.longhuan.activity;

import android.app.AlertDialog;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.test2019.tyapp.longhuan.R;
import com.test2019.tyapp.longhuan.activity.base.BaseActivity;
import com.test2019.tyapp.longhuan.presenter.ProfilePresenter;
import com.test2019.tyapp.longhuan.utils.ActivityUtils;
import com.test2019.tyapp.longhuan.utils.ToastUtil;
import com.test2019.tyapp.longhuan.view.IProfileView;
import com.tuya.smart.android.mvp.bean.Result;

public class ProfileSettingActivity extends BaseActivity implements IProfileView {

    private final String TAG = "ProfileSettingActivity";

    private ProfilePresenter mPresenter;

    private Button btnUserName;
    private EditText txtUserInfo;
    private EditText txtUserPass;
    private EditText txtGetCode;

    private ImageButton btnPassSwitch;
    private Button btnGet;
    private boolean isPassShow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        initView();
        initData();
        initPresenter();
    }

    private void initView() {

        btnUserName = findViewById(R.id.btn_profile_nickname);
        txtUserInfo = findViewById(R.id.profile_user_info);
        txtUserPass = findViewById(R.id.profile_user_pass);
        txtGetCode = findViewById(R.id.profile_verification_code);

        btnUserName.setOnClickListener((v)->{
            AlertDialog.Builder builder = new AlertDialog.Builder(ProfileSettingActivity.this);
            builder.setTitle("Re - NickName");
            // Set up the input
            LayoutInflater inflater = getLayoutInflater();
            View dialogLayout = inflater.inflate(R.layout.dialog_renick, null);
            builder.setView(dialogLayout);

            final EditText txtNiceName = dialogLayout.findViewById(R.id.txt_nick_name);
            // Set up the buttons
            builder.setPositiveButton("Rename", ((dialog, which) -> {
                String nickName = txtNiceName.getText().toString();
                if (nickName.isEmpty()) {
                    ToastUtil.showToast(ProfileSettingActivity.this, "NickName can not be empty");
                    return;
                }
                mPresenter.reNickName(nickName);
                dialog.cancel();
            }));
            builder.setNegativeButton("Cancel", ((dialog, which) -> dialog.cancel()));
            builder.show();
        });

        btnPassSwitch = findViewById(R.id.profile_password_switch);
        btnPassSwitch.setOnClickListener((v -> {
            isPassShow = !isPassShow;
            showPassword(isPassShow);
        }));

        btnGet = findViewById(R.id.btn_get_code);
        btnGet.setOnClickListener((v -> getCode()));

        Button btnUpdate = findViewById(R.id.btn_profile_update);
        btnUpdate.setOnClickListener((v -> mPresenter.updateUserInfo()));

        ImageButton btnBack = findViewById(R.id.btn_back);
        btnBack.setOnClickListener((v -> onBackPressed()));
    }

    private void initData() {
        isPassShow = false;
        showPassword(false);
    }

    private void initPresenter() {
        mPresenter = new ProfilePresenter(this, this);
    }

    private void showPassword(boolean show){
        if (show) {
            btnPassSwitch.setImageResource(R.mipmap.password_on);
            txtUserPass.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
        }
        else {
            btnPassSwitch.setImageResource(R.mipmap.password_off);
            txtUserPass.setTransformationMethod(PasswordTransformationMethod.getInstance());
        }
    }

    private void resetGetValidateCode() {
        if (!btnGet.isEnabled()) {
            btnGet.setEnabled(true);
        }
    }

    @Override
    public String getUserInfo() {
        return txtUserInfo.getText().toString();
    }

    @Override
    public String getUserPass() {
        return txtUserPass.getText().toString();
    }

    @Override
    public String getVerificationCode() {
        return txtGetCode.getText().toString();
    }

    @Override
    public void setUserName(String nickname) {
        if (nickname.isEmpty())
            btnUserName.setText("Nick Name");
        else
            btnUserName.setText(nickname);
    }

    @Override
    public void setUserInfo(String email) {
        txtUserInfo.setText(email);
    }

    @Override
    public void onBackPressed() {
        ActivityUtils.gotoMainActivity(this);
    }

    public void getCode() {
        hideIMM();
        disableGetValidateCode();
        mPresenter.getValidateCode();
    }

    @Override
    public void disableGetValidateCode() {
        if (btnGet.isEnabled()) {
            btnGet.setEnabled(false);
        }
    }

    @Override
    public void setCountdown(int sec) {
        btnGet.setText(getString(R.string.reget_validation_second, sec));
    }

    @Override
    public void enableGetValidateCode() {
        btnGet.setText(R.string.login_reget_code);
    }

    @Override
    public void checkValidateCode() {
        if (!mPresenter.isSended()) {
            resetGetValidateCode();
        }
    }

    @Override
    public void modelResult(int what, Result result) {
        switch (what) {
            case ProfilePresenter.MSG_SEND_VALIDATE_CODE_ERROR:
            case ProfilePresenter.MSG_RESET_PASSWORD_FAIL:
                ToastUtil.shortToast(this, result.error);
                break;

            case ProfilePresenter.MSG_SEND_VALIDATE_CODE_SUCCESS:
                disableGetValidateCode();
                txtGetCode.requestFocus();
                break;

        }
    }
}
