package com.test2019.tyapp.longhuan.activity;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.test2019.tyapp.longhuan.R;
import com.test2019.tyapp.longhuan.activity.base.BaseActivity;
import com.test2019.tyapp.longhuan.adapter.Adapter_Category;
import com.test2019.tyapp.longhuan.global.Global;
import com.test2019.tyapp.longhuan.lisener.Clicked_Position_Listener;
import com.test2019.tyapp.longhuan.model.DeviceTypeModel;
import com.test2019.tyapp.longhuan.utils.ActivityUtils;
import com.test2019.tyapp.longhuan.global.IconPaths;
import com.test2019.tyapp.longhuan.utils.PreferenceUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;



public class SelectDeviceIconActivity extends BaseActivity implements View.OnClickListener, Clicked_Position_Listener {

    private RecyclerView rel_dev_icon;
    private int nCurrent_pos = -1;

    private String mFromActivity = null;
    private String mCurDevId = null;
    private String mFromCurFragment = null;
    private int nCur_categoryPos;

    private EditText et_dev_name;
    private EditText et_dev_search_name;
    private EditText et_cur_reverse_cmd;
    private String mCurIconPath = null;


    private ArrayList<DeviceTypeModel> mDevIconModels = new ArrayList<>();

    private String [] mIconName;

