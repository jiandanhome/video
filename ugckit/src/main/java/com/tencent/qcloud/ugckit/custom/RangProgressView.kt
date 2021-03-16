package com.tencent.qcloud.ugckit.custom

import android.content.Context
import android.graphics.Rect
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.MotionEvent
import androidx.constraintlayout.widget.ConstraintLayout
import com.tencent.qcloud.ugckit.R
import com.tencent.qcloud.ugckit.component.timeline.ViewTouchProcess
import kotlinx.android.synthetic.main.view_rang_progress.view.*

class RangProgressView(context: Context,attributeSet: AttributeSet):ConstraintLayout(context,attributeSet) {

    private var touchProcess:ViewTouchProcess?=null

    init {
        LayoutInflater.from(context).inflate(R.layout.view_rang_progress,this)
        ViewTouchProcess(ivLeft).setOnPositionChangedListener(object:ViewTouchProcess.OnPositionChangedListener{
            override fun onPostionChanged(distance: Float) {
            }
            override fun onChangeComplete() {
            }
        })

        ViewTouchProcess(ivLeft).setOnPositionChangedListener(object:ViewTouchProcess.OnPositionChangedListener{
            override fun onPostionChanged(distance: Float) {
            }
            override fun onChangeComplete() {
            }
        })
    }

    private val rect:Rect by lazy{
        Rect()
    }

}