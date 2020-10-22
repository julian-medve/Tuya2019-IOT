package com.test2019.tyapp.longhuan.service;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.os.SystemClock;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import android.util.Log;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.test2019.tyapp.longhuan.MainApplication;
import com.test2019.tyapp.longhuan.R;
import com.test2019.tyapp.longhuan.app.Constant;
import com.test2019.tyapp.longhuan.global.Categories;
import com.test2019.tyapp.longhuan.global.Global;
import com.test2019.tyapp.longhuan.login.SplashActivity;
import com.test2019.tyapp.longhuan.speech.SpeechManage;
import com.test2019.tyapp.longhuan.speech.SpeechModel;
import com.test2019.tyapp.longhuan.utils.DeviceUtil;
import com.test2019.tyapp.longhuan.utils.PreferenceUtils;
import com.test2019.tyapp.longhuan.utils.ToastUtil;
import com.test2019.tyapp.longhuan.view.ISpeechView;
import com.tuya.smart.android.base.utils.PreferencesUtil;
import com.tuya.smart.android.common.utils.L;
import com.tuya.smart.home.sdk.TuyaHomeSdk;
import com.tuya.smart.home.sdk.api.ITuyaHomeStatusListener;
import com.tuya.smart.home.sdk.bean.HomeBean;
import com.tuya.smart.home.sdk.callback.ITuyaGetHomeListCallback;
import com.tuya.smart.home.sdk.callback.ITuyaHomeResultCallback;
import com.tuya.smart.sdk.api.IDevListener;
import com.tuya.smart.sdk.api.IResultCallback;
import com.tuya.smart.sdk.api.ITuyaDevice;
import com.tuya.smart.sdk.bean.DeviceBean;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AppService extends Service implements IDevListener, ISpeechView {

    private final static String TAG = "AppService";

    private NotificationManagerCompat notificationManager;
    private int curNotificationId = 0;

    private ArrayList<ITuyaDevice> devices;
    private Notification notification;

    private boolean isStarted;

    private String voicedata;
    private List<DeviceBean> arrayDevice;

    private ITuyaDevice mITuyaDevice;
    private DeviceBean mDevBean;
    private boolean bOn;
    private boolean bOnGet;
    private boolean bAgreeSpeech = false;


    private SpeechManage mSpeechManage;
    private String mSt_currentLanguage = "en-US";
    private SpeechModel speechModel;

    private CountDownTimer mCountDownTimer;

    private Context mContext;

    public AppService() {

    }

    @Override
    public void onCreate() {
        // Get the HandlerThread's Looper and use it for our Handler
        super.onCreate();
        Log.d(TAG, "onCreate");
        mContext = this;

        devices = new ArrayList<>();
        isStarted = false;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand: start service");

        voicedata = intent.getStringExtra("VoiceData");
        //mSpeechManage = new SpeechManage( mContext,this);

        ToastUtil.showToast(this, "service" + voicedata);

        initSpeech();

        if (!isStarted) {
            Log.d(TAG, "isStarted was false");
            getDataFromServer();
            isStarted = true;
        } else {
            Log.d(TAG, "isStarted was true ");
        }
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy: service done");
        notificationManager = NotificationManagerCompat.from(getApplicationContext());
        notificationManager.cancelAll();
        if (!devices.isEmpty()) {
            for (ITuyaDevice device : devices) {
                device.unRegisterDevListener();
            }
        }
        isStarted = false;
        RestartAlarm();
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


//        DeviceBean bean = TuyaHomeSdk.getDataInstance().getDeviceBean(devId);
//        notificationManager = NotificationManagerCompat.from(getApplicationContext());
//
//        String notificationMessage = "Unknown Device";
//        if (bean.getProductId().equals(Global.DEVICE_TYPE_SWITCH)) {
//
//            JSONObject jsonObject = JSONObject.parseObject(dpStr);
//            Boolean open = jsonObject.getBoolean(Global.SWITCH_DPID_CMD);
//            if (open == null)
//                return;
//            if (open) {
//                notificationMessage = "Switch Turn On";
//            } else {
//                notificationMessage = "Switch Turn Off";
//            }
//        }
//
//        notification = getNotification(notificationMessage);
//        if (curNotificationId != 0)
//            notificationManager.cancel(curNotificationId);
//        int notificationId = (int) ((new Date().getTime() / 1000L) % Integer.MAX_VALUE);
//        startForeground(notificationId, notification);
////        notificationManager.notify(notificationId, notification);
//        curNotificationId = notificationId;
    }

    private void startCountDown(long miliSecond) {

        mCountDownTimer = new CountDownTimer(miliSecond, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {
                bAgreeSpeech = true;
            }
        }.start();
    }


    public void getDataFromServer() {

        TuyaHomeSdk.getHomeManagerInstance().queryHomeList(new ITuyaGetHomeListCallback() {
            @Override
            public void onSuccess(List<HomeBean> homeBeans) {
                if (homeBeans.size() == 0) {
                    return;
                }

                final long homeId = homeBeans.get(0).getHomeId();
                Constant.HOME_ID = homeId;
                PreferencesUtil.set("homeId", Constant.HOME_ID);
                TuyaHomeSdk.newHomeInstance(homeId).getHomeDetail(new ITuyaHomeResultCallback() {
                    @Override
                    public void onSuccess(HomeBean bean) {

                        arrayDevice = bean.getDeviceList();
                        String dev_name = arrayDevice.get(0).getName();
                        Toast.makeText(getApplicationContext(), dev_name, Toast.LENGTH_SHORT).show();

                        getAllDevices(bean.getDeviceList());

                        if(voicedata != null){
                            DeviceSelect(voicedata);
                        }

                    }

                    @Override
                    public void onError(String errorCode, String errorMsg) {

                    }
                });
                TuyaHomeSdk.newHomeInstance(homeId).registerHomeStatusListener(new ITuyaHomeStatusListener() {
                    @Override
                    public void onDeviceAdded(String devId) {

                    }

                    @Override
                    public void onDeviceRemoved(String devId) {

                    }

                    @Override
                    public void onGroupAdded(long groupId) {

                    }

                    @Override
                    public void onGroupRemoved(long groupId) {

                    }

                    @Override
                    public void onMeshAdded(String meshId) {
                        L.d(TAG, "onMeshAdded: " + meshId);
                    }


                });

            }

            @Override
            public void onError(String errorCode, String error) {

                Constant.HOME_ID = PreferencesUtil.getLong("homeId", Constant.HOME_ID);
                TuyaHomeSdk.newHomeInstance(Constant.HOME_ID).getHomeLocalCache(new ITuyaHomeResultCallback() {
                    @Override
                    public void onSuccess(HomeBean bean) {

                        arrayDevice = bean.getDeviceList();
                        String dev_name = arrayDevice.get(0).getName();
                        Toast.makeText(getApplicationContext(), dev_name, Toast.LENGTH_SHORT).show();

                        L.d(TAG, com.alibaba.fastjson.JSONObject.toJSONString(bean));
                        getAllDevices(bean.getDeviceList());

                        if(voicedata != null){
                            DeviceSelect(voicedata);
                        }
                    }

                    @Override
                    public void onError(String errorCode, String errorMsg) {
                    }
                });
            }
        });
    }

    private void getAllDevices(List<DeviceBean> all) {

        devices.clear();
        if (!all.isEmpty()) {
            for (DeviceBean bean : all) {
                ITuyaDevice device = TuyaHomeSdk.newDeviceInstance(bean.getDevId());
                Log.d(TAG, "getAllDevices: " + bean.getDevId());

                device.registerDevListener(this);
                devices.add(device);
            }

        }
    }

    private void DeviceSelect(String voice){

        String voicetext = voice.toLowerCase();

        String[] array = voicetext.split(" ");
        String commandtext = "";

        for (int i = 1; i < array.length; i ++){
            commandtext = commandtext + " " + array[i];
        }

        for (int i = 0; i< arrayDevice.size(); i ++){
           String devId = arrayDevice.get(i).getDevId();
           String dev_name = PreferenceUtils.getDevName(mContext, devId);

           String deviceName = arrayDevice.get(i).getName();
           String devNameTmp = deviceName.trim().toLowerCase();

           if(dev_name != null && array[0].equals(dev_name) && devNameTmp.contains(Categories.WIFI_SWITCH)){
               registerListener(devId);
               checkCommand(devId, commandtext);
           }

           Log.d(TAG, "DeviceSelect: " + dev_name);
        }

        isStarted = false;
    }


    private void registerListener(String mDevId){
        if (mITuyaDevice == null) mITuyaDevice = TuyaHomeSdk.newDeviceInstance(mDevId);
        mITuyaDevice.registerDevListener(this);

        ToastUtil.showToast(this, "success" + mDevId);
        Log.d(TAG, "success: " + mDevId);

        mDevBean = TuyaHomeSdk.getDataInstance().getDeviceBean(mDevId);
        if (mDevBean == null) this.stopSelf();

        Map<String, Object> dps = mDevBean.dps;
        if (dps.containsKey(Global.SWITCH_DPID_CMD)){
            bOn = (boolean)dps.get(Global.SWITCH_DPID_CMD);
        }

    }

    private void cmd_turn_on_off(String mDevId){

        if(!DeviceUtil.isOnline(mDevId)){
            ToastUtil.showToast(this, "Device is currently offline!");
            return;
        }

        bOn = !bOn;

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put(Global.SWITCH_DPID_CMD, bOn);
        send_CMD(hashMap);

        //startCountDown(1000);
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

    //-------------- unused code -------------------

    private Notification getNotification(String message) {

        Intent intent = new Intent(this, SplashActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

        return new NotificationCompat.Builder(this.getApplicationContext(), Global.NOTIFICATION_CHANNEL)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("Alert")
                .setContentText(message)
                .setAutoCancel(true)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setContentIntent(pendingIntent)
                .setVibrate(new long[] {300, 300})
                .setPriority(NotificationManagerCompat.IMPORTANCE_HIGH)
                .build();
    }

    private void RestartAlarm()
    {
        if (!isStarted) {
            ToastUtil.showToast(this, "Start AlarmService");
            Intent intent = new Intent(this, RestartReceiver.class);
            intent.setAction("ACTION_RESTART");
            sendBroadcast(intent);

            PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, 0);
            long firstTime = SystemClock.elapsedRealtime();
            Log.d(TAG, "RestartAlarm: " + firstTime);
            firstTime += 1000;

            AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
            alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, firstTime, 1000, pendingIntent);
        }
    }

    @Override
    public void onRemoved(String s) {

    }

    @Override
    public void onStatusChanged(String s, boolean b) {

    }

    @Override
    public void onNetworkStatusChanged(String s, boolean b) {

    }

    @Override
    public void onDevInfoUpdate(String s) {

    }


    @Override
    public void showText(String text) {

    }

    @Override
    public void showTest(String text) {

    }

    //========= voice recognizer switch ============

    private void initSpeech(){
        Log.d(TAG, "initSpeech: ");

        init_speechModule();

        mSpeechManage = new SpeechManage( mContext,this);
        mSt_currentLanguage = MainApplication.getDefaultEncoding();

    }

    private void init_speechModule(){
        if(speechModel == null)
            speechModel = new SpeechModel();
        String settings = PreferenceUtils.getString(mContext, MainApplication.getDefaultEncoding());

        if(settings == null)
            return;
        try {
            org.json.JSONObject result = new org.json.JSONObject(settings);

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

    private int recognizeCommand(String text){
        int result = 0;


        if(!speechModel.isCheckAll)
            return result;
        if (speechModel.isTurnOn) {
            if (speechModel.mTurnOn != null && !speechModel.mTurnOn.isEmpty() && speechModel.mTurnOn.toLowerCase().trim().equals(text.toLowerCase().trim())) {
                result = 1;
            }
        }

        if (speechModel.isTurnOff) {
            if (speechModel.mTurnOff != null && !speechModel.mTurnOff.isEmpty() && speechModel.mTurnOff.toLowerCase().trim().equals(text.toLowerCase().trim())) {
                result = 2;
            }
        }

        if (speechModel.isReversal) {
            if (speechModel.mReversal != null && !speechModel.mReversal.isEmpty() && speechModel.mReversal.toLowerCase().trim().equals(text.toLowerCase().trim())) {
                result = 3;
            }
        }

        return result;

    }

    private void checkCommand(String mdevId, String commandtext){

        int command = recognizeCommand(commandtext);

        if(!DeviceUtil.isOnline(mdevId)){
            Toast.makeText(mContext, "Device is currently offline!", Toast.LENGTH_LONG).show();
            return;
        }

        if(!speechModel.isCheckAll){
            return;
        }

        if(command == 1){

            if(bOn){
                if (speechModel.isTurnOnReply) {
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
                if (speechModel.isTurnOffReply) {
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
            if(speechModel.isCheckAll && speechModel.isTurnOnReply && !speechModel.mTurnOnReply.equals(Global.EMPTY)){
                mSpeechManage.playSpeech(speechModel.mTurnOnReply);
            }else{
                mSpeechManage.playSpeech("Device is open");
            }
        }else{
            if(speechModel.isCheckAll && speechModel.isTurnOffReply && !speechModel.mTurnOffReply.equals(Global.EMPTY)){
                mSpeechManage.playSpeech(speechModel.mTurnOffReply);
            }else{
                mSpeechManage.playSpeech("Device is closed");
            }
        }

        bAgreeSpeech = false;
    }
}
