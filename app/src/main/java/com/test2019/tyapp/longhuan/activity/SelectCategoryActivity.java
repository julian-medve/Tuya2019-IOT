package com.test2019.tyapp.longhuan.activity;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.test2019.tyapp.longhuan.R;
import com.test2019.tyapp.longhuan.activity.base.BaseActivity;
import com.test2019.tyapp.longhuan.adapter.Adapter_Category;
import com.test2019.tyapp.longhuan.global.Global;
import com.test2019.tyapp.longhuan.lisener.Clicked_Position_Listener;
import com.test2019.tyapp.longhuan.model.DeviceTypeModel;
import com.test2019.tyapp.longhuan.utils.ActivityUtils;
import com.test2019.tyapp.longhuan.global.IconPaths;

import java.util.ArrayList;

public class SelectCategoryActivity extends BaseActivity implements View.OnClickListener, Clicked_Position_Listener {

    private RecyclerView rel_dev_category;
    private Button btn_next;
    private int nCurrent_pos = -1;
    private String mCurDevId = null;

    private ArrayList<DeviceTypeModel> mCategoryModels = new ArrayList<>();

    private String [] mCategoryNames = IconPaths.categoryNames;

    private int [] mCategoryIcons = IconPaths.categoryIcons;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_category);

        if (getIntent() != null)
            mCurDevId = getIntent().getStringExtra(Global.CURRENT_DEV);

        init_view();
    }

    private void init_view() {
        rel_dev_category = findViewById(R.id.rel_dev_category);
        rel_dev_category.setLayoutManager(new GridLayoutManager(this, 3));

        btn_next = findViewById(R.id.btn_next);
        btn_next.setOnClickListener(this);

        init_recyclerView();
    }

    private void init_recyclerView() {
        if (mCategoryModels.size() > 0) mCategoryModels.clear();

        for (int i = 0; i < mCategoryIcons.length; i++) {
            DeviceTypeModel model;
            model = new DeviceTypeModel( mCategoryNames[i], mCategoryIcons[i], DeviceTypeModel.DEVICE_WIFI, true);
            mCategoryModels.add(model);
        }

        Adapter_Category adapter_category = new Adapter_Category(mCategoryModels, this);
        rel_dev_category.setAdapter(adapter_category);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_next:
                goToNext();
                break;
        }
    }

    @Override
    public void onBackPressed() {
        Toast.makeText(this,
                "Please go next!", Toast.LENGTH_LONG).show();
    }

    //===== Clicked_Position_Listener =================//
    @Override
    public void get_clicked_position(int nPosition) {
        this.nCurrent_pos = nPosition;
//        Toast.makeText(this, nCurrent_pos + "", Toast.LENGTH_SHORT).show();
    }

    private void goToNext(){
        if (!isValidate()) return;

        Intent intent = new Intent(this, SelectDeviceIconActivity.class);
        intent.putExtra(Global.FROM_ACTIVITY, "CategoryActivity");
        intent.putExtra(Global.CURRENT_DEV, mCurDevId);
        intent.putExtra(Global.CURRENT_DEV_CATEGORY, nCurrent_pos);
        ActivityUtils.startActivity(this, intent, ActivityUtils.ANIMATE_FORWARD, false);
    }

    private boolean isValidate() {
        if (nCurrent_pos == -1){
            Toast.makeText(this,
                    "Please select device type!", Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }
}
