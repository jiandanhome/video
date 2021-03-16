package com.eju.ugcvideojoin

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.eju.ugcvideojoin.adapter.SelectedVideoAdapter
import com.eju.ugcvideojoin.adapter.VideoAdapter
import com.tencent.liteav.demo.videoediter.TCVideoCoverSelectActivity
import com.tencent.qcloud.ugckit.UGCKitConstants
import com.tencent.qcloud.ugckit.module.picker.data.PickerManagerKit
import com.tencent.qcloud.ugckit.module.picker.data.TCVideoFileInfo
import com.tencent.qcloud.ugckit.utils.DateTimeUtil
import com.tencent.qcloud.ugckit.utils.ScreenUtils
import kotlinx.android.synthetic.main.activity_ugc_select_video.*
import kotlin.concurrent.thread

class UGCSelectVideoActivity:AppCompatActivity() {

    
    private var videoAdapter: VideoAdapter?=null

    private var selectedVideoAdapter: SelectedVideoAdapter?=null

    private val itemTouchHelper=ItemTouchHelper(object : ItemTouchHelper.Callback() {
        override fun getMovementFlags(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder
        ): Int {
            return makeMovementFlags(
                ItemTouchHelper.UP or ItemTouchHelper.DOWN or ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT,
                0
            )
        }

        override fun onMove(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            target: RecyclerView.ViewHolder
        ): Boolean {
            selectedVideoAdapter?.swapItem(viewHolder.adapterPosition, target.adapterPosition)
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
        checkPermission()
    }

    private fun checkPermission(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val permissions = arrayOf(
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE
            )
            val mPermissionList: MutableList<String> = java.util.ArrayList()
            for (i in permissions.indices) {
                if (ContextCompat.checkSelfPermission(this, permissions[i]) != PackageManager.PERMISSION_GRANTED) {
                    mPermissionList.add(permissions[i])
                }
            }
            if (mPermissionList.isEmpty()) {
                queryVideoList()
            } else {
                //存在未允许的权限
                val permissionsArr = mPermissionList.toTypedArray()
                ActivityCompat.requestPermissions(this, permissionsArr, 11)
            }
        } else {
            //FIXBUG:Android6.0以下不需要动态获取权限
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                queryVideoList()
            }
        }
    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String?>, grantResults: IntArray) {
        var forbidden = false
        for (i in grantResults.indices) {
            if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                forbidden = true
            }
        }
        if (!forbidden) {
            queryVideoList()
        }
    }


    private fun setListeners() {
        ibBack.setOnClickListener { onBackPressed() }
        tvNext.setOnClickListener {
            val selectedList:ArrayList<TCVideoFileInfo> =selectedVideoAdapter?.selectedVideoList?: arrayListOf()
            if(selectedList.isNotEmpty()){
                if(selectedList.size==1){
                    val videoOutputPath=selectedList.get(0).filePath
                    val videoOutputUri=selectedList.get(0).fileUri
//                    startActivity(Intent(this, TCVideoCutActivity::class.java)
//                        .putExtra(UGCKitConstants.VIDEO_PATH,videoOutputPath)
//                        .putExtra(UGCKitConstants.VIDEO_URI, videoOutputUri.toString())
//                    )
                    TCVideoCoverSelectActivity.open(this,videoOutputPath,videoOutputUri.toString())
                }else{
                    startActivity(
                        Intent(this, UGCVideoJoinActivity::class.java)
                            .putParcelableArrayListExtra(
                                UGCKitConstants.INTENT_KEY_MULTI_CHOOSE,
                                selectedList
                            )
                    )
                }
            }
        }
    }

    private fun setViews(){
        rvVideo.addItemDecoration(
            VideoListItemDecoration(
                ScreenUtils.dp2px(this, 8F).toInt(), ScreenUtils.dp2px(
                    this,
                    3F
                ).toInt()
            )
        )
    }

    private fun setPadding(){
        clContent.setPadding(0, ScreenUtils.getStatusBarHeight(this), 0, 0)
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

        selectedVideoAdapter= SelectedVideoAdapter(arrayListOf()).also {
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