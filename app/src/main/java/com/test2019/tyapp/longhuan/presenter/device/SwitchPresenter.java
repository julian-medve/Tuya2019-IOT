package com.test2019.tyapp.longhuan.presenter.device;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.core.content.ContextCompat;
import androidx.core.content.PermissionChecker;
import android.util.Log;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.test2019.tyapp.longhuan.MainApplication;
import com.test2019.tyapp.longhuan.fragment.device.common.DpCountDownLatch;
import com.test2019.tyapp.longhuan.global.Global;
import com.test2019.tyapp.longhuan.speech.SpeechManage;
import com.test2019.tyapp.longhuan.speech.SpeechModel;
import com.test2019.tyapp.longhuan.utils.DeviceUtil;
import com.test2019.tyapp.longhuan.utils.PreferenceUtils;
import com.test2019.tyapp.longhuan.view.ISpeechView;
import com.test2019.tyapp.longhuan.view.device.ISwitchView;
import com.tuya.smart.android.mvp.presenter.BasePresenter;
import com.tuya.smart.home.sdk.TuyaHomeSdk;
import com.tuya.smart.sdk.api.IDevListener;
import com.tuya.smart.sdk.api.IResultCallback;
import com.tuya.smart.sdk.api.ITuyaDevice;
import com.tuya.smart.sdk.bean.DeviceBean;

import org.json.JSONException;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

public class SwitchPresenter extends BasePresenter implements IDevListener, ISpeechView {

    private static final String TAG = "SwitchPresenter";

    private static final int RECORD_PERMISSION_RESULT = 333;

    private Activity mContext;
    private Fragment mFragment;
    private ISwitchView mView;

    private String mDevId;
    private DeviceBean mDevBean;
    private ITuyaDevice mTuyaDevice;
    private DpCountDownLatch mDownLatch;

    private SpeechManage speechManage;
//    private TextToSpeech textToSpeech;

    private SpeechModel speechModel;
    private boolean isSpeech = false;
    private Boolean bOpen_temp;
    private String mSt_currentLanguage = "en-US";

    public static String[] PERMISSIONS = {
            Manifest.permission.CHANGE_WIFI_STATE,
            Manifest.permission.ACCESS_WIFI_STATE,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.READ_PHONE_STATE
    };

    public SwitchPresenter(Activity context, Fragment fragment, ISwitchView view) {
        this.mContext = context;
        this.mFragment = fragment;
        this.mView = view;
        Log.d(TAG, "SwitchPresenter:createview");
//        checkPermission(PERMISSIONS, RECORD_PERMISSION_RESULT);
        checkSinglePermission(Manifest.permission.RECORD_AUDIO, RECORD_PERMISSION_RESULT);
//        initData();
    }

    //========= init =====================//
    public void initData() {
        if (mFragment.getArguments() != null)
            mDevId = mFragment.getArguments().getString(Global.CURRENT_DEV);
        mDevBean = TuyaHomeSdk.getDataInstance().getDeviceBean(mDevId);

        if (mDevBean == null) {
            mContext.finish();
        } else {
            mView.updateTitle(mDevBean.getName());

            //====== my test device info ==========//
            mView.initDeviceInfo(mDevBean);
            mView.setDevImage(mDevBean);
//===========================================================//
            boolean open = (boolean) TuyaHomeSdk.getDataInstance().getDp(mDevBean.getDevId(), Global.SWITCH_DPID_CMD);
            bOpen_temp = open;

            mView.setStatus(open);
            if (open) {
                mView.showOpenView();
            }
            else mView.showCloseView();
        }
    }

    public void onStart() {
        Log.d(TAG, "onStart");
        initListener();
        initSpeech();
    }

    private void initListener() {
        if (mTuyaDevice == null)
            mTuyaDevice = TuyaHomeSdk.newDeviceInstance(mDevId);
        mTuyaDevice.registerDevListener(this);
    }

