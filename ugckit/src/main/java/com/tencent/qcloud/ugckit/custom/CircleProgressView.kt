package com.tencent.qcloud.ugckit.custom

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.util.Log
import android.view.View
import com.tencent.qcloud.ugckit.utils.ScreenUtils

class CircleProgressView(context: Context, attributeSet: AttributeSet):View(context,attributeSet) {

    private val progressColor= Color.parseColor("#3d71ef")

    var progress=0F
        set(value) {
            field=value
            postInvalidate()
        }

    private var lineWidth=ScreenUtils.dp2px(context,2F)
    private var interval= ScreenUtils.dp2px(context,3F)

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    }

    private val paint:Paint by lazy {
        Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color=progressColor
        }
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        paint.style=Paint.Style.STROKE
        paint.strokeWidth = lineWidth
        canvas?.drawCircle(width/2F,height/2F,width/2F-lineWidth/2f,paint)


        paint.style=Paint.Style.FILL
        val left=lineWidth+interval
        canvas?.drawArc(left,left,width-left,height-left,-90F,progress*360,true,paint)
    }
}