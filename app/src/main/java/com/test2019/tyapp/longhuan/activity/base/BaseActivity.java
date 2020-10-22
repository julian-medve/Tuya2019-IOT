package com.test2019.tyapp.longhuan.activity.base;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.StrictMode;
import android.os.SystemClock;
import androidx.appcompat.app.AppCompatActivity;

import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import com.test2019.tyapp.longhuan.app.Constant;
import com.test2019.tyapp.longhuan.utils.ActivityUtils;
import com.test2019.tyapp.longhuan.utils.LoginHelper;
import com.test2019.tyapp.longhuan.utils.ProgressUtil;
import com.test2019.tyapp.longhuan.utils.ToastUtil;
import com.tuya.smart.android.common.utils.L;
import com.test2019.tyapp.longhuan.R;
import com.tuya.smart.home.sdk.TuyaHomeSdk;

import java.util.Timer;
import java.util.TimerTask;


/**
 * Created by letian on 16/7/15.
 */
public class BaseActivity extends AppCompatActivity {
    private static final String TAG = "BaseActivity";

    private boolean mIsPaused = true;

    protected View mPanelTopView;

    private long resumeUptime;

    private GestureDetector mGestureDetector;

    private boolean mNeedDefaultAni = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        GestureDetector.OnGestureListener gestureListener = obtainGestureListener();
        if (gestureListener != null) {
            mGestureDetector = new GestureDetector(this, gestureListener);
        }
        Constant.attachActivity(this);
        checkLogin();
        StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
                .detectActivityLeaks()
                .detectLeakedSqlLiteObjects()
                .detectLeakedClosableObjects()
                .detectAll()
                .penaltyLog()
                .build());
    }

    public void closeDefaultAni() {
        mNeedDefaultAni = false;
    }


    protected boolean isUseCustomTheme() {
        return false;
    }

    private void checkLogin() {
        if (needLogin() && !TuyaHomeSdk.getUserInstance().isLogin()) {
            LoginHelper.reLogin(this);
        }
    }

    @TargetApi(19)
    protected void setTranslucentStatus(boolean on) {
        Window win = getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        final int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
        if (on) {
            winParams.flags |= bits;
        } else {
            winParams.flags &= ~bits;
        }
        win.setAttributes(winParams);
    }

    @Override
    public void startActivity(Intent intent) {
        super.startActivity(intent);
        if (mNeedDefaultAni) {
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        }
    }

    @Override
    public void startActivityForResult(Intent intent, int requestCode, Bundle options) {
        super.startActivityForResult(intent, requestCode, options);
        if (mNeedDefaultAni) {
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mIsPaused = false;
        resumeUptime = SystemClock.uptimeMillis();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mIsPaused = true;
    }

    @Override
    public void onBackPressed() {
        ActivityUtils.back(this);
        super.onBackPressed();
        if (mNeedDefaultAni) {
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        }
    }



    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (mGestureDetector != null) {
            mGestureDetector.onTouchEvent(ev);
        }
        return super.dispatchTouchEvent(ev);
    }

    private static boolean isExit = false;

    protected void exitBy2Click() {
        Timer tExit = null;
        if (!isExit) {
            isExit = true;
            ToastUtil.shortToast(this, getString(R.string.action_tips_exit_hint) + " "
                    + getString(R.string.app_name));
            tExit = new Timer();
            tExit.schedule(new TimerTask() {
                @Override
                public void run() {
                    isExit = false;
                }
            }, 2000);
        } else {
            LoginHelper.exit(this);
        }
    }

    protected GestureDetector.OnGestureListener obtainGestureListener() {
        return null;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (!this.isFinishing()) {
            if (keyCode == KeyEvent.KEYCODE_BACK) {
                long eventtime = event.getEventTime();
                if (Math.abs(eventtime - resumeUptime) < 400) {
                    L.d(TAG, "baseactivity onKeyDown after onResume to close, do none");
                    return true;
                }
            }

            if (!(event.getRepeatCount() > 0) && !onPanelKeyDown(keyCode, event)) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    ActivityUtils.back(this);
                    return true;
                } else {
                    return super.onKeyDown(keyCode, event);
                }
            } else {
                L.d(TAG, "baseactivity onKeyDown true");
                return true;
            }

        } else {
            return true;
        }
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_SETTINGS) {
            return true;
        }
        return super.onKeyUp(keyCode, event);
    }

    protected boolean onPanelKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_SETTINGS) {
            return true;
        } else if (keyCode == KeyEvent.KEYCODE_BACK) {
            onBackPressed();
            return true;
        }
        return false;
    }

    @Override
    public void setContentView(int layoutResID) {
        LayoutInflater inflater =
                (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mPanelTopView = inflater.inflate(layoutResID, null);
        super.setContentView(mPanelTopView);
    }

    @Override
    public void setContentView(View view) {
        mPanelTopView = view;
        super.setContentView(view);
    }

    @Override
    public void setContentView(View view, ViewGroup.LayoutParams params) {
        mPanelTopView = view;
        super.setContentView(view, params);
    }

    /**
     * Whether you need to log in, the subclass is determined according to business needs.
     * By default, all interfaces need to determine whether they are logged in.
     */
    public boolean needLogin() {
        return true;
    }

    public boolean isContainFragment() {
        return false;
    }

    public static void setViewVisible(View view) {
        if (view.getVisibility() != View.VISIBLE) {
            view.setVisibility(View.VISIBLE);
        }
    }

    public static void setViewGone(View view) {
        if (view.getVisibility() != View.GONE) {
            view.setVisibility(View.GONE);
        }
    }


    protected boolean isPause() {
        return mIsPaused;
    }

    protected void hideIMM() {
        if (getCurrentFocus() != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                    InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    public void showToast(int resId) {
        ToastUtil.showToast(this, resId);
    }

    public void showToast(String tip) {
        ToastUtil.showToast(this, tip);
    }

    public void showLoading(int resId) {
        ProgressUtil.showLoading(this, resId);
    }

    public void showLoading() {
        ProgressUtil.showLoading(this, R.string.loading);
    }

    public void hideLoading() {
        ProgressUtil.hideLoading();
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public void finishActivity() {
        onBackPressed();
    }
}
