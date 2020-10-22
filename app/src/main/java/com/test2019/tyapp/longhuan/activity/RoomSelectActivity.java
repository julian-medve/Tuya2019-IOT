package com.test2019.tyapp.longhuan.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;

import com.test2019.tyapp.longhuan.R;
import com.test2019.tyapp.longhuan.activity.base.BaseActivity;
import com.test2019.tyapp.longhuan.adapter.RoomSelectAdapter;
import com.test2019.tyapp.longhuan.global.Global;
import com.test2019.tyapp.longhuan.presenter.RoomSelectPresenter;
import com.test2019.tyapp.longhuan.utils.ToastUtil;
import com.test2019.tyapp.longhuan.view.IRoomSelectView;

public class RoomSelectActivity extends BaseActivity implements IRoomSelectView {

    private final int NONE_SELECTED = -1;
    private int curRoomType = NONE_SELECTED;

    private GridView gView;
    private RoomSelectPresenter mPresenter;

    private String mCurDevId = null;
//    private String mCurCategoryName = null;
//    private String mCurDevImgPath = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room_select);

        if (getIntent() != null){
            mCurDevId = getIntent().getStringExtra(Global.CURRENT_DEV);
//            mCurCategoryName = getIntent().getStringExtra(Global.CURRENT_DEV_CATEGORY);
//            mCurDevImgPath = getIntent().getStringExtra(Global.CURRENT_DEV_IMG_PATH);
        }

        initView();
        initPresenter();
    }

    private void initView() {
        gView = findViewById(R.id.grid_room_select);
        gView.setOnItemClickListener(((parent, view, position, id) -> {
            if (curRoomType == position)
                curRoomType = NONE_SELECTED;
            else
                curRoomType = position;
            RoomSelectAdapter adapter = (RoomSelectAdapter) parent.getAdapter();
            adapter.selectRoom(curRoomType);
        }));

        ImageView imgBack = findViewById(R.id.img_back);
        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        Button btnNext = findViewById(R.id.btn_room_select);
        btnNext.setOnClickListener((v -> {
            if (curRoomType == NONE_SELECTED) {
                ToastUtil.showToast(RoomSelectActivity.this, "You must select room");
                return;
            }
            mPresenter.addDevice(curRoomType, mCurDevId);
//            mPresenter.addDevice(curRoomType, mCurDevId, mCurCategoryName, mCurDevImgPath);
        }));
    }

    private void initPresenter() {
        mPresenter = new RoomSelectPresenter(this, this);
    }

    @Override
    public void LoadRoom(RoomSelectAdapter adapter) {
        gView.setAdapter(adapter);
    }


}
