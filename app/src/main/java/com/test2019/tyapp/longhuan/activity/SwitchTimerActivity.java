package com.test2019.tyapp.longhuan.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.TimePicker;

import com.alibaba.fastjson.JSONObject;
import com.test2019.tyapp.longhuan.R;
import com.test2019.tyapp.longhuan.global.Categories;
import com.test2019.tyapp.longhuan.global.Global;
import com.test2019.tyapp.longhuan.utils.PreferenceUtils;
import com.test2019.tyapp.longhuan.utils.ToastUtil;
import com.tuya.smart.home.sdk.TuyaHomeSdk;
import com.tuya.smart.sdk.api.IResultCallback;
import com.tuya.smart.sdk.api.ITuyaDevice;

import java.util.HashMap;
import java.util.Locale;



public class SwitchTimerActivity extends AppCompatActivity
        implements View.OnClickListener, TimePicker.OnTimeChangedListener {

    private String mDevId;
    private ITuyaDevice mITuyaDevice;
    private String currentSocket;

    private FrameLayout frame_timepicker, frame_timecalcul;
    private TimePicker timePicker;
    private TextView tv_thread;
    private FrameLayout frame_start, frame_cancel;

    private int nPicker_hour, nPicker_minite;
    private CountDownTimer mCountDownTimer;
    private int hours, minutes, seconds;
    private boolean isSetTimer;
    private long mTimeLeftInMillis;
    private long mEndTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_switch_timer);

        if (getIntent() != null) {
            mDevId = getIntent().getStringExtra(Global.CURRENT_DEV);
            currentSocket = getIntent().getStringExtra(Global.CURRENT_SOCKET);
        }

        init_view();


    }

    private void init_view(){
        frame_timepicker = (FrameLayout)findViewById(R.id.frame_timepicker);
        timePicker = (TimePicker)findViewById(R.id.timepicker);
        frame_timecalcul = (FrameLayout)findViewById(R.id.frame_timecalcul);
        tv_thread = (TextView)findViewById(R.id.tv_thread);

        frame_start = (FrameLayout)findViewById(R.id.frame_start);
        frame_cancel = (FrameLayout)findViewById(R.id.frame_cancel);

        mITuyaDevice = TuyaHomeSdk.newDeviceInstance(mDevId);

        findViewById(R.id.img_back).setOnClickListener(this);
        findViewById(R.id.btn_start).setOnClickListener(this::onClick);
        findViewById(R.id.btn_cancel).setOnClickListener(this::onClick);

        setTimePicer();
        timePicker.setOnTimeChangedListener(this::onTimeChanged);
    }

    private void setTimePicer(){
        nPicker_hour = 0;
        nPicker_minite = 30;
        timePicker.setIs24HourView(true);
        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.M){
            timePicker.setHour(nPicker_hour);
            timePicker.setMinute(nPicker_minite);
        }
        else{
            timePicker.setCurrentHour(nPicker_hour);
            timePicker.setCurrentMinute(nPicker_minite);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        mEndTime = PreferenceUtils.getLong(this, mDevId + Global.SWITCH_TIMER_ENDTIME);
        if (mEndTime == 0 || mEndTime <= System.currentTimeMillis()) {
            isSetTimer = false;
            update_UI(isSetTimer);
        }
        else {
            isSetTimer = true;
            update_UI(isSetTimer);
            mTimeLeftInMillis = mEndTime - System.currentTimeMillis();
            startCountDown(mTimeLeftInMillis);
        }
    }

    @Override
    protected void onPause() {
        PreferenceUtils.set(this, mDevId + Global.SWITCH_TIMER_ENDTIME, mEndTime);
        resetTimer();
        super.onPause();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.img_back:
                finish();
                break;
            case R.id.btn_start:
                startTimer();
                break;
            case R.id.btn_cancel:
                cancelTimer();
                break;
        }
    }

    @Override
    public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
        nPicker_hour = hourOfDay;
        nPicker_minite = minute;
    }

    private void startTimer() {
        mTimeLeftInMillis = nPicker_hour * 3600000 + nPicker_minite * 60000;
        if (mTimeLeftInMillis == 0) {
            ToastUtil.showToast(this, "Please set time correctly!");
            return;
        }
        mEndTime = System.currentTimeMillis() + mTimeLeftInMillis;
        send_CMD(mTimeLeftInMillis / 1000);
        isSetTimer = true;
        update_UI(isSetTimer);
        startCountDown(mTimeLeftInMillis);
    }

    private void startCountDown(long miliSecond) {
        mCountDownTimer = new CountDownTimer(miliSecond, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                updateCountDownText(millisUntilFinished);
            }

            @Override
            public void onFinish() {
                isSetTimer = false;
                update_UI(isSetTimer);
            }
        }.start();
    }

    private void cancelTimer() {
        send_CMD(0);
        resetTimer();
        mEndTime = 0;
        isSetTimer = false;
        update_UI(isSetTimer);
    }

    private void resetTimer() {
        if(mCountDownTimer != null){
            mCountDownTimer.cancel();
        }
    }

    private void send_CMD(long time) {

        if(currentSocket.contains(Categories.SMART_SOCKET)){
            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put(Global.SMART_SOCKET_TIMER, time);
            send_JSON(hashMap);
        }else if(currentSocket.contains(Categories.SMART_WIFI_SOCKET)){
            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put(Global.SWITCH_TIMER, time);
            send_JSON(hashMap);
        }else if(currentSocket.contains(Categories.SMART_INTELIGENTNY_SOCKET)){
            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put(Global.SMART_INTELIGENTNY_SOCKET_TIMER, time);
            send_JSON(hashMap);
        }

    }

    private void send_JSON(HashMap hashMap){
        mITuyaDevice.publishDps(JSONObject.toJSONString(hashMap), new IResultCallback() {
            @Override
            public void onError(String s, String s1) {
                String ss = "ss";
            }

            @Override
            public void onSuccess() {
                String ss = "ss";
            }
        });
    }

    private void update_UI(boolean b) {
        if (b) {
            frame_timepicker.setVisibility(View.GONE);
            frame_start.setVisibility(View.GONE);
            frame_timecalcul.setVisibility(View.VISIBLE);
            frame_cancel.setVisibility(View.VISIBLE);
        }
        else {
            frame_timepicker.setVisibility(View.VISIBLE);
            frame_start.setVisibility(View.VISIBLE);
            frame_timecalcul.setVisibility(View.GONE);
            frame_cancel.setVisibility(View.GONE);
        }
    }

    private void updateCountDownText(long miliSecond) {
        hours = (int)(miliSecond / 1000) / 3600;
        minutes = (int)((miliSecond / 1000) % 3600)/60;
        seconds = (int)(miliSecond / 1000) % 60;

        String timeLeftFormatted = String.format(Locale.getDefault(),
                "%02d:%02d:%02d", hours, minutes, seconds);
        tv_thread.setText(timeLeftFormatted);
    }
}
