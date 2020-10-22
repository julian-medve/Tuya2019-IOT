package com.test2019.tyapp.longhuan.presenter;

import android.app.Activity;

import com.test2019.tyapp.longhuan.R;
import com.test2019.tyapp.longhuan.adapter.RoomSelectAdapter;
import com.test2019.tyapp.longhuan.global.Global;
import com.test2019.tyapp.longhuan.utils.ActivityUtils;
import com.test2019.tyapp.longhuan.utils.PreferenceUtils;
import com.test2019.tyapp.longhuan.utils.ProgressUtil;
import com.test2019.tyapp.longhuan.utils.ToastUtil;
import com.test2019.tyapp.longhuan.view.IRoomSelectView;
import com.tuya.smart.android.base.utils.PreferencesUtil;
import com.tuya.smart.home.sdk.TuyaHomeSdk;
import com.tuya.smart.home.sdk.bean.HomeBean;
import com.tuya.smart.home.sdk.bean.RoomBean;
import com.tuya.smart.sdk.api.IResultCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class RoomSelectPresenter {

    private Activity mContext;
    private IRoomSelectView mView;

    private List<RoomBean> roomBeans;
    public RoomSelectPresenter(Activity context, IRoomSelectView view) {
        this.mContext = context;
        this.mView = view;

        initRoom();
    }

    private void initRoom() {
        long homeId = PreferencesUtil.getLong(Global.CURRENT_HOME);
        HomeBean home = TuyaHomeSdk.newHomeInstance(homeId).getHomeBean();
        roomBeans = home.getRooms();
        LoadRoom(roomBeans);
    }

    private void LoadRoom(List<RoomBean> list) {
        RoomSelectAdapter adapter = new RoomSelectAdapter(mContext, list);
        mView.LoadRoom(adapter);
    }

    public void addDevice(int position, String devId) {
        ProgressUtil.showLoading(mContext, mContext.getResources().getString(R.string.loading));
        RoomBean roomBean = roomBeans.get(position);
        TuyaHomeSdk.newRoomInstance(roomBean.getRoomId()).addDevice(devId, new IResultCallback() {
            @Override
            public void onError(String s, String s1) {
                ProgressUtil.hideLoading();
                ToastUtil.showToast(mContext, "Failed to add device to room\nPlease try to add device");
                ActivityUtils.gotoMainActivity(mContext);
            }

            @Override
            public void onSuccess() {
                ProgressUtil.hideLoading();
                ToastUtil.showToast(mContext, "Device is added to the room!");
                ActivityUtils.gotoMainActivity(mContext);

//                if (isSaveData(devId, categoryName, imgPath)){
//                    ActivityUtils.gotoMainActivity(mContext);
//                }
//                else {
//                    //========= Couldn't save data by some error ==========//
//
//                }
            }
        });
    }

    private boolean isSaveData(String devId, String categoryName, String imgPath){
        JSONObject object = new JSONObject();
        try {
            object.put(Global.CURRENT_DEV_CATEGORY, categoryName);
            object.put(Global.CURRENT_DEV_IMG_PATH, imgPath);
        } catch (JSONException e) {
            e.printStackTrace();
            return false;
        }
        PreferenceUtils.set(mContext, devId, object.toString());
        return true;
    }
}
