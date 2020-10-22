package com.test2019.tyapp.longhuan.activity;

import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.test2019.tyapp.longhuan.R;
import com.test2019.tyapp.longhuan.activity.base.BaseActivity;
import com.test2019.tyapp.longhuan.adapter.Adapter_WiFi_Item;
import com.test2019.tyapp.longhuan.lisener.Wifi_Item_Clicked_Listener;
import com.test2019.tyapp.longhuan.model.Item_Wifi;
import com.test2019.tyapp.longhuan.utils.ActivityUtils;

import java.util.ArrayList;
import java.util.List;

import static com.test2019.tyapp.longhuan.global.Global.CONFIG_PASSWORD;
import static com.test2019.tyapp.longhuan.global.Global.CONFIG_SSID;


public class SelectWifiActivity extends BaseActivity implements View.OnClickListener, Wifi_Item_Clicked_Listener {

    private ImageView img_back, img_refresh;
    private RecyclerView rel_wifi_item;
    private EditText et_wifi_password;
    private ImageButton ib_password_switch;
    private Button btn_device_connect;
    private boolean passwordOn = true;

    //======== wifi variable ========//
    private WifiManager mWifiManager;
    private List<ScanResult> mResults = new ArrayList<>();
    private List<Item_Wifi> mItem_wifis = new ArrayList<>();
    private boolean bWifiState = false;
//    private ProgressDialog progressDialog;
    private String mSSID = null;
    private String mPassword;

    private BroadcastReceiver mWifiStatusRecevier = new BroadcastReceiver() {
        @Override
        public void onReceive(Context c, final Intent intent) {
            if (intent.getAction() == null)
                return;
            if (intent.getAction().equals(WifiManager.NETWORK_STATE_CHANGED_ACTION)) {
                refresh_wifi_list();
            }
        }
    };

