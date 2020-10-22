package com.test2019.tyapp.longhuan.speech;

import android.content.Context;

import com.test2019.tyapp.longhuan.global.Global;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

public class SpeechModel implements IJsonGenerator, Serializable {

    private final String TAG = "SpeechModel";
    public Context mContext;

    public boolean isCheckAll;
    public String mTurnOn;
    public boolean isTurnOn;
    public String mTurnOnReply;
    public boolean isTurnOnReply;
    public String mTurnOff;
    public boolean isTurnOff;
    public String mTurnOffReply;
    public boolean isTurnOffReply;
    public String mReversal;
    public boolean isReversal;
    public String mStatusOnReply;
    public boolean isGetOn;
    public String mStatusOffReply;
    public boolean isGetOff;

//    public SpeechModel(Context ctx) {
//        mContext = ctx;
//    }

    @Override
    public String generateJson() {
        JSONObject jsonObj = new JSONObject();
        try {

            jsonObj.put(Global.SPEECH_SETTING_ALL, isCheckAll);
            if (mTurnOn != null)  // && !mTurnOn.equals(""))
                jsonObj.put(Global.SPEECH_SETTING_TURN_ON,mTurnOn);
            jsonObj.put(Global.SPEECH_SETTING_TURN_ON_CHECK, isTurnOn);
            if (mTurnOnReply != null) // && !mTurnOnReply.equals(""))
                jsonObj.put(Global.SPEECH_SETTING_TURN_ON_RESPOND,mTurnOnReply);
            jsonObj.put(Global.SPEECH_SETTING_TURN_ON_RESPOND_CHECK, isTurnOnReply);
            if (mTurnOff != null) // && !mTurnOff.equals(""))
                jsonObj.put(Global.SPEECH_SETTING_TURN_OFF,mTurnOff);
            jsonObj.put(Global.SPEECH_SETTING_TURN_OFF_CHECK, isTurnOff);
            if (mTurnOffReply != null) // && !mTurnOffReply.equals(""))
                jsonObj.put(Global.SPEECH_SETTING_TURN_OFF_RESPOND,mTurnOffReply);
            jsonObj.put(Global.SPEECH_SETTING_TURN_OFF_RESPOND_CHECK, isTurnOffReply);
            if (mReversal != null) // && !mReversal.equals(""))
                jsonObj.put(Global.SPEECH_SETTING_TURN_RESERVE,mReversal);
            jsonObj.put(Global.SPEECH_SETTING_TURN_RESERVE_CHECK, isReversal);
            if (mStatusOnReply != null) // && !mStatusOnReply.equals(""))
                jsonObj.put(Global.SPEECH_SETTING_GET_ON,mStatusOnReply);
            jsonObj.put(Global.SPEECH_SETTING_GET_ON_CHECK, isGetOn);
            if (mStatusOffReply != null) // && !mStatusOffReply.equals(""))
                jsonObj.put(Global.SPEECH_SETTING_GET_OFF,mStatusOffReply);
            jsonObj.put(Global.SPEECH_SETTING_GET_OFF_CHECK, isGetOff);

            return jsonObj.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return "";
    }

//    public JSONObject getSettings() {
//        String settings = PreferenceUtils.getString(mContext, Global.SPEECH_SETTING);
////        JSONParser parser = new JSONParser();
////        JSONObject json = (JSONObject) parser.parse(stringToParse);
//        JSONObject result = null;
//        try {
//            result = new JSONObject(settings);
//        } catch (JSONException e) {
//            Log.d(TAG, e.getMessage());
//        }
//
//        return result;
//    }
//
//    public void setSettings(JSONObject obj) {
//        String result = obj.toString();
//        PreferenceUtils.set(mContext, Global.SPEECH_SETTING, result);
//    }
}