    private void initSpeech() {
        Log.d(TAG, "initSpeech: ");

        //==== init speech module ===//
        init_speechModule();

        speechManage = new SpeechManage(mContext, this);
        mSt_currentLanguage = MainApplication.getDefaultEncoding();
//        textToSpeech = new TextToSpeech(mContext, (event) -> {
//
//            if (event == TextToSpeech.SUCCESS) {
//                //== init textToSpeech ====//
//                init_TextToSpeech_Voice();
//                speechManage.setTextToSpeech(textToSpeech);
//            } else {
//                ToastUtil.showToast(mContext, "Failed to init TextToSpeech Setting");
//            }
//        });
    }

    private void init_speechModule() {
        if (speechModel == null)
            speechModel = new SpeechModel();
        String settings = PreferenceUtils.getString(mContext, MainApplication.getDefaultEncoding());

        if (settings == null)
            return;
        org.json.JSONObject result;
        try {
            result = new org.json.JSONObject(settings);

            speechModel.isCheckAll = result.getBoolean(Global.SPEECH_SETTING_ALL);
            speechModel.mTurnOn = result.getString(Global.SPEECH_SETTING_TURN_ON);
            speechModel.isTurnOn = result.getBoolean(Global.SPEECH_SETTING_TURN_ON_CHECK);
            speechModel.mTurnOnReply = result.getString(Global.SPEECH_SETTING_TURN_ON_RESPOND);
            speechModel.isTurnOnReply = result.getBoolean(Global.SPEECH_SETTING_TURN_ON_RESPOND_CHECK);
            speechModel.mTurnOff = result.getString(Global.SPEECH_SETTING_TURN_OFF);
            speechModel.isTurnOff = result.getBoolean(Global.SPEECH_SETTING_TURN_OFF_CHECK);
            speechModel.mTurnOffReply = result.getString(Global.SPEECH_SETTING_TURN_OFF_RESPOND);
            speechModel.isTurnOffReply = result.getBoolean(Global.SPEECH_SETTING_TURN_OFF_RESPOND_CHECK);
            speechModel.mReversal = result.getString(Global.SPEECH_SETTING_TURN_RESERVE);
            speechModel.isReversal = result.getBoolean(Global.SPEECH_SETTING_TURN_RESERVE_CHECK);
            speechModel.mStatusOnReply = result.getString(Global.SPEECH_SETTING_GET_ON);
            speechModel.isGetOn = result.getBoolean(Global.SPEECH_SETTING_GET_ON_CHECK);
            speechModel.mStatusOffReply = result.getString(Global.SPEECH_SETTING_GET_OFF);
            speechModel.isGetOff = result.getBoolean(Global.SPEECH_SETTING_GET_OFF_CHECK);
        } catch (JSONException e) {
            Log.d(TAG, e.getMessage());
        }
    }

//    private void init_TextToSpeech_Voice() {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//
//            Locale ss = Locale.forLanguageTag(MainApplication.getDefaultEncoding());
//            textToSpeech.setLanguage(Locale.forLanguageTag(MainApplication.getDefaultEncoding()));  // "en-US" or "pl-PL"
//Set<Locale> languages = textToSpeech.getAvailableLanguages();
//            Set<Voice> voices = textToSpeech.getVoices();
//            if (voices == null){
//                return;
//            }
//            // set male Polish voice as default (if it is available)
//            for (Voice tmpVoice : voices) {
//                if (tmpVoice.getName().toLowerCase().contains("#male") && tmpVoice.getName().toLowerCase().contains(MainApplication.getDefaultEncodingPrefix())) {
//                    //if (tmpVoice.getName().toLowerCase().contains(MainApplication.getDefaultEncodingPrefix())) {
//                    textToSpeech.setVoice(tmpVoice);
//                    break;
//                }
//            }
//        } else {
//            textToSpeech.setLanguage(new Locale(MainApplication.getDefaultEncoding()));
////            Set<Voice> voices = textToSpeech.getVoices()
//        }
//    }

    public void onStop() {
        Log.d(TAG, "onStop");
        if (mDownLatch != null) {
            mDownLatch.countDown();
        }

//        textToSpeech.shutdown();
        speechManage.destroy();
        mTuyaDevice.unRegisterDevListener();

        if (mTuyaDevice != null)
            mTuyaDevice.onDestroy();
    }

