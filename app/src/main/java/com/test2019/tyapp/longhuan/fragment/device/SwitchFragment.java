package com.test2019.tyapp.longhuan.fragment.device;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.skyfishjy.library.RippleBackground;
import com.test2019.tyapp.longhuan.R;
import com.test2019.tyapp.longhuan.activity.MainActivity;
import com.test2019.tyapp.longhuan.activity.SelectDeviceIconActivity;
import com.test2019.tyapp.longhuan.activity.SwitchSpeechSettingActivity;
import com.test2019.tyapp.longhuan.global.Global;
import com.test2019.tyapp.longhuan.presenter.device.SwitchPresenter;
import com.test2019.tyapp.longhuan.utils.ActivityUtils;
import com.test2019.tyapp.longhuan.utils.PreferenceUtils;
import com.test2019.tyapp.longhuan.view.device.ISwitchView;
import com.tuya.smart.sdk.bean.DeviceBean;

import static com.test2019.tyapp.longhuan.global.Global.ALPHA_DARK;
import static com.test2019.tyapp.longhuan.global.Global.ALPHA_LIGHT;
import static com.test2019.tyapp.longhuan.utils.PreferenceUtils.set_dev_image;

public class SwitchFragment extends Fragment implements View.OnClickListener, ISwitchView, View.OnTouchListener {

    private final String TAG = "SwitchFragment";

    private volatile static SwitchFragment mSelfFragment;

    private SwitchPresenter switchPresenter;

    private LinearLayout lin_dev_option_wrapper;
    private FrameLayout frameSpeechSetting;
    private RelativeLayout rel_switch;
    private ImageView btnSwitch, img_switch_ring;
    private TextView txtDeviceName;
    private FrameLayout fram_txt_wrap;
    private TextView txtRecognized;
    private RippleBackground vwRipple;
    private ImageView imgRecognizerEffect;

    private TextView txtReal;

    private String mCurDevId = null;
    private DeviceBean mDeviceBean;
    private boolean isRecord = false;
    private boolean mStatus = false;

    boolean bShowOption = false;

//    private BroadcastReceiver mEditIconReceiver = new BroadcastReceiver() {
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(mEditIconReceiver);
//
//            String action = intent.getAction();
//            if (action.equals(Global.ACTION_FINISH)) {
//                hideOptionWrapper();
//            }
//            if (action.equals(Global.ACTION_ICON_CHANGED)){
//                hideOptionWrapper();
//                set_dev_image(context, mDeviceBean, btnSwitch);
//                ((MainActivity)getActivity()).deviceAdapter.notifyDataSetChanged();
//            }
//        }
//    };

//    public void registerEditReceiver(){
//        IntentFilter intentFilter = new IntentFilter();
//        intentFilter.addAction(Global.ACTION_FINISH);
//        intentFilter.addAction(Global.ACTION_ICON_CHANGED);
//        LocalBroadcastManager.getInstance(getContext())
//                .registerReceiver(mEditIconReceiver, intentFilter);
//    }

    private void hideOptionWrapper(){
        bShowOption = false;
        lin_dev_option_wrapper.setVisibility(View.GONE);
    }

