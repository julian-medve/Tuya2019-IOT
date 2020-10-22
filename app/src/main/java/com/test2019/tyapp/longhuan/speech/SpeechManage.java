package com.test2019.tyapp.longhuan.speech;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.speech.tts.Voice;
import android.util.Log;

import com.test2019.tyapp.longhuan.MainApplication;
import com.test2019.tyapp.longhuan.utils.ToastUtil;
import com.test2019.tyapp.longhuan.view.ISpeechView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class SpeechManage implements RecognitionListener {

    private final String TAG = "SpeechManage";

    private Context mContext;
    private SpeechRecognizer speechRecognizer = null;
    private ISpeechView mView;
    private Intent recIntent = null;
    private TextToSpeech textToSpeech = null;

    public SpeechManage(Context context, ISpeechView view)
    {
        this.mContext = context;
        this.mView = view;
        initRecognizer();

        //==== My test for suppoted language ==//
        Intent intent = new Intent(RecognizerIntent.ACTION_GET_LANGUAGE_DETAILS);
        intent.setPackage("com.google.android.googlequicksearchbox");
        PackageManager packageManager = mContext.getPackageManager();
        for (PackageInfo packageInfo: packageManager.getInstalledPackages(0)){
            if (packageInfo.packageName.contains("com.google.android.googlequicksearchbox"))
                Log.e("PackageInfo-Google", packageInfo.packageName + ", " + packageInfo.versionName);
        }

        mContext.sendOrderedBroadcast(intent, null, new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
//                Bundle bundle = getResultExtras(true);
//                final Bundle bundle = getResultExtras(true);
                Bundle bundle = intent.getExtras();
                if (bundle != null) {
                    if (bundle.containsKey(RecognizerIntent.EXTRA_SUPPORTED_LANGUAGES)){
                        ArrayList<String> arr_Locales = bundle.getStringArrayList(
                                RecognizerIntent.EXTRA_SUPPORTED_LANGUAGES
                        );
                        ArrayList<String> arr = arr_Locales;
                    }
                }
            }
        }, null, 1234, null, null);

        //============= End Test ======================================//

        textToSpeech = new TextToSpeech(mContext, (event) -> {

            if (event == TextToSpeech.SUCCESS) {
                //== init textToSpeech ====//
                init_TextToSpeech_Voice();
            } else {
                ToastUtil.showToast(mContext, "Failed to init TextToSpeech Setting");
            }
        });
    }

    public void setTextToSpeech(TextToSpeech speech)
    {
        this.textToSpeech = speech;
    }

    private void initRecognizer()
    {
        if (SpeechRecognizer.isRecognitionAvailable(mContext)) {

//            speechRecognizer = SpeechRecognizer.createSpeechRecognizer(mContext, ComponentName.unflattenFromString("com.google.android.googlequicksearchbox/com.google.android.voicesearch.serviceapi.GoogleRecognitionService"));
            speechRecognizer = SpeechRecognizer.createSpeechRecognizer(mContext);
            speechRecognizer.setRecognitionListener(this);
            recIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
//            recIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
            recIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, MainApplication.getDefaultEncoding());  // "en-US" or "pl-PL"
            recIntent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, mContext.getPackageName());
            recIntent.putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true);
