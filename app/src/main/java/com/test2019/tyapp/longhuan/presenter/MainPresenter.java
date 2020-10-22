package com.test2019.tyapp.longhuan.presenter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;

import com.test2019.tyapp.longhuan.activity.ProfileSettingActivity;
import com.test2019.tyapp.longhuan.activity.RoomAddActivity;
import com.test2019.tyapp.longhuan.activity.SelectWifiActivity;
import com.test2019.tyapp.longhuan.datahelper.FamilySpHelper;
import com.test2019.tyapp.longhuan.fragment.device.CurtainSwitchFragment;
import com.test2019.tyapp.longhuan.fragment.device.DimmerFragment;
import com.test2019.tyapp.longhuan.fragment.device.SmartWiFiSocketFragment;
import com.test2019.tyapp.longhuan.fragment.device.SwitchNewFragment;
import com.test2019.tyapp.longhuan.fragment.device.common.CommonDeviceFragment;
import com.test2019.tyapp.longhuan.global.Global;
import com.test2019.tyapp.longhuan.model.RoomModel;
import com.test2019.tyapp.longhuan.R;
import com.test2019.tyapp.longhuan.activity.MainActivity;
import com.test2019.tyapp.longhuan.app.Constant;
import com.test2019.tyapp.longhuan.fragment.HomeFragment;
import com.test2019.tyapp.longhuan.service.AppService;
import com.test2019.tyapp.longhuan.service.RestartReceiver;
import com.test2019.tyapp.longhuan.utils.ActivityUtils;
import com.test2019.tyapp.longhuan.global.Categories;
import com.test2019.tyapp.longhuan.utils.CollectionUtils;
import com.test2019.tyapp.longhuan.utils.EventCurrentHomeChange;
import com.test2019.tyapp.longhuan.utils.PreferenceUtils;
import com.test2019.tyapp.longhuan.utils.ProgressUtil;
import com.test2019.tyapp.longhuan.utils.ToastUtil;
import com.test2019.tyapp.longhuan.view.IMainView;
import com.tuya.smart.android.base.event.NetWorkStatusEvent;
import com.tuya.smart.android.base.event.NetWorkStatusEventModel;
import com.tuya.smart.android.base.utils.PreferencesUtil;
import com.tuya.smart.android.common.utils.L;
import com.tuya.smart.android.mvp.presenter.BasePresenter;
import com.tuya.smart.android.user.api.ILogoutCallback;
import com.tuya.smart.android.user.bean.User;
import com.tuya.smart.home.sdk.TuyaHomeSdk;
import com.tuya.smart.home.sdk.api.ITuyaHomeStatusListener;
import com.tuya.smart.home.sdk.bean.HomeBean;
import com.tuya.smart.home.sdk.bean.RoomBean;
import com.tuya.smart.home.sdk.callback.ITuyaGetHomeListCallback;
import com.tuya.smart.home.sdk.callback.ITuyaHomeResultCallback;
import com.tuya.smart.mqtt.MqttService;
import com.tuya.smart.sdk.TuyaSdk;
import com.tuya.smart.sdk.api.IResultCallback;
import com.tuya.smart.sdk.bean.DeviceBean;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

public class MainPresenter extends BasePresenter implements NetWorkStatusEvent {

    private final String TAG = "MainPresenter";
    private final String HOME_ID = "tuya_home_id";

    private Activity mContext;
    private IMainView mView;

    private ArrayList<RoomModel> arrayRoom;
    private ArrayList<DeviceBean> arrayDeviceWithRoom;

    private HomeBean currentHomeBean;
    private FamilySpHelper mFamilySpHelper;


    private int mCurPosition;
    private boolean auto_start;
    private RestartReceiver receiver = null;

    public MainPresenter(Context mContext, IMainView mView) {
        this.mContext = (Activity) mContext;
        this.mView = mView;

        mCurPosition = 0;
        arrayRoom = new ArrayList<>();
        arrayDeviceWithRoom = new ArrayList<>();
        mFamilySpHelper = new FamilySpHelper();
        TuyaSdk.getEventBus().register(this);   //==========

        Constant.HOME_ID = PreferencesUtil.getLong("homeId", Constant.HOME_ID);

    }

    public void selectHome(boolean bRoom, boolean bMain) {
        mView.setHomeBackground(bMain);
        mView.showMainMenu(bRoom, bMain);
    }

