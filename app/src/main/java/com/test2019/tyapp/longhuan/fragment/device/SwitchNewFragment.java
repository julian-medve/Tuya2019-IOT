package com.test2019.tyapp.longhuan.fragment.device;

import android.content.Intent;
import android.os.Bundle;
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
import com.test2019.tyapp.longhuan.activity.SwitchSpeechNewSettingActivity;
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

import java.util.HashMap;
import java.util.Map;

import static com.test2019.tyapp.longhuan.global.Global.ALPHA_DARK;
import static com.test2019.tyapp.longhuan.global.Global.ALPHA_LIGHT;

public class SwitchNewFragment extends Fragment implements View.OnClickListener,
        IDevListener, ISpeechView, View.OnTouchListener {

    //====== UI element =========//

    private LinearLayout lin_dev_option_wrapper;
    private FrameLayout frameSpeechSetting;
    private RelativeLayout rel_switch;
    private ImageView btnSwitch, img_switch_ring;
    private TextView txtDeviceName;
    private FrameLayout fram_txt_wrap;
    private TextView txtRecognized;
    private RippleBackground rippleView;
    private ImageView imgRecognizerEffect;

    private boolean bShowOption = false;

    //========== device info =========//
    private DeviceBean mDevBean;
    private String mDevId;

    private boolean bOn;                // show device is on or off

    //======= linstener ===============//
    private ITuyaDevice mITuyaDevice;

    //======= speech manage ===========//
    private SpeechManage mSpeechManage;
    private String mSt_currentLanguage;
    private boolean isRecord;

    private int nFromVoiceCmd;


    private boolean isCheckall, isTurnOn, isTurnOnResponse, isTurnOff, isTurnOffResponse, isReverse, isGetOn, isGetOff;
    private String stTurnOn, stTurnOnResponse, stTurnOff, stTurnOffResponse, stReverse, stGetOn, stGetOff;

    public SwitchNewFragment(){}

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_switch, container, false);

        rel_switch = v.findViewById(R.id.rel_switch);
        btnSwitch = v.findViewById(R.id.img_switch);
        img_switch_ring = v.findViewById(R.id.img_switch_ring);
        txtDeviceName = v.findViewById(R.id.txt_device_name);
        fram_txt_wrap = v.findViewById(R.id.fram_txt_wrap);
        txtRecognized = v.findViewById(R.id.txt_recognized);
        rippleView = v.findViewById(R.id.rippleView);
        imgRecognizerEffect = v.findViewById(R.id.centerImage);

        lin_dev_option_wrapper = v.findViewById(R.id.lin_dev_option_wrapper);
        frameSpeechSetting = v.findViewById(R.id.frame_speech_setting);

        rel_switch.setOnClickListener(this);
        imgRecognizerEffect.setOnTouchListener(this);

        //=============== frame option click ===============
        v.findViewById(R.id.frame_option).setOnClickListener(this::onClick);
        v.findViewById(R.id.frame_edit_device).setOnClickListener(this::onClick);
        v.findViewById(R.id.frame_edit_icon).setOnClickListener(this::onClick);
        frameSpeechSetting.setOnClickListener(this);

        return v;
    }

    @Override
    public void onStart() {
        super.onStart();

        bShowOption = false;
        showOpthionWrapper(bShowOption);

        init_data();
        registerListener();
        init_speech();
    }

    @Override
    public void onStop() {
        super.onStop();

        mSpeechManage.destroy();
        mITuyaDevice.unRegisterDevListener();

        if (mITuyaDevice != null)
            mITuyaDevice.onDestroy();
    }

    private void showOpthionWrapper(boolean bonoff){
        if (bonoff) lin_dev_option_wrapper.setVisibility(View.VISIBLE);
        else lin_dev_option_wrapper.setVisibility(View.GONE);

    }

    private void init_data() {
        if (getArguments() != null) mDevId = getArguments().getString(Global.CURRENT_DEV);
        else getActivity().finish();

        if (mDevId == null) getActivity().finish();
        mDevBean = TuyaHomeSdk.getDataInstance().getDeviceBean(mDevId);
        if (mDevBean == null) getActivity().finish();

        PreferenceUtils.set_icon_image(this.getContext(), mDevBean, btnSwitch);

        Map<String, Object> dps = mDevBean.dps;
        if (dps.containsKey(Global.SWITCH_DPID_CMD)){
            bOn = (boolean)dps.get(Global.SWITCH_DPID_CMD);
        }

        updateUI(bOn);

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

    private void updateUI(boolean bOnOff) {
        if (bOnOff) {           // device is on
            btnSwitch.setAlpha(ALPHA_LIGHT);
            txtRecognized.setText("Device is On.");
            txtRecognized.setAlpha(ALPHA_LIGHT);
            fram_txt_wrap.setBackgroundResource(R.drawable.round_border_blue);
            fram_txt_wrap.setAlpha(ALPHA_LIGHT);
            img_switch_ring.setImageResource(R.drawable.ring_blue);
            img_switch_ring.setAlpha(ALPHA_LIGHT);
        }
        else {
            // device is off
            btnSwitch.setAlpha(ALPHA_DARK);
            txtRecognized.setText("Device is Off.");
            txtRecognized.setAlpha(ALPHA_DARK);
            fram_txt_wrap.setBackgroundResource(R.drawable.round_border_white);
            fram_txt_wrap.setAlpha(ALPHA_DARK);
            img_switch_ring.setImageResource(R.drawable.ring_white);
            img_switch_ring.setAlpha(ALPHA_DARK);
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
            case R.id.rel_switch:
                cmd_turn_on_off();
                break;
            case R.id.frame_speech_setting:
                Intent intent_speech_setting = new Intent(getContext(), SwitchSpeechNewSettingActivity.class);
                intent_speech_setting.putExtra(Global.CURRENT_DEV, mDevId);
                ActivityUtils.startActivity(getActivity(), intent_speech_setting, ActivityUtils.ANIMATE_FORWARD, false);
                break;
            case R.id.frame_edit_device:
                Intent intent = new Intent(getContext(), SelectDeviceIconActivity.class);
                intent.putExtra(Global.FROM_ACTIVITY, "MainActivity");
                intent.putExtra(Global.CURRENT_DEV, mDevId);
                intent.putExtra(Global.CURRENT_DEV_CATEGORY, 2);
                ActivityUtils.startActivity(getActivity(), intent, ActivityUtils.ANIMATE_FORWARD, false);
                break;
            case R.id.frame_edit_icon:
                Intent intent_edit_icon = new Intent(getContext(), SelectCircleIconActivity.class);
                intent_edit_icon.putExtra(Global.FROM_ACTIVITY, "MainActivity");
                intent_edit_icon.putExtra(Global.CURRENT_DEV, mDevId);
                intent_edit_icon.putExtra(Global.CURRENT_DEV_CATEGORY, 2);
                ActivityUtils.startActivity(getActivity(),intent_edit_icon, ActivityUtils.ANIMATE_FORWARD, false);
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
                speechGetOn();
                nFromVoiceCmd = 0;
                break;
            case 4:
                nFromVoiceCmd = 0;
                break;

        }
    }

    private void speechGetOn() {
        if (bOn) {
            if (isCheckall && isGetOn && !stGetOn.equals(Global.EMPTY)) {
                mSpeechManage.playSpeech(stGetOn);
            }
            else {
                mSpeechManage.playSpeech("Device is on");
            }
        }
        else {
            if (isCheckall && isGetOff && !stGetOff.equals(Global.EMPTY)) {
                mSpeechManage.playSpeech(stGetOff);
            }
            else {
                mSpeechManage.playSpeech("Device is off");
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
        isCheckall = isTurnOn = isTurnOnResponse = isTurnOff = isTurnOffResponse = isReverse = isGetOn = isGetOff  = false;
        stTurnOn = stTurnOnResponse = stTurnOff = stTurnOffResponse = stReverse = stGetOn = stGetOff  = Global.EMPTY;

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

    //======= ISpeechView ========//
    @Override
    public void showText(String text) {
        ToastUtil.shortToast(getContext(), text);

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
                nFromVoiceCmd = 3;
                cmd_turn_on_off();
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

}
