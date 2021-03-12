package com.tencent.qcloud.ugckit.utils;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import android.content.res.Resources;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;

/**
 * ScreenUtils
 */
public class ScreenUtils {
    /**
     * 获取屏幕宽
     *
     * @return
     */
    public static int getScreenWidth(@Nullable Context context) {
        if (context == null) return 0;
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        return dm.widthPixels;
    }

    /**
     * 获取屏幕高度
     *
     * @return
     */
    public static int getScreenHeight(@Nullable Context context) {
        if (context == null) return 0;
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        return dm.heightPixels;
    }

    public static float dp2px(@NonNull Context context, float dp) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return dp * scale + 0.5f;
    }

    public static float sp2px(@NonNull Context context, float sp) {
        final float scale = context.getResources().getDisplayMetrics().scaledDensity;
        return sp * scale;
    }

    /**
     * 获取状态栏高度
     * @param context
     * @return
     */
    public static int getStatusBarHeight(Context context) {
        Resources resources = context.getResources();
        int resourceId = resources.getIdentifier("status_bar_height", "dimen", "android");
        int height = resources.getDimensionPixelSize(resourceId);
        return height;
    }


    public static void setMarginTop(View view){
        ViewGroup.LayoutParams lp = view.getLayoutParams();
        if(lp instanceof ViewGroup.MarginLayoutParams){
            ((ViewGroup.MarginLayoutParams) lp).topMargin=getStatusBarHeight(view.getContext());
            view.setLayoutParams(lp);
        }
    }


    public static void setPaddingTop(View view){
        view.setPadding(view.getPaddingLeft(),view.getPaddingTop()+getStatusBarHeight(view.getContext()),view.getPaddingRight(),view.getPaddingBottom());
    }

}