    public void selectRoom(boolean bRoom, boolean bMain, int position) {
        mView.setHomeBackground(false);
        if (mCurPosition == position)
            mView.showRoomMenu(bRoom, bMain, true);
        else {
            mCurPosition = position;
            mView.showRoomMenu(bRoom, bMain, false);
        }

        initRoomSubMenu(position);
    }


    private void initRoom(List<RoomBean> roomList) {
        Log.d(TAG, "initRoom: Called");
        LoadRoom(roomList);
        mView.loadRoom(arrayRoom);

    }

    private void initRoomSubMenu(int position){
        getDeviceLists(position);
        mView.loadDevice(arrayDeviceWithRoom);
    }

    private void LoadRoom(List<RoomBean> lists) {

        arrayRoom.clear();

        for (int i = 0; i < lists.size(); i++) {
            int type = PreferenceUtils.getInt(mContext, Long.toString(lists.get(i).getRoomId()));
            RoomModel roomItem = new RoomModel(lists.get(i), type);
            arrayRoom.add(roomItem);
        }
    }

    private void getDeviceLists(int position) {

        arrayDeviceWithRoom.clear();

        long roomId = arrayRoom.get(position).getRoomBean().getRoomId();

        List<DeviceBean> deviceBeans = TuyaHomeSdk.getDataInstance().getRoomDeviceList(roomId);

        arrayDeviceWithRoom.addAll(deviceBeans);
    }

    public void onResume() {
        if (receiver != null)
            mContext.unregisterReceiver(receiver);
        mContext.stopService(new Intent(mContext, AppService.class));

        if (mContext.getIntent() != null){
            auto_start = mContext.getIntent().getBooleanExtra(Global.AUTOSTART_CHANNEL, false);
            if (auto_start){
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                {
                    mContext.finishAndRemoveTask();
                } else {
                    mContext.finish();
                }
            }
        }
    }

    public void onPause() {
        mContext.startService(new Intent(mContext, MqttService.class));
//        receiver = new RestartReceiver();
//        IntentFilter filter = new IntentFilter();
//        filter.addAction(Intent.ACTION_SCREEN_ON);
//        filter.addAction(Intent.ACTION_SCREEN_OFF);
//        filter.addAction(Intent.ACTION_USER_PRESENT);
//        mContext.registerReceiver(receiver, filter);
//
//        Intent intent = new Intent(mContext, RestartReceiver.class);
//        intent.setAction("ACTION_RESTART");
//        mContext.sendBroadcast(intent);
    }

    public void onDestroy() {
        TuyaSdk.getEventBus().unregister(this);
    }

