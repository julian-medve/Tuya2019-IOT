package com.test2019.tyapp.longhuan.presenter;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.provider.Settings;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.PermissionChecker;
import androidx.appcompat.app.AlertDialog;
import android.text.TextUtils;

import com.test2019.tyapp.longhuan.R;
import com.test2019.tyapp.longhuan.activity.DeviceAddActivity;
import com.test2019.tyapp.longhuan.utils.ActivityUtils;
import com.test2019.tyapp.longhuan.utils.DialogUtil;
import com.test2019.tyapp.longhuan.utils.ToastUtil;
import com.test2019.tyapp.longhuan.view.IDeviceConnectionSettingView;
import com.tuya.smart.android.common.utils.NetworkUtil;
import com.tuya.smart.android.device.utils.WiFiUtil;
import com.tuya.smart.android.mvp.presenter.BasePresenter;

import static com.test2019.tyapp.longhuan.global.Global.CONFIG_PASSWORD;
import static com.test2019.tyapp.longhuan.global.Global.CONFIG_SSID;


public class DeviceConnectionSettingPresenter extends BasePresenter {

    public static final String TAG = "DeviceConnectionSettingPresenter";

    public static final int PRIVATE_CODE = 1315;
    public static final int CODE_FOR_LOCATION_PERMISSION = 222;

//    private int mType;

