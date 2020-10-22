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
import com.test2019.tyapp.longhuan.activity.CurtainSpeechSettingActivity;
import com.test2019.tyapp.longhuan.activity.CurtainSwitchMoveTimerActivity;
import com.test2019.tyapp.longhuan.activity.MainActivity;
import com.test2019.tyapp.longhuan.activity.SelectDeviceIconActivity;
import com.test2019.tyapp.longhuan.global.Global;
import com.test2019.tyapp.longhuan.model.CountUpTimer;
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

import java.util.HashMap;
import java.util.Map;

import static com.test2019.tyapp.longhuan.global.Global.ALPHA_DARK;
import static com.test2019.tyapp.longhuan.global.Global.ALPHA_LIGHT;

public class CurtainSwitchFragment extends Fragment implements View.OnClickListener,
        IDevListener, ISpeechView, View.OnTouchListener {

    //====== UI element =========//
    private LinearLayout lin_dev_option_wrapper;
    private RelativeLayout rel_curtain_timer;
    private ImageView img_switch_main_ring, img_switch_stop, img_switch_open, img_switch_close, img_switch_ring;
    private FrameLayout frame_txt_wrap;
    private TextView txt_recognized, txt_switch_stop, txt_timer_number;
    private RippleBackground rippleView;
    private ImageView img_voice_recoding;

    private boolean bShowOption = false;

    //========== device info =========//
    private DeviceBean mDevBean;
    private String mDevId;
    private String currentSocket;
    private String switch_state;

    private boolean bOn;                // show device is turn on or turn off
    private boolean b_State;            // device work or unwork


    //=== countdowntimer ===//
    private long mEndTime = 0;
    private boolean isSetTimer = false;
    private CountDownTimer mCountDownTimer;

    private int i_timer_number;
    private String st_timer_number;

    //===== countuptimer =========//

    private CountUpTimer mCountUpTimer;
    private long currentTimeNumber = 0;      //current view time(countdown)
    private long leftTimeNumber;         // total time - current view time;
    private long saveTimeNumber = 0;   // current save time
    private long saveTimeStopNumber = 0;  //when stop time
    //======= linstener ===============//
    private ITuyaDevice mITuyaDevice;

    //======= speech manage ===========//
    private SpeechManage mSpeechManage;
    private String mSt_currentLanguage;
    private boolean isRecord;

    private int nFromVoiceCmd = 0;
    private boolean isButtonClicked = false;

    private boolean isCheckall, isTurnOn, isTurnOnResponse, isTurnOff, isTurnOffResponse, isReverse;
    private boolean isTurnOnResponse_before, isTurnOffResponse_before;
    private String stTurnOn, stTurnOnResponse, stTurnOff, stTurnOffResponse, stReverse;
    private String stTurnOnResponse_before, stTurnOffResponse_before;

//    private boolean  isReverse, isGetOn, isGetOff;
//    private String  stGetOn, stGetOff;

    private static final String TAG = "CurtainSwitchFragment";

    public CurtainSwitchFragment(){}

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_curtain_switch, container, false);

        lin_dev_option_wrapper = v.findViewById(R.id.lin_dev_option_wrapper);
        frame_txt_wrap = v.findViewById(R.id.frame_txt_wrap);
        txt_recognized = v.findViewById(R.id.txt_recognized);

        img_switch_main_ring = v.findViewById(R.id.img_switch_main_ring);
        img_switch_open = v.findViewById(R.id.img_switch_open);
        img_switch_close = v.findViewById(R.id.img_switch_close);
        img_switch_stop = v.findViewById(R.id.img_switch_stop);
        img_switch_ring = v.findViewById(R.id.img_switch_ring);
        txt_switch_stop = v.findViewById(R.id.tv_switch_stop);

        rel_curtain_timer = v.findViewById(R.id.rel_curtain_timer);
        txt_timer_number = v.findViewById(R.id.tv_timer_number);

        rippleView = v.findViewById(R.id.rippleView);
        img_voice_recoding = v.findViewById(R.id.img_voice_recoding);
        img_voice_recoding.setOnTouchListener(this);

        rel_curtain_timer.setOnClickListener(this);


        v.findViewById(R.id.frame_option).setOnClickListener(this::onClick);
        v.findViewById(R.id.frame_speech_setting).setOnClickListener(this::onClick);
        v.findViewById(R.id.frame_edit_device).setOnClickListener(this::onClick);

        v.findViewById(R.id.img_switch_open).setOnClickListener(this::onClick);
        v.findViewById(R.id.img_switch_close).setOnClickListener(this::onClick);
        v.findViewById(R.id.img_switch_stop).setOnClickListener(this::onClick);


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

        mEndTime = saveTimeNumber + System.currentTimeMillis();

        PreferenceUtils.set(getContext(), mDevId + Global.CURTAIN_TIMER_ENDTIME, mEndTime);
        PreferenceUtils.set(getContext(), mDevId + Global.CURTAIN_TIMER_STATE, isSetTimer);
        PreferenceUtils.set(getContext(), mDevId + Global.CURTAIN_TIMER_WORK, b_State);
        PreferenceUtils.set(getContext(), mDevId + Global.CURTAIN_SAVE_TIME, saveTimeNumber);

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

        mEndTime = PreferenceUtils.getLong(getContext(), mDevId + Global.CURTAIN_TIMER_ENDTIME);
        i_timer_number = PreferenceUtils.getInt(getContext(), mDevId + Global.CURTAIN_SWITCH_TIMER);
        b_State = PreferenceUtils.getBoolean(getContext(), mDevId + Global.CURTAIN_TIMER_WORK);
        saveTimeStopNumber = PreferenceUtils.getLong(getContext(), mDevId + Global.CURTAIN_SAVE_TIME);


        isSetTimer = PreferenceUtils.getBoolean(getContext(), mDevId + Global.CURTAIN_TIMER_STATE);

        stReverse = PreferenceUtils.getCurReverseCmd(getContext(), mDevId);
        if(stReverse != null){
            isReverse = true;
        }


        if (mDevId == null) getActivity().finish();
        mDevBean = TuyaHomeSdk.getDataInstance().getDeviceBean(mDevId);
        if (mDevBean == null) getActivity().finish();

        Map<String, Object> dps = mDevBean.dps;
        if (dps.containsKey(Global.SWITCH_DPID_CMD)){
            switch_state = (String)dps.get(Global.SWITCH_DPID_CMD);
        }
        if(dps.containsKey(Global.CURTAIN_SWITCH)){
            bOn = (boolean)dps.get(Global.CURTAIN_SWITCH);
        }

        updateUI_ring(bOn);
        updateUI_button(switch_state);

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

        if(mEndTime < System.currentTimeMillis()){

            updateCountDownText(i_timer_number * 1000);
            b_State = false;
            updateUI_button(switch_state);

        }else{

            if(!isSetTimer){
                updateCountDownText(saveTimeStopNumber);
            }

            saveTimeNumber = mEndTime - System.currentTimeMillis();

            resetCountTime();
        }

    }

    private void resetCountTime(){

        if(bOn){

            if(switch_state.equals("off")){

                if(saveTimeNumber == 0){
                    long mTimeLeftInMilis = i_timer_number * 1000;
                    startCountDown(mTimeLeftInMilis);
                }else{
                    startCountDown(saveTimeNumber);
                }

            }else if(switch_state.equals("on")){
                leftTimeNumber = i_timer_number * 1000 - saveTimeNumber;
                currentTimeNumber = saveTimeNumber;
                resetTimer();
                startCountUp(leftTimeNumber);
            }

        }else{

            if(switch_state.equals("on")){

                if(saveTimeNumber == 0){
                    long mTimeLeftInMilis = i_timer_number * 1000;
                    startCountDown(mTimeLeftInMilis);
                }else{
                    startCountDown(saveTimeNumber);
                }

            }else if(switch_state.equals("off")){
                leftTimeNumber = i_timer_number * 1000 - saveTimeNumber;
                currentTimeNumber = saveTimeNumber;
                resetTimer();
                startCountUp(leftTimeNumber);

            }
        }

    }

    private void startCountDown(long miliSecond) {

        resetTimer();
        mCountDownTimer = new CountDownTimer(miliSecond, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {

                if(!isSetTimer){
                    return;
                }
                currentTimeNumber = millisUntilFinished;
                saveTimeNumber = currentTimeNumber;

                if(millisUntilFinished < 1000){
                    resetTimer();
                    onFinish();
                    return;
                }

                updateCountDownText(millisUntilFinished);
            }

            @Override
            public void onFinish() {

                isSetTimer = false;
                b_State = false;
                updateCountDownText(i_timer_number * 1000);
                saveTimeNumber = 0;

                img_switch_stop.setEnabled(false);
                img_switch_stop.setAlpha(ALPHA_DARK);
                txt_switch_stop.setAlpha(ALPHA_DARK);

                cmd_turn(!bOn);
                cmd_state("stop");
            }

        }.start();

    }

    private void startCountUp(long miliSecond){

        mCountUpTimer = new CountUpTimer(miliSecond) {
            @Override
            public void onTick(int second) {

                if(!isSetTimer){
                    return;
                }

                long i_upcount = currentTimeNumber + second * 1000 + 500;
                saveTimeNumber = i_upcount;
                updateCountDownText(i_upcount);

            }

            @Override
            public void onFinish() {
                updateCountDownText(i_timer_number * 1000);
                b_State = false;
                isSetTimer = false;
                saveTimeNumber = 0;


                img_switch_stop.setEnabled(false);
                img_switch_stop.setAlpha(ALPHA_DARK);
                txt_switch_stop.setAlpha(ALPHA_DARK);
                cmd_state("stop");
            }
        };

        mCountUpTimer.start();

    }

    private void updateCountDownText(long miliSecond) {
        int time = (int)(miliSecond / 1000);
        st_timer_number = Integer.toString(time);
        txt_timer_number.setText(st_timer_number + "s");

    }


    private void resetTimer() {
        if(mCountDownTimer != null){
            mCountDownTimer.cancel();
        }

        if(mCountUpTimer != null){
            mCountUpTimer.cancel();
        }
    }

    private void updateUI_ring(boolean bOnOff) {
        if (bOnOff) {           // device is on
//            img_switch_ring.setVisibility(View.VISIBLE);
//            frame_txt_wrap.setBackgroundResource(R.drawable.round_border_blue);
//            frame_txt_wrap.setAlpha(ALPHA_LIGHT);
            txt_recognized.setText("OPEN");
            txt_recognized.setAlpha(ALPHA_LIGHT);

        }
        else {                  // device is off
//            img_switch_ring.setVisibility(View.GONE);
//            frame_txt_wrap.setBackgroundResource(R.drawable.round_border_white);
//            frame_txt_wrap.setAlpha(ALPHA_DARK);
            txt_recognized.setText("CLOSED");
            txt_recognized.setAlpha(ALPHA_LIGHT);

        }

        ((MainActivity)getActivity()).deviceAdapter.notifyDataSetChanged();
    }

    private void updateUI_button(String state) {

        if(state.equals("on")){
            img_switch_open.setEnabled(false);
            img_switch_open.setAlpha(ALPHA_DARK);
            img_switch_close.setEnabled(true);
            img_switch_close.setAlpha(ALPHA_LIGHT);

            if(!b_State){
                img_switch_stop.setEnabled(false);
                img_switch_stop.setAlpha(ALPHA_DARK);
                txt_switch_stop.setAlpha(ALPHA_DARK);
            }else{
                img_switch_stop.setEnabled(true);
                img_switch_stop.setAlpha(ALPHA_LIGHT);
                txt_switch_stop.setAlpha(ALPHA_LIGHT);
            }
        }else if(state.equals("off")){
            img_switch_open.setEnabled(true);
            img_switch_open.setAlpha(ALPHA_LIGHT);
            img_switch_close.setEnabled(false);
            img_switch_close.setAlpha(ALPHA_DARK);

            if(!b_State){
                img_switch_stop.setEnabled(false);
                img_switch_stop.setAlpha(ALPHA_DARK);
                txt_switch_stop.setAlpha(ALPHA_DARK);
            }else{
                img_switch_stop.setEnabled(true);
                img_switch_stop.setAlpha(ALPHA_LIGHT);
                txt_switch_stop.setAlpha(ALPHA_LIGHT);
            }

        }else if(state.equals("stop")){
            if(b_State){

                img_switch_open.setEnabled(true);
                img_switch_open.setAlpha(ALPHA_LIGHT);
                img_switch_close.setEnabled(true);
                img_switch_close.setAlpha(ALPHA_LIGHT);

            }else{
                if(bOn){
                    img_switch_open.setEnabled(false);
                    img_switch_open.setAlpha(ALPHA_DARK);
                    img_switch_close.setEnabled(true);
                    img_switch_close.setAlpha(ALPHA_LIGHT);

                    img_switch_stop.setEnabled(false);
                    img_switch_stop.setAlpha(ALPHA_DARK);
                    txt_switch_stop.setAlpha(ALPHA_DARK);

                }else {
                    img_switch_open.setEnabled(true);
                    img_switch_open.setAlpha(ALPHA_LIGHT);
                    img_switch_close.setEnabled(false);
                    img_switch_close.setAlpha(ALPHA_DARK);

                    img_switch_stop.setEnabled(false);
                    img_switch_stop.setAlpha(ALPHA_DARK);
                    txt_switch_stop.setAlpha(ALPHA_DARK);
                }
            }
        }

    }

    //==== View.OnClickListener ========//
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case  R.id.frame_option:
                bShowOption = !bShowOption;
                showOpthionWrapper(bShowOption);
                break;
            case R.id.frame_speech_setting:
                Intent intent_speech_setting = new Intent(getContext(), CurtainSpeechSettingActivity.class);
                intent_speech_setting.putExtra(Global.CURRENT_DEV, mDevId);
                ActivityUtils.startActivity(getActivity(), intent_speech_setting, ActivityUtils.ANIMATE_FORWARD, false);
                break;
            case R.id.frame_edit_device:
                Intent intent = new Intent(getContext(), SelectDeviceIconActivity.class);
                intent.putExtra(Global.FROM_ACTIVITY, "MainActivity");
                intent.putExtra(Global.CURRENT_DEV, mDevId);
                intent.putExtra(Global.CURRENT_DEV_CATEGORY, 2);
                intent.putExtra(Global.FROM_CUR_FRAGMENT, "CurFragment");
                ActivityUtils.startActivity(getActivity(), intent, ActivityUtils.ANIMATE_FORWARD, false);
                break;
            case R.id.rel_curtain_timer:
                resetTimer();
                isSetTimer = false;
                b_State = false;
                saveTimeNumber = 0;
                Intent intent_curtain_switch = new Intent(getContext(), CurtainSwitchMoveTimerActivity.class);
                intent_curtain_switch.putExtra(Global.CURRENT_DEV, mDevId);
                ActivityUtils.startActivity(getActivity(), intent_curtain_switch, ActivityUtils.ANIMATE_FORWARD, false);
                break;
            case R.id.img_switch_open:
                switch_open();
                break;
            case R.id.img_switch_close:
                switch_close();
                break;
            case R.id.img_switch_stop:
                switch_stop();
                break;

        }
    }

    private void switch_open(){

        b_State = true;

        switch_state = "on";

        //updateUI_button(switch_state);

        cmd_state(switch_state);

        cmd_number(i_timer_number);

        //resetCountTime();

        isSetTimer = true;


    }

    private void switch_close(){

        b_State = true;

        switch_state = "off";

        //updateUI_button(switch_state);

        cmd_state(switch_state);

        cmd_number(i_timer_number);

        //resetCountTime();

        isSetTimer = true;

    }

    private void switch_stop(){

        switch_state = "stop";

        //updateUI_button(switch_state);

        cmd_state(switch_state);

        isSetTimer = false;

        resetTimer();

    }

    private void cmd_state(String state){

        if (!DeviceUtil.isOnline(mDevId)){
            Toast.makeText(getContext(), "Device is currently offline!", Toast.LENGTH_LONG).show();
            return;
        }

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put(Global.SWITCH_DPID_CMD, state);
        send_CMD(hashMap);

    }

    private void cmd_number(int number){
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put(Global.SWITCH_TIMER, number);
        send_CMD(hashMap);
    }

    private void cmd_turn(boolean b){

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put(Global.CURTAIN_SWITCH, b);
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

            switch_state = jsonObject.getString(Global.SWITCH_DPID_CMD);

            if(!switch_state.equals("stop")){

                b_State = true;
                isSetTimer = true;
                updateUI_button(switch_state);
                resetCountTime();
                return;

            }else{
                if(isSetTimer){
                    return;
                }
                isSetTimer = false;
                resetTimer();
                updateUI_button(switch_state);
                resetCountTime();
                return;
            }

        }

        if (jsonObject.containsKey(Global.SWITCH_TIMER)) {

            int i = jsonObject.getInteger(Global.SWITCH_TIMER);
            Log.d(TAG, Integer.toString(i));

        }

        if(jsonObject.containsKey(Global.CURTAIN_SWITCH)){
            bOn = jsonObject.getBoolean(Global.CURTAIN_SWITCH);
            updateUI_ring(bOn);

            speechAfterCommand();

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
                    mSpeechManage.playSpeech("Device is open");
                }
                nFromVoiceCmd = 0;
                break;
            case 2:                 // turn off response
                if (isCheckall && isTurnOffResponse && !stTurnOffResponse.equals(Global.EMPTY)) {
                    mSpeechManage.playSpeech(stTurnOffResponse);
                }
                else {
                    mSpeechManage.playSpeech("Device is closed");
                }
                nFromVoiceCmd = 0;
                break;
            case 3:                 // reverse response
                //speechGetOn();
                nFromVoiceCmd = 0;
                break;
            case 0:                 // normal
                if (isButtonClicked) {              // form button click
                    isButtonClicked = false;
                    //speechGetOn();
                }

                else {                            // when user change device
                   // speechGetOn();
                }
        }
    }

