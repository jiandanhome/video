package com.tencent.qcloud.ugckit.custom

import android.content.Context
import android.graphics.Bitmap
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.tencent.qcloud.ugckit.R
import com.tencent.qcloud.ugckit.utils.ScreenUtils
import kotlinx.android.synthetic.main.view_video_thumb.view.*

class VideoThumbView(context: Context,attributeSet: AttributeSet):ConstraintLayout(context,attributeSet){

    private val videoThumbList= mutableListOf<Bitmap>()

    private var adapter:VideoThumbAdapter?=null

    private var toScrolledDistance=0F

    init {
        LayoutInflater.from(context).inflate(R.layout.view_video_thumb,this)
        adapter=VideoThumbAdapter().also {
            rvVideoThumb.adapter=it
        }
    }

    fun addVideoThumb(bitmap: Bitmap){
        adapter?.addVideoThumb(bitmap)
    }

    fun scrollThumb(dx: Float){
        rvVideoThumb.scrollBy(dx.toInt(),0)
    }

    fun addOnScrollListener(onScrollListener: RecyclerView.OnScrollListener){
        rvVideoThumb.addOnScrollListener(onScrollListener)
    }


    inner class VideoThumbAdapter:RecyclerView.Adapter<VideoThumbViewHolder>(){

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VideoThumbViewHolder {
            return if(viewType!=0){
                VideoThumbViewHolder(View(parent.context).apply {
                    layoutParams=ViewGroup.LayoutParams(ScreenUtils.getScreenWidth(parent.context)/2,1)
                })
            }else{
                VideoThumbViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_ugc_video_cut_thumb,parent,false))
            }
        }

        override fun onBindViewHolder(holder: VideoThumbViewHolder, position: Int) {
            val itemViewType=getItemViewType(position)
            if(itemViewType==0){
                holder.ivThumb?.setImageBitmap(videoThumbList[position-1])
            }
        }

        fun addVideoThumb(bitmap: Bitmap){
            videoThumbList.add(bitmap)
            notifyItemInserted(videoThumbList.size)
        }

        override fun getItemViewType(position: Int): Int {
            return when (position) {
                0 -> -1
                itemCount-1 -> 1
                else -> 0
            }
        }

        override fun getItemCount(): Int {
            return videoThumbList.size+2
        }

    }

    class VideoThumbViewHolder(view:View):RecyclerView.ViewHolder (view){
        val ivThumb:ImageView?=view.findViewById(R.id.ivThumb)
    }

}