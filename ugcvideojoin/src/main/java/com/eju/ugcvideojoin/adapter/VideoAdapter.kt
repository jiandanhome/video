package com.eju.ugcvideojoin.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.eju.ugcvideojoin.R
import com.tencent.qcloud.ugckit.module.picker.data.TCVideoFileInfo
import com.tencent.qcloud.ugckit.utils.DateTimeUtil

class VideoAdapter(private var videoList: MutableList<TCVideoFileInfo>): RecyclerView.Adapter<VideoAdapter.VideoHolder>() {

    var videoSelectedCallback:((selectedVideo:TCVideoFileInfo)->Unit)?=null
    var videoUnselectedCallback:((unSelectedVideo:TCVideoFileInfo)->Unit)?=null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VideoHolder {
        return VideoHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_ugc_video,
                parent,
                false
            )
        ).also { viewHolder->
            viewHolder.itemView.setOnClickListener {
                val position=viewHolder.adapterPosition
                val item=getItem(position)
                item.isSelected=!item.isSelected
                notifyItemChanged(position)
                if(item.isSelected){
                    videoSelectedCallback?.invoke(item)
                }else{
                    videoUnselectedCallback?.invoke(item)
                }
            }
        }
    }

    fun update(videoList: MutableList<TCVideoFileInfo>){
        this.videoList.clear()
        this.videoList.addAll(videoList)
        notifyDataSetChanged()
    }

    fun unSelect(video:TCVideoFileInfo){
        val position=videoList.indexOf(video)
        video.isSelected=false
        notifyItemChanged(position)
    }

    override fun onBindViewHolder(holder: VideoHolder, position: Int) {
        val fileInfo=getItem(position)
        holder.tvVideoDuration.text = DateTimeUtil.formattedTime(fileInfo.duration / 1000)
        holder.vCover.visibility=if(fileInfo.isSelected)View.VISIBLE else View.GONE
        Glide.with(holder.ivVideo).load(fileInfo.fileUri)
            .centerCrop()
            .placeholder(R.drawable.ic_ugc_video_placeholder)
            .dontAnimate().into(holder.ivVideo)
    }

    fun getItem(position: Int)=videoList[position]

    override fun getItemCount(): Int {
        return videoList.size
    }


    class VideoHolder(view: View):RecyclerView.ViewHolder(view){
        val ivVideo=view.findViewById<ImageView>(R.id.ivVideo)
        val vCover=view.findViewById<View>(R.id.vCover)
        val tvVideoDuration=view.findViewById<TextView>(R.id.tvVideoDuration)
    }

}