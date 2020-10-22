package com.test2019.tyapp.longhuan.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.test2019.tyapp.longhuan.R;
import com.test2019.tyapp.longhuan.global.Global;
import com.test2019.tyapp.longhuan.utils.PreferenceUtils;



public class CurtainSwitchMoveTimerActivity extends AppCompatActivity implements View.OnClickListener {

    private LinearLayout lin_next;
    private ImageView img_back;
    private TextView tv_curtain_timer_number;

    private String mDevId;

    private int i_timer_number = 10;
    private String st_timer_number;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_curtain_switch_move_timer);

        if(getIntent() != null){
            mDevId = getIntent().getStringExtra(Global.CURRENT_DEV);
        }

        initView();


    }

    private void initView(){
        lin_next = (LinearLayout)findViewById(R.id.lin_next);
        img_back = (ImageView)findViewById(R.id.img_back);
        tv_curtain_timer_number = (TextView)findViewById(R.id.tv_curtain_timer_number);

        lin_next.setOnClickListener(this::onClick);
        img_back.setOnClickListener(this::onClick);

        update_UI(i_timer_number);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.lin_next:
                Intent intent = new Intent(getApplicationContext(), CurtainSwitchTimerActivity.class);
                intent.putExtra(Global.CURRENT_DEV, mDevId);
                startActivity(intent);
                break;
            case R.id.img_back:
                finish();
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        i_timer_number = PreferenceUtils.getInt(this, mDevId + Global.CURTAIN_SWITCH_TIMER);
        update_UI(i_timer_number);
    }

    private void update_UI(int i){
        st_timer_number = Integer.toString(i);
        tv_curtain_timer_number.setText(st_timer_number);
    }
}
