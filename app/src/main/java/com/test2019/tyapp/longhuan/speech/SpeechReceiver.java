package com.test2019.tyapp.longhuan.speech;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class SpeechReceiver extends BroadcastReceiver {

    private Context mContext;

    public SpeechReceiver(Context ctx)
    {
        this.mContext = ctx ;
    }

    @Override
    public void onReceive(Context context, Intent intent) {

    }
}
