package com.test2019.tyapp.longhuan.presenter;

import android.app.Activity;
import android.os.CountDownTimer;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.test2019.tyapp.longhuan.MainApplication;
import com.test2019.tyapp.longhuan.global.Categories;
import com.test2019.tyapp.longhuan.global.Global;
import com.test2019.tyapp.longhuan.speech.SpeechManage;
import com.test2019.tyapp.longhuan.utils.DeviceUtil;
import com.test2019.tyapp.longhuan.utils.PreferenceUtils;
import com.test2019.tyapp.longhuan.utils.ToastUtil;
import com.test2019.tyapp.longhuan.view.ISpeechView;
import com.tuya.smart.android.mvp.presenter.BasePresenter;
import com.tuya.smart.home.sdk.TuyaHomeSdk;
import com.tuya.smart.sdk.api.IDevListener;
import com.tuya.smart.sdk.api.IResultCallback;
import com.tuya.smart.sdk.api.ITuyaDevice;
import com.tuya.smart.sdk.bean.DeviceBean;

import org.json.JSONException;

import java.util.HashMap;
import java.util.Map;

public class SocketSpeechPresenter extends BasePresenter implements IDevListener, ISpeechView {

    private Activity mContext;

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

    public SocketSpeechPresenter(Activity context){
        this.mContext = context;
    }

    public void startSpeechManage(){
        mSpeechManage = new SpeechManage(mContext, this);
    }

    public void SpeechManageDestroy(){
        mSpeechManage.destroy();
    }


    public void registerListener(String mdevId, String devNameTmp){
        mDevId = mdevId;
        currentSocket = devNameTmp;

        mITuyaDevice = TuyaHomeSdk.newDeviceInstance(mDevId);
        mITuyaDevice.registerDevListener(this);

        stReverse = null;
        isReverse = false;

        mDevBean = TuyaHomeSdk.getDataInstance().getDeviceBean(mDevId);
        if (mDevBean == null) mContext.finish();

        Map<String, Object> dps = mDevBean.dps;
        if (dps.containsKey(Global.SWITCH_DPID_CMD)){
            bOn = (boolean)dps.get(Global.SWITCH_DPID_CMD);
        }

        stReverse = PreferenceUtils.getCurReverseCmd(mContext, mDevId);
        if(stReverse != null){
            isReverse = true;
        }

        setCountTime();
    }


    private void setCountTime() {
        mEndTime = PreferenceUtils.getLong(mContext, mDevId + Global.SWITCH_TIMER_ENDTIME);
        if (mEndTime == 0 || mEndTime <= System.currentTimeMillis()) {
            isSetTimer = false;

        }
        else {
            isSetTimer = true;

            long mTimeLeftInMillis = mEndTime - System.currentTimeMillis();
            startCountDown(mTimeLeftInMillis);
        }
    }



    private void startCountDown(long miliSecond) {
        resetTimer();
        mCountDownTimer = new CountDownTimer(miliSecond, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {

            }
        }.start();
    }


    private void resetTimer() {
        if(mCountDownTimer != null){
            mCountDownTimer.cancel();
        }
    }


    private void cmd_turn_on_off(){
        //===== check device is online ========//
        if (!DeviceUtil.isOnline(mDevId)){
            Toast.makeText(mContext, "Device is currently offline!", Toast.LENGTH_LONG).show();
            return;
        }

        bOn = !bOn;

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put(Global.SWITCH_DPID_CMD, bOn);


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

    @Override
    public void onDpUpdate(String devId, String dpStr) {
        JSONObject jsonObject = JSONObject.parseObject(dpStr);

        if (jsonObject.containsKey(Global.SWITCH_DPID_CMD)) {
            bOn = jsonObject.getBoolean(Global.SWITCH_DPID_CMD);
            //updateUI(bOn);

            if (isSetTimer) {
                isSetTimer = false;
                mEndTime = 0;
                //update_CountTime_UI(isSetTimer);
                resetTimer();
            }
            speechAfterCommand();
        }
        if (jsonObject.containsKey(Global.SWITCH_TIMER)) {
            if (nFromVoiceCmd == 4) {

                if(mSt_currentLanguage.equals("en-US")){
                    mSpeechManage.playSpeech("Set it to" + stTimerSetting);
                }else{
                    mSpeechManage.playSpeech("Ustawiam" + stTimerSetting);
                }

                nFromVoiceCmd = 0;
            }
        }

        if(jsonObject.containsKey(Global.SMART_SOCKET_TIMER)){
            if (nFromVoiceCmd == 4) {

                if(mSt_currentLanguage.equals("en-US")){
                    mSpeechManage.playSpeech("Set it to" + stTimerSetting);
                }else{
                    mSpeechManage.playSpeech("Ustawiam" + stTimerSetting);
                }
                nFromVoiceCmd = 0;
            }
        }

        if(jsonObject.containsKey(Global.SMART_INTELIGENTNY_SOCKET_TIMER)){
            if (nFromVoiceCmd == 4) {

                if(mSt_currentLanguage.equals("en-US")){
                    mSpeechManage.playSpeech("Set it to" + stTimerSetting);
                }else{
                    mSpeechManage.playSpeech("Ustawiam" + stTimerSetting);
                }
                nFromVoiceCmd = 0;
            }
        }

    }

    public void onStop(){

        mITuyaDevice.unRegisterDevListener();

        if (mITuyaDevice != null)
            mITuyaDevice.onDestroy();

        PreferenceUtils.set(mContext, mDevId + Global.SWITCH_TIMER_ENDTIME, mEndTime);
        resetTimer();
    }

    public void initSpeech() {

        mSt_currentLanguage = MainApplication.getDefaultEncoding();

        nFromVoiceCmd = 0;
        init_speech_module();
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

    private void init_speech_module(){
        isCheckall = isTurnOn = isTurnOnResponse = isTurnOff = isTurnOffResponse = isSwitchTimer = false;
        stTurnOn = stTurnOnResponse = stTurnOff = stTurnOffResponse = stTimerPrefix = Global.EMPTY;

        String st_settings = PreferenceUtils.getString(mContext, mDevId + mSt_currentLanguage);

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


    public void checkCommand(String text) {
        ToastUtil.shortToast(mContext, text);

        if (stTimerPrefix != Global.EMPTY && text.startsWith(stTimerPrefix)){
            if (!isCheckall || !isSwitchTimer) {
                ToastUtil.shortToast(mContext, "Voice controll is not setting!");
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


    private int recongnize_CMD(String text){
        if (isCheckall && isTurnOn && stTurnOn.toLowerCase().trim().replace(" ", "").equals(text.toLowerCase().trim())) return 1;          // turn on
        else if (isCheckall && isTurnOff && stTurnOff.toLowerCase().replace(" ", "").trim().equals(text.toLowerCase().trim())) return 2;   // turn off
        else if (isCheckall && isReverse && stReverse.toLowerCase().replace(" ", "").trim().equals(text.toLowerCase().trim())) return 3;   // reverse

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



    //=========== unused code =============

    @Override
    public void showText(String text) {

    }

    @Override
    public void showTest(String text) {

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
}
