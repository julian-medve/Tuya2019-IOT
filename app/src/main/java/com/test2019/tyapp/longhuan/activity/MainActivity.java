package com.test2019.tyapp.longhuan.activity;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.provider.Settings;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.test2019.tyapp.longhuan.model.RoomModel;
import com.test2019.tyapp.longhuan.R;
import com.test2019.tyapp.longhuan.activity.base.BaseActivity;
import com.test2019.tyapp.longhuan.adapter.DeviceAdapter;
import com.test2019.tyapp.longhuan.adapter.RoomAdapter;
import com.test2019.tyapp.longhuan.presenter.MainPresenter;
import com.test2019.tyapp.longhuan.utils.LoginHelper;
import com.test2019.tyapp.longhuan.utils.PreferenceUtils;
import com.test2019.tyapp.longhuan.utils.ToastUtil;
import com.test2019.tyapp.longhuan.view.IMainView;
import com.test2019.tyapp.longhuan.view.OnItemClickListener;
import com.test2019.tyapp.longhuan.view.OnItemLongClickListener;
import com.test2019.tyapp.longhuan.weather.GPSTracker;
import com.test2019.tyapp.longhuan.weather.PermissionSupport;
import com.test2019.tyapp.longhuan.weather.WeatherResponse;
import com.test2019.tyapp.longhuan.weather.WeatherService;
import com.tuya.smart.android.user.bean.User;
import com.tuya.smart.home.sdk.TuyaHomeSdk;
import com.tuya.smart.sdk.api.IResultCallback;
import com.tuya.smart.sdk.bean.DeviceBean;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.test2019.tyapp.longhuan.global.Global.ALPHA_DARK;
import static com.test2019.tyapp.longhuan.global.Global.ALPHA_LIGHT;

public class MainActivity extends BaseActivity implements View.OnClickListener, IMainView, OnItemClickListener, OnItemLongClickListener {

    private final String TAG = "MainActivity";

    private final int TYPE_HOME_MENU = 0x001;
    private final int TYPE_ROOM_MENU = 0x010;
    private final int TYPE_DEVICE_MENU = 0x100;

    private FrameLayout mFrame_home_background;
    private FrameLayout mFrame_profile, mFrame_voice, mFrame_setting, mFrame_addRoom, mFrame_addDevice, mFrame_logOut;
    private ImageView mImg_home;
    private TextView mTvHome;
    private RecyclerView mRoomView;
    private RecyclerView mSubMenuView;
//    private LinearLayout mLLSubMenu;
    private ConstraintLayout mCLHomeMenu;

    private boolean isRoom;
    private boolean isMain;

    private MainPresenter mMainPresenter;

    private boolean isWidget;


    public static String BaseUrl = "http://api.openweathermap.org/";
    public static String AppId = "1487dd8a93bfd85d278d9ac8dcfee94c";

    private TextView txtUserName;

    private TextView txt_description, txt_temperature, txt_presure, txt_humidity;
    private TextView txt_temp_title, txt_presure_title, txt_humidity_title;
    private ImageView img_icon, img_getButton;

    private String username;

    private String lati = "50.0412";
    private String loni = "21.9991";

    private GPSTracker gpsTracker;

    private static final int GPS_ENABLE_REQUEST_CODE = 2001;
    private static final int PERMISSIONS_REQUEST_CODE = 100;
    String[] REQUIRED_PERMISSIONS  = {
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.RECORD_AUDIO};

    private PermissionSupport permission;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(TAG, "onCreate");

        permissioncheck();

        initView();
        initData();
        initPresenter();

//        initRequestWhiteList();

        //== my test ==========//
//        TuyaSmartSdk.setDebugMode(true);
//        register_push_idToken();
//        permission_notification();
        //=======================//
    }



//========= test app =======//
    private void register_push_idToken() {
        String idToken = PreferenceUtils.getIdToken(this);
        TuyaHomeSdk.getPushInstance().registerDevice(idToken, "FCM", new IResultCallback() {
            @Override
            public void onError(String code, String error) {
                Toast.makeText(MainActivity.this,
                        code + "\n" + error, Toast.LENGTH_LONG).show();
            }

            @Override
            public void onSuccess() {
                Toast.makeText(MainActivity.this,
                        "Register success!", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void permission_notification() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Intent intent = new Intent();
            String packageName = getPackageName();
            PowerManager pm = (PowerManager) getSystemService(POWER_SERVICE);
            if (!pm.isIgnoringBatteryOptimizations(packageName)) {
                intent.setAction(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
                intent.setData(Uri.parse("package:" + packageName));
                startActivity(intent);
            }
        }

        requestNotificationPermission();
    }

    private int NOTIFICATION_PERMISSION_CODE = 1001;
    private void requestNotificationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_NOTIFICATION_POLICY) == PackageManager.PERMISSION_GRANTED)
            return;

        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_NOTIFICATION_POLICY}, NOTIFICATION_PERMISSION_CODE );
    }