    public void checkFamilyCount() {
        ProgressUtil.showLoading(mContext, R.string.loading);
        TuyaHomeSdk.getHomeManagerInstance().queryHomeList(new ITuyaGetHomeListCallback() {
            @Override
            public void onSuccess(List<HomeBean> list) {

                if (CollectionUtils.isEmpty(list)) { // && null == getCurrentHome()) {
                    Constant.finishActivity();

                    User user = TuyaHomeSdk.getUserInstance().getUser();
                    String homeName = user.getUid();
                    List<String> checkRoomList = new ArrayList<>();
                    createHome(homeName, checkRoomList);

                    return;
                }else {
                    Log.d(TAG, "onSuccess: checkFamily");
                    setCurrentHome(list.get(0));
                }

                if (ProgressUtil.isShowLoading())
                    ProgressUtil.hideLoading();
                long homeId = list.get(0).getHomeId();

                TuyaHomeSdk.newHomeInstance(homeId).getHomeDetail(new ITuyaHomeResultCallback() {
                    @Override
                    public void onSuccess(HomeBean bean) {
                        PreferenceUtils.set(mContext, HOME_ID, homeId);

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
            public void onError(String s, String s1) {
                TuyaHomeSdk.newHomeInstance(Constant.HOME_ID).getHomeLocalCache(new ITuyaHomeResultCallback() {
                    @Override
                    public void onSuccess(HomeBean bean) {
                        L.d(TAG, com.alibaba.fastjson.JSONObject.toJSONString(bean));
                    }

                    @Override
                    public void onError(String errorCode, String errorMsg) {

                    }
                });

                User user = TuyaHomeSdk.getUserInstance().getUser();
                String homeName = user.getUid();
                List<String> checkRoomList = new ArrayList<>();
                createHome(homeName, checkRoomList);
            }
        });
    }

    private void createHome(String homeName, List<String> roomList) {
        TuyaHomeSdk.getHomeManagerInstance().createHome(homeName,
        0, 0, "", roomList, new ITuyaHomeResultCallback() {
            @Override
            public void onSuccess(HomeBean homeBean) {

                Log.d(TAG, "onSuccess: createHome");
                setCurrentHome(homeBean);
            }

            @Override
            public void onError(String s, String s1) {
                if (ProgressUtil.isShowLoading())
                    ProgressUtil.hideLoading();
                Log.d(TAG, "onError: " + s + s1);
            }
        });
    }

    private void setCurrentHome(HomeBean homeBean) {

        if (ProgressUtil.isShowLoading()) {
            ProgressUtil.hideLoading();
        }

        if (null == homeBean) {
            return;
        }

        boolean isChange = false;

        if (null == currentHomeBean) {
            Log.i(TAG, "setCurrentHome  currentHome is null so push current home change event");
        } else {
            long currentHomeId = currentHomeBean.getHomeId();
            long targetHomeId = homeBean.getHomeId();
            Log.i(TAG, "setCurrentHome: currentHomeId=" + currentHomeId + " targetHomeId=" + targetHomeId);
            if (currentHomeId != targetHomeId) {
                isChange = true;
            }
        }

        currentHomeBean = homeBean;
        mFamilySpHelper.putCurrentHome(currentHomeBean);

        PreferencesUtil.set(Global.CURRENT_HOME, currentHomeBean.getHomeId());
        initRoom(currentHomeBean.getRooms());

        if (isChange) {
            EventBus.getDefault().post(new EventCurrentHomeChange(currentHomeBean));
        }
    }


    private HomeBean getCurrentHome() {
        if (null == currentHomeBean) {
            setCurrentHome(mFamilySpHelper.getCurrentHome());
        }
        return currentHomeBean;
    }


    public long getCurrentHomeId() {
        HomeBean currentHome = getCurrentHome();
        if (null == currentHome) {
            return -1;
        }
        return currentHome.getHomeId();
    }

    public void selectDevice(int position) {
        DeviceBean bean = arrayDeviceWithRoom.get(position);

        if (!bean.getIsOnline()){
            ToastUtil.showToast(mContext, "Device is offline");
            gotoHomeFragment();
            return;
        }

//        String deviceName = getDeviceCategory(bean.getName());
        String deviceName = bean.getName();

        Log.d(TAG, "selectDevice: " + bean.getCategory());
        Log.d(TAG, "selectDevice: Product ID: " + bean.getProductId());

//        Fragment switchFragment= SwitchFragment.newInstance();
//        switchFragment.setArguments(args);
//        ((MainActivity)mContext).getSupportFragmentManager().beginTransaction()
//                .replace(R.id.fragment_home, switchFragment, "switchFragment")
//                .addToBackStack(null)
//                .commit();
        String devNameTmp = deviceName.trim().toLowerCase();

        Bundle args = new Bundle();
        args.putString(Global.CURRENT_DEV, bean.devId);
        args.putString(Global.CURRENT_SOCKET, devNameTmp);

        if (devNameTmp.contains(Categories.WIFI_SWITCH) || deviceName.contains(Categories.CHINESE_SWITCH) || deviceName.contains(Categories.SMART_TOUCH_SWITCH)) {
            //Fragment switchFragment= SwitchFragment.newInstance();
            Fragment switchFragment = new SwitchNewFragment();
            switchFragment.setArguments(args);
            ((MainActivity)mContext).getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_home, switchFragment, Categories.WIFI_SWITCH)
                    .addToBackStack(null)
                    .commit();
        }
        else if (devNameTmp.contains(Categories.DIMMER_SWITCH)) {
            Fragment dimmerFragment = new DimmerFragment();
            dimmerFragment.setArguments(args);
            ((MainActivity)mContext).getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_home, dimmerFragment)
                    .addToBackStack(null)
                    .commit();
        }
        else if (devNameTmp.contains(Categories.SMART_WIFI_SOCKET)) {
            Fragment smartWifiSockectFragment = new SmartWiFiSocketFragment();
            smartWifiSockectFragment.setArguments(args);
            ((MainActivity)mContext).getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_home, smartWifiSockectFragment)
                    .addToBackStack(null)
                    .commit();
        }
        else if(devNameTmp.contains(Categories.SMART_SOCKET)){
            Fragment smartWifiSockectFragment = new SmartWiFiSocketFragment();
            smartWifiSockectFragment.setArguments(args);
            ((MainActivity)mContext).getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_home, smartWifiSockectFragment)
                    .addToBackStack(null)
                    .commit();
        }else if(devNameTmp.contains(Categories.SMART_INTELIGENTNY_SOCKET)){
            Fragment smartWifiSockectFragment = new SmartWiFiSocketFragment();
            smartWifiSockectFragment.setArguments(args);
            ((MainActivity)mContext).getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_home, smartWifiSockectFragment)
                    .addToBackStack(null)
                    .commit();
        }
        else if(devNameTmp.contains(Categories.CURTAIN_SWITCH)){
            Fragment curtainSwitchFragment = new CurtainSwitchFragment();
            curtainSwitchFragment.setArguments(args);
            ((MainActivity)mContext).getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_home, curtainSwitchFragment)
                    .addToBackStack(null)
                    .commit();
        }
        else {
            Fragment commonFragment = CommonDeviceFragment.newInstance();
            commonFragment.setArguments(args);
            ((MainActivity)mContext).getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_home, commonFragment, "commonFragment")
                    .addToBackStack(null)
                    .commit();
        }