//    private void speechGetOn() {
//        if (switch_state.equals("on")) {
//            if (isCheckall && isGetOn && !stGetOn.equals(Global.EMPTY)) {
//                mSpeechManage.playSpeech(stGetOn);
//            }
//            else {
//                mSpeechManage.playSpeech("Device is open");
//            }
//        }
//        else if(switch_state.equals("off")){
//            if (isCheckall && isGetOff && !stGetOff.equals(Global.EMPTY)) {
//                mSpeechManage.playSpeech(stGetOff);
//            }
//            else {
//                mSpeechManage.playSpeech("Device is close");
//            }
//        }
//    }

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
        isCheckall = isTurnOn = isTurnOnResponse = isTurnOff = isTurnOffResponse = false;
        isTurnOnResponse_before = isTurnOffResponse_before = false;
        stTurnOn = stTurnOnResponse = stTurnOff = stTurnOffResponse = Global.EMPTY;
        stTurnOnResponse_before = stTurnOffResponse_before = Global.EMPTY;

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
            if(objSettings.has(Global.SPEECH_SETTING_TURN_ON_RESPOND_BEFORE_CHECK))
                isTurnOnResponse_before = objSettings.getBoolean(Global.SPEECH_SETTING_TURN_ON_RESPOND_BEFORE_CHECK);
            if(objSettings.has(Global.SPEECH_SETTING_TURN_OFF_RESPOND_BEFORE_CHECK))
                isTurnOffResponse_before = objSettings.getBoolean(Global.SPEECH_SETTING_TURN_OFF_RESPOND_BEFORE_CHECK);


            if (objSettings.has(Global.SPEECH_SETTING_TURN_ON))
                stTurnOn = objSettings.getString(Global.SPEECH_SETTING_TURN_ON);
            if (objSettings.has(Global.SPEECH_SETTING_TURN_ON_RESPOND))
                stTurnOnResponse = objSettings.getString(Global.SPEECH_SETTING_TURN_ON_RESPOND);
            if (objSettings.has(Global.SPEECH_SETTING_TURN_OFF))
                stTurnOff = objSettings.getString(Global.SPEECH_SETTING_TURN_OFF);
            if (objSettings.has(Global.SPEECH_SETTING_TURN_OFF_RESPOND))
                stTurnOffResponse = objSettings.getString(Global.SPEECH_SETTING_TURN_OFF_RESPOND);
            if(objSettings.has(Global.SPEECH_SETTING_TURN_ON_RESPOND_BEFORE))
                stTurnOnResponse_before = objSettings.getString(Global.SPEECH_SETTING_TURN_ON_RESPOND_BEFORE);
            if(objSettings.has(Global.SPEECH_SETTING_TURN_OFF_RESPOND_BEFORE))
                stTurnOffResponse_before = objSettings.getString(Global.SPEECH_SETTING_TURN_OFF_RESPOND_BEFORE);

        } catch (JSONException e) {
        }
    }

    //======= ISpeechView ========//
    @Override
    public void showText(String text) {
        ToastUtil.shortToast(getContext(), text);

       // Toast.makeText(getContext(), text, Toast.LENGTH_LONG).show();

        int cmd = recongnize_CMD(text);
        switch (cmd) {
            case 1:                 // open

                if(!b_State && bOn){
                    if(mSt_currentLanguage.equals("en-US")){
                        mSpeechManage.playSpeech("Device is already open state");
                    }
                    else mSpeechManage.playSpeech("Urządzenie jest już otwarte");
                    return;
                }

                if(!b_State && !bOn){

                    if(isCheckall && isTurnOnResponse_before && !stTurnOnResponse_before.equals(Global.EMPTY)){
                        mSpeechManage.playSpeech(stTurnOnResponse_before);
                    }else{
                        if(mSt_currentLanguage.equals("en-US")){
                            mSpeechManage.playSpeech("I am openning");
                        }
                        else mSpeechManage.playSpeech("Otwieram");
                    }
                }

                if(!switch_state.equals("on")){
                    nFromVoiceCmd = 1;
                    switch_state = "on";
                    cmd_state(switch_state);
                }else{
                    if(mSt_currentLanguage.equals("en-US")){
                        mSpeechManage.playSpeech("Device is already open state");
                    }
                    else mSpeechManage.playSpeech("Urządzenie jest już otwarte");
                }

                break;

            case 2:                 // close

                if(!b_State && !bOn){
                    if (mSt_currentLanguage.equals("en-US")){
                        mSpeechManage.playSpeech("Device is already closed");
                    }
                    else mSpeechManage.playSpeech("urządzenie jest już zamknięte");
                    return;
                }

                if(!b_State && bOn){

                    if(isCheckall && isTurnOffResponse_before && !stTurnOffResponse_before.equals(Global.EMPTY)){
                        mSpeechManage.playSpeech(stTurnOffResponse_before);
                    }else{
                        if(mSt_currentLanguage.equals("en-US")){
                            mSpeechManage.playSpeech("I am closing");
                        }
                        else mSpeechManage.playSpeech("Zamykam");
                    }
                }

                if (!switch_state.equals("off")) {
                    nFromVoiceCmd = 2;
                    switch_state = "off";
                    cmd_state(switch_state);
                }
                else {
                    if (mSt_currentLanguage.equals("en-US")){
                        mSpeechManage.playSpeech("Device is already closed");
                    }
                    else mSpeechManage.playSpeech("urządzenie jest już zamknięte");
                }
                break;
            case 3:                 // reverse


                if(b_State){
                    if(isSetTimer){
                        if(switch_state.equals("on")){
                            switch_state = "off";
                            cmd_state(switch_state);
                        }else if(switch_state.equals("off")){
                            switch_state = "on";
                            cmd_state(switch_state);
                        }
                    }else{
                        if(bOn){
                            switch_state = "on";
                            cmd_state(switch_state);
                        }else {
                            switch_state = "off";
                            cmd_state(switch_state);
                        }
                    }
                }else{
                    if(bOn){
                        nFromVoiceCmd = 2;

                        switch_state = "off";
                        cmd_state(switch_state);

                        if(isCheckall && isTurnOffResponse_before && !stTurnOffResponse_before.equals(Global.EMPTY)){
                            mSpeechManage.playSpeech(stTurnOffResponse_before);
                        }else{
                            if(mSt_currentLanguage.equals("en-US")){
                                mSpeechManage.playSpeech("I am closing");
                            }
                            else mSpeechManage.playSpeech("Zamykam");
                        }

                    }else{
                        nFromVoiceCmd = 1;

                        switch_state = "on";
                        cmd_state(switch_state);

                        if(isCheckall && isTurnOnResponse_before && !stTurnOnResponse_before.equals(Global.EMPTY)){
                            mSpeechManage.playSpeech(stTurnOnResponse_before);
                        }else{
                            if(mSt_currentLanguage.equals("en-US")){
                                mSpeechManage.playSpeech("I am openning");
                            }
                            else mSpeechManage.playSpeech("Otwieram");
                        }

                    }
                }
                break;
            case 4:                 // unknown command
                mSpeechManage.playSpeech(Global.SPEECH_UNKNOWN_COMMAND);
                break;
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
