package com.test2019.tyapp.longhuan.utils;

import android.content.res.Resources;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import com.test2019.tyapp.longhuan.MainApplication;

import java.lang.reflect.Field;

/**
 * desc  : Size related tools
 */
public class SizeUtils {

    private SizeUtils() {
        throw new UnsupportedOperationException("u can't instantiate me...");
    }

    /**
     * dp to px
     *
     * @param dpValue dp value
     * @return px value
     */
    public static int dp2px(float dpValue) {
       
        float scale = MainApplication.getAppContext().getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * px to dp
     *
     * @param pxValue px value
     * @return dp value
     */
    public static int px2dp(float pxValue) {
        float scale = MainApplication.getAppContext().getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }


    /**
     * sp to px
     *
     * @param spValue sp Value
     * @return px Value
     */
    public static int sp2px(float spValue) {
        float fontScale = MainApplication.getAppContext().getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }

    /**
     * px to sp
     *
     * @param pxValue px Value
     * @return sp Value
     */
    public static int px2sp(float pxValue) {
        float fontScale = MainApplication.getAppContext().getResources().getDisplayMetrics().scaledDensity;
        return (int) (pxValue / fontScale + 0.5f);
    }

    /**
     * Various unit conversion
     * <p>This method exists in TypedValue</p>
     *
     * @param unit    unit
     * @param value   value
     * @param metrics DisplayMetrics
     * @return Conversion Result
     */
    public static float applyDimension(int unit, float value, DisplayMetrics metrics) {
        switch (unit) {
            case TypedValue.COMPLEX_UNIT_PX:
                return value;
            case TypedValue.COMPLEX_UNIT_DIP:
                return value * metrics.density;
            case TypedValue.COMPLEX_UNIT_SP:
                return value * metrics.scaledDensity;
            case TypedValue.COMPLEX_UNIT_PT:
                return value * metrics.xdpi * (1.0f / 72);
            case TypedValue.COMPLEX_UNIT_IN:
                return value * metrics.xdpi;
            case TypedValue.COMPLEX_UNIT_MM:
                return value * metrics.xdpi * (1.0f / 25.4f);
        }
        return 0;
    }

    /**
     * Get the size of the view in onCreate
     * <p>Need to call back the onGetSizeListener interface, get the view width and height in onGetSize</p>
     * <p>Example usage is as follows</p>
     * <pre>
     * SizeUtils.forceGetViewSize(view, new SizeUtils.onGetSizeListener() {
     *     Override
     *     public void onGetSize(View view) {
     *         view.getWidth();
     *     }
     * });
     * </pre>
     *
     * @param view     view
     * @param listener Listener
     */
    public static void forceGetViewSize(final View view, final onGetSizeListener listener) {
        view.post(new Runnable() {
            @Override
            public void run() {
                if (listener != null) {
                    listener.onGetSize(view);
                }
            }
        });
    }

    /**
     * Get Listener to the view size
     */
    public interface onGetSizeListener {
        void onGetSize(View view);
    }

    /**
     * Measuring view size
     *
     * @param view view
     * @return arr[0]: view width, arr[1]: view height
     */
    public static int[] measureView(View view) {
        ViewGroup.LayoutParams lp = view.getLayoutParams();
        if (lp == null) {
            lp = new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            );
        }
        int widthSpec = ViewGroup.getChildMeasureSpec(0, 0, lp.width);
        int lpHeight = lp.height;
        int heightSpec;
        if (lpHeight > 0) {
            heightSpec = View.MeasureSpec.makeMeasureSpec(lpHeight, View.MeasureSpec.EXACTLY);
        } else {
            heightSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        }
        view.measure(widthSpec, heightSpec);
        return new int[]{view.getMeasuredWidth(), view.getMeasuredHeight()};
    }

    /**
     * Get measurement view width
     *
     * @param view view
     * @return view width
     */
    public static int getMeasuredWidth(View view) {
        return measureView(view)[0];
    }

    /**
     * Get measurement view height
     *
     * @param view view
     * @return view height
     */
    public static int getMeasuredHeight(View view) {
        return measureView(view)[1];
    }

    public static int getWidth() {
        int width = MainApplication.getAppContext().getResources().getDisplayMetrics().widthPixels;
        return width;
    }

    public static int getHeight() {
        int height = MainApplication.getAppContext().getResources().getDisplayMetrics().heightPixels;
        return height;
    }


    /**
     * Get the height of the status bar
     *
     * @return
     */
    public static int getStatusBarHeight() {
        Class<?> c = null;
        Object obj = null;
        Field field = null;
        int x = 0, sbar = 38; //The default is 38
        try {
            c = Class.forName("com.android.internal.R$dimen");
            obj = c.newInstance();
            field = c.getField("status_bar_height");
            x = Integer.parseInt(field.get(obj).toString());
            sbar = MainApplication.getAppContext().getResources().getDimensionPixelSize(x);
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        return sbar;
    }


    public static int getNavigationBarHeight() {
        Resources resources = MainApplication.getAppContext().getResources();
        int resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android");
        int height = resources.getDimensionPixelSize(resourceId);
        return height;
    }




}