    public static Fragment newInstance() {
        if (mSelfFragment != null) {
            mSelfFragment = null;
        }
        synchronized (SwitchFragment.class) {
            if (mSelfFragment == null) {
                mSelfFragment = new SwitchFragment();
            }
        }
        return mSelfFragment;
    }

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_switch, container, false);

        rel_switch = v.findViewById(R.id.rel_switch);
        btnSwitch = v.findViewById(R.id.img_switch);
        img_switch_ring = v.findViewById(R.id.img_switch_ring);
        txtDeviceName = v.findViewById(R.id.txt_device_name);
        fram_txt_wrap = v.findViewById(R.id.fram_txt_wrap);
        txtRecognized = v.findViewById(R.id.txt_recognized);
        vwRipple = v.findViewById(R.id.rippleView);
        imgRecognizerEffect = v.findViewById(R.id.centerImage);

        txtReal = v.findViewById(R.id.txt_real);

        lin_dev_option_wrapper = v.findViewById(R.id.lin_dev_option_wrapper);
        frameSpeechSetting = v.findViewById(R.id.frame_speech_setting);

        rel_switch.setOnClickListener(this);
        imgRecognizerEffect.setOnTouchListener(this);

        //=========== handle device option ============//
        v.findViewById(R.id.frame_option).setOnClickListener(this::onClick);
        v.findViewById(R.id.frame_edit_device).setOnClickListener(this::onClick);
        frameSpeechSetting.setOnClickListener(this);

        return v;
    }

    //==================== ICommonView ==============================//
    @Override
    public void initDeviceInfo(DeviceBean deviceBean){
        mDeviceBean = deviceBean;
        mCurDevId = deviceBean.getDevId();
    }

    @Override
    public void setDevImage(DeviceBean deviceBean) {
        set_dev_image(this.getContext(), deviceBean, btnSwitch);
    }

    //==========================================================//
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.d(TAG, "onActivityCreated: onActivityCreated");
        initPresenter();
    }

    private void initPresenter() {
        switchPresenter = new SwitchPresenter(getActivity(), this, this);
    }

    @Override
    public void onStart() {
        super.onStart();
        hideOptionWrapper();
        switchPresenter.initData();
        switchPresenter.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
        switchPresenter.onStop();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rel_switch:
                switchPresenter.onClick();
                break;
            case R.id.frame_option:
                showOptions();
                break;
            case R.id.frame_speech_setting:
                ActivityUtils.gotoActivity(getActivity(), SwitchSpeechSettingActivity.class, ActivityUtils.ANIMATE_FORWARD, false);
                break;
            case R.id.frame_edit_device:
//                registerEditReceiver();
                int nCategoryNumber = PreferenceUtils.getDevCategoryNumber(getContext(), mCurDevId);
                Intent intent = new Intent(getContext(), SelectDeviceIconActivity.class);
                intent.putExtra(Global.FROM_ACTIVITY, "MainActivity");
                intent.putExtra(Global.CURRENT_DEV, mCurDevId);
                intent.putExtra(Global.CURRENT_DEV_CATEGORY, nCategoryNumber);
                ActivityUtils.startActivity(getActivity(), intent, ActivityUtils.ANIMATE_FORWARD, false);
                break;
        }
    }

    private void showOptions(){
        bShowOption = !bShowOption;
        if (bShowOption) {
            lin_dev_option_wrapper.setVisibility(View.VISIBLE);
        }
        else lin_dev_option_wrapper.setVisibility(View.GONE);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction())
        {
            case MotionEvent.ACTION_DOWN:
                Log.e("Touch Down","Downn");
                        isRecord = true;
                        actionChangeRecord();
                        switchPresenter.SetSpeechAction(true);
                break;
            case MotionEvent.ACTION_UP:
                Log.e("Touch Up","Up");
                        isRecord = false;
                        actionChangeRecord();
                        switchPresenter.SetSpeechAction(false);
                break;
        }
        return true;
    }

    //====== show device is on or off =====//
    private void showSwitchStatus(boolean status) {
        if (status) {                  // device is on
            btnSwitch.setAlpha(ALPHA_LIGHT);
            txtRecognized.setText("Device is On.");
            txtRecognized.setAlpha(ALPHA_LIGHT);
            fram_txt_wrap.setBackgroundResource(R.drawable.round_border_blue);
            fram_txt_wrap.setAlpha(ALPHA_LIGHT);
            img_switch_ring.setImageResource(R.drawable.ring_blue);
            img_switch_ring.setAlpha(ALPHA_LIGHT);
        } else {
            btnSwitch.setAlpha(ALPHA_DARK);
            txtRecognized.setText("Device is Off.");
            txtRecognized.setAlpha(ALPHA_DARK);
            fram_txt_wrap.setBackgroundResource(R.drawable.round_border_white);
            fram_txt_wrap.setAlpha(ALPHA_DARK);
            img_switch_ring.setImageResource(R.drawable.ring_white);
            img_switch_ring.setAlpha(ALPHA_DARK);
        }

        ((MainActivity)getActivity()).deviceAdapter.notifyDataSetChanged();
    }

    //====== ISwitchView Override =====//
    @Override
    public void showOpenView() {
        showSwitchStatus(true);
    }

    @Override
    public void showCloseView() {
        showSwitchStatus(false);
    }

    @Override
    public void showErrorTip() {

    }

    @Override
    public void showRemoveTip() {

    }

    @Override
    public void changeNetworkErrorTip(boolean status) {

    }

    @Override
    public void statusChangedTip(boolean status) {

    }

    @Override
    public void updateTitle(String titleName) {
        txtDeviceName.setText(titleName);
    }

    @Override
    public void setStatus(boolean status) {
        mStatus = status;
    }

    @Override
    public boolean getStatus() {
        return mStatus;
    }

    @Override
    public void onDestroyView() {
        if (switchPresenter != null)
//            switchPresenter.onDestroy();
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public void actionChangeRecord()
    {
        if (isRecord)
        {
            vwRipple.startRippleAnimation();
        }
        else
        {
            vwRipple.stopRippleAnimation();
        }
    }

    @Override
    public void setRecognizedText(String text) {
        txtRecognized.setText(text);
    }

    @Override
    public void setReal(String text) {
        txtReal.setText(text);
    }
}