//            recIntent.putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_POSSIBLY_COMPLETE_SILENCE_LENGTH_MILLIS, 5_000);
//            recIntent.putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_COMPLETE_SILENCE_LENGTH_MILLIS, 5_000);
//            recIntent.putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_MINIMUM_LENGTH_MILLIS, 2_000);
        } else {
            Log.d(TAG, "initRecognizer: Not Available");
        }
    }

    public void startSpeechRecognizer()
    {
        if (speechRecognizer != null){
            speechRecognizer.startListening(recIntent);
            String ss = "ss";
        }

    }

    public void stopSpeechRecognizer() {
        if (speechRecognizer != null) {
            speechRecognizer.stopListening();
            String ss = "ss";
        }

    }

    public void destroy()
    {
        if (textToSpeech != null) {
            textToSpeech.shutdown();
            textToSpeech = null;
        }

        if (speechRecognizer != null) {
            speechRecognizer.cancel();
            speechRecognizer.destroy();
        }
        //== modified ===//
//        speechRecognizer = null;
    }

    public void playSpeech(String text) {
        if (textToSpeech != null){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                textToSpeech.speak(text,TextToSpeech.QUEUE_FLUSH, null,"speak");
            } else {
                textToSpeech.speak(text,TextToSpeech.QUEUE_FLUSH, null);
            }
        }
    }

    @Override
    public void onReadyForSpeech(Bundle params) {

    }

    @Override
    public void onBeginningOfSpeech() {

    }

    @Override
    public void onRmsChanged(float rmsdB) {

    }

    @Override
    public void onBufferReceived(byte[] buffer) {

    }

    @Override
    public void onEndOfSpeech() {

    }

    @Override
    public void onError(int error) {
        switch (error) {
            case SpeechRecognizer.ERROR_AUDIO:
                ToastUtil.showToast(mContext, "Error Audio");
                break;

            case SpeechRecognizer.ERROR_CLIENT:
                ToastUtil.showToast(mContext, "Error Client");
                break;

            case SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS:
                ToastUtil.showToast(mContext, "Error Insufficient permission");
                break;

            case SpeechRecognizer.ERROR_NETWORK:
                ToastUtil.showToast(mContext, "Error Network");
                break;

            case SpeechRecognizer.ERROR_NETWORK_TIMEOUT:
                ToastUtil.showToast(mContext, "Error Network Timeout");
                break;
            case SpeechRecognizer.ERROR_NO_MATCH:
                ToastUtil.showToast(mContext, "Error No Match");
                break;

            case SpeechRecognizer.ERROR_RECOGNIZER_BUSY:
                ToastUtil.showToast(mContext, "Error recognizer busy");
                break;

            case SpeechRecognizer.ERROR_SERVER:
                ToastUtil.showToast(mContext, "Error Server");
                break;

            case SpeechRecognizer.ERROR_SPEECH_TIMEOUT:
                ToastUtil.showToast(mContext, "Error Speech Timeout");
                break;

            default:
                ToastUtil.showToast(mContext, "Error Unknown");
                break;
        }
    }

    @Override
    public void onResults(Bundle results) {
        List list = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
        if (list == null)
            return;
        if (!list.isEmpty()) {
            String text = (String) list.get(0);
            if (text.endsWith("."))  text = text.substring(0, text.length() - 1);
            Log.d(TAG, "onResults: " + text);
            mView.showTest(text);
            mView.showText(text);
//            int command = recognizeCommand(text);
//            if (command > 0) {
//                mView.showText(text);
//            } else {
//                mView.showText(Global.SPEECH_UNKNOWN_COMMAND_PL);
//            }
        }
    }

    @Override
    public void onPartialResults(Bundle partialResults) {

    }

    @Override
    public void onEvent(int eventType, Bundle params) {

    }

    //=========== My test ================//
    private void init_TextToSpeech_Voice() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Locale ss = Locale.forLanguageTag(MainApplication.getDefaultEncoding());
            Set<Locale> languages = textToSpeech.getAvailableLanguages();

            textToSpeech.setLanguage(Locale.forLanguageTag(MainApplication.getDefaultEncoding()));  // "en-US" or "pl-PL"
            Set<Voice> voices = textToSpeech.getVoices();
            if (voices == null){
                return;
            }
            // set male Polish voice as default (if it is available)
            for (Voice tmpVoice : voices) {
                if (tmpVoice.getName().toLowerCase().contains("#male") && tmpVoice.getName().toLowerCase().contains(MainApplication.getDefaultEncodingPrefix())) {
                    //if (tmpVoice.getName().toLowerCase().contains(MainApplication.getDefaultEncodingPrefix())) {
                    textToSpeech.setVoice(tmpVoice);
                    break;
                }
            }
        } else {
            textToSpeech.setLanguage(new Locale(MainApplication.getDefaultEncoding()));
//            Set<Voice> voices = textToSpeech.getVoices()
        }
    }
}
