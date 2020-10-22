package com.test2019.tyapp.longhuan.model;

import android.net.wifi.ScanResult;

public class Item_Wifi {
    public ScanResult mWifi;
    public boolean bChecked;

    public Item_Wifi(ScanResult wifi){
        this.mWifi = wifi;
        bChecked = false;
    }
}
