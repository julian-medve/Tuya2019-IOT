package com.test2019.tyapp.longhuan.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.widget.ImageView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.test2019.tyapp.longhuan.R;
import com.test2019.tyapp.longhuan.global.Global;
import com.tuya.smart.sdk.bean.DeviceBean;

import org.json.JSONException;
import org.json.JSONObject;

public class PreferenceUtils {

    private static final String APP_NAME = "Custom_Tuya";
    private static SharedPreferences mPreference;

    public static void set(Context context, String channel, int data) {
        mPreference = context.getSharedPreferences(APP_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = mPreference.edit();
        editor.putInt(channel, data);
        editor.apply();
    }

    public static void set(Context context, String channel, long data) {
        mPreference = context.getSharedPreferences(APP_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = mPreference.edit();
        editor.putLong(channel, data);
        editor.apply();
    }

    public static void set(Context context, String channel, boolean data) {
        mPreference = context.getSharedPreferences(APP_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = mPreference.edit();
        editor.putBoolean(channel, data);
        editor.apply();
    }

    public static void set(Context context, String channel, String data) {
        mPreference = context.getSharedPreferences(APP_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = mPreference.edit();
        editor.putString(channel, data);
        editor.apply();
    }

    public static int getInt(Context context, String channel) {
        int data;
        mPreference = context.getSharedPreferences(APP_NAME, Context.MODE_PRIVATE);
        data = mPreference.getInt(channel, 0);
        return data;
    }

    public static long getLong(Context context, String channel) {
        long data;
        mPreference = context.getSharedPreferences(APP_NAME, Context.MODE_PRIVATE);
        data = mPreference.getLong(channel, 0);
        return data;
    }

    public static boolean getBoolean(Context context, String channel) {
        boolean data;
        mPreference = context.getSharedPreferences(APP_NAME, Context.MODE_PRIVATE);
        data = mPreference.getBoolean(channel, false);
        return data;
    }

    public static String getString(Context context, String channel) {
        String data;
        mPreference = context.getSharedPreferences(APP_NAME, Context.MODE_PRIVATE);
        data = mPreference.getString(channel, null);
        return data;
    }

    public static void removeSingleChannel(Context context, String channel) {
        mPreference = context.getSharedPreferences(APP_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = mPreference.edit();
        editor.remove(channel);
//        editor.apply();
        editor.commit();
    }

    public static void clear(Context context) {
        mPreference = context.getSharedPreferences(APP_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = mPreference.edit();
        editor.clear();
        editor.apply();
    }

    //======== set device image ================//

    public static void set_dev_image(Context context, DeviceBean deviceBean, ImageView target){
        String devImgPath = getImgPath(context, deviceBean.getDevId());
        if (devImgPath != null) {
            target.setImageResource(Integer.parseInt(devImgPath.trim()));
        } else {
            Picasso.get()
                    .load(deviceBean.getIconUrl())
                    .into(target, new Callback() {
                        @Override
                        public void onSuccess() {

                        }

                        @Override
                        public void onError(Exception e) {
                            target.setImageResource(R.mipmap.add);
                        }
                    });
        }
    }

    public static void set_icon_image(Context context, DeviceBean deviceBean, ImageView target){
        String devIconpath = getIconPath(context, deviceBean.getDevId());
        if(devIconpath != null){
            target.setImageResource(Integer.parseInt(devIconpath.trim()));
        }else{
            target.setImageResource(R.mipmap.ic_dev1);
        }
    }

    public static String getImgPath(Context context, String devId){
        String stImgPath = null;

        String devInfo = PreferenceUtils.getString(context, devId);
        if (devInfo == null) return null;

        try {
            JSONObject object = new JSONObject(devInfo);
            stImgPath = object.getString(Global.CURRENT_DEV_IMG_PATH);
        } catch (JSONException e) {
            return null;
        }

        return stImgPath;
    }

    public static String getIconPath(Context context, String devId){
        String stIconPath = null;

        String devInfo = PreferenceUtils.getString(context, devId + "icon");

        if(devInfo == null) return null;

        try{
            JSONObject object = new JSONObject(devInfo);
            stIconPath = object.getString(Global.CURRENT_DEV_ICON_PATH);
        }catch (JSONException e){
            return null;
        }

        return stIconPath;
    }

    public static String getDevName(Context context, String devId){
        String stDevName = null;

        String devInfo = PreferenceUtils.getString(context, devId);
        if (devInfo == null) return null;

        try {
            JSONObject object = new JSONObject(devInfo);
            stDevName = object.getString(Global.CURRENT_DEV_NAME);
        } catch (JSONException e) {
            return null;
        }

        return stDevName;
    }

    public static String getDevSearchName(Context context, String devId){

        String stDevSearchName = null;
        String devInfo = PreferenceUtils.getString(context, devId);

        if(devInfo == null) return null;

        try{
            JSONObject object = new JSONObject(devInfo);
            stDevSearchName = object.getString(Global.CURRENT_DEV_SEARCH_NAME);
        }catch (JSONException e){
            return null;
        }

        return stDevSearchName;
    }

    public static String getCurReverseCmd(Context context, String devId){

        String stCurReverseCmd = null;
        String devInfo = PreferenceUtils.getString(context, devId);

        if(devInfo == null) return null;

        try{
            JSONObject object = new JSONObject(devInfo);
            stCurReverseCmd = object.getString(Global.CURRENT_CUR_REVERSE_CMD);
        }catch (JSONException e){
            return null;
        }

        return  stCurReverseCmd;
    }

    public static int getDevCategoryNumber(Context context, String devId){
        int nNumber = 0;

        String devInfo = PreferenceUtils.getString(context, devId);
        if (devInfo == null) return nNumber;

        try {
            JSONObject object = new JSONObject(devInfo);
            nNumber = object.getInt(Global.CURRENT_DEV_CATEGORY);
        } catch (JSONException e) {
            return nNumber;
        }

        return nNumber;
    }

    //======= Firebase id Token ================//
    private static final String FIREBASE_ID_TOKEN = "firebase_idToken";
    public static void setIdToken(Context context, String idToken) {
        mPreference = context.getSharedPreferences(APP_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = mPreference.edit();
        editor.putString(FIREBASE_ID_TOKEN, idToken);
        editor.apply();
    }

    public static String getIdToken(Context context){
        mPreference = context.getSharedPreferences(APP_NAME, Context.MODE_PRIVATE);
        return mPreference.getString(FIREBASE_ID_TOKEN, null);
    }
}