//==========================================//

    @Override
    protected void onResume() {
        Log.d(TAG, "onResume");
        super.onResume();

        setName();
        getCurrentData();
        mMainPresenter.onResume();

    }

    @Override
    protected void onPause() {
        Log.d(TAG, "onPause");
        super.onPause();

        mMainPresenter.onPause();

    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy");
        super.onDestroy();
        if (mMainPresenter != null) {
            mMainPresenter.onDestroy();
        }
    }

    private void initPresenter() {
        Log.d(TAG, "initPresenter: ");
        mMainPresenter = new MainPresenter(this, this);
        mMainPresenter.gotoHomeFragment();
        mMainPresenter.checkFamilyCount();

        if(getIntent() != null){
            isWidget = getIntent().getBooleanExtra("WidgetActivity", false);

            Log.d(TAG, "widgetActivity State :" + Boolean.toString(isWidget));

            if(isWidget){
                Intent i = new Intent(getApplicationContext(), WidgetActivity.class);
                startActivity(i);
                isWidget = false;

            }
        }
    }

    private void initView() {
        mFrame_home_background = findViewById(R.id.frame_home_background);
        mImg_home = findViewById(R.id.img_home);
        mTvHome = findViewById(R.id.tv_home);
        mRoomView = findViewById(R.id.view_rooms);
        mSubMenuView = findViewById(R.id.view_sub_menu);
        mCLHomeMenu = findViewById(R.id.cl_home_menu);



//        mLLSubMenu = findViewById(R.id.ll_sub_menu);

//        img_getButton = findViewById(R.id.img_getButton);
//        img_getButton.setOnClickListener(this);


        mFrame_profile = findViewById(R.id.new_profile);
        mFrame_voice = findViewById(R.id.new_voice);
        mFrame_setting = findViewById(R.id.new_setting);
        mFrame_addRoom = findViewById(R.id.new_addroom);
        mFrame_addDevice = findViewById(R.id.new_adddevice);
        mFrame_logOut = findViewById(R.id.new_logout);

        mFrame_profile.setOnClickListener(this);
        mFrame_voice.setOnClickListener(this);
        mFrame_setting.setOnClickListener(this);
        mFrame_addRoom.setOnClickListener(this);
        mFrame_addDevice.setOnClickListener(this);
        mFrame_logOut.setOnClickListener(this);

        mFrame_home_background.setOnClickListener(this);


        //======== weather part ========

        txtUserName = (TextView)findViewById(R.id.main_name);
        txt_description = (TextView)findViewById(R.id.txt_description);
        txt_presure = (TextView)findViewById(R.id.txt_presure);
        txt_humidity = (TextView)findViewById(R.id.txt_humidity);
        txt_temperature = (TextView)findViewById(R.id.txt_temperature);
        img_icon = (ImageView)findViewById(R.id.img_icon);

        txt_temp_title = (TextView)findViewById(R.id.txt_temperature_title);
        txt_humidity_title = (TextView)findViewById(R.id.txt_humidity_title);
        txt_presure_title = (TextView)findViewById(R.id.txt_presure_title);


    }

    private void initData() {
        isRoom = false;
        isMain = false;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.frame_home_background:
                isMain = !isMain;
                mMainPresenter.gotoHomeFragment();
                mMainPresenter.selectHome(isRoom, isMain);
                break;
            case R.id.new_profile:
                mMainPresenter.gotoProfileActivity();
                break;
            case R.id.new_voice:
                Intent i = new Intent(getApplicationContext(), WidgetActivity.class);
                startActivity(i);
                break;
            case R.id.new_setting:
                ToastUtil.showToast(this, "Clicked Setting");
                break;
            case R.id.new_logout:
                showLoading();
                mMainPresenter.onSignOut();
                break;
            case R.id.new_adddevice:
                mMainPresenter.onAddDevice();
                break;
            case R.id.new_addroom:
                mMainPresenter.onAddRoom();
                break;

//            case R.id.img_getButton:
//                getCurrentData();
//                break;
        }
    }

    @Override
    public void loadRoom(ArrayList<RoomModel> datas) {
        RoomAdapter adapter = new RoomAdapter(this, datas, this, this);
        mRoomView.setAdapter(adapter);
        mRoomView.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false));
    }
    //== make device adapter public for using another fragments =========//
    public DeviceAdapter deviceAdapter;
    @Override
    public void loadDevice(ArrayList<DeviceBean> datas) {
        mSubMenuView.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false));
        deviceAdapter = new DeviceAdapter(this, datas, this, this);
        mSubMenuView.setAdapter(deviceAdapter);
    }

    @Override
    public void showMainMenu(boolean bRoom, boolean bMain) {
        if (bRoom) {
            isRoom = false;
            RoomAdapter adapter = (RoomAdapter) mRoomView.getAdapter();
            if (adapter != null)
                adapter.reset();
//            mLLSubMenu.setVisibility(View.GONE);
            mSubMenuView.setVisibility(View.GONE);
        }

        if (bMain)
            mCLHomeMenu.setVisibility(View.VISIBLE);
        else
            mCLHomeMenu.setVisibility(View.GONE);
    }

    @Override
    public void showRoomMenu(boolean bRoom, boolean bMain, boolean isCurrentPosition) {
        if (bMain) {
            isMain = false;
            mCLHomeMenu.setVisibility(View.GONE);
        }

        if (isCurrentPosition){
            if (bRoom) {
//                mLLSubMenu.setVisibility(View.VISIBLE);
                mSubMenuView.setVisibility(View.VISIBLE);
            } else {
//                mLLSubMenu.setVisibility(View.GONE);
                mSubMenuView.setVisibility(View.GONE);
            }
        } else {
            if (bMain) {
//                mLLSubMenu.setVisibility(View.VISIBLE);
                mSubMenuView.setVisibility(View.VISIBLE);
            } else {
                if (bRoom) {
//                    mLLSubMenu.setVisibility(View.VISIBLE);
                    mSubMenuView.setVisibility(View.VISIBLE);
                } else {
                    isRoom = true;
                }
            }
        }
    }

    @Override
    public void setHomeBackground(boolean bMain) {
        if (bMain){
            mFrame_home_background.setBackgroundColor(ContextCompat.getColor(this, R.color.color_orange));
            mImg_home.setAlpha(ALPHA_LIGHT);
            mTvHome.setAlpha(ALPHA_LIGHT);
//            mImg_home.setImageResource(R.drawable.ic_home_click);
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
//                mImg_home.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.color_white)));
        } else {
            mFrame_home_background.setBackgroundColor(getResources().getColor(R.color.black_80));
            mImg_home.setAlpha(ALPHA_DARK);
            mTvHome.setAlpha(ALPHA_DARK);
//            mImg_home.setImageResource(R.drawable.ic_home);
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
//                mImg_home.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.color_orange)));
        }
    }

    @Override
    public void onClick(View view, int type, int position) {
        switch (type) {
            case TYPE_HOME_MENU:
                mMainPresenter.gotoHomeFragment();
                break;
            case TYPE_ROOM_MENU:
                isRoom = !isRoom;
                mMainPresenter.selectRoom(isRoom, isMain, position);
                mMainPresenter.gotoHomeFragment();
                break;
            case TYPE_DEVICE_MENU:
                mMainPresenter.selectDevice(position);
                break;
        }
    }

    @Override
    public void onLongClick(View view, int type, int position) {
        switch (type) {
            case TYPE_ROOM_MENU:
                mMainPresenter.removeRoom(position);
                break;
            case TYPE_DEVICE_MENU:
                mMainPresenter.removeDevice(position);
                break;
        }
    }

    @Override
    public void loadProgressBar() {
        showLoading();
    }

    @Override
    public void stopProgressBar() {
        hideLoading();
    }

    @Override
    public void SignOut() {
        LoginHelper.reLogin(this, false);
        hideLoading();
    }

    private void initRequestWhiteList() {
        Intent intent = new Intent();
        String packageName = getPackageName();
        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (pm.isIgnoringBatteryOptimizations(packageName))
                intent.setAction(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS);
            else {
                intent.setAction(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
                intent.setData(Uri.parse("package:" + packageName));
            }
            startActivity(intent);
        }
    }


    //====== weather part===========


    private void getCurrentData(){

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BaseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        gpsTracker = new GPSTracker(MainActivity.this);
        double latitude = gpsTracker.getLatitude();
        double longitude = gpsTracker.getLongitude();

        WeatherService service = retrofit.create(WeatherService.class);

        Call<WeatherResponse> call = service.getCurrentWeatherData(Double.toString(latitude), Double.toString(longitude), AppId);
        //Call<WeatherResponse> call = service.getCurrentWeatherData(lati, loni, AppId);
        call.enqueue(new Callback<WeatherResponse>() {
            @Override
            public void onResponse(Call<WeatherResponse> call, Response<WeatherResponse> response) {
                if(response.code() == 200){
                    WeatherResponse weatherResponse = response.body();
                    assert weatherResponse != null;

                    String icon = weatherResponse.weather.get(0).icon;
//                    String iconUrl = "https://openweathermap.org/img/w/" + icon + ".png";
//                    Picasso.get().load(iconUrl).into(img_icon);

                    int temp = (int) weatherResponse.main.temp - 273;

                    switch (icon){
                        case "01d":
                            txt_description.setText(R.string.clear_sky);
                            img_icon.setImageResource(R.drawable.ic_clear_sky);
                            break;
                        case "01n":
                            txt_description.setText(R.string.clear_sky);
                            img_icon.setImageResource(R.drawable.ic_clear_sky);
                            break;
                        case "02d":
                            txt_description.setText(R.string.few_clouds);
                            img_icon.setImageResource(R.drawable.ic_few_clouds);
                            break;
                        case "02n":
                            txt_description.setText(R.string.few_clouds);
                            img_icon.setImageResource(R.drawable.ic_few_clouds);
                            break;
                        case "03d":
                            txt_description.setText(R.string.scattered_clouds);
                            img_icon.setImageResource(R.drawable.ic_scattered_clouds);
                            break;
                        case "03n":
                            txt_description.setText(R.string.scattered_clouds);
                            img_icon.setImageResource(R.drawable.ic_scattered_clouds);
                            break;
                        case "04d":
                            txt_description.setText(R.string.broken_clouds);
                            img_icon.setImageResource(R.drawable.ic_broken_clouds);
                            break;
                        case "04n":
                            txt_description.setText(R.string.broken_clouds);
                            img_icon.setImageResource(R.drawable.ic_broken_clouds);
                            break;
                        case "09d":
                            txt_description.setText(R.string.shower_rain);
                            img_icon.setImageResource(R.drawable.ic_shower_rain);
                            break;
                        case "09n":
                            txt_description.setText(R.string.shower_rain);
                            img_icon.setImageResource(R.drawable.ic_shower_rain);
                            break;
                        case "10d":
                            txt_description.setText(R.string.rain);
                            img_icon.setImageResource(R.drawable.ic_rain);
                            break;
                        case "10n":
                            txt_description.setText(R.string.rain);
                            img_icon.setImageResource(R.drawable.ic_rain);
                            break;
                        case "11d":
                            txt_description.setText(R.string.thunderstorm);
                            img_icon.setImageResource(R.drawable.ic_thunderstorm);
                            break;
                        case "11n":
                            txt_description.setText(R.string.thunderstorm);
                            img_icon.setImageResource(R.drawable.ic_thunderstorm);
                            break;
                        case "13d":
                            txt_description.setText(R.string.snow);
                            img_icon.setImageResource(R.drawable.ic_snow);
                            break;
                        case "13n":
                            txt_description.setText(R.string.snow);
                            img_icon.setImageResource(R.drawable.ic_snow);
                            break;
                        case "50d":
                            txt_description.setText(R.string.mist);
                            img_icon.setImageResource(R.drawable.ic_mist);
                            break;
                        case "50n":
                            txt_description.setText(R.string.mist);
                            img_icon.setImageResource(R.drawable.ic_mist);
                            break;
                        default:
                            txt_description.setText(R.string.clear_sky);
                            img_icon.setImageResource(R.drawable.ic_clear_sky);
                    }

                    txt_humidity.setText(Double.toString(weatherResponse.main.humidity) + "%");
                    txt_presure.setText(Double.toString(weatherResponse.main.pressure) + "hPa");
                    txt_temperature.setText(Integer.toString(temp) + " °C");

                    txt_temp_title.setVisibility(View.VISIBLE);
                    txt_humidity_title.setVisibility(View.VISIBLE);
                    txt_presure_title.setVisibility(View.VISIBLE);

                }
            }

            @Override
            public void onFailure(Call<WeatherResponse> call, Throwable t) {
                ToastUtil.showToast(getApplicationContext(), t.getMessage());
            }
        });

    }


    private void permissioncheck(){

        if (!checkLocationServicesStatus()) {

            showDialogForLocationServiceSetting();
        }else {

            checkRunTimePermission();
        }
    }



    @Override
    public void onRequestPermissionsResult(int permsRequestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grandResults) {

        if ( permsRequestCode == PERMISSIONS_REQUEST_CODE && grandResults.length == REQUIRED_PERMISSIONS.length) {

            boolean check_result = true;

            for (int result : grandResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    check_result = false;
                    break;
                }
            }


            if ( check_result ) {


            }
            else {


                if (ActivityCompat.shouldShowRequestPermissionRationale(this, REQUIRED_PERMISSIONS[0])
                        || ActivityCompat.shouldShowRequestPermissionRationale(this, REQUIRED_PERMISSIONS[1])
                        || ActivityCompat.shouldShowRequestPermissionRationale(this, REQUIRED_PERMISSIONS[2])) {

                    Toast.makeText(MainActivity.this, "permission faild. you have to replay this app", Toast.LENGTH_LONG).show();
                    finish();


                }else {

                    Toast.makeText(MainActivity.this, "permission failed. you have to permission in setting ", Toast.LENGTH_LONG).show();

                }
            }

        }
    }

    void checkRunTimePermission(){


        int hasFineLocationPermission = ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        int hasCoarseLocationPermission = ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.ACCESS_COARSE_LOCATION);

        int hasRecordAudioPermission = ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.RECORD_AUDIO);


        if (hasFineLocationPermission == PackageManager.PERMISSION_GRANTED &&
                hasCoarseLocationPermission == PackageManager.PERMISSION_GRANTED &&
                hasRecordAudioPermission == PackageManager.PERMISSION_GRANTED) {

        } else {


            if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, REQUIRED_PERMISSIONS[0])) {


                Toast.makeText(MainActivity.this, "if you try do it, you need location permission", Toast.LENGTH_LONG).show();

                ActivityCompat.requestPermissions(MainActivity.this, REQUIRED_PERMISSIONS,
                        PERMISSIONS_REQUEST_CODE);


            } else {

                ActivityCompat.requestPermissions(MainActivity.this, REQUIRED_PERMISSIONS,
                        PERMISSIONS_REQUEST_CODE);
            }

        }

    }


    public String getCurrentAddress( double latitude, double longitude) {


        Geocoder geocoder = new Geocoder(this, Locale.getDefault());

        List<Address> addresses;

        try {

            addresses = geocoder.getFromLocation(
                    latitude,
                    longitude,
                    7);
        } catch (IOException ioException) {

            Toast.makeText(this, "you can't use geocoder service", Toast.LENGTH_LONG).show();
            return "geocoder service use faild";
        } catch (IllegalArgumentException illegalArgumentException) {
            Toast.makeText(this, "incorrect gps location", Toast.LENGTH_LONG).show();
            return "incorrect gps location";

        }


        if (addresses == null || addresses.size() == 0) {
            Toast.makeText(this, "can't find address", Toast.LENGTH_LONG).show();
            return "can't find address";

        }

        Address address = addresses.get(0);
        return address.getAddressLine(0).toString()+"\n";

    }



    private void showDialogForLocationServiceSetting() {

        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("location service inactivity");
        builder.setMessage("you need location service for use this app.\n"
                + "do you change this setting?");
        builder.setCancelable(true);
        builder.setPositiveButton("setting", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                Intent callGPSSettingIntent
                        = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivityForResult(callGPSSettingIntent, GPS_ENABLE_REQUEST_CODE);
            }
        });
        builder.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        builder.create().show();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {

            case GPS_ENABLE_REQUEST_CODE:

                if (checkLocationServicesStatus()) {
                    if (checkLocationServicesStatus()) {

                        Log.d("@@@", "onActivityResult : GPS activitied");
                        checkRunTimePermission();
                        return;
                    }
                }

                break;
        }
    }

    public boolean checkLocationServicesStatus() {
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }


    private void permissionCheck(){
        if(Build.VERSION.SDK_INT >= 23){
            permission = new PermissionSupport(this, this);

            if(!permission.checkPermission()){
                permission.requestPermission();
            }
        }
    }

    public void setName() {

        User userInfo = TuyaHomeSdk.getUserInstance().getUser();

        if (userInfo.getNickName().isEmpty())
            username = userInfo.getUsername();
        else
            username = userInfo.getNickName();

        if (username != null)
            txtUserName.setText(username);

    }




}
