package com.test2019.tyapp.longhuan.presenter;

import android.app.Activity;

import com.test2019.tyapp.longhuan.R;
import com.test2019.tyapp.longhuan.adapter.RoomTypeAdapter;
import com.test2019.tyapp.longhuan.global.Global;
import com.test2019.tyapp.longhuan.utils.ActivityUtils;
import com.test2019.tyapp.longhuan.utils.PreferenceUtils;
import com.test2019.tyapp.longhuan.utils.ProgressUtil;
import com.test2019.tyapp.longhuan.utils.ToastUtil;
import com.test2019.tyapp.longhuan.view.IRoomAddView;
import com.tuya.smart.android.base.utils.PreferencesUtil;
import com.tuya.smart.home.sdk.TuyaHomeSdk;
import com.tuya.smart.home.sdk.bean.RoomBean;
import com.tuya.smart.home.sdk.callback.ITuyaRoomResultCallback;

public class RoomAddPresenter {

    private Activity mContext;
    private IRoomAddView mView;

    public RoomAddPresenter(Activity context, IRoomAddView view) {
        this.mContext = context;
        this.mView = view;

        initRoomType();
    }

    private void initRoomType(){
        RoomTypeAdapter adapter = new RoomTypeAdapter(mContext);
        mView.LoadRoomType(adapter);
    }

    public void addRoom(String name, final int roomType) {
        ProgressUtil.showLoading(mContext, mContext.getResources().getString(R.string.loading));
        long homeId = PreferencesUtil.getLong(Global.CURRENT_HOME);
        TuyaHomeSdk.newHomeInstance(homeId).addRoom(name, new ITuyaRoomResultCallback() {
            @Override
            public void onSuccess(RoomBean roomBean) {
                ProgressUtil.hideLoading();
                PreferenceUtils.set(mContext, Long.toString(roomBean.getRoomId()), roomType);
                ActivityUtils.gotoMainActivity(mContext);
            }

            @Override
            public void onError(String s, String s1) {
                ProgressUtil.hideLoading();
                ToastUtil.showToast(mContext, "Failed to add room");
            }
        });
    }
}
