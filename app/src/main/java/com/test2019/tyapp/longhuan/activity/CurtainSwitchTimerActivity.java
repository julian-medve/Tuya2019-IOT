package com.test2019.tyapp.longhuan.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.test2019.tyapp.longhuan.R;
import com.test2019.tyapp.longhuan.global.Global;
import com.test2019.tyapp.longhuan.utils.PreferenceUtils;

public class CurtainSwitchTimerActivity extends AppCompatActivity implements View.OnClickListener{

    private ImageView img_back, img_plus, img_minus;
    private Button btn_verify;
    private TextView tv_curtain_number;
    private String st_timer_number;

    private String mDevId;

    private int i_timer_number = 10;
    private boolean i_countflag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_curtain_switch_timer);

        if(getIntent() != null){
            mDevId = getIntent().getStringExtra(Global.CURRENT_DEV);
        }

        initView();
    }

    private void initView(){
        img_back = (ImageView)findViewById(R.id.img_back);
        img_minus = (ImageView)findViewById(R.id.img_minus);
        img_plus = (ImageView)findViewById(R.id.img_plus);

        btn_verify = (Button)findViewById(R.id.btn_verify);
        tv_curtain_number = (TextView)findViewById(R.id.tv_curtain_number);

        img_plus.setOnClickListener(this);
        img_minus.setOnClickListener(this);
        img_back.setOnClickListener(this);
        btn_verify.setOnClickListener(this);

        update_UI(i_timer_number);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.img_back:
                finish();
                break;
            case R.id.img_plus:
                i_countflag = true;
                count_number();
                break;
            case R.id.img_minus:
                i_countflag = false;
                count_number();
                break;
            case R.id.btn_verify:
                PreferenceUtils.set(this, mDevId + Global.CURTAIN_SWITCH_TIMER, i_timer_number);
                finish();
                break;
        }
    }

    private void count_number(){
        if(i_countflag){
            i_timer_number ++;

            if(i_timer_number > 120){
                i_timer_number = 120;
                update_UI(i_timer_number);
            }else{
                update_UI(i_timer_number);
            }

        }else{
            i_timer_number --;

            if(i_timer_number < 10){
                i_timer_number = 10;
                update_UI(i_timer_number);
            }else{
                update_UI(i_timer_number);
            }
        }
    }

    private void update_UI(int i){
        st_timer_number = Integer.toString(i);
        tv_curtain_number.setText(st_timer_number);
    }

    @Override
    protected void onResume() {
        super.onResume();

        i_timer_number = PreferenceUtils.getInt(this, mDevId + Global.CURTAIN_SWITCH_TIMER);
        update_UI(i_timer_number);

    }

}
