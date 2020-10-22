package com.test2019.tyapp.longhuan.fragment.device;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.alibaba.fastjson.JSONObject;
import com.skyfishjy.library.RippleBackground;
import com.test2019.tyapp.longhuan.MainApplication;
import com.test2019.tyapp.longhuan.R;
import com.test2019.tyapp.longhuan.activity.MainActivity;
import com.test2019.tyapp.longhuan.activity.SelectCircleIconActivity;
import com.test2019.tyapp.longhuan.activity.SelectDeviceIconActivity;
import com.test2019.tyapp.longhuan.activity.SocketSpeechSettingActivity;
import com.test2019.tyapp.longhuan.activity.SwitchTimerActivity;
import com.test2019.tyapp.longhuan.global.Categories;
import com.test2019.tyapp.longhuan.global.Global;
import com.test2019.tyapp.longhuan.speech.SpeechManage;
import com.test2019.tyapp.longhuan.utils.ActivityUtils;
import com.test2019.tyapp.longhuan.utils.DeviceUtil;
import com.test2019.tyapp.longhuan.utils.PreferenceUtils;
import com.test2019.tyapp.longhuan.utils.ToastUtil;
import com.test2019.tyapp.longhuan.view.ISpeechView;
import com.tuya.smart.home.sdk.TuyaHomeSdk;
import com.tuya.smart.sdk.api.IDevListener;
import com.tuya.smart.sdk.api.IResultCallback;
import com.tuya.smart.sdk.api.ITuyaDevice;
import com.tuya.smart.sdk.bean.DeviceBean;

import org.json.JSONException;
//import org.json.JSONObject;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import static com.test2019.tyapp.longhuan.global.Global.ALPHA_DARK;
import static com.test2019.tyapp.longhuan.global.Global.ALPHA_LIGHT;