    private int [] mIconPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_device_icon);

        if (getIntent() != null){
            mFromActivity = getIntent().getStringExtra(Global.FROM_ACTIVITY);
            mCurDevId = getIntent().getStringExtra(Global.CURRENT_DEV);
            nCur_categoryPos = getIntent().getIntExtra(Global.CURRENT_DEV_CATEGORY, 0);
            mFromCurFragment = getIntent().getStringExtra(Global.FROM_CUR_FRAGMENT);
        }

        init_data();
        init_view();

    }

    private void init_data() {
        mIconName = IconPaths.devIconNames[nCur_categoryPos];
        mIconPath = IconPaths.devIconPath[nCur_categoryPos];
    }

    private void init_view() {
        et_dev_name = findViewById(R.id.et_dev_name);
        et_dev_search_name = findViewById(R.id.et_dev_search_name);
        et_cur_reverse_cmd = findViewById(R.id.et_dev_reverse_cmd);


        String dev_name = PreferenceUtils.getDevName(this, mCurDevId);
        if (dev_name != null) et_dev_name.setText(dev_name);

        String dev_search_name = PreferenceUtils.getDevSearchName(this, mCurDevId);
        if(dev_search_name != null) et_dev_search_name.setText(dev_search_name);

        String cur_reverse_cmd = PreferenceUtils.getCurReverseCmd(this, mCurDevId);
        if(cur_reverse_cmd != null) et_cur_reverse_cmd.setText(cur_reverse_cmd);


        if (mFromActivity.equals("CategoryActivity")){
            findViewById(R.id.btn_change).setVisibility(View.GONE);
            findViewById(R.id.btn_next).setVisibility(View.VISIBLE);
            findViewById(R.id.lin_reverse_cmd).setVisibility(View.GONE);
        }
        else if (mFromActivity.equals("MainActivity")) {
            findViewById(R.id.btn_change).setVisibility(View.VISIBLE);
            findViewById(R.id.btn_next).setVisibility(View.GONE);
            findViewById(R.id.lin_reverse_cmd).setVisibility(View.GONE);

            if(mFromCurFragment != null){
                findViewById(R.id.lin_reverse_cmd).setVisibility(View.VISIBLE);
            }

        }

        findViewById(R.id.img_back).setOnClickListener(this);
        findViewById(R.id.btn_change).setOnClickListener(this);
        findViewById(R.id.btn_next).setOnClickListener(this);

        rel_dev_icon = findViewById(R.id.rel_dev_icon);
        rel_dev_icon.setLayoutManager(new GridLayoutManager(this, 3));

        init_recyclerView();
    }

    private void init_recyclerView() {
        mCurIconPath = PreferenceUtils.getImgPath(this, mCurDevId);

        if (mDevIconModels.size() > 0) mDevIconModels.clear();

        for (int i = 0; i < mIconPath.length; i++) {
            DeviceTypeModel model;
            model = new DeviceTypeModel( mIconName[i], mIconPath[i], 0, true);
            mDevIconModels.add(model);

            if (String.valueOf(mIconPath[i]).equals(mCurIconPath)){
                nCurrent_pos = i;
            }
        }

        Adapter_Category adapter_icon = new Adapter_Category(mDevIconModels, this);
        adapter_icon.set_selected_index(nCurrent_pos);
        rel_dev_icon.setAdapter(adapter_icon);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.img_back:
                finish();
                break;
            case R.id.btn_change:
                changeIcon();
                break;
            case R.id.btn_next:
                goToNext();
                break;
        }
    }

    private void changeIcon(){

        if(mFromCurFragment == null){
            if (!isValidate()) return;
            finish();
        }else{
            if(!isReverseCmd()) return;
            finish();
        }

//        Intent intent = new Intent(Global.ACTION_ICON_CHANGED);
//        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);

    }

    private void goToNext(){
        if (!isValidate()) return;

        Intent intent = new Intent(this, RoomSelectActivity.class);
        intent.putExtra(Global.CURRENT_DEV, mCurDevId);
//        intent.putExtra(Global.CURRENT_DEV_CATEGORY, mCurCategoryName);
//        intent.putExtra(Global.CURRENT_DEV_IMG_PATH, String.valueOf(mIconPath[nCurrent_pos]));
        ActivityUtils.startActivity(this, intent, ActivityUtils.ANIMATE_FORWARD, false);
    }

    private boolean isReverseCmd(){
        String st_cur_reverse_cmd = et_cur_reverse_cmd.getText().toString();
        String st_dev_name = et_dev_name.getText().toString();
        String st_dev_search_name = et_dev_search_name.getText().toString();

        if(nCurrent_pos == -1 && st_dev_name.isEmpty() && st_dev_search_name.isEmpty() && st_cur_reverse_cmd.isEmpty()){

            Toast.makeText(this,
                    "Please select device icon or input any name!", Toast.LENGTH_LONG).show();
            return false;
        }else if(nCurrent_pos == -1 && st_dev_name.isEmpty() && st_dev_search_name.isEmpty() && !st_cur_reverse_cmd.isEmpty()){

            saveCmd(st_cur_reverse_cmd);
        }else if(nCurrent_pos == -1 && st_dev_name.isEmpty() && !st_dev_search_name.isEmpty() && st_cur_reverse_cmd.isEmpty()){

            saveSearchName(st_dev_search_name);
        }else if(nCurrent_pos == -1 && st_dev_name.isEmpty() && !st_dev_search_name.isEmpty() && !st_cur_reverse_cmd.isEmpty()){

            saveSearchNameCmd(st_dev_search_name, st_cur_reverse_cmd);
        }else if(nCurrent_pos == -1 && !st_dev_name.isEmpty() && st_dev_search_name.isEmpty() && st_cur_reverse_cmd.isEmpty()){

            saveDevName(st_dev_name);
        }else if(nCurrent_pos == -1 && !st_dev_name.isEmpty() && !st_dev_search_name.isEmpty() && st_cur_reverse_cmd.isEmpty()){

            saveDevSearchName(st_dev_name, st_dev_search_name);
        }else if(nCurrent_pos == -1 && !st_dev_name.isEmpty() && st_dev_search_name.isEmpty() && !st_cur_reverse_cmd.isEmpty()){

            saveDevCmd(st_dev_name, st_cur_reverse_cmd);
        }else if(nCurrent_pos == -1 && !st_dev_name.isEmpty() && !st_dev_search_name.isEmpty() && !st_cur_reverse_cmd.isEmpty()){

            saveDevSearchNameCmd(st_dev_name, st_dev_search_name, st_cur_reverse_cmd);
        }else if(nCurrent_pos != -1 && st_dev_name.isEmpty() && st_dev_search_name.isEmpty() && st_cur_reverse_cmd.isEmpty()){

            saveIconPath();
        }else if(nCurrent_pos != -1 && st_dev_name.isEmpty() && st_dev_search_name.isEmpty() && !st_cur_reverse_cmd.isEmpty()){

            saveIconCmd(st_cur_reverse_cmd);
        }else if(nCurrent_pos != -1 && st_dev_name.isEmpty() && !st_dev_search_name.isEmpty() && st_cur_reverse_cmd.isEmpty()){

            saveIconSearchName(st_dev_search_name);
        }else if(nCurrent_pos != -1 && st_dev_name.isEmpty() && !st_dev_search_name.isEmpty() && !st_cur_reverse_cmd.isEmpty()){

            saveIconSearchNameCmd(st_dev_search_name, st_cur_reverse_cmd);
        }else if(nCurrent_pos != -1 && !st_dev_name.isEmpty() && st_dev_search_name.isEmpty() && st_cur_reverse_cmd.isEmpty()){

            saveIconDevName(st_dev_name);
        }else if(nCurrent_pos != -1 && !st_dev_name.isEmpty() && !st_dev_search_name.isEmpty() && st_cur_reverse_cmd.isEmpty()){

            saveDevInfo(st_dev_name, st_dev_search_name);
        }else if(nCurrent_pos != -1 && !st_dev_name.isEmpty() && st_dev_search_name.isEmpty() && !st_cur_reverse_cmd.isEmpty()){

            saveIconDevCmd(st_dev_name, st_cur_reverse_cmd);
        }else{

            saveCurReverseCmd(st_dev_name, st_dev_search_name, st_cur_reverse_cmd);
        }

        return true;
    }


    private boolean isValidate(){

        String st_dev_name = et_dev_name.getText().toString();
        String st_dev_search_name = et_dev_search_name.getText().toString();

        if (nCurrent_pos == -1 && st_dev_name.isEmpty() && st_dev_search_name.isEmpty()){

            Toast.makeText(this,
                    "Please select device icon or device name!", Toast.LENGTH_LONG).show();
            return false;

        }else if(nCurrent_pos == -1 && !st_dev_name.isEmpty() && st_dev_search_name.isEmpty()){

            saveDevName(st_dev_name);

        }else if (nCurrent_pos == -1 && st_dev_name.isEmpty() && !st_dev_search_name.isEmpty()){

            saveSearchName(st_dev_search_name);

        }else if(nCurrent_pos == -1 && !st_dev_name.isEmpty() && !st_dev_search_name.isEmpty()){

            saveDevSearchName(st_dev_name, st_dev_search_name);
        }
        else if (nCurrent_pos != -1 && st_dev_name.isEmpty() && st_dev_search_name.isEmpty()){

            saveIconPath();

        }else if(nCurrent_pos != -1 && !st_dev_name.isEmpty() && st_dev_search_name.isEmpty()){

            saveIconDevName(st_dev_name);

        }else if(nCurrent_pos != -1 && st_dev_name.isEmpty() && !st_dev_search_name.isEmpty()){

            saveIconSearchName(st_dev_search_name);

        }
        else{

            saveDevInfo(st_dev_name, st_dev_search_name);
        }

        return true;
    }

    private void saveIconSearchName(String search_name){
        JSONObject object = new JSONObject();
        try {
            object.put(Global.CURRENT_DEV_CATEGORY, String.valueOf(nCur_categoryPos));
            object.put(Global.CURRENT_DEV_IMG_PATH, String.valueOf(mIconPath[nCurrent_pos]));
            object.put(Global.CURRENT_DEV_SEARCH_NAME, search_name);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        PreferenceUtils.set(this, mCurDevId, object.toString());
    }

    private void saveIconDevName(String dev_name){
        JSONObject object = new JSONObject();
        try {
            object.put(Global.CURRENT_DEV_CATEGORY, String.valueOf(nCur_categoryPos));
            object.put(Global.CURRENT_DEV_IMG_PATH, String.valueOf(mIconPath[nCurrent_pos]));
            object.put(Global.CURRENT_DEV_NAME, dev_name);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        PreferenceUtils.set(this, mCurDevId, object.toString());
    }

    private void saveIconPath(){
        JSONObject object = new JSONObject();
        try {
            object.put(Global.CURRENT_DEV_CATEGORY, String.valueOf(nCur_categoryPos));
            object.put(Global.CURRENT_DEV_IMG_PATH, String.valueOf(mIconPath[nCurrent_pos]));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        PreferenceUtils.set(this, mCurDevId, object.toString());
    }

    private void saveDevName(String dev_name){
        JSONObject object = new JSONObject();
        try {
            object.put(Global.CURRENT_DEV_CATEGORY, String.valueOf(nCur_categoryPos));
            object.put(Global.CURRENT_DEV_NAME, dev_name);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        PreferenceUtils.set(this, mCurDevId, object.toString());
    }

    private void saveSearchName(String search_name){
        JSONObject object = new JSONObject();
        try {
            object.put(Global.CURRENT_DEV_CATEGORY, String.valueOf(nCur_categoryPos));
            object.put(Global.CURRENT_DEV_SEARCH_NAME, search_name);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        PreferenceUtils.set(this, mCurDevId, object.toString());
    }

    private void saveDevSearchName(String dev_name, String search_name){
        JSONObject object = new JSONObject();
        try {
            object.put(Global.CURRENT_DEV_CATEGORY, String.valueOf(nCur_categoryPos));
            object.put(Global.CURRENT_DEV_NAME, dev_name);
            object.put(Global.CURRENT_DEV_SEARCH_NAME, search_name);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        PreferenceUtils.set(this, mCurDevId, object.toString());
    }

    private void saveDevInfo(String dev_name, String search_name){
        JSONObject object = new JSONObject();
        try {
            object.put(Global.CURRENT_DEV_CATEGORY, String.valueOf(nCur_categoryPos));
            object.put(Global.CURRENT_DEV_IMG_PATH, String.valueOf(mIconPath[nCurrent_pos]));
            object.put(Global.CURRENT_DEV_NAME, dev_name);
            object.put(Global.CURRENT_DEV_SEARCH_NAME, search_name);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        PreferenceUtils.set(this, mCurDevId, object.toString());
    }




    private void saveCmd(String reverseCmd){
        JSONObject object = new JSONObject();
        try {
            object.put(Global.CURRENT_DEV_CATEGORY, String.valueOf(nCur_categoryPos));
            object.put(Global.CURRENT_CUR_REVERSE_CMD, reverseCmd);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        PreferenceUtils.set(this, mCurDevId, object.toString());
    }

    private void saveSearchNameCmd(String search_name, String reverseCmd){
        JSONObject object = new JSONObject();
        try {
            object.put(Global.CURRENT_DEV_CATEGORY, String.valueOf(nCur_categoryPos));
            object.put(Global.CURRENT_DEV_SEARCH_NAME, search_name);
            object.put(Global.CURRENT_CUR_REVERSE_CMD, reverseCmd);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        PreferenceUtils.set(this, mCurDevId, object.toString());
    }

    private void saveDevCmd(String dev_name, String reverseCmd){
        JSONObject object = new JSONObject();
        try {
            object.put(Global.CURRENT_DEV_CATEGORY, String.valueOf(nCur_categoryPos));
            object.put(Global.CURRENT_DEV_NAME, dev_name);
            object.put(Global.CURRENT_CUR_REVERSE_CMD, reverseCmd);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        PreferenceUtils.set(this, mCurDevId, object.toString());
    }

    private void saveDevSearchNameCmd(String dev_name, String search_name, String reverseCmd){
        JSONObject object = new JSONObject();
        try {
            object.put(Global.CURRENT_DEV_CATEGORY, String.valueOf(nCur_categoryPos));
            object.put(Global.CURRENT_DEV_NAME, dev_name);
            object.put(Global.CURRENT_DEV_SEARCH_NAME, search_name);
            object.put(Global.CURRENT_CUR_REVERSE_CMD, reverseCmd);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        PreferenceUtils.set(this, mCurDevId, object.toString());
    }

    private void saveIconCmd(String reverseCmd){
        JSONObject object = new JSONObject();
        try {
            object.put(Global.CURRENT_DEV_CATEGORY, String.valueOf(nCur_categoryPos));
            object.put(Global.CURRENT_DEV_IMG_PATH, String.valueOf(mIconPath[nCurrent_pos]));
            object.put(Global.CURRENT_CUR_REVERSE_CMD, reverseCmd);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        PreferenceUtils.set(this, mCurDevId, object.toString());
    }

    private void saveIconSearchNameCmd(String search_name, String reverseCmd){
        JSONObject object = new JSONObject();
        try {
            object.put(Global.CURRENT_DEV_CATEGORY, String.valueOf(nCur_categoryPos));
            object.put(Global.CURRENT_DEV_IMG_PATH, String.valueOf(mIconPath[nCurrent_pos]));
            object.put(Global.CURRENT_DEV_SEARCH_NAME, search_name);
            object.put(Global.CURRENT_CUR_REVERSE_CMD, reverseCmd);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        PreferenceUtils.set(this, mCurDevId, object.toString());
    }

    private void saveIconDevCmd(String dev_name, String reverseCmd){
        JSONObject object = new JSONObject();
        try {
            object.put(Global.CURRENT_DEV_CATEGORY, String.valueOf(nCur_categoryPos));
            object.put(Global.CURRENT_DEV_IMG_PATH, String.valueOf(mIconPath[nCurrent_pos]));
            object.put(Global.CURRENT_DEV_NAME, dev_name);
            object.put(Global.CURRENT_CUR_REVERSE_CMD, reverseCmd);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        PreferenceUtils.set(this, mCurDevId, object.toString());
    }


    private void saveCurReverseCmd(String dev_name, String search_name, String reverseCmd){
        JSONObject object = new JSONObject();
        try {
            object.put(Global.CURRENT_DEV_CATEGORY, String.valueOf(nCur_categoryPos));
            object.put(Global.CURRENT_DEV_IMG_PATH, String.valueOf(mIconPath[nCurrent_pos]));
            object.put(Global.CURRENT_DEV_NAME, dev_name);
            object.put(Global.CURRENT_DEV_SEARCH_NAME, search_name);
            object.put(Global.CURRENT_CUR_REVERSE_CMD, reverseCmd);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        PreferenceUtils.set(this, mCurDevId, object.toString());
    }


    private void saveCategoryNumber(){
        JSONObject object = new JSONObject();
        try {
            object.put(Global.CURRENT_DEV_CATEGORY, String.valueOf(nCur_categoryPos));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        PreferenceUtils.set(this, mCurDevId, object.toString());
    }

    public void sendFinishBroadcast(){
        Intent intent = new Intent(Global.ACTION_FINISH);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    //===== Clicked_Position_Listener =================//
    @Override
    public void get_clicked_position(int nPosition) {
        nCurrent_pos = nPosition;
    }
}
