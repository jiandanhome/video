package com.tencent.qcloud.ugckit.custom

import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import com.tencent.qcloud.ugckit.R
import kotlinx.android.synthetic.main.dialog_ugc_alert.*

class UgcAlertDialog: DialogFragment() {

    var leftClickCallback:(()->Unit)?=null
    var rightClickCallback:(()->Unit)?=null

    companion object{
        fun newInstance(content: String,leftText:String?=null,rightText:String?=null):UgcAlertDialog {
            return UgcAlertDialog().apply {
                arguments=Bundle().apply {
                    putString("content", content)
                    putString("leftText", leftText)
                    putString("rightText", rightText)
                }
            }
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
        return inflater.inflate(R.layout.dialog_ugc_alert, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        tvContent.text=arguments?.getString("content")
        val leftText=arguments?.getString("leftText")
        if(!leftText.isNullOrEmpty()){
            tvLeft.text=leftText
        }
        val rightText=arguments?.getString("rightText")
        if(!rightText.isNullOrEmpty()){
            tvRight.text=rightText
        }

        tvLeft.setOnClickListener {
            leftClickCallback?.invoke()
            dismissAllowingStateLoss()
        }
        tvRight.setOnClickListener {
            rightClickCallback?.invoke()
            dismissAllowingStateLoss()
        }
    }

    fun showAllowingStateLoss(fragmentManager: FragmentManager?) {
        if (fragmentManager == null || fragmentManager.isDestroyed) {
            return
        }
        fragmentManager.beginTransaction().add(this, javaClass.simpleName).commitAllowingStateLoss()
    }


    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            (resources.displayMetrics.widthPixels * 2F / 3).toInt(),
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        dialog?.window?.setDimAmount(0F)
    }
}