public class SmartWiFiSocketFragment extends Fragment implements View.OnClickListener,
        IDevListener, ISpeechView, View.OnTouchListener {

    //====== UI element =========//
    private LinearLayout lin_dev_option_wrapper;
    private ImageView img_switch_ring, img_switch;
    private FrameLayout frame_txt_wrap;
    private TextView txt_recognized;
    private RippleBackground rippleView;
    private ImageView img_voice_recoding;
    private RelativeLayout rel_timeState;
    private TextView tv_CountTime, tv_switchState;

    private boolean bShowOption = false;

    //========== device info =========//
    private DeviceBean mDevBean;
    private String mDevId;
    private String currentSocket;

    private boolean bOn;                // show device is on or off

    //=== countdowntimer ===//
    private long mEndTime = 0;
    private boolean isSetTimer;
    private CountDownTimer mCountDownTimer;

    //======= linstener ===============//
    private ITuyaDevice mITuyaDevice;

    //======= speech manage ===========//
    private SpeechManage mSpeechManage;
    private String mSt_currentLanguage;
    private boolean isRecord;

    private int nFromVoiceCmd = 0;
    private boolean isButtonClicked = false;

    private boolean isCheckall, isTurnOn, isTurnOnResponse, isTurnOff, isTurnOffResponse, isReverse, isSwitchTimer;
    private String stTurnOn, stTurnOnResponse, stTurnOff, stTurnOffResponse, stReverse, stTimerPrefix;
    private String stTimerSetting;

    public SmartWiFiSocketFragment(){}

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_smart_wifi_socket, container, false);

        lin_dev_option_wrapper = v.findViewById(R.id.lin_dev_option_wrapper);
        frame_txt_wrap = v.findViewById(R.id.frame_txt_wrap);
        txt_recognized = v.findViewById(R.id.txt_recognized);

        rel_timeState = v.findViewById(R.id.rel_timeState);
        tv_CountTime = v.findViewById(R.id.tv_CountTime);
        tv_switchState = v.findViewById(R.id.tv_switchState);

        img_switch_ring = v.findViewById(R.id.img_switch_ring);
        img_switch = v.findViewById(R.id.img_switch);

        rippleView = v.findViewById(R.id.rippleView);
        img_voice_recoding = v.findViewById(R.id.img_voice_recoding);
        img_voice_recoding.setOnTouchListener(this);

        img_switch_ring.setOnClickListener(this::onClick);
        v.findViewById(R.id.frame_option).setOnClickListener(this::onClick);
        v.findViewById(R.id.frame_speech_setting).setOnClickListener(this::onClick);
        v.findViewById(R.id.frame_edit_device).setOnClickListener(this::onClick);
        v.findViewById(R.id.frame_timer).setOnClickListener(this);
        v.findViewById(R.id.frame_edit_icon).setOnClickListener(this);
        return v;
    }

    @Override
    public void onResume() {
        super.onResume();

        bShowOption = false;
        showOpthionWrapper(bShowOption);

        init_data();
        registerListener();
        init_speech();
    }

    @Override
    public void onPause() {
        mSpeechManage.destroy();
        mITuyaDevice.unRegisterDevListener();

        if (mITuyaDevice != null)
            mITuyaDevice.onDestroy();

        PreferenceUtils.set(getContext(), mDevId + Global.SWITCH_TIMER_ENDTIME, mEndTime);
        resetTimer();
        super.onPause();
    }

    private void showOpthionWrapper(boolean bonoff){
        if (bonoff) lin_dev_option_wrapper.setVisibility(View.VISIBLE);
        else lin_dev_option_wrapper.setVisibility(View.GONE);
    }

    private void init_data() {
        if (getArguments() != null){
            mDevId = getArguments().getString(Global.CURRENT_DEV);
            currentSocket = getArguments().getString(Global.CURRENT_SOCKET);
        }
        else getActivity().finish();

        stReverse = null;
        isReverse = false;

        if (mDevId == null) getActivity().finish();
        mDevBean = TuyaHomeSdk.getDataInstance().getDeviceBean(mDevId);
        if (mDevBean == null) getActivity().finish();

        PreferenceUtils.set_icon_image(this.getContext(), mDevBean, img_switch);

        Map<String, Object> dps = mDevBean.dps;
        if (dps.containsKey(Global.SWITCH_DPID_CMD)){
            bOn = (boolean)dps.get(Global.SWITCH_DPID_CMD);
        }

        stReverse = PreferenceUtils.getCurReverseCmd(getContext(), mDevId);
        if(stReverse != null){
            isReverse = true;
        }

        updateUI(bOn);
        setCountTime();
    }

    private void registerListener(){
        if (mITuyaDevice == null) mITuyaDevice = TuyaHomeSdk.newDeviceInstance(mDevId);
        mITuyaDevice.registerDevListener(this);
    }

    private void init_speech() {
        isRecord = false;
        mSpeechManage = new SpeechManage(getContext(), this);
        mSt_currentLanguage = MainApplication.getDefaultEncoding();

        nFromVoiceCmd = 0;
        init_speech_module();
    }

    //========= set count down timer ========//
    private void setCountTime() {
        mEndTime = PreferenceUtils.getLong(getContext(), mDevId + Global.SWITCH_TIMER_ENDTIME);
        if (mEndTime == 0 || mEndTime <= System.currentTimeMillis()) {
            isSetTimer = false;
            update_CountTime_UI(isSetTimer);
        }
        else {
            isSetTimer = true;
            update_CountTime_UI(isSetTimer);
            long mTimeLeftInMillis = mEndTime - System.currentTimeMillis();
            startCountDown(mTimeLeftInMillis);
        }
    }

    private void update_CountTime_UI(boolean isSetTimer) {
        if (isSetTimer) {
            rel_timeState.setVisibility(View.VISIBLE);
            if (bOn)    tv_switchState.setText("later off");
            else        tv_switchState.setText("later on");
        }
        else {
            rel_timeState.setVisibility(View.GONE);
        }
    }

    private void startCountDown(long miliSecond) {
        resetTimer();
        mCountDownTimer = new CountDownTimer(miliSecond, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                updateCountDownText(millisUntilFinished);
            }

            @Override
            public void onFinish() {
//                isSetTimer = false;
//                update_CountTime_UI(isSetTimer);
            }
        }.start();
    }

    private void updateCountDownText(long miliSecond) {
        int hours = (int)(miliSecond / 1000) / 3600;
        int minutes = (int)((miliSecond / 1000) % 3600)/60;
        int seconds = (int)(miliSecond / 1000) % 60;

        String timeLeftFormatted = String.format(Locale.getDefault(),
                "%02d:%02d:%02d", hours, minutes, seconds);
        tv_CountTime.setText(timeLeftFormatted);
    }

    private void resetTimer() {
        if(mCountDownTimer != null){
            mCountDownTimer.cancel();
        }
    }

    private void updateUI(boolean bOnOff) {
        if (bOnOff) {           // device is on
            img_switch.setAlpha(ALPHA_LIGHT);
            img_switch_ring.setImageResource(R.drawable.ring_blue);
            img_switch_ring.setAlpha(ALPHA_LIGHT);
            frame_txt_wrap.setBackgroundResource(R.drawable.round_border_blue);
            frame_txt_wrap.setAlpha(ALPHA_LIGHT);
            txt_recognized.setText("Device is On.");
            txt_recognized.setAlpha(ALPHA_LIGHT);
        }
        else {                  // device is off
            img_switch.setAlpha(ALPHA_DARK);
            img_switch_ring.setImageResource(R.drawable.ring_white);
            img_switch_ring.setAlpha(ALPHA_DARK);
            frame_txt_wrap.setBackgroundResource(R.drawable.round_border_white);
            frame_txt_wrap.setAlpha(ALPHA_DARK);
            txt_recognized.setText("Device is Off.");
            txt_recognized.setAlpha(ALPHA_DARK);
        }

        ((MainActivity)getActivity()).deviceAdapter.notifyDataSetChanged();
    }


    //==== View.OnClickListener ========//
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case  R.id.frame_option:
                bShowOption = !bShowOption;
                showOpthionWrapper(bShowOption);
                break;
            case R.id.img_switch_ring:
                isButtonClicked = true;
                cmd_turn_on_off();
                break;
            case R.id.frame_speech_setting:
                Intent intent_speech_setting = new Intent(getContext(), SocketSpeechSettingActivity.class);
                intent_speech_setting.putExtra(Global.CURRENT_DEV, mDevId);
                ActivityUtils.startActivity(getActivity(), intent_speech_setting, ActivityUtils.ANIMATE_FORWARD, false);
                break;
            case R.id.frame_edit_device:
                Intent intent = new Intent(getContext(), SelectDeviceIconActivity.class);
                intent.putExtra(Global.FROM_ACTIVITY, "MainActivity");
                intent.putExtra(Global.CURRENT_DEV, mDevId);
                intent.putExtra(Global.CURRENT_DEV_CATEGORY, 2);
                intent.putExtra(Global.FROM_CUR_FRAGMENT, "SocketFragment");
                ActivityUtils.startActivity(getActivity(), intent, ActivityUtils.ANIMATE_FORWARD, false);
                break;
            case R.id.frame_timer:
                Intent intent_timer = new Intent(getContext(), SwitchTimerActivity.class);
                intent_timer.putExtra(Global.CURRENT_DEV, mDevId);
                intent_timer.putExtra(Global.CURRENT_SOCKET, currentSocket);
                ActivityUtils.startActivity(getActivity(),intent_timer,ActivityUtils.ANIMATE_FORWARD, false);
                break;
            case R.id.frame_edit_icon:
                Intent intent_edit_icon = new Intent(getContext(), SelectCircleIconActivity.class);
                intent_edit_icon.putExtra(Global.FROM_ACTIVITY, "MainActivity");
                intent_edit_icon.putExtra(Global.CURRENT_DEV, mDevId);
                intent_edit_icon.putExtra(Global.CURRENT_DEV_CATEGORY, 2);
                ActivityUtils.startActivity(getActivity(), intent_edit_icon, ActivityUtils.ANIMATE_FORWARD, false);
                break;
        }
    }

    private void cmd_turn_on_off(){
        //===== check device is online ========//
        if (!DeviceUtil.isOnline(mDevId)){
            Toast.makeText(getContext(), "Device is currently offline!", Toast.LENGTH_LONG).show();
            return;
        }

        bOn = !bOn;

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put(Global.SWITCH_DPID_CMD, bOn);
        //hashMap.put(Global.SMART_SOCKET_TIMER, (int) 1800000 / 1000);

        send_CMD(hashMap);
    }

    private void send_CMD(HashMap<String, Object> hashMap){
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

    //============== IDevListener ======================//
    @Override
    public void onDpUpdate(String devId, String dpStr) {
        JSONObject jsonObject = JSONObject.parseObject(dpStr);

        if (jsonObject.containsKey(Global.SWITCH_DPID_CMD)) {
            bOn = jsonObject.getBoolean(Global.SWITCH_DPID_CMD);
            updateUI(bOn);

            if (isSetTimer) {
                isSetTimer = false;
                mEndTime = 0;
                update_CountTime_UI(isSetTimer);
                resetTimer();
            }
            speechAfterCommand();
        }
        if (jsonObject.containsKey(Global.SWITCH_TIMER)) {
            if (nFromVoiceCmd == 4) {
                mSpeechManage.playSpeech(stTimerSetting);
                nFromVoiceCmd = 0;
            }
        }

        if(jsonObject.containsKey(Global.SMART_SOCKET_TIMER)){
            if (nFromVoiceCmd == 4) {
                mSpeechManage.playSpeech(stTimerSetting);
                nFromVoiceCmd = 0;
            }
        }

        if(jsonObject.containsKey(Global.SMART_INTELIGENTNY_SOCKET_TIMER)){
            if (nFromVoiceCmd == 4) {
                mSpeechManage.playSpeech(stTimerSetting);
                nFromVoiceCmd = 0;
            }
        }

    }

    private void speechAfterCommand() {
        //======= speech voice ======//
        switch (nFromVoiceCmd) {
            case 1:                 // turn on response
                if (isCheckall && isTurnOnResponse && !stTurnOnResponse.equals(Global.EMPTY)) {     // turn on response
                    mSpeechManage.playSpeech(stTurnOnResponse);
                }
                else {
                    mSpeechManage.playSpeech("Device is on");
                }
                nFromVoiceCmd = 0;
                break;
            case 2:                 // turn off response
                if (isCheckall && isTurnOffResponse && !stTurnOffResponse.equals(Global.EMPTY)) {
                    mSpeechManage.playSpeech(stTurnOffResponse);
                }
                else {
                    mSpeechManage.playSpeech("Device is off");
                }
                nFromVoiceCmd = 0;
                break;
            case 3:                 // reverse response
                nFromVoiceCmd = 0;
                break;
            case 0:                 // normal
                if (isButtonClicked) {              // form button click
                    isButtonClicked = false;
                }

        }
    }



    @Override
    public void onRemoved(String devId) {

    }

    @Override
    public void onStatusChanged(String devId, boolean online) {

    }

    @Override
    public void onNetworkStatusChanged(String devId, boolean status) {

    }

    @Override
    public void onDevInfoUpdate(String devId) {

    }

    //========= voice recognize touch event ===============//
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (!isCheckall) {
            ToastUtil.shortToast(getContext(), "Voice control is not setted!");
            return false;
        }

        switch (event.getAction())
        {
            case MotionEvent.ACTION_DOWN:
                Log.e("Touch Down","Downn");
                isRecord = true;
                actionChangeRecord();
                break;
            case MotionEvent.ACTION_UP:
                Log.e("Touch Up","Up");
                isRecord = false;
                actionChangeRecord();
                break;
        }
        return true;
    }

    public void actionChangeRecord()
    {
        if (isRecord) {
            rippleView.startRippleAnimation();
            mSpeechManage.startSpeechRecognizer();
        }
        else {
            rippleView.stopRippleAnimation();
            mSpeechManage.stopSpeechRecognizer();
        }
    }

    private void init_speech_module(){
        isCheckall = isTurnOn = isTurnOnResponse = isTurnOff = isTurnOffResponse = isSwitchTimer = false;
        stTurnOn = stTurnOnResponse = stTurnOff = stTurnOffResponse = stTimerPrefix = Global.EMPTY;

        String st_settings = PreferenceUtils.getString(getContext(), mDevId + mSt_currentLanguage);

        if (st_settings == null) {
            return;
        }

        try {
            org.json.JSONObject objSettings = new org.json.JSONObject(st_settings);

            if (objSettings.has(Global.SPEECH_SETTING_ALL))
                isCheckall = objSettings.getBoolean(Global.SPEECH_SETTING_ALL);
            if (objSettings.has(Global.SPEECH_SETTING_TURN_ON_CHECK))
                isTurnOn = objSettings.getBoolean(Global.SPEECH_SETTING_TURN_ON_CHECK);
            if (objSettings.has(Global.SPEECH_SETTING_TURN_ON_RESPOND_CHECK))
                isTurnOnResponse = objSettings.getBoolean(Global.SPEECH_SETTING_TURN_ON_RESPOND_CHECK);
            if (objSettings.has(Global.SPEECH_SETTING_TURN_OFF_CHECK))
                isTurnOff = objSettings.getBoolean(Global.SPEECH_SETTING_TURN_OFF_CHECK);
            if (objSettings.has(Global.SPEECH_SETTING_TURN_OFF_RESPOND_CHECK))
                isTurnOffResponse = objSettings.getBoolean(Global.SPEECH_SETTING_TURN_OFF_RESPOND_CHECK);
            if (objSettings.has(Global.SPEECH_SETTING_SWITCH_TIMER_CHECK))
                isSwitchTimer = objSettings.getBoolean(Global.SPEECH_SETTING_SWITCH_TIMER_CHECK);

            if (objSettings.has(Global.SPEECH_SETTING_TURN_ON))
                stTurnOn = objSettings.getString(Global.SPEECH_SETTING_TURN_ON);
            if (objSettings.has(Global.SPEECH_SETTING_TURN_ON_RESPOND))
                stTurnOnResponse = objSettings.getString(Global.SPEECH_SETTING_TURN_ON_RESPOND);
            if (objSettings.has(Global.SPEECH_SETTING_TURN_OFF))
                stTurnOff = objSettings.getString(Global.SPEECH_SETTING_TURN_OFF);
            if (objSettings.has(Global.SPEECH_SETTING_TURN_OFF_RESPOND))
                stTurnOffResponse = objSettings.getString(Global.SPEECH_SETTING_TURN_OFF_RESPOND);
            if (objSettings.has(Global.SPEECH_SETTING_SWITCH_TIMER_PREFIX))
                stTimerPrefix = objSettings.getString(Global.SPEECH_SETTING_SWITCH_TIMER_PREFIX);
        } catch (JSONException e) {
        }
    }

    //======= ISpeechView ========//
    @Override
    public void showText(String text) {
        ToastUtil.shortToast(getContext(), text);

        if (stTimerPrefix != Global.EMPTY && text.startsWith(stTimerPrefix)){
            if (!isCheckall || !isSwitchTimer) {
                ToastUtil.shortToast(getContext(), "Voice controll is not setting!");
                return;
            }
            stTimerSetting = text;
            String st_tmp = text.replace(stTimerPrefix + " ", "");
            String[] array = st_tmp.split(" ");
            int nHours = 0;
            int nMinutes = 0;
            if (array.length == 2) {
                if (array[1].equals("hour") || array[1].equals("hours")
                        || array[1].equals("godzina") || array[1].equals("godziny")) {
                    nHours = getIntFromString(array[0]);
                }
                else if (array[1].equals("minutes") || array[1].equals("minute")
                        || array[1].equals("minuta") || array[1].equals("minuty") || array[1].equals("minut")) {
                    nMinutes = getIntFromString(array[0]);
                }
            }
            else if (array.length == 4) {
                nHours = getIntFromString(array[0]);
                nMinutes = getIntFromString(array[2]);
                if (nHours == 0 || nMinutes == 0) {
                    mSpeechManage.playSpeech(Global.SPEECH_UNKNOWN_COMMAND);
                    return;
                }
            }
            else if (array.length == 5) {
                nHours = getIntFromString(array[0]);
                nMinutes = getIntFromString(array[3]);
                if (nHours == 0 || nMinutes == 0) {
                    mSpeechManage.playSpeech(Global.SPEECH_UNKNOWN_COMMAND);
                    return;
                }
            }

            if (nHours + nMinutes == 0) {
                mSpeechManage.playSpeech(Global.SPEECH_UNKNOWN_COMMAND);
                return;
            }
            else {
                nFromVoiceCmd = 4;
                long mTimeLeftInMillis = nHours * 3600000 + nMinutes * 60000;
                mEndTime = System.currentTimeMillis() + mTimeLeftInMillis;

                if(currentSocket.contains(Categories.SMART_SOCKET)){
                    HashMap<String, Object> hashMap = new HashMap<>();
                    hashMap.put(Global.SMART_SOCKET_TIMER, (int)mTimeLeftInMillis / 1000);
                    send_CMD(hashMap);
                }else if(currentSocket.contains(Categories.SMART_WIFI_SOCKET)){
                    HashMap<String, Object> hashMap = new HashMap<>();
                    hashMap.put(Global.SWITCH_TIMER, (int) mTimeLeftInMillis / 1000);
                    send_CMD(hashMap);
                }else if(currentSocket.contains(Categories.SMART_INTELIGENTNY_SOCKET)){
                    HashMap<String, Object> hashMap = new HashMap<>();
                    hashMap.put(Global.SMART_INTELIGENTNY_SOCKET_TIMER, (int)mTimeLeftInMillis / 1000);
                    send_CMD(hashMap);
                }

 //               hashMap.put(Global.SMART_SOCKET_TIMER, (int) mTimeLeftInMillis / 1000);


                isSetTimer = true;
                update_CountTime_UI(isSetTimer);
                startCountDown(mTimeLeftInMillis);
            }
        }
        else {
            int cmd = recongnize_CMD(text);
            switch (cmd) {
                case 1:                 // turn on
                    if (!bOn) {
                        nFromVoiceCmd = 1;
                        cmd_turn_on_off();
                    }
                    else {
                        if (mSt_currentLanguage.equals("en-US")){
                            mSpeechManage.playSpeech("Device is already turned on");
                        }
                        else mSpeechManage.playSpeech("Urządzenie jest już włączone");
                    }
                    break;
                case 2:                 // turn off
                    if (bOn) {
                        nFromVoiceCmd = 2;
                        cmd_turn_on_off();
                    }
                    else {
                        if (mSt_currentLanguage.equals("en-US")){
                            mSpeechManage.playSpeech("Device is already turned off");
                        }
                        else mSpeechManage.playSpeech("Urządzenie jest już wyłączone");
                    }
                    break;
                case 3:                 // reverse
                    if (!bOn) {
                        nFromVoiceCmd = 1;
                        cmd_turn_on_off();
                    }else{
                        nFromVoiceCmd = 2;
                        cmd_turn_on_off();
                    }
                    break;
                case 4:                 // unknown command
                    mSpeechManage.playSpeech(Global.SPEECH_UNKNOWN_COMMAND);
                    break;
            }
        }
    }

    @Override
    public void showTest(String text) {

    }

    private int recongnize_CMD(String text){
        if (isCheckall && isTurnOn && stTurnOn.toLowerCase().trim().equals(text.toLowerCase().trim())) return 1;          // turn on
        else if (isCheckall && isTurnOff && stTurnOff.toLowerCase().trim().equals(text.toLowerCase().trim())) return 2;   // turn off
        else if (isCheckall && isReverse && stReverse.toLowerCase().trim().equals(text.toLowerCase().trim())) return 3;   // reverse

        return 4;
    }

    private int getIntFromString(String input) {
        int number = 0;
        if (isParsable(input)) number = Integer.parseInt(input);
        return number;
    }

    private boolean isParsable(String input){
        try{
            Integer.parseInt(input);
            return true;
        }catch(NumberFormatException e){
            return false;
        }
    }
}
