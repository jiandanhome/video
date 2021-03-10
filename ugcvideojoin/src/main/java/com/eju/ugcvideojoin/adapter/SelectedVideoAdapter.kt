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
import java.util.*

class SelectedVideoAdapter(var selectedVideoList: MutableList<TCVideoFileInfo>): RecyclerView.Adapter<SelectedVideoAdapter.VideoHolder>() {

    var videoDeletedCallback:((selectedVideo:TCVideoFileInfo)->Unit)?=null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VideoHolder {
        return VideoHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_ugc_selected_video,
                parent,
                false
            )
        ).also { viewHolder->
            viewHolder.ibDelete.setOnClickListener {
                val position=viewHolder.adapterPosition
                val item=getItem(position)
                selectedVideoList.remove(item)
                notifyItemRemoved(position)
                videoDeletedCallback?.invoke(item)
            }
        }
    }

    fun addLast(video:TCVideoFileInfo){
        selectedVideoList.add(video)
        notifyItemInserted(selectedVideoList.size)
    }

    fun delete(video:TCVideoFileInfo){
        val position=selectedVideoList.indexOf(video)
        selectedVideoList.removeAt(position)
        notifyItemRemoved(position)
    }

    fun swapItem(fromPosition:Int,toPosition:Int){
        Collections.swap(selectedVideoList,fromPosition,toPosition)
        notifyItemMoved(fromPosition,toPosition)
    }


    fun selectedVideoDuration():Long{
        return selectedVideoList.sumOf { it.duration }
    }

    override fun onBindViewHolder(holder: VideoHolder, position: Int) {
        val fileInfo=getItem(position)
        holder.tvVideoDuration.text = DateTimeUtil.formattedTime(fileInfo.duration / 1000)
        Glide.with(holder.ivVideo).load(fileInfo.fileUri)
            .centerCrop()
            .placeholder(R.drawable.ic_ugc_video_placeholder)
            .dontAnimate().into(holder.ivVideo)
    }

    fun getItem(position: Int)=selectedVideoList[position]

    override fun getItemCount(): Int {
        return selectedVideoList.size
    }


    class VideoHolder(view: View):RecyclerView.ViewHolder(view){
        val ivVideo=view.findViewById<ImageView>(R.id.ivSelectedVideo)
        val ibDelete=view.findViewById<ImageView>(R.id.ibDelete)
        val tvVideoDuration=view.findViewById<TextView>(R.id.tvVideoDuration)
    }

}