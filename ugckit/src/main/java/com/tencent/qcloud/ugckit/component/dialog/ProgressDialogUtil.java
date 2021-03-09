package com.tencent.qcloud.ugckit.component.dialog;

import android.content.Context;

import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

import com.tencent.qcloud.ugckit.custom.UGCLoadingDialog;

public class ProgressDialogUtil {

    private Context        mContext;

    private UGCLoadingDialog loadingDialog;

    public ProgressDialogUtil(Context context) {
        mContext = context;
    }

    public void showProgressDialog() {
        FragmentManager fragmentManager=null;
        if(mContext instanceof FragmentActivity){
            fragmentManager=((FragmentActivity) mContext).getSupportFragmentManager();
        }
        if(fragmentManager!=null){
            try {
                loadingDialog=UGCLoadingDialog.Companion.newInstance("");
                loadingDialog.show(fragmentManager,"loadingDialog");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void dismissProgressDialog() {
        try {
            loadingDialog.dismissAllowingStateLoss();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setProgressDialogMessage(String message) {
    }
}
