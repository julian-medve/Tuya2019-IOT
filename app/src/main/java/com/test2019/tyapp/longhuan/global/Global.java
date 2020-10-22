package com.test2019.tyapp.longhuan.global;

public class Global {

    public static final String NOTIFICATION_CHANNEL = "tuya_app_notification_channel";

    public static final String AUTOSTART_CHANNEL = "customapp_auto_start";
    public static final String CURRENT_HOME = "curhomeId";
    public static final String CURRENT_DEV = "curdevId";
    public static final String CURRENT_SOCKET = "cursocketID";
    public static final String GATEWAY_CHANNEL = "is_gateway";

//    public static final String DEVICE_TYPE_SWITCH = "QJWIe4tienG6B4KA";
//    public static final String DEVICE_TYPE_DOOR = "door";
//    public static final String DEVICE_TYPE_GATEWAY = "aaxie7jyiljmtpxo";
//    public static final String DEVICE_TYPE_PIR = "pir";
//    public static final String DEVICE_TYPE_TEMPERATURE = "temperature";

    public static final String DEVICE_TYPE_SWITCH = "switch";
    public static final String DEVICE_TYPE_DOOR = "door";
    public static final String DEVICE_TYPE_GATEWAY = "aaxie7jyiljmtpxo";
    public static final String DEVICE_TYPE_PIR = "pir";
    public static final String DEVICE_TYPE_TEMPERATURE = "temperature";

    public static String getDeviceCategory(String string){
        String[] array = string.split(" ");
        return array[array.length - 1].trim().toLowerCase();
    }

//    public static final String SPEECH_SWITCH_TURN_ON_EN = "TURN ON";
//    public static final String SPEECH_SWITCH_TURN_OFF_EN = "TURN OFF";
//    public static final String SPEECH_UNKNOWN_COMMAND_EN = "Unknown Command";
//
//    public static final String SPEECH_SWITCH_TURN_ON_CN = "打开";
//    public static final String SPEECH_SWITCH_TURN_OFF_CN = "关掉";
//    public static final String SPEECH_UNKNOWN_COMMAND_CN = "未知的命令";
//
//    public static final String SPEECH_SWITCH_TURN_ON_PL = "włącz światło w pokoju";
//    public static final String SPEECH_SWITCH_TURN_OFF_PL = "wyłącz światło w pokoju";
//    public static final String SPEECH_UNKNOWN_COMMAND_PL = "nieznane polecenie";
//
//    public static final String SPEECH_SETTING = "tuya_app_speech_setting";

    public static final String SPEECH_SETTING_ALL = "CHECK_ALL";
    public static final String SPEECH_SETTING_TURN_ON = "TURN_ON";
    public static final String SPEECH_SETTING_TURN_ON_CHECK = "TURN_ON_CHECK";
    public static final String SPEECH_SETTING_TURN_ON_RESPOND = "TURN_ON_RESPOND";
    public static final String SPEECH_SETTING_TURN_ON_RESPOND_BEFORE = "TURN_ON_RESPOND_BEFORE";
    public static final String SPEECH_SETTING_TURN_ON_RESPOND_CHECK = "TURN_ON_RESPOND_CHECK";
    public static final String SPEECH_SETTING_TURN_ON_RESPOND_BEFORE_CHECK = "TURN_ON_RESPOND_BEFORE_CHECK";
    public static final String SPEECH_SETTING_TURN_OFF = "TURN_OFF";
    public static final String SPEECH_SETTING_TURN_OFF_CHECK = "TURN_OFF_CHECK";
    public static final String SPEECH_SETTING_TURN_OFF_RESPOND = "TURN_OFF_RESPOND";
    public static final String SPEECH_SETTING_TURN_OFF_RESPOND_BEFORE = "TURN_OFF_RESPOND_BEFORE";
    public static final String SPEECH_SETTING_TURN_OFF_RESPOND_CHECK = "TURN_OFF_RESPOND_CHECK";
    public static final String SPEECH_SETTING_TURN_OFF_RESPOND_BEFORE_CHECK = "TURN_OFF_RESPOND_BEFORE_CHECK";
    public static final String SPEECH_SETTING_TURN_RESERVE = "TURN_RESERVE";
    public static final String SPEECH_SETTING_TURN_RESERVE_CHECK = "TURN_RESERVE_CHECK";
    public static final String SPEECH_SETTING_GET_ON = "GET_ON";
    public static final String SPEECH_SETTING_GET_ON_CHECK = "GET_ON_CHECK";
    public static final String SPEECH_SETTING_GET_OFF = "GET_OFF";
    public static final String SPEECH_SETTING_GET_OFF_CHECK = "GET_OFF_CHECK";
    public static final String SPEECH_UNKNOWN_COMMAND = "Unknown Command";
    public static final String SPEECH_SETTING_DIMMING_PERCENT_CHECK = "dimming_percent_check";
    public static final String SPEECH_SETTING_DIMMING_TEXT_PREFIX = "dimming_text_prefix";
    public static final String SPEECH_SETTING_SWITCH_TIMER_CHECK = "switch_timer_check";
    public static final String SPEECH_SETTING_SWITCH_TIMER_PREFIX = "switch_timer_prefix";

    public static final String LANGUAGE_SELECTED = "selected_lang";
    public static final String LANGUAGE_SETTING = "language_setting";
    public static final String LANGUAGE_SETTING_PREFIX = "language_setting_prefix";

    //=========== cmd =================//
    public static final String SWITCH_DPID_CMD = "1";
    public static final String DIMMER_PERCENT_CMD = "2";
    public static final String SMART_SOCKET_TIMER = "2";
    public static final String SWITCH_TIMER = "9";

    public static final String CURTAIN_SWITCH = "101";
    public static final String SMART_INTELIGENTNY_SOCKET_TIMER = "102";


    //======== my variable ========//
    public static final String EMPTY = "empty";
    public static final String CONFIG_PASSWORD = "config_password";
    public static final String CONFIG_SSID = "config_ssid";

    public static final String FROM_ACTIVITY = "from_activity";
    public static final String FROM_CUR_FRAGMENT = "from_cur_fragment";
    public static final String CURRENT_DEV_CATEGORY = "current_dev_category";
    public static final String CURRENT_DEV_IMG_PATH = "current_dev_img_path";
    public static final String CURRENT_DEV_ICON_PATH = "current_dev_icon_path";
    public static final String CURRENT_DEV_NAME = "current_dev_name";
    public static final String CURRENT_DEV_SEARCH_NAME = "current_dev_search_name";
    public static final String CURRENT_CUR_REVERSE_CMD = "current_cur_reverse_cmd";
    public static final String SWITCH_TIMER_ENDTIME = "switch_timer_suffix";

    public static final String CURTAIN_SWITCH_TIMER = "curtain_switch_timer";
    public static final String CURTAIN_TIMER_ENDTIME = "curtain_timer_suffix";
    public static final String CURTAIN_TIMER_STATE = "curtain_timer_state";
    public static final String CURTAIN_TIMER_WORK = "curtain_timer_work";
    public static final String CURTAIN_SAVE_TIME = "curtain_save_time";

    public static final float ALPHA_LIGHT = 1.0f;
    public static final float ALPHA_DARK = 0.3f;

    public static final String ACTION_FINISH = "action_finish";
    public static final String ACTION_ICON_CHANGED = "action_icon_changed";


}