//        switch (deviceName.trim().toLowerCase()) {
//            case Categories.WIFI_SWITCH:
//                Fragment switchFragment= SwitchFragment.newInstance();
//                switchFragment.setArguments(args);
//                ((MainActivity)mContext).getSupportFragmentManager().beginTransaction()
//                        .replace(R.id.fragment_home, switchFragment, Categories.WIFI_SWITCH)
//                        .addToBackStack(null)
//                        .commit();
//                break;
//            case Categories.LG_WIFI_SWITCH:
//                Fragment lgSwitchFragment= SwitchFragment.newInstance();
//                lgSwitchFragment.setArguments(args);
//                ((MainActivity)mContext).getSupportFragmentManager().beginTransaction()
//                        .replace(R.id.fragment_home, lgSwitchFragment, Categories.LG_WIFI_SWITCH)
//                        .addToBackStack(null)
//                        .commit();
//                break;
//            case Categories.DIMMER_SWITCH:
//                Fragment dimmerFragment = new DimmerFragment();
//                dimmerFragment.setArguments(args);
//                ((MainActivity)mContext).getSupportFragmentManager().beginTransaction()
//                        .replace(R.id.fragment_home, dimmerFragment)
//                        .addToBackStack(null)
//                        .commit();
//                break;
//            case Categories.SMART_WIFI_SOCKET:
//                Fragment smartWifiSockectFragment = new SmartWiFiSocketFragment();
//                smartWifiSockectFragment.setArguments(args);
//                ((MainActivity)mContext).getSupportFragmentManager().beginTransaction()
//                        .replace(R.id.fragment_home, smartWifiSockectFragment)
//                        .addToBackStack(null)
//                        .commit();
//                break;
//            case Global.DEVICE_TYPE_GATEWAY:
//                break;
//            case Global.DEVICE_TYPE_PIR:
//                break;
//            case Global.DEVICE_TYPE_TEMPERATURE:
//                break;
//            default:
//                Fragment commonFragment = CommonDeviceFragment.newInstance();
//                commonFragment.setArguments(args);
//                ((MainActivity)mContext).getSupportFragmentManager().beginTransaction()
//                        .replace(R.id.fragment_home, commonFragment, "commonFragment")
//                        .addToBackStack(null)
//                        .commit();
//                break;
//        }
    }

    public void gotoHomeFragment() {
        User userInfo = TuyaHomeSdk.getUserInstance().getUser();
        String username;

        if (userInfo.getNickName().isEmpty())
            username = userInfo.getUsername();
        else
            username = userInfo.getNickName();
        //Bundle args = new Bundle();
        //args.putString("UserName", username);

        Fragment homeFragment = HomeFragment.newInstance();
        //homeFragment.setArguments(args);

        PreferenceUtils.set(mContext, "username", username);

        ((MainActivity)mContext).getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_home, homeFragment, "homeFragment")
                .addToBackStack(null)
                .commit();

    }

    public void gotoProfileActivity() {
        ActivityUtils.gotoActivity(mContext, ProfileSettingActivity.class, ActivityUtils.ANIMATE_FORWARD, true);
    }

    public void onAddDevice() {
        if (arrayRoom.isEmpty()) {
            ToastUtil.showToast(mContext, "No Room\nYou must add Room at first");
            return;
        }
//        ActivityUtils.gotoActivity(mContext, DeviceSelectActivity.class, ActivityUtils.ANIMATE_FORWARD, true);
        ActivityUtils.gotoActivity(mContext, SelectWifiActivity.class, ActivityUtils.ANIMATE_FORWARD, true);
    }

    public void onAddRoom() {
        ActivityUtils.gotoActivity(mContext, RoomAddActivity.class, ActivityUtils.ANIMATE_FORWARD, true);
    }

    public void onSignOut() {
        TuyaHomeSdk.getUserInstance().logout(new ILogoutCallback() {
            @Override
            public void onSuccess() {
                mView.SignOut();
//                resultSuccess(WHAT_SETTING_LOGOUT_SUCCESS, true);
            }

            @Override
            public void onError(String errorCode, String errorMsg) {
                ToastUtil.showToast(mContext, "Sign out Failed");
//                resultError(WHAT_SETTING_LOGOUT_ERROR, errorCode, errorMsg);
            }
        });

    }



    public void removeRoom(final int position) {
        final long homeId = currentHomeBean.getHomeId();
        final RoomBean room = arrayRoom.get(position).getRoomBean();
        final String roomId = String.valueOf(room.getRoomId());

        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);

        builder.setMessage("Do you remove " + room.getName().toUpperCase() + " room?")
                .setCancelable(false)
                .setPositiveButton("Yes", (dialog, id) ->
                    TuyaHomeSdk.newHomeInstance(homeId).removeRoom(room.getRoomId(), new IResultCallback() {
                        @Override
                        public void onError(String s, String s1) {
                            Log.d(TAG, "onError: " + s + ":" + s1);
                            ToastUtil.showToast(mContext, "Failed to remove room\nTry again.");
                        }

                        @Override
                        public void onSuccess() {
                            PreferenceUtils.removeSingleChannel(mContext, roomId);
                            currentHomeBean = TuyaHomeSdk.getDataInstance().getHomeBean(homeId);
                            setCurrentHome(currentHomeBean);
                            gotoHomeFragment();
                        }
                    })
                )
                .setNegativeButton("No", (dialog, id) -> {

                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    public void removeDevice(final int position) {

        final RoomBean room = arrayRoom.get(mCurPosition).getRoomBean();
        final DeviceBean device = arrayDeviceWithRoom.get(position);
        final String dev_id = device.getDevId();

        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setMessage("Do you remove " + device.getName() + " device in " + room.getName() + " room?")
                .setCancelable(false)
                .setPositiveButton("Yes", (dialog, id)-> {
                    ProgressUtil.showLoading(mContext, mContext.getResources().getString(R.string.loading));
                    TuyaHomeSdk.newRoomInstance(room.getRoomId()).removeDevice(device.getDevId(), new IResultCallback() {
                        @Override
                        public void onError(String s, String s1) {
                            ProgressUtil.hideLoading();
                            ToastUtil.showToast(mContext, "Failed to remove device\nTry again.");
                        }

                        @Override
                        public void onSuccess() {
                            ProgressUtil.hideLoading();

                            // remove device info in preference
                            if (PreferenceUtils.getImgPath(mContext, dev_id) != null ||
                                PreferenceUtils.getDevName(mContext, dev_id) != null) {
                                PreferenceUtils.removeSingleChannel(mContext, dev_id);
                            }
                            initRoomSubMenu(mCurPosition);
                            gotoHomeFragment();
                        }
                    });
                })
                .setNegativeButton("No", (dialog, which) -> {

                });
        AlertDialog alert = builder.create();
        alert.show();
    }



    @Override
    public void onEvent(NetWorkStatusEventModel eventModel) {
//        netStatusCheck(eventModel.isAvailable());
    }

//    private boolean netStatusCheck(boolean isNetOk) {
//        networkTip(isNetOk, R.string.no_net_info);
//        return true;
//    }

//    private void networkTip(boolean networkok, int tipRes) {
//        if (networkok) {
//            mView.hideNetWorkTipView();
//        } else {
//            mView.showNetWorkTipView(tipRes);
//        }
//    }
}
