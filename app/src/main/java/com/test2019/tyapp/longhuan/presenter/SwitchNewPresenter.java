package com.test2019.tyapp.longhuan.presenter;

import android.app.Activity;
import android.util.Log;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.test2019.tyapp.longhuan.MainApplication;
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

public class SwitchNewPresenter extends BasePresenter implements IDevListener, ISpeechView {

    private Activity mContext;

    private ITuyaDevice mITuyaDevice;
    private DeviceBean mDevBean;
    private String devId;
    private boolean bOn;
    private boolean bOnGet;
    private boolean bAgreeSpeech = false;

    private static final String TAG = "VoiceWidget";

    private SpeechManage mSpeechManage;
    private String mSt_currentLanguage;

    private boolean isCheckall, isTurnOn, isTurnOnResponse, isTurnOff, isTurnOffResponse, isReverse, isGetOn, isGetOff, isDimmingPercent;
    private String stTurnOn, stTurnOnResponse, stTurnOff, stTurnOffResponse, stReverse, stGetOn, stGetOff, stDimmingPrefix;


    public SwitchNewPresenter(Activity context) {
        this.mContext = context;
    }

    public void startSpeechManage(){
        mSpeechManage = new SpeechManage( mContext,this);
    }

    public void SpeechManageDestroy(){
        mSpeechManage.destroy();
    }

    public void registerListener(String mDevId){

        devId = mDevId;

        mITuyaDevice = TuyaHomeSdk.newDeviceInstance(mDevId);
        mITuyaDevice.registerDevListener(this);

        ToastUtil.showToast(mContext, "success" + mDevId);
        Log.d(TAG, "success: " + mDevId);

        mDevBean = TuyaHomeSdk.getDataInstance().getDeviceBean(mDevId);
        if (mDevBean == null) mContext.finish();

        Map<String, Object> dps = mDevBean.dps;
        if (dps.containsKey(Global.SWITCH_DPID_CMD)){
            bOn = (boolean)dps.get(Global.SWITCH_DPID_CMD);
        }
    }

    public void unregisterListener(){

        mITuyaDevice.unRegisterDevListener();

        mITuyaDevice = null;
    }

    public void initSpeech(){
        Log.d(TAG, "initSpeech: ");

        mSt_currentLanguage = MainApplication.getDefaultEncoding();

        init_speech_module();

    }

