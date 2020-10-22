package com.test2019.tyapp.longhuan.view;

import com.test2019.tyapp.longhuan.model.RoomModel;
import com.tuya.smart.sdk.bean.DeviceBean;

import java.util.ArrayList;

public interface IMainView {
    void showMainMenu(boolean room, boolean main);
    void showRoomMenu(boolean room, boolean main, boolean isCurrentPosition);
    void setHomeBackground(boolean main);
    void loadRoom(ArrayList<RoomModel> datas);
//    void loadHome(ArrayList<MainMenuModel> datas);
    void loadDevice(ArrayList<DeviceBean> datas);

    void loadProgressBar();
    void stopProgressBar();
    void SignOut();

}
