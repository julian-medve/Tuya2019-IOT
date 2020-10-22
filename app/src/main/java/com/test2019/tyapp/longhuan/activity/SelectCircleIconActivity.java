package com.test2019.tyapp.longhuan.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.test2019.tyapp.longhuan.R;
import com.test2019.tyapp.longhuan.activity.base.BaseActivity;
import com.test2019.tyapp.longhuan.adapter.Adapter_Category;
import com.test2019.tyapp.longhuan.global.Global;
import com.test2019.tyapp.longhuan.global.IconPaths;
import com.test2019.tyapp.longhuan.lisener.Clicked_Position_Listener;
import com.test2019.tyapp.longhuan.model.DeviceTypeModel;
import com.test2019.tyapp.longhuan.utils.PreferenceUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class SelectCircleIconActivity extends BaseActivity implements View.OnClickListener, Clicked_Position_Listener {

    private RecyclerView rel_dev_icon;
    private int nCurrent_pos = -1;
    private String mFromActivity = null;
    private String mCurDevId = null;
    private String mFromCurFragment = null;
    private int nCur_categoryPos;

    private String mCurIconPath = null;

    private ArrayList<DeviceTypeModel> mDevIconModels = new ArrayList<>();
    private String [] mIconName;
    private int [] mIconPath;
    private Button btn_change;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_circle_icon);

        if(getIntent() != null){
            mFromActivity = getIntent().getStringExtra(Global.FROM_ACTIVITY);
            mCurDevId = getIntent().getStringExtra(Global.CURRENT_DEV);
            nCur_categoryPos = getIntent().getIntExtra(Global.CURRENT_DEV_CATEGORY, 0);
            mFromCurFragment = getIntent().getStringExtra(Global.FROM_CUR_FRAGMENT);
        }

        init_data();
        init_view();
    }


    private void init_data(){
        mIconName = IconPaths.devIconNames[nCur_categoryPos];
        mIconPath = IconPaths.devIconPath[nCur_categoryPos];
    }

    private void init_view(){
        btn_change = (Button)findViewById(R.id.btn_change);
        btn_change.setOnClickListener(this);
        findViewById(R.id.img_back).setOnClickListener(this);

        rel_dev_icon = (RecyclerView)findViewById(R.id.rel_dev_icon);
        rel_dev_icon.setLayoutManager(new GridLayoutManager(this, 3));

        init_recyclerView();
    }

    private void init_recyclerView(){

        mCurIconPath = PreferenceUtils.getIconPath(this, mCurDevId);

        if(mDevIconModels.size() > 0) mDevIconModels.clear();

        for(int i = 0; i < mIconPath.length; i ++){
            DeviceTypeModel model;
            model = new DeviceTypeModel(mIconName[i], mIconPath[i], 0, true);
            mDevIconModels.add(model);

            if(String.valueOf(mIconPath[i]).equals(mCurIconPath)){
                nCurrent_pos = i;
            }
        }

        Adapter_Category adapter_icon = new Adapter_Category(mDevIconModels, this);
        adapter_icon.set_selected_index(nCurrent_pos);
        rel_dev_icon.setAdapter(adapter_icon);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.img_back:
                finish();
                break;
            case R.id.btn_change:
                changeIcon();
                break;
        }
    }

    private void changeIcon(){
        if(!isValidate()) return;
        finish();
    }


    private boolean isValidate(){
        if(nCurrent_pos == -1){
            Toast.makeText(this, "Please select device icon!", Toast.LENGTH_LONG).show();
            return false;
        }else {
            saveIconPath();
            return true;
        }
    }

    private void saveIconPath(){
        JSONObject object = new JSONObject();
        try{
           object.put(Global.CURRENT_DEV_ICON_PATH, String.valueOf(mIconPath[nCurrent_pos]));
        }catch (JSONException e){
            e.printStackTrace();
        }

        PreferenceUtils.set(this, mCurDevId + "icon", object.toString());
    }

    @Override
    public void get_clicked_position(int nPosition) {
        nCurrent_pos = nPosition;
    }
}
