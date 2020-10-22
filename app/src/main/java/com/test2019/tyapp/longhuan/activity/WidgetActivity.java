package com.test2019.tyapp.longhuan.activity;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.skyfishjy.library.RippleBackground;
import com.test2019.tyapp.longhuan.R;
import com.test2019.tyapp.longhuan.app.Constant;
import com.test2019.tyapp.longhuan.global.Categories;
import com.test2019.tyapp.longhuan.presenter.CurtainSwitchPresenter;
import com.test2019.tyapp.longhuan.presenter.SocketSpeechPresenter;
import com.test2019.tyapp.longhuan.presenter.SwitchNewPresenter;
import com.test2019.tyapp.longhuan.service.AppService;
import com.test2019.tyapp.longhuan.speech.SpeechRecognizerManager;
import com.test2019.tyapp.longhuan.utils.PreferenceUtils;
import com.test2019.tyapp.longhuan.utils.ToastUtil;
import com.test2019.tyapp.longhuan.view.ISpeechView;
import com.test2019.tyapp.longhuan.widget.Voicewidget;
import com.tuya.smart.android.base.utils.PreferencesUtil;
import com.tuya.smart.android.common.utils.L;
import com.tuya.smart.home.sdk.TuyaHomeSdk;
import com.tuya.smart.home.sdk.api.ITuyaHomeStatusListener;
import com.tuya.smart.home.sdk.bean.HomeBean;
import com.tuya.smart.home.sdk.callback.ITuyaGetHomeListCallback;
import com.tuya.smart.home.sdk.callback.ITuyaHomeResultCallback;
import com.tuya.smart.sdk.api.IDevListener;
import com.tuya.smart.sdk.api.ITuyaDevice;
import com.tuya.smart.sdk.bean.DeviceBean;

import java.util.ArrayList;
import java.util.List;

public class WidgetActivity extends AppCompatActivity implements View.OnTouchListener, View.OnClickListener, IDevListener, ISpeechView {

    public static final String PREFS_NAME = "voice";
    public static final String PREFS_VOICE_RECOGNITION_RESULT = "result";
    public static final String EXTRA_APP_WIDGET_ID = "app_widget_id";
    private static final String TAG = "VoiceWidget";

    private final String HOME_ID = "tuya_home_id";

    private int mAppWidgetId = 0;

    private SpeechRecognizerManager mSpeechManager;

    private RippleBackground rippleView;
    private RelativeLayout img_voice_recoding;
    private boolean isRecord;

    private ImageView iv_voiceswitchring;
    private ImageView iv_close;
    private TextView tv_voiceText;

    private String voicetext = "";

    private ArrayList<ITuyaDevice> devices;

    private List<DeviceBean> arrayDevice;

    private ITuyaDevice mITuyaDevice;
    private DeviceBean mDevBean;
    private String devId;
    private boolean bOn;
    private boolean bOnGet;
    private boolean bAgreeSpeech = false;

    private SwitchNewPresenter mSwitchPresenter;
    private CurtainSwitchPresenter mCurtainPresenter;
    private SocketSpeechPresenter mSocketPresenter;



    private Context mContext;

    private boolean isCurtainState = false;
    private boolean isSocketState = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_widget);

