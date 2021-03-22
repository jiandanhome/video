package com.tencent.qcloud.ugckit.custom

import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import com.tencent.qcloud.ugckit.R
import com.tencent.qcloud.ugckit.utils.ToastUtil
import kotlinx.android.synthetic.main.dialog_ugc_refactor_name.*

class UgcRefactorNameDialog: DialogFragment() {

    var leftClickCallback:(()->Unit)?=null
    var rightClickCallback:((String)->Unit)?=null

    companion object{
        fun newInstance(name:String):UgcRefactorNameDialog {
            return UgcRefactorNameDialog().apply {
                arguments=Bundle().apply {
                    putString("name",name)
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
        return inflater.inflate(R.layout.dialog_ugc_refactor_name, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        tvLeft.setOnClickListener {
            leftClickCallback?.invoke()
            dismissAllowingStateLoss()
        }
        tvRight.setOnClickListener {
            val newName=etInput.text.toString().trim()
            if(!newName.isNullOrEmpty()){
                rightClickCallback?.invoke(newName)
                dismissAllowingStateLoss()
            }else{
                ToastUtil.toastShortMessage("请输入短视频名字")
            }
        }
        tvRight.postDelayed({
            val imm =
                activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS)
            arguments?.getString("name")?.let { name->
                etInput.setText(name)
                etInput.setSelection(name.length)
            }
        }, 50)
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
            (resources.displayMetrics.widthPixels * 270F / 375).toInt(),
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        dialog?.window?.setDimAmount(0F)
    }
}