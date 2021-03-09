package com.tencent.qcloud.ugckit.custom

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View

class UGCProgressView(context: Context,attributeSet: AttributeSet):View(context,attributeSet) {

    private var _progress=0F

    private val bgColor= Color.parseColor("#B3FFFFFF")
    private val progressColor= Color.parseColor("#3d71ef")



    fun setProgress(progress: Float){
        this._progress=progress;
        if(_progress<0){
            _progress=0F
        }else if(_progress>1){
            _progress=1F;
        }
        postInvalidate()
    }

    private val paint:Paint by lazy{
        Paint(Paint.ANTI_ALIAS_FLAG)
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        paint.color=bgColor
        canvas?.drawRoundRect(0F,0F,width.toFloat(),height.toFloat(),height.toFloat(),height.toFloat(),paint)

        paint.color=progressColor
        canvas?.drawRoundRect(0F,0F,width*_progress,height.toFloat(),height.toFloat(),height.toFloat(),paint)
    }
}