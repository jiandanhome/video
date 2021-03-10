package com.eju.ugcvideojoin

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.ItemTouchUIUtil
import androidx.recyclerview.widget.RecyclerView
import com.eju.ugcvideojoin.adapter.SelectedVideoAdapter
import com.eju.ugcvideojoin.adapter.VideoAdapter
import com.tencent.qcloud.ugckit.module.picker.data.PickerManagerKit
import com.tencent.qcloud.ugckit.module.picker.data.TCVideoFileInfo
import com.tencent.qcloud.ugckit.utils.DateTimeUtil
import com.tencent.qcloud.ugckit.utils.ScreenUtils
import kotlinx.android.synthetic.main.activity_ugc_select_video.*
import kotlin.concurrent.thread

class UGCSelectVideoActivity:AppCompatActivity() {

    
    private var videoAdapter: VideoAdapter?=null

    private var selectedVideoAdapter: SelectedVideoAdapter?=null

    private val itemTouchHelper=ItemTouchHelper(object:ItemTouchHelper.Callback(){
        override fun getMovementFlags(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder
        ): Int {
            return makeMovementFlags(ItemTouchHelper.UP or ItemTouchHelper.DOWN or ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT,0)
        }
        override fun onMove(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            target: RecyclerView.ViewHolder
        ): Boolean {
            selectedVideoAdapter?.swapItem(viewHolder.adapterPosition,target.adapterPosition)
            return true
        }
        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        }

    })


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ugc_select_video)
        setListeners()
        setViews()
        setPadding()
        setAdapter()
        queryVideoList()
    }

    private fun setListeners() {
        ibBack.setOnClickListener { onBackPressed() }
        tvNext.setOnClickListener {
            val selectedList=selectedVideoAdapter?.selectedVideoList?: emptyList()
            if(selectedList.isNotEmpty()){
                //todo
            }
        }
    }

    private fun setViews(){
        rvVideo.addItemDecoration(VideoLIstItemDecoration(ScreenUtils.dp2px(this,8F).toInt(), ScreenUtils.dp2px(this,3F).toInt()))
    }

    private fun setPadding(){
        clContent.setPadding(0,ScreenUtils.getStatusBarHeight(this),0,0)
        clBottom.post {
            rvVideo.setPadding(0, 0, 0, clBottom.height)
        }
    }

    private fun setAdapter(){
        videoAdapter= VideoAdapter(mutableListOf()).also {
            it.videoSelectedCallback={ video->
                selectedVideoAdapter?.addLast(video)
                onSelectedChanged()
            }
            it.videoUnselectedCallback={ video->
                selectedVideoAdapter?.delete(video)
                onSelectedChanged()
            }
            rvVideo.adapter=it
        }

        selectedVideoAdapter= SelectedVideoAdapter(mutableListOf()).also {
            it.videoDeletedCallback={ video->
                videoAdapter?.unSelect(video)
                onSelectedChanged()
            }
            rvSelectedVideo.adapter=it
        }
        itemTouchHelper.attachToRecyclerView(rvSelectedVideo)
    }

    private fun queryVideoList(){
        thread {
            val videoList=PickerManagerKit.getInstance(this).getAllVideo(61000)
            runOnUiThread {
                videoAdapter?.update(videoList)
            }
        }
    }

    private fun onSelectedChanged(){
        val duration=selectedVideoAdapter?.selectedVideoDuration()?:0L;
        tvTotalTime.text = DateTimeUtil.formattedTime(duration / 1000)
    }


}