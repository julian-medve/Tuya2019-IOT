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



public class SwitchSpeechNewSettingActivity extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener {

    private EditText et_turn_on, et_turn_on_respond, et_turn_off, et_turn_off_respond, et_reverse, et_get_on, et_get_off;

    private Switch check_all, switch_turn_on, switch_turn_on_respond, switch_turn_off, switch_turn_off_respond;
    private Switch switch_turn_reserve, switch_get_on, switch_get_off;


    private Spinner spinner_select_lang;
    private ImageView img_back;

    private int nSelectedLanguagPos;
    private String stDefaultLanguage;

    private String mDevId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_speech_setting);

        if (getIntent() != null) mDevId = getIntent().getStringExtra(Global.CURRENT_DEV);

        //======== get default laguage ==========//
        stDefaultLanguage = MainApplication.getDefaultEncoding();

        init_view();


    }

    private void init_view() {
        et_turn_on = findViewById(R.id.turn_on_command);
        et_turn_on_respond = findViewById(R.id.turn_on_respond);
        et_turn_off = findViewById(R.id.turn_off_command);
        et_turn_off_respond = findViewById(R.id.turn_off_respond);
        et_reverse = findViewById(R.id.turn_reserve_command);
        et_get_on = findViewById(R.id.get_on_command);
        et_get_off = findViewById(R.id.get_off_command);


        check_all = findViewById(R.id.check_all);
        switch_turn_on = findViewById(R.id.switch_turn_on);
        switch_turn_on_respond = findViewById(R.id.switch_turn_on_respond);
        switch_turn_off = findViewById(R.id.switch_turn_off);
        switch_turn_off_respond = findViewById(R.id.switch_turn_off_respond);
        switch_turn_reserve = findViewById(R.id.switch_turn_reserve);
        switch_get_on = findViewById(R.id.switch_get_on);
        switch_get_off = findViewById(R.id.switch_get_off);


        check_all.setOnCheckedChangeListener(this::onCheckedChanged);
        switch_turn_on.setOnCheckedChangeListener(this::onCheckedChanged);
        switch_turn_on_respond.setOnCheckedChangeListener(this::onCheckedChanged);
        switch_turn_off.setOnCheckedChangeListener(this::onCheckedChanged);
        switch_turn_off_respond.setOnCheckedChangeListener(this::onCheckedChanged);
        switch_turn_reserve.setOnCheckedChangeListener(this::onCheckedChanged);
        switch_get_on.setOnCheckedChangeListener(this::onCheckedChanged);
        switch_get_off.setOnCheckedChangeListener(this::onCheckedChanged);


        img_back = findViewById(R.id.btn_back);
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
                    PreferenceUtils.set(SwitchSpeechNewSettingActivity.this, Global.LANGUAGE_SETTING, "en-US");
                    PreferenceUtils.set(SwitchSpeechNewSettingActivity.this, Global.LANGUAGE_SETTING_PREFIX, "en");
                    stDefaultLanguage = "en-US";
                } else if (position == 1){
                    PreferenceUtils.set(SwitchSpeechNewSettingActivity.this, Global.LANGUAGE_SETTING, "pl-PL");
                    PreferenceUtils.set(SwitchSpeechNewSettingActivity.this, Global.LANGUAGE_SETTING_PREFIX, "pl");
                    stDefaultLanguage = "pl-PL";
                }
                PreferenceUtils.set(SwitchSpeechNewSettingActivity.this, Global.LANGUAGE_SELECTED, position);

                init_data();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                PreferenceUtils.set(SwitchSpeechNewSettingActivity.this, Global.LANGUAGE_SETTING, "en-US");
                PreferenceUtils.set(SwitchSpeechNewSettingActivity.this, Global.LANGUAGE_SETTING_PREFIX, "en");
                PreferenceUtils.set(SwitchSpeechNewSettingActivity.this, Global.LANGUAGE_SELECTED, 0);
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
            case R.id.switch_turn_reserve:
                et_reverse.setEnabled(isChecked);
                break;
            case R.id.switch_get_on:
                et_get_on.setEnabled(isChecked);
                break;
            case R.id.switch_get_off:
                et_get_off.setEnabled(isChecked);
                break;

        }
    }

    private void setEnableAllSwitch(boolean check) {

        if (!check) {

            switch_turn_on.setEnabled(false);
            switch_turn_on_respond.setEnabled(false);
            switch_turn_off.setEnabled(false);
            switch_turn_off_respond.setEnabled(false);
            switch_turn_reserve.setEnabled(false);
            switch_get_on.setEnabled(false);
            switch_get_off.setEnabled(false);


            et_turn_on.setEnabled(false);
            et_turn_on_respond.setEnabled(false);
            et_turn_off.setEnabled(false);
            et_turn_off_respond.setEnabled(false);
            et_reverse.setEnabled(false);
            et_get_on.setEnabled(false);
            et_get_off.setEnabled(false);


        } else {
            switch_turn_on.setEnabled(true);
            switch_turn_on_respond.setEnabled(true);
            switch_turn_off.setEnabled(true);
            switch_turn_off_respond.setEnabled(true);
            switch_turn_reserve.setEnabled(true);
            switch_get_on.setEnabled(true);
            switch_get_off.setEnabled(true);


            et_turn_on.setEnabled(switch_turn_on.isChecked());
            et_turn_on_respond.setEnabled(switch_turn_on_respond.isChecked());
            et_turn_off.setEnabled(switch_turn_off.isChecked());
            et_turn_off_respond.setEnabled(switch_turn_off_respond.isChecked());
            et_reverse.setEnabled(switch_turn_reserve.isChecked());
            et_get_on.setEnabled(switch_get_on.isChecked());
            et_get_off.setEnabled(switch_get_off.isChecked());

        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        setAllSpeechSettings();
    }

    private boolean isCheckall, isTurnOn, isTurnOnResponse;
    private boolean isTurnOff, isTurnOffResponse, isReverse;
    private boolean isGetOn, isGetOff, isDimmingPercent;

    private String stTurnOn, stTurnOnResponse, stTurnOff;
    private String stTurnOffResponse, stReverse, stGetOn, stGetOff;
    private String stDimmingPrefix;

    private void setAllSpeechSettings(){
        stTurnOn = et_turn_on.getText().toString();
        if (stTurnOn.isEmpty()) stTurnOn = Global.EMPTY;
        stTurnOnResponse = et_turn_on_respond.getText().toString();
        if (stTurnOnResponse.isEmpty()) stTurnOnResponse = Global.EMPTY;
        stTurnOff = et_turn_off.getText().toString();
        if (stTurnOff.isEmpty()) stTurnOff = Global.EMPTY;
        stTurnOffResponse = et_turn_off_respond.getText().toString();
        if (stTurnOffResponse.isEmpty()) stTurnOffResponse = Global.EMPTY;
        stReverse = et_reverse.getText().toString();
        if (stReverse.isEmpty()) stReverse = Global.EMPTY;
        stGetOn = et_get_on.getText().toString();
        if (stGetOn.isEmpty()) stGetOn = Global.EMPTY;
        stGetOff = et_get_off.getText().toString();
        if (stGetOff.isEmpty()) stGetOff = Global.EMPTY;


        JSONObject jsonObj = new JSONObject();
        try {
            jsonObj.put(Global.SPEECH_SETTING_ALL, check_all.isChecked());
            jsonObj.put(Global.SPEECH_SETTING_TURN_ON_CHECK, switch_turn_on.isChecked());
            jsonObj.put(Global.SPEECH_SETTING_TURN_ON_RESPOND_CHECK, switch_turn_on_respond.isChecked());
            jsonObj.put(Global.SPEECH_SETTING_TURN_OFF_CHECK, switch_turn_off.isChecked());
            jsonObj.put(Global.SPEECH_SETTING_TURN_OFF_RESPOND_CHECK, switch_turn_off_respond.isChecked());
            jsonObj.put(Global.SPEECH_SETTING_TURN_RESERVE_CHECK, switch_turn_reserve.isChecked());
            jsonObj.put(Global.SPEECH_SETTING_GET_ON_CHECK, switch_get_on.isChecked());
            jsonObj.put(Global.SPEECH_SETTING_GET_OFF_CHECK, switch_get_off.isChecked());


            jsonObj.put(Global.SPEECH_SETTING_TURN_ON, stTurnOn);
            jsonObj.put(Global.SPEECH_SETTING_TURN_ON_RESPOND, stTurnOnResponse);
            jsonObj.put(Global.SPEECH_SETTING_TURN_OFF, stTurnOff);
            jsonObj.put(Global.SPEECH_SETTING_TURN_OFF_RESPOND, stTurnOffResponse);
            jsonObj.put(Global.SPEECH_SETTING_TURN_RESERVE, stReverse);
            jsonObj.put(Global.SPEECH_SETTING_GET_ON, stGetOn);
            jsonObj.put(Global.SPEECH_SETTING_GET_OFF, stGetOff);
            jsonObj.put(Global.SPEECH_SETTING_DIMMING_TEXT_PREFIX, stDimmingPrefix);
        } catch (JSONException e) {

        }
        PreferenceUtils.set(this, mDevId + stDefaultLanguage, jsonObj.toString());
    }

    private void init_data(){
        isCheckall = isTurnOn = isTurnOnResponse = isTurnOff = isTurnOffResponse = isReverse = isGetOn = isGetOff = isDimmingPercent = false;
        stTurnOn = stTurnOnResponse = stTurnOff = stTurnOffResponse = stReverse = stGetOn = stGetOff = stDimmingPrefix = Global.EMPTY;

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
            if (objSettings.has(Global.SPEECH_SETTING_TURN_RESERVE_CHECK))
                isReverse = objSettings.getBoolean(Global.SPEECH_SETTING_TURN_RESERVE_CHECK);
            if (objSettings.has(Global.SPEECH_SETTING_GET_ON_CHECK))
                isGetOn = objSettings.getBoolean(Global.SPEECH_SETTING_GET_ON_CHECK);
            if (objSettings.has(Global.SPEECH_SETTING_GET_OFF_CHECK))
                isGetOff = objSettings.getBoolean(Global.SPEECH_SETTING_GET_OFF_CHECK);
            if (objSettings.has(Global.SPEECH_SETTING_DIMMING_PERCENT_CHECK))
                isDimmingPercent = objSettings.getBoolean(Global.SPEECH_SETTING_DIMMING_PERCENT_CHECK);

            if (objSettings.has(Global.SPEECH_SETTING_TURN_ON))
                stTurnOn = objSettings.getString(Global.SPEECH_SETTING_TURN_ON);
            if (objSettings.has(Global.SPEECH_SETTING_TURN_ON_RESPOND))
                stTurnOnResponse = objSettings.getString(Global.SPEECH_SETTING_TURN_ON_RESPOND);
            if (objSettings.has(Global.SPEECH_SETTING_TURN_OFF))
                stTurnOff = objSettings.getString(Global.SPEECH_SETTING_TURN_OFF);
            if (objSettings.has(Global.SPEECH_SETTING_TURN_OFF_RESPOND))
                stTurnOffResponse = objSettings.getString(Global.SPEECH_SETTING_TURN_OFF_RESPOND);
            if (objSettings.has(Global.SPEECH_SETTING_TURN_RESERVE))
                stReverse = objSettings.getString(Global.SPEECH_SETTING_TURN_RESERVE);
            if (objSettings.has(Global.SPEECH_SETTING_GET_ON))
                stGetOn = objSettings.getString(Global.SPEECH_SETTING_GET_ON);
            if (objSettings.has(Global.SPEECH_SETTING_GET_OFF))
                stGetOff = objSettings.getString(Global.SPEECH_SETTING_GET_OFF);
            if (objSettings.has(Global.SPEECH_SETTING_DIMMING_TEXT_PREFIX))
                stDimmingPrefix = objSettings.getString(Global.SPEECH_SETTING_DIMMING_TEXT_PREFIX);

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
        switch_turn_reserve.setChecked(isReverse);
        switch_get_on.setChecked(isGetOn);
        switch_get_off.setChecked(isGetOff);


        if (!stTurnOn.equals(Global.EMPTY))             et_turn_on.setText(stTurnOn);
        else                                            et_turn_on.getText().clear();
        if (!stTurnOnResponse.equals(Global.EMPTY))     et_turn_on_respond.setText(stTurnOnResponse);
        else                                            et_turn_on_respond.getText().clear();
        if (!stTurnOff.equals(Global.EMPTY))            et_turn_off.setText(stTurnOff);
        else                                            et_turn_off.getText().clear();
        if (!stTurnOffResponse.equals(Global.EMPTY))    et_turn_off_respond.setText(stTurnOffResponse);
        else                                            et_turn_off_respond.getText().clear();
        if (!stReverse.equals(Global.EMPTY))            et_reverse.setText(stReverse);
        else                                            et_reverse.getText().clear();
        if (!stGetOn.equals(Global.EMPTY))              et_get_on.setText(stGetOn);
        else                                            et_get_on.getText().clear();
        if (!stGetOff.equals(Global.EMPTY))             et_get_off.setText(stGetOff);
        else                                            et_get_off.getText().clear();


        setEnableAllSwitch(isCheckall);
    }
}
