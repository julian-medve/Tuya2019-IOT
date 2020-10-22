package com.test2019.tyapp.longhuan.activity;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import com.test2019.tyapp.longhuan.MainApplication;
import com.test2019.tyapp.longhuan.R;
import com.test2019.tyapp.longhuan.global.Global;
import com.test2019.tyapp.longhuan.speech.SpeechModel;
import com.test2019.tyapp.longhuan.utils.PreferenceUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class SwitchSpeechSettingActivity extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener {

    private final String TAG = "SwitchSpeechSettingActivity";

    private final int EN_LANGUAGE = 0;
    private final int PL_LANGUAGE = 1;

    ImageButton btnBack;

    Switch switchAll;
    Switch switchTurnOn;
    Switch switchTurnOnRespond;
    Switch switchTurnOff;
    Switch switchTurnOffRespond;
    Switch switchTurnReserve;
    Switch switchGetOn;
    Switch switchGetOff;

    TextView txtTurnOn;
    TextView txtTurnOnRespond;
    TextView txtTurnOff;
    TextView txtTurnOffRespond;
    TextView txtTurnReserve;
    TextView txtGetOn;
    TextView txtGetOff;
    Spinner spinner;

    SpeechModel speechModel;
    int nCurLanguage = EN_LANGUAGE;

    boolean isAllCheck = false;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_speech_setting);
        speechModel = new SpeechModel();
        initView();
        getAllSpeechSettings();
    }

    private void initView() {
        btnBack = findViewById(R.id.btn_back);
        btnBack.setOnClickListener((v)->onBackPressed());

        switchAll = findViewById(R.id.check_all);
        switchTurnOn = findViewById(R.id.switch_turn_on);
        switchTurnOnRespond = findViewById(R.id.switch_turn_on_respond);
        switchTurnOff = findViewById(R.id.switch_turn_off);
        switchTurnOffRespond = findViewById(R.id.switch_turn_off_respond);
        switchTurnReserve = findViewById(R.id.switch_turn_reserve);
        switchGetOn = findViewById(R.id.switch_get_on);
        switchGetOff = findViewById(R.id.switch_get_off);

        txtTurnOn = findViewById(R.id.turn_on_command);
        txtTurnOnRespond = findViewById(R.id.turn_on_respond);
        txtTurnOff = findViewById(R.id.turn_off_command);
        txtTurnOffRespond = findViewById(R.id.turn_off_respond);
        txtTurnReserve = findViewById(R.id.turn_reserve_command);
        txtGetOn = findViewById(R.id.get_on_command);
        txtGetOff = findViewById(R.id.get_off_command);

        txtTurnOn.setEnabled(false);
        txtTurnOnRespond.setEnabled(false);
        txtTurnOff.setEnabled(false);
        txtTurnOffRespond.setEnabled(false);
        txtTurnReserve.setEnabled(false);
        txtGetOn.setEnabled(false);
        txtGetOff.setEnabled(false);

        switchAll.setOnCheckedChangeListener(this);
        switchTurnOn.setOnCheckedChangeListener(this);
        switchTurnOnRespond.setOnCheckedChangeListener(this);
        switchTurnOff.setOnCheckedChangeListener(this);
        switchTurnOffRespond.setOnCheckedChangeListener(this);
        switchTurnReserve.setOnCheckedChangeListener(this);
        switchGetOn.setOnCheckedChangeListener(this);
        switchGetOff.setOnCheckedChangeListener(this);

        spinner = findViewById(R.id.spinner_select_lang);

        List<String> languages = new ArrayList<>();
        languages.add("English");
        languages.add("Polish");
        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(this, R.layout.item_spinner, languages);

        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        int selected_lang = PreferenceUtils.getInt(SwitchSpeechSettingActivity.this, Global.LANGUAGE_SELECTED);
        spinner.setAdapter(dataAdapter);
        spinner.setSelection(selected_lang);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (nCurLanguage != position)
                    setAllSpeechSettings();

                if (position == 0) {
                    PreferenceUtils.set(SwitchSpeechSettingActivity.this, Global.LANGUAGE_SETTING, "en-US");
                    PreferenceUtils.set(SwitchSpeechSettingActivity.this, Global.LANGUAGE_SETTING_PREFIX, "en");
                } else if (position == 1){
                    PreferenceUtils.set(SwitchSpeechSettingActivity.this, Global.LANGUAGE_SETTING, "pl-PL");
                    PreferenceUtils.set(SwitchSpeechSettingActivity.this, Global.LANGUAGE_SETTING_PREFIX, "pl");
                }

                getAllSpeechSettings();
                PreferenceUtils.set(SwitchSpeechSettingActivity.this, Global.LANGUAGE_SELECTED, position);
                nCurLanguage = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                PreferenceUtils.set(SwitchSpeechSettingActivity.this, Global.LANGUAGE_SETTING, "en-US");
                PreferenceUtils.set(SwitchSpeechSettingActivity.this, Global.LANGUAGE_SETTING_PREFIX, "en");
                getAllSpeechSettings();
                nCurLanguage = EN_LANGUAGE;
            }
        });

        setEnableAllSwitch(false);
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        int id = buttonView.getId();

        switch (id) {
            case R.id.check_all:
                isAllCheck = isChecked;
                setEnableAllSwitch(isAllCheck);
                break;
            case R.id.switch_turn_on:
                txtTurnOn.setEnabled(isChecked);
                break;
            case R.id.switch_turn_on_respond:
                txtTurnOnRespond.setEnabled(isChecked);
                break;
            case R.id.switch_turn_off:
                txtTurnOff.setEnabled(isChecked);
                break;
            case R.id.switch_turn_off_respond:
                txtTurnOffRespond.setEnabled(isChecked);
                break;
            case R.id.switch_turn_reserve:
                txtTurnReserve.setEnabled(isChecked);
                break;
            case R.id.switch_get_on:
                txtGetOn.setEnabled(isChecked);
                break;
            case R.id.switch_get_off:
                txtGetOff.setEnabled(isChecked);
                break;
        }
    }

    private void setEnableAllSwitch(boolean check) {
        if (!check) {
            switchTurnOn.setEnabled(false);
            switchTurnOnRespond.setEnabled(false);
            switchTurnOff.setEnabled(false);
            switchTurnOffRespond.setEnabled(false);
            switchTurnReserve.setEnabled(false);
            switchGetOn.setEnabled(false);
            switchGetOff.setEnabled(false);

            txtTurnOn.setEnabled(false);
            txtTurnOnRespond.setEnabled(false);
            txtTurnOff.setEnabled(false);
            txtTurnOffRespond.setEnabled(false);
            txtTurnReserve.setEnabled(false);
            txtGetOn.setEnabled(false);
            txtGetOff.setEnabled(false);
        } else {
            switchTurnOn.setEnabled(true);
            switchTurnOnRespond.setEnabled(true);
            switchTurnOff.setEnabled(true);
            switchTurnOffRespond.setEnabled(true);
            switchTurnReserve.setEnabled(true);
            switchGetOn.setEnabled(true);
            switchGetOff.setEnabled(true);

            if (switchTurnOn.isChecked())
                txtTurnOn.setEnabled(true);
            if (switchTurnOnRespond.isChecked())
                txtTurnOnRespond.setEnabled(true);
            if (switchTurnOff.isChecked())
                txtTurnOff.setEnabled(true);
            if (switchTurnOffRespond.isChecked())
                txtTurnOffRespond.setEnabled(true);
            if (switchTurnReserve.isChecked())
                txtTurnReserve.setEnabled(true);
            if (switchGetOn.isChecked())
                txtGetOn.setEnabled(true);
            if (switchGetOff.isChecked())
                txtGetOff.setEnabled(true);
        }
    }

    private void setAllSpeechSettings() {
        speechModel.isCheckAll = switchAll.isChecked();
        speechModel.mTurnOn = txtTurnOn.getText().toString();
        speechModel.isTurnOn = switchTurnOn.isChecked();
        speechModel.mTurnOnReply = txtTurnOnRespond.getText().toString();
        speechModel.isTurnOnReply = switchTurnOnRespond.isChecked();
        speechModel.mTurnOff = txtTurnOff.getText().toString();
        speechModel.isTurnOff = switchTurnOff.isChecked();
        speechModel.mTurnOffReply = txtTurnOffRespond.getText().toString();
        speechModel.isTurnOffReply = switchTurnOffRespond.isChecked();
        speechModel.mReversal = txtTurnReserve.getText().toString();
        speechModel.isReversal = switchTurnReserve.isChecked();
        speechModel.mStatusOnReply = txtGetOn.getText().toString();
        speechModel.isGetOn = switchGetOn.isChecked();
        speechModel.mStatusOffReply = txtGetOff.getText().toString();
        speechModel.isGetOff = switchGetOff.isChecked();
        String savable = speechModel.generateJson();
        PreferenceUtils.set(this, MainApplication.getDefaultEncoding(), savable);
    }

    private void getAllSpeechSettings() {

        String languages = MainApplication.getDefaultEncoding();
        String settings = PreferenceUtils.getString(this, languages);

        if (settings == null)
            return;
        JSONObject result;

        try {
            result = new JSONObject(settings);

            speechModel.isCheckAll = result.getBoolean(Global.SPEECH_SETTING_ALL);
            speechModel.mTurnOn = result.getString(Global.SPEECH_SETTING_TURN_ON);
            speechModel.isTurnOn = result.getBoolean(Global.SPEECH_SETTING_TURN_ON_CHECK);
            speechModel.mTurnOnReply = result.getString(Global.SPEECH_SETTING_TURN_ON_RESPOND);
            speechModel.isTurnOnReply = result.getBoolean(Global.SPEECH_SETTING_TURN_ON_RESPOND_CHECK);
            speechModel.mTurnOff = result.getString(Global.SPEECH_SETTING_TURN_OFF);
            speechModel.isTurnOff = result.getBoolean(Global.SPEECH_SETTING_TURN_OFF_CHECK);
            speechModel.mTurnOffReply = result.getString(Global.SPEECH_SETTING_TURN_OFF_RESPOND);
            speechModel.isTurnOffReply = result.getBoolean(Global.SPEECH_SETTING_TURN_OFF_RESPOND_CHECK);
            speechModel.mReversal = result.getString(Global.SPEECH_SETTING_TURN_RESERVE);
            speechModel.isReversal = result.getBoolean(Global.SPEECH_SETTING_TURN_RESERVE_CHECK);
            speechModel.mStatusOnReply = result.getString(Global.SPEECH_SETTING_GET_ON);
            speechModel.isGetOn = result.getBoolean(Global.SPEECH_SETTING_GET_ON_CHECK);
            speechModel.mStatusOffReply = result.getString(Global.SPEECH_SETTING_GET_OFF);
            speechModel.isGetOff = result.getBoolean(Global.SPEECH_SETTING_GET_OFF_CHECK);
        } catch (JSONException e) {

        }


        if (speechModel != null) {
            switchAll.setChecked(speechModel.isCheckAll);
            switchTurnOn.setChecked(speechModel.isTurnOn);
            switchTurnOnRespond.setChecked(speechModel.isTurnOnReply);
            switchTurnOff.setChecked(speechModel.isTurnOff);
            switchTurnOffRespond.setChecked(speechModel.isTurnOffReply);
            switchTurnReserve.setChecked(speechModel.isReversal);
            switchGetOn.setChecked(speechModel.isGetOn);
            switchGetOff.setChecked(speechModel.isGetOff);

            txtTurnOn.setText(speechModel.mTurnOn);
            txtTurnOnRespond.setText(speechModel.mTurnOnReply);
            txtTurnOff.setText(speechModel.mTurnOff);
            txtTurnOffRespond.setText(speechModel.mTurnOffReply);
            txtTurnReserve.setText(speechModel.mReversal);
            txtGetOn.setText(speechModel.mStatusOnReply);
            txtGetOff.setText(speechModel.mStatusOffReply);
        }
    }

    @Override
    public void onBackPressed() {
        setAllSpeechSettings();
        super.onBackPressed();
    }
}