    private BroadcastReceiver mWifiScanRecevier = new BroadcastReceiver() {
        @Override
        public void onReceive(Context c, final Intent intent) {
            if (intent.getAction() == null)
                return;
            if (intent.getAction().equals(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)) {
                unregisterReceiver(this);
                //=== Scan result ===//
                mResults = mWifiManager.getScanResults();
                bWifiState = true;
                update_WifiListView();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_wifi);

        init_viewItem();
    }

    private void init_viewItem() {
        img_back = findViewById(R.id.img_back);
        img_refresh = findViewById(R.id.img_refresh);
        et_wifi_password = findViewById(R.id.et_wifi_password);
        ib_password_switch = findViewById(R.id.ib_password_switch);
        btn_device_connect = findViewById(R.id.btn_device_connect);

        rel_wifi_item = findViewById(R.id.rel_wifi_item);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        rel_wifi_item.setLayoutManager(llm);

        img_back.setOnClickListener(this);
        img_refresh.setOnClickListener(this);
        btn_device_connect.setOnClickListener(this);
        ib_password_switch.setOnClickListener(this);

        //==== init wifi ========//
        requestSinglePermission(Manifest.permission.ACCESS_FINE_LOCATION);
        mWifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
    }

    @Override
    protected void onResume() {
        super.onResume();
//        registerReceiver(mWifiStatusRecevier, new IntentFilter(WifiManager.NETWORK_STATE_CHANGED_ACTION));
        refresh_wifi_list();
    }

    @Override
    protected void onPause() {
        super.onPause();
//        unregisterReceiver(mWifiStatusRecevier);
    }

    private  void refresh_wifi_list(){
        if (checkWifiState()){      // wifi is on
            startWifiScan();
        }
        else {
            Toast.makeText(SelectWifiActivity.this,
                    "Wifi is off. Please turn on Wi-Fi!", Toast.LENGTH_LONG).show();
            //=== update ui ===//
            bWifiState = false;
            update_WifiListView();
        }
    }

    private boolean checkWifiState() {
        return mWifiManager.isWifiEnabled();
    }

    private void startWifiScan() {
        registerReceiver(mWifiScanRecevier, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
//        progressDialog = progressDialog.show(this, "", "Roading...");
        showLoading();
        mWifiManager.startScan();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.img_back:
                ActivityUtils.gotoActivity(this, MainActivity.class, ActivityUtils.ANIMATE_BACK, true);
                break;
            case R.id.img_refresh:
                refresh_wifi_list();
                break;
            case R.id.ib_password_switch:
                clickPasswordSwitch();
                break;
            case R.id.btn_device_connect:
                goNextStep();
                break;
        }
    }

    private void update_WifiListView(){
//        if (progressDialog.isShowing()){
//            progressDialog.dismiss();
//        }

        hideLoading();

        if (mItem_wifis.size() > 0) mItem_wifis.clear();

        if (!bWifiState) {
            if (mResults.size() > 0) mResults.clear();
        }

        for (ScanResult result : mResults) {
            Item_Wifi item_wifi = new Item_Wifi(result);
            mItem_wifis.add(item_wifi);
        }

        Adapter_WiFi_Item adapter_wiFi_item = new Adapter_WiFi_Item(mItem_wifis, this);
        rel_wifi_item.setAdapter(adapter_wiFi_item);
    }

    @Override
    public void onBackPressed() {
        ActivityUtils.gotoActivity(this, MainActivity.class, ActivityUtils.ANIMATE_BACK, true);
    }

    public void requestSinglePermission(String permission){
        if (!isPermissionGranted(permission)) {
            ActivityCompat.requestPermissions(this,
                    new String[]{permission}, 1);
        }
    }

    public boolean isPermissionGranted(String permission) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED) {
                return true;
            } else {
                return false;
            }
        } else {
            return true;
        }
    }

    //==== wifi item clicked listener =============//
    @Override
    public void get_wifi_SSID(String ssid) {
//        Toast.makeText(this, ssid + " clicked", Toast.LENGTH_SHORT).show();
        this.mSSID = ssid;
    }

    private void clickPasswordSwitch() {
        passwordOn = !passwordOn;
        if (passwordOn) {
            ib_password_switch.setImageResource(R.mipmap.password_off);
            et_wifi_password.setTransformationMethod(PasswordTransformationMethod.getInstance());
        } else {
            ib_password_switch.setImageResource(R.mipmap.password_on);
            et_wifi_password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
        }
    }

    private void goNextStep(){
        if (!validate()) return;
        Intent intent = new Intent(this, DeviceAddActivity.class);
        intent.putExtra(CONFIG_PASSWORD, mPassword);
        intent.putExtra(CONFIG_SSID, mSSID);
//        intent.putExtra(Global.DEVICE_TYPE_CHANNEL, mType);
        ActivityUtils.startActivity(this, intent, ActivityUtils.ANIMATE_FORWARD, true);
    }

    private boolean validate() {
        mPassword = et_wifi_password.getText().toString();

        if (!checkWifiState()){
            Toast.makeText(this,
                    "Wifi is not enabled!", Toast.LENGTH_LONG).show();
            return false;
        } else if (mSSID == null) {
            Toast.makeText(this,
                    "Wifi is not selected!", Toast.LENGTH_LONG).show();
            return false;
        } else if (is5GHz()) {
            Toast.makeText(this,
                    getString(R.string.ez_notSupport_5G_tip), Toast.LENGTH_LONG).show();
            return false;
        } else if (mPassword.isEmpty()) {
            Toast.makeText(this,
                    "Please insert password!", Toast.LENGTH_LONG).show();
            return false;
        }

        return true;
    }

    private boolean is5GHz() {
        WifiInfo wifiInfo = mWifiManager.getConnectionInfo();
        if (wifiInfo != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            int freq = wifiInfo.getFrequency();
            return freq > 4900 && freq < 5900;
        } else {
            return false;
        }
    }
}
