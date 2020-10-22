package com.test2019.tyapp.longhuan.presenter;

import android.app.Activity;
import android.os.CountDownTimer;
import android.util.Log;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.test2019.tyapp.longhuan.MainApplication;
import com.test2019.tyapp.longhuan.global.Global;
import com.test2019.tyapp.longhuan.model.CountUpTimer;
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

public class CurtainSwitchPresenter extends BasePresenter implements IDevListener, ISpeechView {

    private Activity mContext;

    private String mdevId;
    private String switch_state;
    private ITuyaDevice mITuyaDevice;
    private DeviceBean mDevBean;

    private static final String TAG = "CurtainSwitchPresenter";

    private SpeechManage mSpeechManage;
    private String mSt_currentLanguage;
    private int nFromVoiceCmd = 0;

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

    private boolean bOn;                // show device is turn on or turn off
    private boolean b_State;
    private boolean isButtonClicked = false;
    private boolean bOnGet;
    private boolean bAgreeSpeech = false;


    private boolean isCheckall, isTurnOn, isTurnOnResponse, isTurnOff, isTurnOffResponse, isReverse;
    private boolean isTurnOnResponse_before, isTurnOffResponse_before;
    private String stTurnOn, stTurnOnResponse, stTurnOff, stTurnOffResponse, stReverse;
    private String stTurnOnResponse_before, stTurnOffResponse_before;

    public CurtainSwitchPresenter(Activity context) {
        this.mContext = context;
    }

    public void startSpeechManage(){
        mSpeechManage = new SpeechManage( mContext,this);
    }

    public void SpeechManageDestroy(){
        mSpeechManage.destroy();
    }

    public void registerListener(String mDevId){

        mdevId = mDevId;

        mITuyaDevice = TuyaHomeSdk.newDeviceInstance(mDevId);
        mITuyaDevice.registerDevListener(this);

        ToastUtil.showToast(mContext, "success" + mDevId);
        Log.d(TAG, "success: " + mDevId);


        mDevBean = TuyaHomeSdk.getDataInstance().getDeviceBean(mDevId);
        if (mDevBean == null) mContext.finish();

        Map<String, Object> dps = mDevBean.dps;
        if (dps.containsKey(Global.SWITCH_DPID_CMD)){
            switch_state = (String)dps.get(Global.SWITCH_DPID_CMD);
        }
        if(dps.containsKey(Global.CURTAIN_SWITCH)){
            bOn = (boolean)dps.get(Global.CURTAIN_SWITCH);
        }

        stReverse = null;
        isReverse = false;

        mEndTime = PreferenceUtils.getLong(mContext, mDevId + Global.CURTAIN_TIMER_ENDTIME);
        i_timer_number = PreferenceUtils.getInt(mContext, mDevId + Global.CURTAIN_SWITCH_TIMER);
        saveTimeStopNumber = PreferenceUtils.getLong(mContext, mDevId + Global.CURTAIN_SAVE_TIME);

        b_State = PreferenceUtils.getBoolean(mContext, mDevId + Global.CURTAIN_TIMER_WORK);
        isSetTimer = PreferenceUtils.getBoolean(mContext, mDevId + Global.CURTAIN_TIMER_STATE);
        saveTimeNumber = 0;

        stReverse = PreferenceUtils.getCurReverseCmd(mContext, mDevId);
        if(stReverse != null){
            isReverse = true;
        }

        setCountTime();

    }

    private void setCountTime() {
        b_State = false;
        resetCountTime();

//        if(mEndTime < System.currentTimeMillis()){
//
//            b_State = false;
//
//        }else{
//
//            saveTimeNumber = mEndTime - System.currentTimeMillis();
//
//            resetCountTime();
//        }
    }

    public void initSpeech(){
        Log.d(TAG, "initSpeech: ");

        mSt_currentLanguage = MainApplication.getDefaultEncoding();

        init_speech_module();

    }