//        Log.d(TAG, "onCreate: " + getIntent().getData());
//        mAppWidgetId = Integer.parseInt(getIntent().getData().toString());
//        final int appWidgetId = getIntent().getIntExtra(EXTRA_APP_WIDGET_ID, -1);
//        Log.d(TAG, "onCreate: " + appWidgetId);

        init_view();
        init_presenter();

    }

    private void init_view(){

        mContext = this;
        devices = new ArrayList<>();

        rippleView = (RippleBackground)findViewById(R.id.rippleView);
        iv_voiceswitchring = (ImageView)findViewById(R.id.voice_switch_ring);
        iv_close = (ImageView)findViewById(R.id.iv_close);
        tv_voiceText = (TextView)findViewById(R.id.tv_voiceText);
        img_voice_recoding = (RelativeLayout) findViewById(R.id.img_voice_recoding);

        iv_close.setOnClickListener(this::onClick);
        img_voice_recoding.setOnTouchListener(this);

    }

    private void startSpeechManage(){
        mSwitchPresenter.startSpeechManage();
        mCurtainPresenter.startSpeechManage();
        mSocketPresenter.startSpeechManage();

    }

    private void destroySpeechManage(){
        mSwitchPresenter.SpeechManageDestroy();
        mCurtainPresenter.SpeechManageDestroy();
        mSocketPresenter.SpeechManageDestroy();
    }

    private void init_presenter(){
        mSwitchPresenter = new SwitchNewPresenter(this);
        mCurtainPresenter = new CurtainSwitchPresenter(this);
        mSocketPresenter = new SocketSpeechPresenter(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.iv_close:
                destroySpeechManage();
                finish();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    protected void onResume() {
        super.onResume();

        startSpeechManage();

    }

    @Override
    protected void onPause() {
        super.onPause();

        destroySpeechManage();
        CurtainDeviceState();

    }

    private void SetSpeechListener(){

        mSpeechManager = new SpeechRecognizerManager(this, new SpeechRecognizerManager.onResultsReady() {
            @Override
            public void onResults(ArrayList<String> results) {
                if(results != null && results.size() > 0 ){
                    mSpeechManager.destroy();
                    mSpeechManager = null;

//                    final SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
//                    final SharedPreferences.Editor editor = prefs.edit();
                    final ArrayList<String> matches = results;

                    voicetext = matches.get(0);

                    tv_voiceText.setText(voicetext);

                    getDataFromServer();

//                    if(matches.size() > 0){
//                        editor.putString(PREFS_VOICE_RECOGNITION_RESULT, matches.get(0));
//                    }
//                    editor.commit();

                    //Senddata_Service(voicetext);
                    //updateRequestForVoiceWidget();

                    Toast.makeText(getApplicationContext(), results.get(0), Toast.LENGTH_SHORT).show();

                }else{
                    tv_voiceText.setText("I'm sorry, I don't understand what you just said");
                    mSpeechManager.destroy();
                    Toast.makeText(getApplicationContext(), "No matching result", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void getDataFromServer() {

        TuyaHomeSdk.getHomeManagerInstance().queryHomeList(new ITuyaGetHomeListCallback() {
            @Override
            public void onSuccess(List<HomeBean> homeBeans) {
                if (homeBeans.size() == 0) {
                    return;
                }

                final long homeId = homeBeans.get(0).getHomeId();
                String roomName = homeBeans.get(0).getRooms().get(0).getName();



                ToastUtil.showToast(mContext, roomName);

                Constant.HOME_ID = homeId;
                PreferencesUtil.set("homeId", Constant.HOME_ID);
                TuyaHomeSdk.newHomeInstance(homeId).getHomeDetail(new ITuyaHomeResultCallback() {
                    @Override
                    public void onSuccess(HomeBean bean) {

                        arrayDevice = bean.getDeviceList();
                        String dev_name = arrayDevice.get(0).getName();

                        Toast.makeText(getApplicationContext(), dev_name, Toast.LENGTH_SHORT).show();

                        getAllDevices(bean.getDeviceList());

                        if(voicetext != null){
                            DeviceSelect(voicetext);
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

                        if(voicetext != null){
                            DeviceSelect(voicetext);
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

    private void CurtainDeviceState(){

        if(isCurtainState){
            mCurtainPresenter.onStop();
        }

        if(isSocketState){
            mSocketPresenter.onStop();
        }

        isCurtainState = false;
        isSocketState = false;

    }

    private void DeviceSelect(String voice){

        String voicetext = voice.toLowerCase();

        CurtainDeviceState();

        String[] array = voicetext.split(" ");
        String all_arraytext = "";

        for (int i = 0; i< array.length; i ++){
            all_arraytext = all_arraytext + array[i];
        }

        for (int i = 0; i< arrayDevice.size(); i ++){

            devId = arrayDevice.get(i).getDevId();
            String dev_name = PreferenceUtils.getDevSearchName(mContext, devId);
            String cur_reverse_cmd = PreferenceUtils.getCurReverseCmd(mContext, devId);

            String deviceName = arrayDevice.get(i).getName();
            String devNameTmp = deviceName.trim().toLowerCase();

            if(cur_reverse_cmd != null){
                String[] cur_reverse_array = cur_reverse_cmd.split(" ");
                String all_cur_reverse_cmd = "";

                for (int j = 0; j < cur_reverse_array.length; j ++){
                    all_cur_reverse_cmd = all_cur_reverse_cmd + cur_reverse_array[j];
                }

                if(all_cur_reverse_cmd.equals(all_arraytext) && devNameTmp.contains(Categories.CURTAIN_SWITCH)){

                    isCurtainState = true;

                    mCurtainPresenter.registerListener(devId);
                    mCurtainPresenter.initSpeech();
                    mCurtainPresenter.checkCommand(devId, all_arraytext);

                    return;
                }if(all_arraytext.contains(all_cur_reverse_cmd) && devNameTmp.contains(Categories.SMART_WIFI_SOCKET)){

                    String commandtext = "";
                    isSocketState = true;

                    String[] arraytext = all_arraytext.split(all_cur_reverse_cmd);

                    if(dev_name != null){
                        String[] dev_name_array = dev_name.split(" ");
                        String all_dev_name = "";

                        for (int j = 0; j < dev_name_array.length; j ++){
                            all_dev_name = all_dev_name + dev_name_array[j];
                        }

                        if(all_cur_reverse_cmd.contains(all_dev_name) || all_dev_name.contains(all_cur_reverse_cmd)){

                            String[] arraydev_text = all_arraytext.split(all_dev_name);

                            if(arraydev_text.length != 0){
                                commandtext = arraytext[0];
                            }else{
                                commandtext = all_dev_name;
                            }
                        }
                    }


                    if(all_arraytext.equals(all_cur_reverse_cmd)){
                        commandtext = all_arraytext;
                    }else{
                        if(arraytext[0].isEmpty()){

                            String timeCmdtext = "";
                            int timeCmdNumber = 0;

                            for(int j = 0; j < array.length ; j ++){

                                timeCmdtext = timeCmdtext + array[j];

                                if(timeCmdtext.equals(all_cur_reverse_cmd)){
                                    timeCmdNumber = j;
                                    break;
                                }
                            }

                            for(int k = timeCmdNumber + 1; k < array.length; k ++){
                                commandtext = commandtext  + array[k] + " ";
                            }

                        }
                    }

                    mSocketPresenter.registerListener(devId, devNameTmp);
                    mSocketPresenter.initSpeech();
                    mSocketPresenter.checkCommand(commandtext);

                    return;

                }if(all_arraytext.contains(all_cur_reverse_cmd) && devNameTmp.contains(Categories.SMART_SOCKET)){

                    String commandtext = "";
                    isSocketState = true;

                    String[] arraytext = all_arraytext.split(all_cur_reverse_cmd);

                    if(dev_name != null){
                        String[] dev_name_array = dev_name.split(" ");
                        String all_dev_name = "";

                        for (int j = 0; j < dev_name_array.length; j ++){
                            all_dev_name = all_dev_name + dev_name_array[j];
                        }

                        if(all_cur_reverse_cmd.contains(all_dev_name) || all_dev_name.contains(all_cur_reverse_cmd)){

                            String[] arraydev_text = all_arraytext.split(all_dev_name);

                            if(arraydev_text.length != 0){
                                commandtext = arraytext[0];
                            }else{
                                commandtext = all_dev_name;
                            }
                        }
                    }


                    if(all_arraytext.equals(all_cur_reverse_cmd)){
                        commandtext = all_arraytext;
                    }else{
                        if(arraytext[0].isEmpty()){

                            String timeCmdtext = "";
                            int timeCmdNumber = 0;

                            for(int j = 0; j < array.length ; j ++){

                                timeCmdtext = timeCmdtext + array[j];

                                if(timeCmdtext.equals(all_cur_reverse_cmd)){
                                    timeCmdNumber = j;
                                    break;
                                }
                            }

                            for(int k = timeCmdNumber + 1; k < array.length; k ++){
                                commandtext = commandtext  + array[k] + " ";
                            }

                        }
                    }

                    mSocketPresenter.registerListener(devId, devNameTmp);
                    mSocketPresenter.initSpeech();
                    mSocketPresenter.checkCommand(commandtext);

                    return;
                }if(all_arraytext.contains(all_cur_reverse_cmd) && devNameTmp.contains(Categories.SMART_INTELIGENTNY_SOCKET)){

                    String commandtext = "";
                    isSocketState = true;

                    String[] arraytext = all_arraytext.split(all_cur_reverse_cmd);

                    if(dev_name != null){
                        String[] dev_name_array = dev_name.split(" ");
                        String all_dev_name = "";

                        for (int j = 0; j < dev_name_array.length; j ++){
                            all_dev_name = all_dev_name + dev_name_array[j];
                        }

                        if(all_cur_reverse_cmd.contains(all_dev_name) || all_dev_name.contains(all_cur_reverse_cmd)){

                            String[] arraydev_text = all_arraytext.split(all_dev_name);

                            if(arraydev_text.length != 0){
                                commandtext = arraytext[0];
                            }else{
                                commandtext = all_dev_name;
                            }
                        }
                    }


                    if(all_arraytext.equals(all_cur_reverse_cmd)){
                        commandtext = all_arraytext;
                    }else{
                        if(arraytext[0].isEmpty()){

                            String timeCmdtext = "";
                            int timeCmdNumber = 0;

                            for(int j = 0; j < array.length ; j ++){

                                timeCmdtext = timeCmdtext + array[j];

                                if(timeCmdtext.equals(all_cur_reverse_cmd)){
                                    timeCmdNumber = j;
                                    break;
                                }
                            }

                            for(int k = timeCmdNumber + 1; k < array.length; k ++){
                                commandtext = commandtext  + array[k] + " ";
                            }

                        }
                    }

                    mSocketPresenter.registerListener(devId, devNameTmp);
                    mSocketPresenter.initSpeech();
                    mSocketPresenter.checkCommand(commandtext);

                    return;
                }
            }

            if(dev_name != null){

                String[] dev_name_array = dev_name.split(" ");
                String all_dev_name = "";

                for (int j = 0; j < dev_name_array.length; j ++){
                    all_dev_name = all_dev_name + dev_name_array[j];
                }


                if(all_arraytext.contains(all_dev_name) && devNameTmp.contains(Categories.WIFI_SWITCH)){

                    String commandtext = "";
                    String[] arraytext = all_arraytext.split(all_dev_name);

                    if(arraytext.length != 0){
                        commandtext = arraytext[0];
                    }else{
                        commandtext = all_dev_name;
                        ToastUtil.showToast(mContext, "This command is zero");
                    }

                    mSwitchPresenter.registerListener(devId);
                    mSwitchPresenter.initSpeech();
                    mSwitchPresenter.checkCommand(devId, commandtext);

                }else if(all_arraytext.contains(all_dev_name) && deviceName.contains(Categories.CHINESE_SWITCH)){

                    String commandtext = "";
                    String[] arraytext = all_arraytext.split(all_dev_name);

                    if(arraytext.length != 0){
                        commandtext = arraytext[0];
                    }else{
                        commandtext = all_dev_name;
                    }

                    mSwitchPresenter.registerListener(devId);
                    mSwitchPresenter.initSpeech();
                    mSwitchPresenter.checkCommand(devId, commandtext);

                }else if(all_arraytext.contains(all_dev_name) && deviceName.contains(Categories.SMART_TOUCH_SWITCH)){

                    String commandtext = "";
                    String[] arraytext = all_arraytext.split(all_dev_name);

                    if(arraytext.length != 0){
                        commandtext = arraytext[0];
                    }else{
                        commandtext = all_dev_name;
                    }

                    mSwitchPresenter.registerListener(devId);
                    mSwitchPresenter.initSpeech();
                    mSwitchPresenter.checkCommand(devId, commandtext);

                }
                else if(all_arraytext.contains(all_dev_name) && devNameTmp.contains(Categories.CURTAIN_SWITCH)){

                    String commandtext = "";
                    isCurtainState = true;

                    String[] arraytext = all_arraytext.split(all_dev_name);

                    if(arraytext.length != 0){
                        commandtext = arraytext[0];
                    }else{
                        commandtext = all_dev_name;
                    }

                    mCurtainPresenter.registerListener(devId);
                    mCurtainPresenter.initSpeech();
                    mCurtainPresenter.checkCommand(devId, commandtext);

                }else if(all_arraytext.contains(all_dev_name) && devNameTmp.contains(Categories.SMART_WIFI_SOCKET)){

                    String commandtext = "";
                    isSocketState = true;

                    String[] arraytext = all_arraytext.split(all_dev_name);

                    if(arraytext.length != 0){
                        commandtext = arraytext[0];
                    }else{
                        commandtext = all_dev_name;
                    }

                    mSocketPresenter.registerListener(devId, devNameTmp);
                    mSocketPresenter.initSpeech();
                    mSocketPresenter.checkCommand(commandtext);

                }else if(all_arraytext.contains(all_dev_name) && devNameTmp.contains(Categories.SMART_SOCKET)){

                    String commandtext = "";
                    isSocketState = true;

                    String[] arraytext = all_arraytext.split(all_dev_name);

                    if(arraytext.length != 0){
                        commandtext = arraytext[0];
                    }else{
                        commandtext = all_dev_name;
                    }

                    mSocketPresenter.registerListener(devId, devNameTmp);
                    mSocketPresenter.initSpeech();
                    mSocketPresenter.checkCommand(commandtext);

                }else if(all_arraytext.contains(all_dev_name) && devNameTmp.contains(Categories.SMART_INTELIGENTNY_SOCKET)){

                    String commandtext = "";
                    isSocketState = true;

                    String[] arraytext = all_arraytext.split(all_dev_name);

                    if(arraytext.length != 0){
                        commandtext = arraytext[0];
                    }else{
                        commandtext = all_dev_name;
                    }

                    mSocketPresenter.registerListener(devId, devNameTmp);
                    mSocketPresenter.initSpeech();
                    mSocketPresenter.checkCommand(commandtext);

                }
            }

            Log.d(TAG, "DeviceSelect: " + dev_name);
        }

    }


    @Override
    public boolean onTouch(View v, MotionEvent event) {
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
            iv_voiceswitchring.setImageResource(R.drawable.ring_widget_ledblue);

            SetSpeechListener();

        }
        else {
            rippleView.stopRippleAnimation();
            iv_voiceswitchring.setImageResource(R.drawable.ring_widget_ledwhite);

            if(mSpeechManager != null){
                mSpeechManager.cancel();
            }

        }
    }


    //================ Unused code ==================

    private void updateRequestForVoiceWidget(){
        if(mAppWidgetId != -1){
            sendBroadcast(new Intent(this, Voicewidget.class)
                    .setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE)
                    .putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, new int[]{mAppWidgetId})

            );
        }
        finish();
    }

    private void Senddata_Service(String data){

        Intent intent = new Intent(getApplicationContext(), AppService.class);
        intent.putExtra("VoiceData", data);
        this.startService(intent);
    }

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

    @Override
    public void onDpUpdate(String devId, String dpStr) {

    }
}
