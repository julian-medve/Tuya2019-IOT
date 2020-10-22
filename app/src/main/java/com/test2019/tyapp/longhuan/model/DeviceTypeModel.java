package com.test2019.tyapp.longhuan.model;

public class DeviceTypeModel {

    public static final int DEVICE_WIFI = 0x01;
    public static final int DEVICE_ZIGBEE = 0x02;
    public static final int DEVICE_BLUETOOTH = 0x03;

    public String   mName;
    public int      mIcon;
    public int      mDeviceType;
    public boolean  isSubDevice;

    public DeviceTypeModel(String name, int icon, int deviceType, boolean subdevice){
        mName = name;
        mIcon = icon;
        mDeviceType = deviceType;
        isSubDevice = subdevice;
    }
}
