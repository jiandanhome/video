package com.eju.ugcvideojoin

import android.graphics.Rect
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.tencent.qcloud.ugckit.utils.ScreenUtils

class VideoListItemDecoration(private val borderSpace:Int, private val dividerSpace:Int):RecyclerView.ItemDecoration(){


    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        super.getItemOffsets(outRect, view, parent, state)

        val layoutManager=parent.layoutManager
        if(layoutManager is GridLayoutManager){

            val spanCount=layoutManager.spanCount
            val perSpaceWidth=((borderSpace*2F+(spanCount-1)*dividerSpace)/spanCount).toInt()
            var spanIndex=(view.layoutParams as GridLayoutManager.LayoutParams).spanIndex
            val left=getLeft(perSpaceWidth,spanCount,spanIndex)
            val right=perSpaceWidth-left
            outRect.left=left
            outRect.right=right
            outRect.bottom=dividerSpace
        }

    }

    private fun getLeft(perSpaceWidth:Int,spanCount:Int,spanIndex:Int):Int{
        var left = borderSpace
        var right= perSpaceWidth-left
        for (index in 0 until spanCount){
            if(spanIndex==index){
                break
            }
            left=dividerSpace-right
            right=perSpaceWidth-left
        }
        return left
    }

}