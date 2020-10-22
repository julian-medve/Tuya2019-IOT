package com.test2019.tyapp.longhuan.presenter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.test2019.tyapp.longhuan.R;
import com.test2019.tyapp.longhuan.activity.DeviceZigBeeTipActivity;
import com.test2019.tyapp.longhuan.activity.DeviceTipActivity;
import com.test2019.tyapp.longhuan.adapter.DeviceTypeAdapter;
import com.test2019.tyapp.longhuan.global.Global;
import com.test2019.tyapp.longhuan.model.DeviceTypeModel;
import com.test2019.tyapp.longhuan.utils.ActivityUtils;
import com.test2019.tyapp.longhuan.utils.PreferenceUtils;
import com.test2019.tyapp.longhuan.utils.ToastUtil;
import com.test2019.tyapp.longhuan.view.IDeviceSelectView;
import com.tuya.smart.android.mvp.presenter.BasePresenter;

import java.util.ArrayList;

public class DeviceSelectPresenter extends BasePresenter {

    private final String TAG = "DeviceSelectPresent";

    private Activity mContext;
    private IDeviceSelectView mView;

    private String [] deviceTypeName = {
            "Switch","GateWay","Door","PIR","Temp",
            "Device6","Device7","Device8","Device9","Device10",
            "Device11","Device12","Device13","Device14","Device15",
            "Device16","Device17","Device18"
    };

//    private int [] deviceTypeList = {
//            Global.DEVICE_TYPE_SWITCH,
//            Global.DEVICE_TYPE_COMMON,
//            Global.DEVICE_TYPE_COMMON,
//            Global.DEVICE_TYPE_COMMON,
//            Global.DEVICE_TYPE_COMMON,
//            Global.DEVICE_TYPE_COMMON,
//            Global.DEVICE_TYPE_COMMON,
//            Global.DEVICE_TYPE_COMMON,
//            Global.DEVICE_TYPE_COMMON,
//            Global.DEVICE_TYPE_COMMON,
//            Global.DEVICE_TYPE_COMMON,
//            Global.DEVICE_TYPE_COMMON,
//            Global.DEVICE_TYPE_COMMON,
//            Global.DEVICE_TYPE_COMMON,
//            Global.DEVICE_TYPE_COMMON,
//            Global.DEVICE_TYPE_COMMON
//    };

    private int [] deviceIcons = {
            R.mipmap.ic_dev1,R.mipmap.ic_dev2,R.mipmap.ic_dev3,R.mipmap.ic_dev4,R.mipmap.ic_dev5,
            R.mipmap.ic_dev6,R.mipmap.ic_dev7,R.mipmap.ic_dev8,R.mipmap.ic_dev9,R.mipmap.ic_dev10,
            R.mipmap.ic_dev11,R.mipmap.ic_dev12,R.mipmap.ic_dev13,R.mipmap.ic_dev14,R.mipmap.ic_dev15,
            R.mipmap.ic_dev16,R.mipmap.ic_dev17, R.mipmap.ic_dev18
    };

    private ArrayList<DeviceTypeModel> arrayTypes = new ArrayList<>();

    private boolean isGateway;

    public DeviceSelectPresenter(Context context, IDeviceSelectView view) {
        this.mContext = (Activity) context;
        this.mView = view;
        initDeviceTypeList();
        isGateway = PreferenceUtils.getBoolean(mContext, Global.GATEWAY_CHANNEL);
    }

    private void initDeviceTypeList() {

        arrayTypes.clear();

        for (int i = 0; i < 18; i++) {
            DeviceTypeModel deviceType;
            deviceType = new DeviceTypeModel( deviceTypeName[i], deviceIcons[i], DeviceTypeModel.DEVICE_WIFI, true);
            arrayTypes.add(deviceType);
        }

//        getDeviceTypeDemo();
        DeviceTypeAdapter adapter = new DeviceTypeAdapter(mContext, arrayTypes);
        mView.LoadDeviceType(adapter);
    }

    private void getDeviceTypeDemo() {
        DeviceTypeModel deviceTypeModel_switch = new DeviceTypeModel(deviceTypeName[0], deviceIcons[0], DeviceTypeModel.DEVICE_WIFI, false);
        arrayTypes.add(deviceTypeModel_switch);
        DeviceTypeModel deviceTypeModel_gateway = new DeviceTypeModel(deviceTypeName[1], deviceIcons[1], DeviceTypeModel.DEVICE_ZIGBEE, false);
        arrayTypes.add(deviceTypeModel_gateway);
        DeviceTypeModel deviceTypeModel_subdevice_door = new DeviceTypeModel(deviceTypeName[2], deviceIcons[2], DeviceTypeModel.DEVICE_ZIGBEE, true);
        arrayTypes.add(deviceTypeModel_subdevice_door);
        DeviceTypeModel deviceTypeModel_subdevice_pir = new DeviceTypeModel(deviceTypeName[3], deviceIcons[3], DeviceTypeModel.DEVICE_ZIGBEE, true);
        arrayTypes.add(deviceTypeModel_subdevice_pir);
        DeviceTypeModel deviceTypeModel_subdevice_temp = new DeviceTypeModel(deviceTypeName[4], deviceIcons[4], DeviceTypeModel.DEVICE_ZIGBEE, true);
        arrayTypes.add(deviceTypeModel_subdevice_temp);
    }

    public void next(int position) {
        if (arrayTypes.get(position).mDeviceType == DeviceTypeModel.DEVICE_WIFI)
            startWifiDevConfig(arrayTypes.get(position).mDeviceType);
        else if (arrayTypes.get(position).mDeviceType == DeviceTypeModel.DEVICE_ZIGBEE){
            if (!arrayTypes.get(position).isSubDevice) {
                isGateway = false;
                startGatewayDevConfig();
            } else if (arrayTypes.get(position).isSubDevice && isGateway)
                startGatewayDevConfig();
            else
                ToastUtil.showToast(mContext, "No Gateway\nPlease try to add Gateway");
        } else {
            ToastUtil.showToast(mContext, "Bluetooth is not supported");
        }
    }

    private void startGatewayDevConfig() {
        Intent intent = new Intent(mContext, DeviceZigBeeTipActivity.class);
        intent.putExtra(Global.GATEWAY_CHANNEL, isGateway);
        ActivityUtils.startActivity(mContext, intent, ActivityUtils.ANIMATE_FORWARD, true);
    }

    private void startWifiDevConfig(int type) {
        Intent intent = new Intent(mContext, DeviceTipActivity.class);
        ActivityUtils.startActivity(mContext, intent, ActivityUtils.ANIMATE_FORWARD, true);
    }
}
