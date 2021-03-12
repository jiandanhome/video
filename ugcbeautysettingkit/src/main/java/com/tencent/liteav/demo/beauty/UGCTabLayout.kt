package com.tencent.liteav.demo.beauty

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import kotlinx.android.synthetic.main.view_ugc_tablayout.view.*

class UGCTabLayout(context: Context,attributeSet: AttributeSet):ConstraintLayout (context,attributeSet){

    var onTabChangedCallback:((Int)->Unit)?=null

    private var currentPosition=0

    init {
        LayoutInflater.from(context).inflate(R.layout.view_ugc_tablayout,this)
        tv0.setOnClickListener {
            onSelectedChanged(0)
        }
        tv1.setOnClickListener {
            onSelectedChanged(1)
        }
    }


    private fun onSelectedChanged(position:Int){
        if(currentPosition!=position){
            this.currentPosition=position
        }
        when(currentPosition){
            0 ->{
                tv0.textSize = 16F
                tv0.setTextColor(Color.WHITE)

                tv1.textSize = 14F
                tv1.setTextColor(Color.argb(178,255,255,255))
            }
            1 ->{
                tv1.textSize = 16F
                tv1.setTextColor(Color.WHITE)

                tv0.textSize = 14F
                tv0.setTextColor(Color.argb(178,255,255,255))
            }
        }
        onTabChangedCallback?.invoke(currentPosition)

    }

    fun setData(titles:List<String>){
        if(titles.isNotEmpty()){
            tv0.text=titles[0]
        }
        if(titles.size>=2){
            tv1.text=titles[1]
        }
    }
}