    private void init_speech_module(){
        isCheckall = isTurnOn = isTurnOnResponse = isTurnOff = isTurnOffResponse = isReverse = isGetOn = isGetOff = isDimmingPercent = false;
        stTurnOn = stTurnOnResponse = stTurnOff = stTurnOffResponse = stReverse = stGetOn = stGetOff  = Global.EMPTY;

        String st_settings = PreferenceUtils.getString(mContext, devId + mSt_currentLanguage);

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
            if (objSettings.has(Global.SPEECH_SETTING_TURN_RESERVE_CHECK))
                isReverse = objSettings.getBoolean(Global.SPEECH_SETTING_TURN_RESERVE_CHECK);
            if (objSettings.has(Global.SPEECH_SETTING_GET_ON_CHECK))
                isGetOn = objSettings.getBoolean(Global.SPEECH_SETTING_GET_ON_CHECK);
            if (objSettings.has(Global.SPEECH_SETTING_GET_OFF_CHECK))
                isGetOff = objSettings.getBoolean(Global.SPEECH_SETTING_GET_OFF_CHECK);


            if (objSettings.has(Global.SPEECH_SETTING_TURN_ON))
                stTurnOn = objSettings.getString(Global.SPEECH_SETTING_TURN_ON);
            if (objSettings.has(Global.SPEECH_SETTING_TURN_ON_RESPOND))
                stTurnOnResponse = objSettings.getString(Global.SPEECH_SETTING_TURN_ON_RESPOND);
            if (objSettings.has(Global.SPEECH_SETTING_TURN_OFF))
                stTurnOff = objSettings.getString(Global.SPEECH_SETTING_TURN_OFF);
            if (objSettings.has(Global.SPEECH_SETTING_TURN_OFF_RESPOND))
                stTurnOffResponse = objSettings.getString(Global.SPEECH_SETTING_TURN_OFF_RESPOND);
            if (objSettings.has(Global.SPEECH_SETTING_TURN_RESERVE))
                stReverse = objSettings.getString(Global.SPEECH_SETTING_TURN_RESERVE);
            if (objSettings.has(Global.SPEECH_SETTING_GET_ON))
                stGetOn = objSettings.getString(Global.SPEECH_SETTING_GET_ON);
            if (objSettings.has(Global.SPEECH_SETTING_GET_OFF))
                stGetOff = objSettings.getString(Global.SPEECH_SETTING_GET_OFF);

        } catch (JSONException e) {
        }
    }

    private int recognizeCommand(String text){
        int result = 0;

        if(!isCheckall)
            return result;
        if (isTurnOn) {
            if (stTurnOn != null && !stTurnOn.isEmpty() && stTurnOn.toLowerCase().trim().replace(" ", "").equals(text.toLowerCase().trim())) {
                result = 1;
            }
        }

        if (isTurnOff) {
            if (stTurnOff != null && !stTurnOff.isEmpty() && stTurnOff.toLowerCase().trim().replace(" ", "").equals(text.toLowerCase().trim())) {
                result = 2;
            }
        }

        if (isReverse) {
            if (stReverse != null && !stReverse.isEmpty() && stReverse.toLowerCase().trim().replace(" ", "").equals(text.toLowerCase().trim())) {
                result = 3;
            }
        }

        return result;

    }

    public void checkCommand(String mdevId, String commandtext){

        int command = recognizeCommand(commandtext);

        if(!DeviceUtil.isOnline(mdevId)){
            Toast.makeText(mContext, "Device is currently offline!", Toast.LENGTH_LONG).show();
            return;
        }

        if(!isCheckall){
            return;
        }

        if(command == 1){

            if(bOn){

                if (isTurnOnResponse) {
                    if (mSt_currentLanguage.equals("en-US")){
                        mSpeechManage.playSpeech("Device is already turned on");
                    }
                    else mSpeechManage.playSpeech("Urządzenie jest już włączone");
                }
            }else{
                bAgreeSpeech = true;
                cmd_turn_on_off(mdevId);
            }

        }else if(command == 2){

            if(!bOn){
                if (isTurnOffResponse) {
                    if (mSt_currentLanguage.equals("en-US")){
                        mSpeechManage.playSpeech("Device is already turned off");
                    }
                    else mSpeechManage.playSpeech("Urządzenie jest już wyłączone");
                }
            }else{
                bAgreeSpeech = true;
                cmd_turn_on_off(mdevId);
            }

        }else if(command == 3){
            bAgreeSpeech = true;
            cmd_turn_on_off(mdevId);
        }else{
            mSpeechManage.playSpeech(Global.SPEECH_UNKNOWN_COMMAND);
        }
    }

    private void speechCommand(){
        if(bOn){
            if(isCheckall && isTurnOnResponse && !stTurnOn.equals(Global.EMPTY)){
                mSpeechManage.playSpeech(stTurnOnResponse);
            }else{
                mSpeechManage.playSpeech("Device is open");
            }
        }else{
            if(isCheckall && isTurnOffResponse && !stTurnOff.equals(Global.EMPTY)){
                mSpeechManage.playSpeech(stTurnOffResponse);
            }else{
                mSpeechManage.playSpeech("Device is closed");
            }
        }

        bAgreeSpeech = false;
    }

    private void cmd_turn_on_off(String mDevId){

        if(!DeviceUtil.isOnline(mDevId)){
            ToastUtil.showToast(mContext, "Device is currently offline!");
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
            public void onError(String code, String error) {
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
        Log.d(TAG, "onDpUpdate: " + devId + " : " + dpStr);

        JSONObject jsonObject = JSONObject.parseObject(dpStr);

        if(jsonObject.containsKey(Global.SWITCH_DPID_CMD)){
            bOnGet = jsonObject.getBoolean(Global.SWITCH_DPID_CMD);

            if(bAgreeSpeech && bOn == bOnGet){
                speechCommand();
            }
        }
    }

    //============ unused code ==================

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

    @Override
    public void showText(String text) {

    }

    @Override
    public void showTest(String text) {

    }
}