    private void init_speech_module(){
        isCheckall = isTurnOn = isTurnOnResponse = isTurnOff = isTurnOffResponse = false;
        isTurnOnResponse_before = isTurnOffResponse_before = false;
        stTurnOn = stTurnOnResponse = stTurnOff = stTurnOffResponse = Global.EMPTY;
        stTurnOnResponse_before = stTurnOffResponse_before = Global.EMPTY;

        String st_settings = PreferenceUtils.getString(mContext, mdevId + mSt_currentLanguage);

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


    private void cmd_state(String devId, String state){

        if (!DeviceUtil.isOnline(mdevId)){
            Toast.makeText(mContext, "Device is currently offline!", Toast.LENGTH_LONG).show();
            return;
        }

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put(Global.SWITCH_DPID_CMD, state);
        send_CMD(hashMap);
    }

    private void cmd_turn(boolean b){

        HashMap<String, Object> hashMap = new HashMap<>();
        bOn = b;
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


    @Override
    public void onDpUpdate(String devId, String dpStr) {
        JSONObject jsonObject = JSONObject.parseObject(dpStr);

        if (jsonObject.containsKey(Global.SWITCH_DPID_CMD)) {

            if(!switch_state.equals("stop")){

                b_State = true;
                isSetTimer = true;
                resetCountTime();
                return;

            }else{

                if(isSetTimer){
                    return;
                }

                isSetTimer = false;
                resetTimer();
                resetCountTime();
                return;
            }

        }

        if(jsonObject.containsKey(Global.CURTAIN_SWITCH)){
            bOnGet = jsonObject.getBoolean(Global.CURTAIN_SWITCH);

            if(bAgreeSpeech && bOn == bOnGet){
                speechAfterCommand();
            }

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

            }

            @Override
            public void onFinish() {

                isSetTimer = false;
                b_State = false;

                saveTimeNumber = 0;

                cmd_turn(!bOn);
                cmd_state(mdevId,"stop");
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


            }

            @Override
            public void onFinish() {

                b_State = false;
                isSetTimer = false;
                saveTimeNumber = 0;

                cmd_state(mdevId, "stop");
            }
        };

        mCountUpTimer.start();

    }


    private void resetTimer() {
        if(mCountDownTimer != null){
            mCountDownTimer.cancel();
        }

        if(mCountUpTimer != null){
            mCountUpTimer.cancel();
        }
    }

    public void onStop(){

        mITuyaDevice.unRegisterDevListener();

        if (mITuyaDevice != null)
            mITuyaDevice.onDestroy();

//        mEndTime = saveTimeNumber + System.currentTimeMillis();
//
//        PreferenceUtils.set(mContext, mdevId + Global.CURTAIN_TIMER_ENDTIME, mEndTime);
//        PreferenceUtils.set(mContext, mdevId + Global.CURTAIN_TIMER_STATE, isSetTimer);
//        PreferenceUtils.set(mContext, mdevId + Global.CURTAIN_TIMER_WORK, b_State);
//        PreferenceUtils.set(mContext, mdevId + Global.CURTAIN_SAVE_TIME, saveTimeNumber);

        resetTimer();
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
                bAgreeSpeech = false;
                break;
            case 2:                 // turn off response
                if (isCheckall && isTurnOffResponse && !stTurnOffResponse.equals(Global.EMPTY)) {
                    mSpeechManage.playSpeech(stTurnOffResponse);
                }
                else {
                    mSpeechManage.playSpeech("Device is closed");
                }
                nFromVoiceCmd = 0;
                bAgreeSpeech = false;
                break;
            case 3:                 // reverse response

                nFromVoiceCmd = 0;
                bAgreeSpeech = false;
                break;
            case 0:                 // normal
                if (isButtonClicked) {              // form button click
                    isButtonClicked = false;

                }
                else {                            // when user change device

                }
                bAgreeSpeech = false;
        }
    }



    public void checkCommand(String mdevId, String commandtext){

        ToastUtil.shortToast(mContext, commandtext);

        int cmd = recongnize_CMD(commandtext);
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
                        else {
                            mSpeechManage.playSpeech("Otwieram");
                        }
                    }
                    cmd_state(mdevId, "on");
                    bAgreeSpeech = true;
                }

                if(!switch_state.equals("on")){
                    nFromVoiceCmd = 1;
                    switch_state = "on";
                    cmd_state(mdevId, switch_state);
                    bAgreeSpeech = true;
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

                    cmd_state(mdevId, "off");
                    bAgreeSpeech = true;
                }

                if (!switch_state.equals("off")) {
                    nFromVoiceCmd = 2;
                    switch_state = "off";
                    cmd_state(mdevId, switch_state);
                    bAgreeSpeech = true;
                }
                else {
                    if (mSt_currentLanguage.equals("en-US")){
                        mSpeechManage.playSpeech("Device is already closed");
                    }
                    else mSpeechManage.playSpeech("urządzenie jest już zamknięte");
                }
                break;
            case 3:                 // reverse
                nFromVoiceCmd = 3;
                bAgreeSpeech = true;
                if(b_State){
                    if(isSetTimer){
                        if(switch_state.equals("on")){
                            switch_state = "off";
                            cmd_state(mdevId, switch_state);
                        }else if(switch_state.equals("off")){
                            switch_state = "on";
                            cmd_state(mdevId, switch_state);
                        }
                    }else{
                        if(bOn){
                            switch_state = "on";
                            cmd_state(mdevId, switch_state);
                        }else {
                            switch_state = "off";
                            cmd_state(mdevId, switch_state);
                        }
                    }

                }else{
                    if(bOn){
                        nFromVoiceCmd = 2;
                        switch_state = "off";
                        cmd_state(mdevId, switch_state);

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
                        cmd_state(mdevId, switch_state);

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

    private int recongnize_CMD(String text){

        if (isCheckall && isTurnOn && stTurnOn.toLowerCase().trim().replace(" ", "").equals(text.toLowerCase().trim())) return 1;          // turn on
        else if (isCheckall && isTurnOff && stTurnOff.toLowerCase().trim().replace(" ", "").equals(text.toLowerCase().trim())) return 2;   // turn off
        else if (isCheckall && isReverse && stReverse.toLowerCase().trim().replace(" ", "").equals(text.toLowerCase().trim())) return 3;   // reverse

        return 4;
    }



    //=============== unused code ==============

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
