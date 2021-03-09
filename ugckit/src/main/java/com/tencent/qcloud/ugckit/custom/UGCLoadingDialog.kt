package com.tencent.qcloud.ugckit.custom

import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.fragment.app.DialogFragment
import com.tencent.qcloud.ugckit.R
import kotlinx.android.synthetic.main.dialog_ugc_loading.*

class UGCLoadingDialog : DialogFragment() {

    companion object{
        fun newInstance(content:String):UGCLoadingDialog{
            var loadingDialog=UGCLoadingDialog()
            var bundle= Bundle()
            bundle.putString("content",content)
            loadingDialog.arguments=bundle
            return loadingDialog
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        dialog?.window?.requestFeature(Window.FEATURE_NO_TITLE)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(0x00000000))
        dialog?.setCancelable(false)
        dialog?.setCanceledOnTouchOutside(false)
        return  return inflater.inflate( R.layout.dialog_ugc_loading, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        var content=arguments?.getString("content")
        textView.visibility=if(content.isNullOrEmpty()) View.GONE else View.VISIBLE
        textView.text=content
    }

}