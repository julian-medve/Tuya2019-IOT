package com.test2019.tyapp.longhuan.utils;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by mikeshou on 15/6/16.
 */
public class ToastUtil {

    private static Toast longToast, shortToast;

    /**
     * Long Toast
     *
     * @param resId resId
     */
    public static synchronized void showToast(Context context, int resId) {
        showToast(context, context.getString(resId));
    }

    public static synchronized void showToast(Context context, String tips) {
        if (longToast == null) {
            longToast = Toast.makeText(context, "", Toast.LENGTH_LONG);
        }

        longToast.setText(tips);
        longToast.show();
    }

    /**
     * Short Toast
     *
     * @param tips tips
     */
    public static synchronized void shortToast(Context context, String tips) {
        if (shortToast == null) {
            shortToast = Toast.makeText(context, "", Toast.LENGTH_SHORT);
        }
        shortToast.setText(tips);
        shortToast.show();
    }

    /**
     * Short Toast
     *
     * @param tipsResId tipsResId
     */
    public static synchronized void shortToast(Context context, int tipsResId) {
        showToast(context, context.getString(tipsResId));
    }

}