    //===== click switch event =============//
    public void onClick() {
        isSpeech = false;

        //===== Check for device is online ================//
        if (!DeviceUtil.isOnline(mDevId)){
            Toast.makeText(mContext, "Device is currently offline!", Toast.LENGTH_LONG).show();
            return;
        }

        onClickSwitch();
    }

    private void onClickSwitch() {
        if (mDownLatch != null) {
            return;
        }

        mView.setStatus(!mView.getStatus());

        new Thread(()-> {
            sendCommand();
            try {
                mDownLatch.await(2, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            checkResult();
            mDownLatch = null;
        }).start();
    }

    private void checkResult() {
        if (mDownLatch.getStatus() == DpCountDownLatch.STATUS_ERROR) {
            ((Activity) mContext).runOnUiThread(()->mView.showErrorTip());
        }
    }

    private void sendCommand() {
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put(Global.SWITCH_DPID_CMD, mView.getStatus());
        mDownLatch = new DpCountDownLatch(1);
        mTuyaDevice.publishDps(JSONObject.toJSONString(hashMap), new IResultCallback() {
            @Override
            public void onError(String s, String s1) {
                mDownLatch.setStatus(DpCountDownLatch.STATUS_ERROR);
                mDownLatch.countDown();
            }

            @Override
            public void onSuccess() {
                String ss = "ss";
            }
        });
    }

    //==== IDevListender Overrides  ==========
    // it is happened when the state of switch is changed ==//
    @Override
    public void onDpUpdate(String devId, String dps) {
        JSONObject jsonObject = JSONObject.parseObject(dps);
        Log.d(TAG, "onDpUpdate: Clicked");
        Boolean open = (Boolean) jsonObject.getBoolean(Global.SWITCH_DPID_CMD);
        if (open == null)
            return;

        //==== Check repeat on the current state ======//
        if (bOpen_temp == open) return;
        bOpen_temp = open;
        //============================//

        mView.setStatus(open);

        if (open) {

            mView.showOpenView();
            if (speechModel.isCheckAll && speechModel.isGetOn && !isSpeech) {
                Log.d(TAG, "onDpUpdate: on");
                speechManage.playSpeech(speechModel.mStatusOnReply);
                mView.setRecognizedText(speechModel.mStatusOnReply);
            }

            if (speechModel.isCheckAll && speechModel.isTurnOnReply && isSpeech) {
                speechManage.playSpeech(speechModel.mTurnOnReply);
                mView.setRecognizedText(speechModel.mTurnOn);
            }
        }
        else {
            mView.showCloseView();
            if (speechModel.isCheckAll && speechModel.isGetOff && !isSpeech) {
                Log.d(TAG, "onDpUpdate: off");
                speechManage.playSpeech(speechModel.mStatusOffReply);
                mView.setRecognizedText(speechModel.mStatusOffReply);
            }

            if (speechModel.isCheckAll && speechModel.isTurnOffReply && isSpeech) {
                speechManage.playSpeech(speechModel.mTurnOffReply);
                mView.setRecognizedText(speechModel.mTurnOff);
            }
        }

        if (mDownLatch != null) {
            mDownLatch.countDown();
        }
    }

    @Override
    public void onRemoved(String s) {
        mView.showRemoveTip();
    }

    @Override
    public void onStatusChanged(String devId, boolean status) {
        mView.statusChangedTip(status);
    }

    @Override
    public void onNetworkStatusChanged(String devId, boolean status) {
        mView.changeNetworkErrorTip(status);
    }

    @Override
    public void onDevInfoUpdate(String devId) {
        String sss = devId;
    }
    //===========================================================================//

    public void SetSpeechAction(boolean action) {
        if (action)
            speechManage.startSpeechRecognizer();
        else
            speechManage.stopSpeechRecognizer();
    }

    private int recognizeCommand(String text) {

        int result = 0;

        if (!speechModel.isCheckAll)
            return result;
        if (speechModel.isTurnOn) {
            if (speechModel.mTurnOn != null && !speechModel.mTurnOn.isEmpty() && text.trim().equals(speechModel.mTurnOn.trim())) {
                result = 1;
            }
        }

        if (speechModel.isTurnOff) {
            if (speechModel.mTurnOff != null && !speechModel.mTurnOff.isEmpty() && text.trim().equals(speechModel.mTurnOff.trim())) {
                result = 2;
            }
        }

        if (speechModel.isReversal) {
            if (speechModel.mReversal != null && !speechModel.mReversal.isEmpty() && text.trim().equals(speechModel.mReversal.trim())) {
                result = 3;
            }
        }

        return result;
    }

    //======== ISpeechView Override =======================//
    @Override
    public void showText(String text) {
        int command = recognizeCommand(text);
        isSpeech = false;

        //======== check device is online ====================//
        if (!DeviceUtil.isOnline(mDevId)){
            Toast.makeText(mContext, "Device is currently offline!", Toast.LENGTH_LONG).show();
            return;
        }

        if (!speechModel.isCheckAll)
            return;
        if (command == 1) {
            isSpeech = true;
            boolean status = mView.getStatus();
            if (!status)
                onClickSwitch();
            else {
                if (speechModel.isTurnOnReply) {
                    if (mSt_currentLanguage.equals("en-US")){
                        speechManage.playSpeech("Device is already turned on");
                    }
                    else speechManage.playSpeech("Urządzenie jest już włączone");
                    mView.setRecognizedText(speechModel.mTurnOn);
                }
            }

        } else if (command == 2) {
            isSpeech = true;
            boolean status = mView.getStatus();
            if (status)
                onClickSwitch();
            else {
                if (speechModel.isTurnOffReply) {
                    if (mSt_currentLanguage.equals("en-US")){
                        speechManage.playSpeech("Device is already turned off");
                    }
                    else speechManage.playSpeech("Urządzenie jest już wyłączone");
                    mView.setRecognizedText(speechModel.mTurnOff);
                }
            }

        } else if (command == 3){
            onClickSwitch();
        } else {
            speechManage.playSpeech(Global.SPEECH_UNKNOWN_COMMAND);
            mView.setRecognizedText(Global.SPEECH_UNKNOWN_COMMAND);
        }
    }

    @Override
    public void showTest(String text) {
        mView.setReal(text);
    }

    //========= BasePresenter Override ===========//
    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy:");
        super.onDestroy();
//        if (mDownLatch != null) {
//            mDownLatch.countDown();
//        }
//
//        textToSpeech.shutdown();
//        speechManage.destroy();
//        mTuyaDevice.unRegisterDevListener();
//
//        if (mTuyaDevice != null)
//            mTuyaDevice.onDestroy();
    }

    //========= permission for audio recode ===============//
    private boolean checkPermission(String [] permissions, int resultCode) {
        boolean hasPermission;
        if (Build.VERSION.SDK_INT < 23) {
            return true;
        } else {
            hasPermission = hasPermissions(mContext, permissions);
        }

        if (!hasPermission) {
            ActivityCompat.requestPermissions(mContext, permissions,
                    resultCode);
            return false;
        }
        return true;
    }

    private boolean checkSinglePermission(String permission, int resultCode) {

        boolean hasPermission;
        if (Build.VERSION.SDK_INT < 23) {
            return true;
        } else {
            hasPermission = hasPermission(permission);
        }

        if (!hasPermission) {
            ActivityCompat.requestPermissions(mContext, new String[] {permission},
                    resultCode);
            return false;
        }

        return true;
    }

    private static boolean hasPermissions(Context context, String... permissions) {
        if (context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    private boolean hasPermission(String permission) {
        int targetSdkVersion = 0;
        try {
            final PackageInfo info = mContext.getPackageManager().getPackageInfo(
                    mContext.getPackageName(), 0);
            targetSdkVersion = info.applicationInfo.targetSdkVersion;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        boolean result = true;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            if (targetSdkVersion >= Build.VERSION_CODES.M) {
                // targetSdkVersion >= Android M, we can
                // use Context#checkSelfPermission
                result = ContextCompat.checkSelfPermission(mContext, permission)
                        == PackageManager.PERMISSION_GRANTED;
            } else {
                // targetSdkVersion < Android M, we have to use PermissionChecker
                result = PermissionChecker.checkSelfPermission(mContext, permission)
                        == PermissionChecker.PERMISSION_GRANTED;
            }
        }

        return result;
    }
}