    private Activity mContext;
    private IDeviceConnectionSettingView mView;

    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context c, final Intent intent) {
            if (intent.getAction() == null)
                return;
            if (intent.getAction().equals(WifiManager.NETWORK_STATE_CHANGED_ACTION)) {
                checkWifiNetworkStatus();
            }
        }
    };

    public DeviceConnectionSettingPresenter(Activity context, IDeviceConnectionSettingView view) {
        this.mContext = context;
        this.mView = view;
        initData();
        initWifi();
    }

    private void initData() {
//        mType = mContext.getIntent().getIntExtra(Global.DEVICE_TYPE_CHANNEL, Global.DEVICE_TYPE_COMMON);
    }

    private void initWifi() {
        registerWifiReceiver();
    }
    private boolean isWifiDisabled() {
        final WifiManager mWifiManager = (WifiManager) mContext.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        return mWifiManager != null && !mWifiManager.isWifiEnabled();
    }

    public void showLocationError() {
        String ssid = mView.getWifiSsId();
        if (!ssid.equals(mContext.getResources().getString(R.string.text_wifi_status))) {
            return;
        } else {
            if (isWifiDisabled()) {
                return;
            }
            if (!checkSystemGPSLocation()){
                return;
            } else {
                checkSinglePermission(Manifest.permission.ACCESS_FINE_LOCATION, CODE_FOR_LOCATION_PERMISSION);
            }
        }
    }

    private boolean checkSystemGPSLocation(){
        boolean isOpen;
        LocationManager locationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
        isOpen = locationManager.isProviderEnabled(android.location.LocationManager.GPS_PROVIDER);
        if (!isOpen) {
            new AlertDialog.Builder(mContext)
                    .setIcon(android.R.drawable.ic_dialog_info)
                    .setTitle(R.string.simple_confirm_title)
                    .setMessage(R.string.notify_location_setup)
                    .setNegativeButton(R.string.cancel,null)
                    .setPositiveButton(R.string.setup, ((dialog, which) -> {
                        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        mContext.startActivityForResult(intent, PRIVATE_CODE);
                        dialog.dismiss();
                    }))
                    .show();
        }
        return isOpen;
    }

    public boolean checkSinglePermission(String permission, int resultCode) {
        boolean hasPermission;
        if (Build.VERSION.SDK_INT < 23) {
            return true;
        } else {
            hasPermission = hasPermission(permission);
        }

        if (!hasPermission) {
//            if (!ActivityCompat.shouldShowRequestPermissionRationale(mContext, permission)) {
//                new AlertDialog.Builder(mContext)
//                        .setIcon(android.R.drawable.ic_dialog_info)
//                        .setTitle(R.string.ty_simple_confirm_title)
//                        .setMessage(R.string.wifi_to_reopen_permission_location)
//                        .setNegativeButton(R.string.cancel,null)
//                        .setPositiveButton(R.string.setup, new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialogInterface, int i) {
//                                //Guide users to the setup page for manual authorization
//                                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
//                                Uri uri = Uri.fromParts("package", mContext.getApplicationContext().getPackageName(), null);
//                                intent.setData(uri);
//                                mContext.startActivity(intent);
//                                dialogInterface.dismiss();
//                            }
//                        })
//                        .show();
//                return false;
//            }
            ActivityCompat.requestPermissions(mContext, new String[]{permission},
                    resultCode);
            return false;
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


    public void checkWifiNetworkStatus() {
        if (NetworkUtil.isNetworkAvailable(mContext)) {
            String currentSSID = WiFiUtil.getCurrentSSID(mContext);
            if (!TextUtils.isEmpty(currentSSID)) {
                mView.setWifiSSID(currentSSID);
                if (is5GHz(currentSSID, mContext)) {
                    mView.show5gTip();
                } else {
                    mView.hide5gTip();
                }
                return;
            }
        }
        mView.showNoWifi();
    }

    public void goNextStep() {
        final String passWord = mView.getWifiPass();
        final String ssid = WiFiUtil.getCurrentSSID(mContext);
        if (!NetworkUtil.isNetworkAvailable(mContext) || TextUtils.isEmpty(ssid)) {
            ToastUtil.showToast(mContext, R.string.connect_phone_to_network);
        } else {
            //Encrypt the password

            if (!is5GHz(ssid, mContext)) {
                gotoBindDeviceActivity(ssid, passWord);
            } else {
                DialogUtil.customDialog(mContext, null, mContext.getString(R.string.ez_notSupport_5G_tip)
                        , mContext.getString(R.string.ez_notSupport_5G_change), mContext.getString(R.string.ez_notSupport_5G_continue), null, ((dialog, which) -> {
                            switch (which) {
                                case DialogInterface.BUTTON_POSITIVE:
                                    userOtherWifi();
                                    break;
                                case DialogInterface.BUTTON_NEGATIVE:
                                    gotoBindDeviceActivity(ssid, passWord);
                                    break;
                            }
                        })).show();
            }
        }
    }

    private static boolean is5GHz(String ssid, Context context) {
        WifiManager wifiManger = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManger.getConnectionInfo();
        if (wifiInfo != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            int freq = wifiInfo.getFrequency();
            return freq > 4900 && freq < 5900;
        } else return ssid.toUpperCase().endsWith("5G");
    }

    private void gotoBindDeviceActivity(String ssid, String passWord) {
        Intent intent = new Intent(mContext, DeviceAddActivity.class);
        intent.putExtra(CONFIG_PASSWORD, passWord);
        intent.putExtra(CONFIG_SSID, ssid);
//        intent.putExtra(Global.DEVICE_TYPE_CHANNEL, mType);
        ActivityUtils.startActivity(mContext, intent, ActivityUtils.ANIMATE_FORWARD, true);
    }

    private void registerWifiReceiver() {
        try {
            mContext.registerReceiver(mBroadcastReceiver, new IntentFilter(
                    WifiManager.NETWORK_STATE_CHANGED_ACTION));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void unRegisterWifiReceiver() {
        try {
            if (mBroadcastReceiver != null) {
                mContext.unregisterReceiver(mBroadcastReceiver);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unRegisterWifiReceiver();
    }

    public void userOtherWifi() {
        Intent wifiSettingsIntent = new Intent("android.settings.WIFI_SETTINGS");
        if (null != wifiSettingsIntent.resolveActivity(mContext.getPackageManager())) {
            mContext.startActivity(wifiSettingsIntent);
        } else {
            wifiSettingsIntent = new Intent(Settings.ACTION_WIFI_SETTINGS);
            if (null != wifiSettingsIntent.resolveActivity(mContext.getPackageManager())) {
                mContext.startActivity(wifiSettingsIntent);
            }
        }
    }

}
