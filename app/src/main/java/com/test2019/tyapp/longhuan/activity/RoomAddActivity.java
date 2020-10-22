package com.test2019.tyapp.longhuan.activity;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;

import com.test2019.tyapp.longhuan.R;
import com.test2019.tyapp.longhuan.adapter.RoomTypeAdapter;
import com.test2019.tyapp.longhuan.presenter.RoomAddPresenter;
import com.test2019.tyapp.longhuan.utils.ActivityUtils;
import com.test2019.tyapp.longhuan.utils.ToastUtil;
import com.test2019.tyapp.longhuan.view.IRoomAddView;

public class RoomAddActivity extends AppCompatActivity implements IRoomAddView, View.OnClickListener {

    private final String TAG = "RoomAddActivity";
    private final int NONE_SELECTED = -1;
    private GridView gView;
    private RoomAddPresenter mRoomAddPresenter;

    private EditText txtRoomName;

    private int curRoomType = NONE_SELECTED;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room_add);

        initView();
        initPresenter();
    }

    private void initView() {
        gView = findViewById(R.id.grid_room_type);
        gView.setOnItemClickListener(((parent, view, position, id) ->{
            if (curRoomType == position)
                curRoomType = NONE_SELECTED;
            else
                curRoomType = position;
            RoomTypeAdapter adapter = (RoomTypeAdapter) parent.getAdapter();
            adapter.setSelection(curRoomType);
        }));
        txtRoomName = findViewById(R.id.info_room_name);
        Button btnAdd = findViewById(R.id.btn_room_add);
        btnAdd.setOnClickListener(this);
    }

    private void initPresenter() {
        mRoomAddPresenter = new RoomAddPresenter(this, this);
    }

    @Override
    public void onClick(View v) {
        String roomName = txtRoomName.getText().toString();

        if (roomName.isEmpty()) {
            ToastUtil.showToast(this, "Please type your room name");
            return;
        }

        if (curRoomType == NONE_SELECTED)
            ToastUtil.showToast(this, "Please select room type");
        else
            mRoomAddPresenter.addRoom(roomName, curRoomType);

    }

    @Override
    public void LoadRoomType(RoomTypeAdapter adapter) {
        gView.setAdapter(adapter);
    }

    @Override
    public void onBackPressed() {
        ActivityUtils.gotoMainActivity(this);
    }
}
