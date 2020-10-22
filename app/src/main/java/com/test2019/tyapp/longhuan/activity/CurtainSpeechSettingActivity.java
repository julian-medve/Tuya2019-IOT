package com.test2019.tyapp.longhuan.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Switch;

import com.test2019.tyapp.longhuan.MainApplication;
import com.test2019.tyapp.longhuan.R;
import com.test2019.tyapp.longhuan.global.Global;
import com.test2019.tyapp.longhuan.utils.PreferenceUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;



public class CurtainSpeechSettingActivity extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener {

    private EditText et_open, et_open_respond, et_close, et_close_respond;
    private EditText et_open_respond_before, et_close_respond_before;
    private Switch check_all, switch_open, switch_open_respond, switch_close, switch_close_respond;
    private Switch  switch_open_respond_before, switch_close_respond_before;

//    private EditText et_reverse, et_get_open, et_get_close;
//    private Switch switch_reserve, switch_get_open, switch_get_close;

    private Spinner spinner_select_lang;
    private ImageView img_back;

    private int nSelectedLanguagPos;
    private String stDefaultLanguage;

    private String mDevId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_curtain_speech_setting);

        if(getIntent() != null) mDevId = getIntent().getStringExtra(Global.CURRENT_DEV);

        //============= get default language ================//
        stDefaultLanguage = MainApplication.getDefaultEncoding();

        initView();


    }

    @Override
    protected void onResume() {
        super.onResume();


    }

    private void initView(){

        et_open = findViewById(R.id.et_open);
        et_open_respond = findViewById(R.id.et_open_respond);
        et_close = findViewById(R.id.et_close);
        et_close_respond = findViewById(R.id.et_close_respond);
//        et_reverse = findViewById(R.id.et_reverse);
//        et_get_open = findViewById(R.id.et_get_open);
//        et_get_close = findViewById(R.id.et_get_close);
        et_open_respond_before = findViewById(R.id.et_open_respond_before);
        et_close_respond_before = findViewById(R.id.et_close_respond_before);

        check_all = findViewById(R.id.check_all);
        switch_open = findViewById(R.id.switch_open);
        switch_open_respond = findViewById(R.id.switch_open_respond);
        switch_close = findViewById(R.id.switch_close);
        switch_close_respond = findViewById(R.id.switch_close_respond);
//        switch_reserve = findViewById(R.id.switch_device_reserve);
//        switch_get_open = findViewById(R.id.switch_get_open);
//        switch_get_close = findViewById(R.id.switch_get_close);
        switch_open_respond_before = findViewById(R.id.switch_open_respond_before);
        switch_close_respond_before = findViewById(R.id.switch_close_respond_before);

        check_all.setOnCheckedChangeListener(this::onCheckedChanged);
        switch_open.setOnCheckedChangeListener(this::onCheckedChanged);
        switch_open_respond.setOnCheckedChangeListener(this::onCheckedChanged);
        switch_close.setOnCheckedChangeListener(this::onCheckedChanged);
        switch_close_respond.setOnCheckedChangeListener(this::onCheckedChanged);
//        switch_reserve.setOnCheckedChangeListener(this::onCheckedChanged);
//        switch_get_open.setOnCheckedChangeListener(this::onCheckedChanged);
//        switch_get_close.setOnCheckedChangeListener(this::onCheckedChanged);
        switch_close_respond_before.setOnCheckedChangeListener(this::onCheckedChanged);
        switch_open_respond_before.setOnCheckedChangeListener(this::onCheckedChanged);

        img_back = findViewById(R.id.img_back);
        img_back.setOnClickListener((v)-> onBackPressed());

        spinner_select_lang = findViewById(R.id.spinner_select_lang);

        List<String> languages = new ArrayList<>();
        languages.add("English");
        languages.add("Polish");

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(this, R.layout.item_spinner, languages);

        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        nSelectedLanguagPos = PreferenceUtils.getInt(this, Global.LANGUAGE_SELECTED);
        spinner_select_lang.setAdapter(dataAdapter);
        spinner_select_lang.setSelection(nSelectedLanguagPos);

        spinner_select_lang.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(nSelectedLanguagPos == position) return;
                nSelectedLanguagPos = position;

                if(position == 0){
                    PreferenceUtils.set(CurtainSpeechSettingActivity.this, Global.LANGUAGE_SETTING, "en-US");
                    PreferenceUtils.set(CurtainSpeechSettingActivity.this, Global.LANGUAGE_SETTING_PREFIX, "en");
                    stDefaultLanguage = "en-US";
                }else if(position == 1){
                    PreferenceUtils.set(CurtainSpeechSettingActivity.this, Global.LANGUAGE_SETTING, "pl-PL");
                    PreferenceUtils.set(CurtainSpeechSettingActivity.this, Global.LANGUAGE_SETTING_PREFIX, "pl");
                    stDefaultLanguage = "pl-PL";
                }

                PreferenceUtils.set(CurtainSpeechSettingActivity.this, Global.LANGUAGE_SELECTED, position);

                init_data();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                PreferenceUtils.set(CurtainSpeechSettingActivity.this, Global.LANGUAGE_SETTING, "en-US");
                PreferenceUtils.set(CurtainSpeechSettingActivity.this, Global.LANGUAGE_SETTING_PREFIX, "en");
                PreferenceUtils.set(CurtainSpeechSettingActivity.this, Global.LANGUAGE_SELECTED, 0);
            }
        });

        init_data();
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

        switch (buttonView.getId()){
            case R.id.check_all:
                setEnableAllSwitch(isChecked);
                break;
            case R.id.switch_open:
                et_open.setEnabled(isChecked);
                break;
            case R.id.switch_open_respond:
                et_open_respond.setEnabled(isChecked);
                break;
            case R.id.switch_close:
                et_close.setEnabled(isChecked);
                break;
            case R.id.switch_close_respond:
                et_close_respond.setEnabled(isChecked);
                break;
            case R.id.switch_open_respond_before:
                et_open_respond_before.setEnabled(isChecked);
                break;
            case R.id.switch_close_respond_before:
                et_close_respond_before.setEnabled(isChecked);
                break;
//            case R.id.switch_device_reserve:
//                et_reverse.setEnabled(isChecked);
//                break;
//            case R.id.switch_get_open:
//                et_get_open.setEnabled(isChecked);
//                break;
//            case R.id.switch_get_close:
//                et_get_close.setEnabled(isChecked);
//                break;

        }

    }

    private void setEnableAllSwitch(boolean check){

        if(!check){

            switch_open.setEnabled(false);
            switch_open_respond.setEnabled(false);
            switch_close.setEnabled(false);
            switch_close_respond.setEnabled(false);
            switch_open_respond_before.setEnabled(false);
            switch_close_respond_before.setEnabled(false);
//            switch_reserve.setEnabled(false);
//            switch_get_open.setEnabled(false);
//            switch_get_close.setEnabled(false);

            et_open.setEnabled(false);
            et_open_respond.setEnabled(false);
            et_close.setEnabled(false);
            et_close_respond.setEnabled(false);
            et_open_respond_before.setEnabled(false);
            et_close_respond_before.setEnabled(false);
//            et_reverse.setEnabled(false);
//            et_get_open.setEnabled(false);
//            et_get_close.setEnabled(false);

        }else {

            switch_open.setEnabled(true);
            switch_open_respond.setEnabled(true);
            switch_close.setEnabled(true);
            switch_close_respond.setEnabled(true);
            switch_open_respond_before.setEnabled(true);
            switch_close_respond_before.setEnabled(true);
//            switch_reserve.setEnabled(true);
//            switch_get_open.setEnabled(true);
//            switch_get_close.setEnabled(true);

            et_open.setEnabled(switch_open.isChecked());
            et_open_respond.setEnabled(switch_open_respond.isChecked());
            et_close.setEnabled(switch_close.isChecked());
            et_close_respond.setEnabled(switch_close_respond.isChecked());
            et_open_respond_before.setEnabled(switch_open_respond_before.isChecked());
            et_close_respond_before.setEnabled(switch_close_respond_before.isChecked());
//            et_reverse.setEnabled(switch_reserve.isChecked());
//            et_get_open.setEnabled(switch_get_open.isChecked());
//            et_get_close.setEnabled(switch_get_close.isChecked());
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        setAllSpeechSettings();
    }

    private boolean isCheckall, isOpen, isOpenResponse, isClose, isCloseResponse;
    private boolean isOpenResponse_before, isCloseResponse_before;
    private String stOpen, stOpenResponse, stClose, stCloseResponse;
    private String stOpenResponse_before, stCloseResponse_before;

//    private boolean isReverse, isGetOpen, isGetClose;
//    private String stReverse, stGetOpen, stGetClose;

    private void setAllSpeechSettings(){

        stOpen = et_open.getText().toString();
            if(stOpen.isEmpty()) stOpen = Global.EMPTY;
        stOpenResponse = et_open_respond.getText().toString();
            if(stOpenResponse.isEmpty()) stOpenResponse = Global.EMPTY;
        stClose = et_close.getText().toString();
            if(stClose.isEmpty()) stClose = Global.EMPTY;
        stCloseResponse = et_close_respond.getText().toString();
            if(stCloseResponse.isEmpty()) stCloseResponse = Global.EMPTY;
        stOpenResponse_before = et_open_respond_before.getText().toString();
            if(stOpenResponse_before.isEmpty()) stOpenResponse_before = Global.EMPTY;
        stCloseResponse_before = et_close_respond_before.getText().toString();
            if(stCloseResponse_before.isEmpty()) stCloseResponse_before = Global.EMPTY;

//        stReverse = et_reverse.getText().toString();
//        if(stReverse.isEmpty()) stReverse = Global.EMPTY;
//        stGetOpen = et_get_open.getText().toString();
//        if(stGetOpen.isEmpty()) stGetOpen = Global.EMPTY;
//        stGetClose = et_get_close.getText().toString();
//        if(stGetClose.isEmpty()) stGetClose = Global.EMPTY;


        JSONObject jsonObject = new JSONObject();

        try{
            jsonObject.put(Global.SPEECH_SETTING_ALL, check_all.isChecked());
            jsonObject.put(Global.SPEECH_SETTING_TURN_ON_CHECK, switch_open.isChecked());
            jsonObject.put(Global.SPEECH_SETTING_TURN_ON_RESPOND_CHECK, switch_open_respond.isChecked());
            jsonObject.put(Global.SPEECH_SETTING_TURN_OFF_CHECK, switch_close.isChecked());
            jsonObject.put(Global.SPEECH_SETTING_TURN_OFF_RESPOND_CHECK, switch_close_respond.isChecked());
//            jsonObject.put(Global.SPEECH_SETTING_TURN_RESERVE_CHECK, switch_reserve.isChecked());
//            jsonObject.put(Global.SPEECH_SETTING_GET_ON_CHECK, switch_get_open.isChecked());
//            jsonObject.put(Global.SPEECH_SETTING_GET_OFF_CHECK, switch_get_close.isChecked());
            jsonObject.put(Global.SPEECH_SETTING_TURN_ON_RESPOND_BEFORE_CHECK, switch_open_respond_before.isChecked());
            jsonObject.put(Global.SPEECH_SETTING_TURN_OFF_RESPOND_BEFORE_CHECK, switch_close_respond_before.isChecked());

            jsonObject.put(Global.SPEECH_SETTING_TURN_ON, stOpen);
            jsonObject.put(Global.SPEECH_SETTING_TURN_ON_RESPOND, stOpenResponse);
            jsonObject.put(Global.SPEECH_SETTING_TURN_OFF, stClose);
            jsonObject.put(Global.SPEECH_SETTING_TURN_OFF_RESPOND, stCloseResponse);
//            jsonObject.put(Global.SPEECH_SETTING_TURN_RESERVE, stReverse);
//            jsonObject.put(Global.SPEECH_SETTING_GET_ON, stGetOpen);
//            jsonObject.put(Global.SPEECH_SETTING_GET_OFF, stGetClose);
            jsonObject.put(Global.SPEECH_SETTING_TURN_ON_RESPOND_BEFORE, stOpenResponse_before);
            jsonObject.put(Global.SPEECH_SETTING_TURN_OFF_RESPOND_BEFORE, stCloseResponse_before);


        }catch (JSONException e){

        }

        PreferenceUtils.set(this, mDevId + stDefaultLanguage, jsonObject.toString());
    }

    private void init_data(){

        isCheckall = isOpen = isOpenResponse = isClose = isCloseResponse = false;
        isOpenResponse_before = isCloseResponse_before = false;
        stOpen = stOpenResponse = stClose = stCloseResponse = Global.EMPTY;
        stOpenResponse_before = stCloseResponse_before = Global.EMPTY;

//        isReverse = isGetOpen = isGetClose = false;
//        stReverse = stGetOpen = stGetClose = Global.EMPTY;

        String st_settings = PreferenceUtils.getString(this, mDevId + stDefaultLanguage);

        if(st_settings == null){
            update_UI();
            return;
        }

        try {
            JSONObject objSettings = new JSONObject(st_settings);

            if (objSettings.has(Global.SPEECH_SETTING_ALL))
                isCheckall = objSettings.getBoolean(Global.SPEECH_SETTING_ALL);
            if (objSettings.has(Global.SPEECH_SETTING_TURN_ON_CHECK))
                isOpen = objSettings.getBoolean(Global.SPEECH_SETTING_TURN_ON_CHECK);
            if (objSettings.has(Global.SPEECH_SETTING_TURN_ON_RESPOND_CHECK))
                isOpenResponse = objSettings.getBoolean(Global.SPEECH_SETTING_TURN_ON_RESPOND_CHECK);
            if (objSettings.has(Global.SPEECH_SETTING_TURN_OFF_CHECK))
                isClose = objSettings.getBoolean(Global.SPEECH_SETTING_TURN_OFF_CHECK);
            if (objSettings.has(Global.SPEECH_SETTING_TURN_OFF_RESPOND_CHECK))
                isCloseResponse = objSettings.getBoolean(Global.SPEECH_SETTING_TURN_OFF_RESPOND_CHECK);
//            if (objSettings.has(Global.SPEECH_SETTING_TURN_RESERVE_CHECK))
//                isReverse = objSettings.getBoolean(Global.SPEECH_SETTING_TURN_RESERVE_CHECK);
//            if (objSettings.has(Global.SPEECH_SETTING_GET_ON_CHECK))
//                isGetOpen = objSettings.getBoolean(Global.SPEECH_SETTING_GET_ON_CHECK);
//            if (objSettings.has(Global.SPEECH_SETTING_GET_OFF_CHECK))
//                isGetClose = objSettings.getBoolean(Global.SPEECH_SETTING_GET_OFF_CHECK);
            if(objSettings.has(Global.SPEECH_SETTING_TURN_ON_RESPOND_BEFORE_CHECK))
                isOpenResponse_before = objSettings.getBoolean(Global.SPEECH_SETTING_TURN_ON_RESPOND_BEFORE_CHECK);
            if(objSettings.has(Global.SPEECH_SETTING_TURN_OFF_RESPOND_BEFORE_CHECK))
                isCloseResponse_before = objSettings.getBoolean(Global.SPEECH_SETTING_TURN_OFF_RESPOND_BEFORE_CHECK);


            if (objSettings.has(Global.SPEECH_SETTING_TURN_ON))
                stOpen = objSettings.getString(Global.SPEECH_SETTING_TURN_ON);
            if (objSettings.has(Global.SPEECH_SETTING_TURN_ON_RESPOND))
                stOpenResponse = objSettings.getString(Global.SPEECH_SETTING_TURN_ON_RESPOND);
            if (objSettings.has(Global.SPEECH_SETTING_TURN_OFF))
                stClose = objSettings.getString(Global.SPEECH_SETTING_TURN_OFF);
            if (objSettings.has(Global.SPEECH_SETTING_TURN_OFF_RESPOND))
                stCloseResponse = objSettings.getString(Global.SPEECH_SETTING_TURN_OFF_RESPOND);
//            if (objSettings.has(Global.SPEECH_SETTING_TURN_RESERVE))
//                stReverse = objSettings.getString(Global.SPEECH_SETTING_TURN_RESERVE);
//            if (objSettings.has(Global.SPEECH_SETTING_GET_ON))
//                stGetOpen = objSettings.getString(Global.SPEECH_SETTING_GET_ON);
//            if (objSettings.has(Global.SPEECH_SETTING_GET_OFF))
//                stGetClose = objSettings.getString(Global.SPEECH_SETTING_GET_OFF);
            if(objSettings.has(Global.SPEECH_SETTING_TURN_ON_RESPOND_BEFORE))
                stOpenResponse_before = objSettings.getString(Global.SPEECH_SETTING_TURN_ON_RESPOND_BEFORE);
            if(objSettings.has(Global.SPEECH_SETTING_TURN_OFF_RESPOND_BEFORE))
                stCloseResponse_before = objSettings.getString(Global.SPEECH_SETTING_TURN_OFF_RESPOND_BEFORE);

            update_UI();

        }catch (JSONException e){
            update_UI();
        }
    }

    private void update_UI(){

        check_all.setChecked(isCheckall);
        switch_open.setChecked(isOpen);
        switch_open_respond.setChecked(isOpenResponse);
        switch_close.setChecked(isClose);
        switch_close_respond.setChecked(isCloseResponse);
//        switch_reserve.setChecked(isReverse);
//        switch_get_open.setChecked(isGetOpen);
//        switch_get_close.setChecked(isGetClose);
        switch_open_respond_before.setChecked(isOpenResponse_before);
        switch_close_respond_before.setChecked(isCloseResponse_before);


        if (!stOpen.equals(Global.EMPTY))             et_open.setText(stOpen);
        else                                            et_open.getText().clear();
        if (!stOpenResponse.equals(Global.EMPTY))     et_open_respond.setText(stOpenResponse);
        else                                            et_open_respond.getText().clear();
        if (!stClose.equals(Global.EMPTY))            et_close.setText(stClose);
        else                                            et_close.getText().clear();
        if (!stCloseResponse.equals(Global.EMPTY))    et_close_respond.setText(stCloseResponse);
        else                                            et_close_respond.getText().clear();
//        if (!stReverse.equals(Global.EMPTY))            et_reverse.setText(stReverse);
//        else                                            et_reverse.getText().clear();
//        if (!stGetOpen.equals(Global.EMPTY))              et_get_open.setText(stGetOpen);
//        else                                            et_get_open.getText().clear();
//        if(!stGetClose.equals(Global.EMPTY))            et_get_close.setText(stGetClose);
//        else                                            et_get_close.getText().clear();
        if(!stOpenResponse_before.equals(Global.EMPTY))    et_open_respond_before.setText(stOpenResponse_before);
        else                                                et_open_respond_before.getText().clear();
        if(!stCloseResponse_before.equals(Global.EMPTY))    et_close_respond_before.setText(stCloseResponse_before);
        else                                                et_close_respond_before.getText().clear();


        setEnableAllSwitch(isCheckall);

    }
}
