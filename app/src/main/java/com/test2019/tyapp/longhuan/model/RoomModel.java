package com.test2019.tyapp.longhuan.model;

import com.tuya.smart.home.sdk.bean.RoomBean;

public class RoomModel {
    private RoomBean mRoom;
    private int mRoomType;

    public RoomModel(RoomBean bean, int type) {
        this.mRoom = bean;
        this.mRoomType = type;
    }

    public RoomBean getRoomBean(){
        return mRoom;
    }

    public void setRoomBean(RoomBean bean) {
        mRoom = bean;
    }

    public int getRoomType(){
        return mRoomType;
    }

    public void setRoomType(int type){
        mRoomType = type;
    }

}
