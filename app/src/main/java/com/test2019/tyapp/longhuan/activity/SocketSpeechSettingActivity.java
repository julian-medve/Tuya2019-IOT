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



public class SocketSpeechSettingActivity extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener {

    private EditText et_turn_on, et_turn_on_respond, et_turn_off, et_turn_off_respond;

    private Switch check_all, switch_turn_on, switch_turn_on_respond, switch_turn_off, switch_turn_off_respond;
    private Switch switch_timer;
    private EditText et_timer_prefix;

    private Spinner spinner_select_lang;
    private ImageView img_back;

    private int nSelectedLanguagPos;
    private String stDefaultLanguage;

    private String mDevId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_socket_speech_setting);

        if (getIntent() != null) mDevId = getIntent().getStringExtra(Global.CURRENT_DEV);

        //======== get default laguage ==========//
        stDefaultLanguage = MainApplication.getDefaultEncoding();

        init_view();


    }

    private void init_view() {
        et_turn_on = findViewById(R.id.et_turn_on);
        et_turn_on_respond = findViewById(R.id.et_turn_on_respond);
        et_turn_off = findViewById(R.id.et_turn_off);
        et_turn_off_respond = findViewById(R.id.et_turn_off_respond);

        et_timer_prefix = findViewById(R.id.et_timer_prefix);

        check_all = findViewById(R.id.check_all);
        switch_turn_on = findViewById(R.id.switch_turn_on);
        switch_turn_on_respond = findViewById(R.id.switch_turn_on_respond);
        switch_turn_off = findViewById(R.id.switch_turn_off);
        switch_turn_off_respond = findViewById(R.id.switch_turn_off_respond);

        switch_timer = findViewById(R.id.switch_timer);

        check_all.setOnCheckedChangeListener(this::onCheckedChanged);
        switch_turn_on.setOnCheckedChangeListener(this::onCheckedChanged);
        switch_turn_on_respond.setOnCheckedChangeListener(this::onCheckedChanged);
        switch_turn_off.setOnCheckedChangeListener(this::onCheckedChanged);
        switch_turn_off_respond.setOnCheckedChangeListener(this::onCheckedChanged);

        switch_timer.setOnCheckedChangeListener(this::onCheckedChanged);

        img_back = findViewById(R.id.img_back);
        img_back.setOnClickListener((v)->onBackPressed());

        spinner_select_lang = findViewById(R.id.spinner_select_lang);

        List<String> languages = new ArrayList<>();
        languages.add("English");
        languages.add("Polish");
        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(this, R.layout.item_spinner, languages);

        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        nSelectedLanguagPos = PreferenceUtils.getInt(this, Global.LANGUAGE_SELECTED);
        spinner_select_lang.setAdapter(dataAdapter);
        spinner_select_lang.setSelection(nSelectedLanguagPos);

        spinner_select_lang.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (nSelectedLanguagPos == position) return;
                nSelectedLanguagPos = position;

                if (position == 0) {
                    PreferenceUtils.set(SocketSpeechSettingActivity.this, Global.LANGUAGE_SETTING, "en-US");
                    PreferenceUtils.set(SocketSpeechSettingActivity.this, Global.LANGUAGE_SETTING_PREFIX, "en");
                    stDefaultLanguage = "en-US";
                } else if (position == 1){
                    PreferenceUtils.set(SocketSpeechSettingActivity.this, Global.LANGUAGE_SETTING, "pl-PL");
                    PreferenceUtils.set(SocketSpeechSettingActivity.this, Global.LANGUAGE_SETTING_PREFIX, "pl");
                    stDefaultLanguage = "pl-PL";
                }
                PreferenceUtils.set(SocketSpeechSettingActivity.this, Global.LANGUAGE_SELECTED, position);

                init_data();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                PreferenceUtils.set(SocketSpeechSettingActivity.this, Global.LANGUAGE_SETTING, "en-US");
                PreferenceUtils.set(SocketSpeechSettingActivity.this, Global.LANGUAGE_SETTING_PREFIX, "en");
                PreferenceUtils.set(SocketSpeechSettingActivity.this, Global.LANGUAGE_SELECTED, 0);
            }
        });

        init_data();
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()) {
            case R.id.check_all:
                setEnableAllSwitch(isChecked);
                break;
            case R.id.switch_turn_on:
                et_turn_on.setEnabled(isChecked);
                break;
            case R.id.switch_turn_on_respond:
                et_turn_on_respond.setEnabled(isChecked);
                break;
            case R.id.switch_turn_off:
                et_turn_off.setEnabled(isChecked);
                break;
            case R.id.switch_turn_off_respond:
                et_turn_off_respond.setEnabled(isChecked);
                break;
            case R.id.switch_timer:
                et_timer_prefix.setEnabled(isChecked);
                break;
        }
    }

    private void setEnableAllSwitch(boolean check) {
        if (!check) {
            switch_turn_on.setEnabled(false);
            switch_turn_on_respond.setEnabled(false);
            switch_turn_off.setEnabled(false);
            switch_turn_off_respond.setEnabled(false);

            switch_timer.setEnabled(false);

            et_turn_on.setEnabled(false);
            et_turn_on_respond.setEnabled(false);
            et_turn_off.setEnabled(false);
            et_turn_off_respond.setEnabled(false);

            et_timer_prefix.setEnabled(false);
        }
        else {
            switch_turn_on.setEnabled(true);
            switch_turn_on_respond.setEnabled(true);
            switch_turn_off.setEnabled(true);
            switch_turn_off_respond.setEnabled(true);

            switch_timer.setEnabled(true);

            et_turn_on.setEnabled(switch_turn_on.isChecked());
            et_turn_on_respond.setEnabled(switch_turn_on_respond.isChecked());
            et_turn_off.setEnabled(switch_turn_off.isChecked());
            et_turn_off_respond.setEnabled(switch_turn_off_respond.isChecked());

            et_timer_prefix.setEnabled(switch_timer.isChecked());
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        setAllSpeechSettings();
    }

    private boolean isCheckall, isTurnOn, isTurnOnResponse;
    private boolean isTurnOff, isTurnOffResponse;
    private boolean isSwitchTimer;

    private String stTurnOn, stTurnOnResponse, stTurnOff;
    private String stTurnOffResponse;
    private String stTimerPrefix;

    private void setAllSpeechSettings(){
        stTurnOn = et_turn_on.getText().toString();
        if (stTurnOn.isEmpty()) stTurnOn = Global.EMPTY;
        stTurnOnResponse = et_turn_on_respond.getText().toString();
        if (stTurnOnResponse.isEmpty()) stTurnOnResponse = Global.EMPTY;
        stTurnOff = et_turn_off.getText().toString();
        if (stTurnOff.isEmpty()) stTurnOff = Global.EMPTY;
        stTurnOffResponse = et_turn_off_respond.getText().toString();
        if (stTurnOffResponse.isEmpty()) stTurnOffResponse = Global.EMPTY;

        stTimerPrefix = et_timer_prefix.getText().toString();
        if (stTimerPrefix.isEmpty()) stTimerPrefix = Global.EMPTY;

        JSONObject jsonObj = new JSONObject();
        try {
            jsonObj.put(Global.SPEECH_SETTING_ALL, check_all.isChecked());
            jsonObj.put(Global.SPEECH_SETTING_TURN_ON_CHECK, switch_turn_on.isChecked());
            jsonObj.put(Global.SPEECH_SETTING_TURN_ON_RESPOND_CHECK, switch_turn_on_respond.isChecked());
            jsonObj.put(Global.SPEECH_SETTING_TURN_OFF_CHECK, switch_turn_off.isChecked());
            jsonObj.put(Global.SPEECH_SETTING_TURN_OFF_RESPOND_CHECK, switch_turn_off_respond.isChecked());
            jsonObj.put(Global.SPEECH_SETTING_SWITCH_TIMER_CHECK, switch_timer.isChecked());

            jsonObj.put(Global.SPEECH_SETTING_TURN_ON, stTurnOn);
            jsonObj.put(Global.SPEECH_SETTING_TURN_ON_RESPOND, stTurnOnResponse);
            jsonObj.put(Global.SPEECH_SETTING_TURN_OFF, stTurnOff);
            jsonObj.put(Global.SPEECH_SETTING_TURN_OFF_RESPOND, stTurnOffResponse);
            jsonObj.put(Global.SPEECH_SETTING_SWITCH_TIMER_PREFIX, stTimerPrefix);
        } catch (JSONException e) {

        }
        PreferenceUtils.set(this, mDevId + stDefaultLanguage, jsonObj.toString());
    }

    private void init_data(){
        isCheckall = isTurnOn = isTurnOnResponse = isTurnOff = isTurnOffResponse  = isSwitchTimer = false;
        stTurnOn = stTurnOnResponse = stTurnOff = stTurnOffResponse  = stTimerPrefix = Global.EMPTY;

        String st_settings = PreferenceUtils.getString(this, mDevId + stDefaultLanguage);

        if (st_settings == null) {
            update_UI();
            return;
        }

        try {
            JSONObject objSettings = new JSONObject(st_settings);

            if (objSettings.has(Global.SPEECH_SETTING_ALL))
                isCheckall = objSettings.getBoolean(Global.SPEECH_SETTING_ALL);
            if (objSettings.has(Global.SPEECH_SETTING_TURN_ON_CHECK))
                isTurnOn = objSettings.getBoolean(Global.SPEECH_SETTING_TURN_ON_CHECK);
            if (objSettings.has(Global.SPEECH_SETTING_TURN_ON_RESPOND_CHECK))
                isTurnOnResponse = objSettings.getBoolean(Global.SPEECH_SETTING_TURN_ON_RESPOND_CHECK);
            if (objSettings.has(Global.SPEECH_SETTING_TURN_OFF_CHECK))
                isTurnOff = objSettings.getBoolean(Global.SPEECH_SETTING_TURN_OFF_CHECK);
            if (objSettings.has(Global.SPEECH_SETTING_TURN_OFF_RESPOND_CHECK))
                isTurnOffResponse = objSettings.getBoolean(Global.SPEECH_SETTING_TURN_OFF_RESPOND_CHECK);

            if (objSettings.has(Global.SPEECH_SETTING_SWITCH_TIMER_CHECK))
                isSwitchTimer = objSettings.getBoolean(Global.SPEECH_SETTING_SWITCH_TIMER_CHECK);

            if (objSettings.has(Global.SPEECH_SETTING_TURN_ON))
                stTurnOn = objSettings.getString(Global.SPEECH_SETTING_TURN_ON);
            if (objSettings.has(Global.SPEECH_SETTING_TURN_ON_RESPOND))
                stTurnOnResponse = objSettings.getString(Global.SPEECH_SETTING_TURN_ON_RESPOND);
            if (objSettings.has(Global.SPEECH_SETTING_TURN_OFF))
                stTurnOff = objSettings.getString(Global.SPEECH_SETTING_TURN_OFF);
            if (objSettings.has(Global.SPEECH_SETTING_TURN_OFF_RESPOND))
                stTurnOffResponse = objSettings.getString(Global.SPEECH_SETTING_TURN_OFF_RESPOND);
            if (objSettings.has(Global.SPEECH_SETTING_SWITCH_TIMER_PREFIX))
                stTimerPrefix = objSettings.getString(Global.SPEECH_SETTING_SWITCH_TIMER_PREFIX);

            update_UI();
        } catch (JSONException e) {
            update_UI();
        }
    }

    private void update_UI() {
        check_all.setChecked(isCheckall);
        switch_turn_on.setChecked(isTurnOn);
        switch_turn_on_respond.setChecked(isTurnOnResponse);
        switch_turn_off.setChecked(isTurnOff);
        switch_turn_off_respond.setChecked(isTurnOffResponse);
        switch_timer.setChecked(isSwitchTimer);

        if (!stTurnOn.equals(Global.EMPTY))             et_turn_on.setText(stTurnOn);
        else                                            et_turn_on.getText().clear();
        if (!stTurnOnResponse.equals(Global.EMPTY))     et_turn_on_respond.setText(stTurnOnResponse);
        else                                            et_turn_on_respond.getText().clear();
        if (!stTurnOff.equals(Global.EMPTY))            et_turn_off.setText(stTurnOff);
        else                                            et_turn_off.getText().clear();
        if (!stTurnOffResponse.equals(Global.EMPTY))    et_turn_off_respond.setText(stTurnOffResponse);
        else                                            et_turn_off_respond.getText().clear();
        if (!stTimerPrefix.equals(Global.EMPTY))        et_timer_prefix.setText(stTimerPrefix);
        else                                            et_timer_prefix.getText().clear();

        setEnableAllSwitch(isCheckall);
    }
}


