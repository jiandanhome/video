package com.tencent.liteav.demo.videoediter

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.tencent.qcloud.ugckit.UGCKit
import com.tencent.qcloud.ugckit.UGCKitConstants
import com.tencent.qcloud.ugckit.custom.EjuVideoConfig
import com.tencent.qcloud.ugckit.custom.UGCImageUtils
import com.tencent.qcloud.ugckit.custom.UGCLoadingDialog
import com.tencent.qcloud.ugckit.module.cut.IVideoCutLayout
import com.tencent.qcloud.ugckit.module.effect.utils.PlayState
import com.tencent.qcloud.ugckit.utils.DateTimeUtil
import com.tencent.qcloud.ugckit.utils.ScreenUtils
import com.tencent.qcloud.ugckit.utils.ToastUtil
import com.tencent.ugc.TXVideoEditConstants
import com.tencent.ugc.TXVideoEditer
import com.tencent.ugc.TXVideoInfoReader
import kotlinx.android.synthetic.main.ugcedit_activity_video_cover_selecet.*
import kotlin.concurrent.thread
import kotlin.math.roundToInt

class TCVideoCoverSelectActivity:AppCompatActivity() {

    private var mVideoResolution:Int=-1

    private var videoPath:String?=null

    private var videoEditer:TXVideoEditer?=null

    private var currentTimeMs=0

    private var videoDuration:Long=0

    private var singleThumbWidth=0

    private var thumbCount=0

    private var allThumbWidth=0

    private var currentScrolledX=0

    private var currentPlayState=PlayState.STATE_NONE



    companion object{
        fun open(activity: Activity, videoPath: String,requestCode:Int) {
            activity.startActivityForResult(
                Intent(activity, TCVideoCoverSelectActivity::class.java)
                    .putExtra(UGCKitConstants.VIDEO_PATH, videoPath)
                    .putExtra(UGCKitConstants.VIDEO_URI, Uri.parse(videoPath).toString()
                    )
                ,requestCode
            )
        }

        fun open(fragment: Fragment, videoPath: String,requestCode:Int) {
            fragment.activity?.let { activity->
                fragment.startActivityForResult(
                    Intent(activity, TCVideoCoverSelectActivity::class.java)
                        .putExtra(UGCKitConstants.VIDEO_PATH, videoPath)
                        .putExtra(UGCKitConstants.VIDEO_URI, Uri.parse(videoPath).toString()
                        )
                    ,requestCode
                )
            }
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.ugcedit_activity_video_cover_selecet)
        setTitle()
        parseIntent()
        init()
        loadThumbList()
        setListeners()
        startPLay()
    }

    private fun setTitle(){
        ScreenUtils.setMarginTop(titleLayout)
        findViewById<View>(R.id.page_title_left_icon).setOnClickListener { finish() }
        val btn_next=findViewById<TextView>(R.id.btn_next)
        btn_next.text="完成"
        btn_next.setOnClickListener {
            if(currentPlayState==PlayState.STATE_PLAY){
                videoEditer?.pausePlay()
                currentPlayState=PlayState.STATE_PAUSE
                ivPlay.setImageResource(R.drawable.ic_ugc_play)
            }
            snapshot()
        }
    }

    private fun snapshot(){
        showLoading()
        thread {
            val coverBitmap=TXVideoInfoReader.getInstance(this).getSampleImage(currentTimeMs.toLong(),intent.getStringExtra(UGCKitConstants.VIDEO_PATH))
            UGCImageUtils.saveBitmap(this,coverBitmap)?.let {
                runOnUiThread { hideLoading() }
                setResult(RESULT_OK,Intent().putExtra(UGCKitConstants.COVER_PIC,it))
                setResult(RESULT_OK,Intent().putExtra(UGCKitConstants.COVER_PIC_TIME_IS_MS,currentTimeMs))
                finish()
            }?:runOnUiThread {
                hideLoading()
                ToastUtil.toastShortMessage("获取封面图失败 请售后重试")
            }
        }
    }

    private fun parseIntent(){
        val videoPath = intent.getStringExtra(UGCKitConstants.VIDEO_PATH)
        val videoUri = intent.getStringExtra(UGCKitConstants.VIDEO_URI)
        this.videoPath=  if(Build.VERSION.SDK_INT >= 29) videoUri else videoPath
        this.videoPath="https://img-test.jiandanhome.com/relations/963/2021-03-09/source/1615255594522.mp4"
    }


    private fun init(){
        try {
            val videoInfo = TXVideoInfoReader.getInstance(UGCKit.getAppContext()).getVideoFileInfo(videoPath)
            videoDuration=videoInfo.duration
        } catch (e: Exception) {
            Toast.makeText(this, "视频文件读取失败", Toast.LENGTH_SHORT).show()
            return
        }
        thumbCount = (videoDuration/3000).toInt()
        singleThumbWidth= ScreenUtils.dp2px(this,40F).toInt()
        allThumbWidth=thumbCount*singleThumbWidth


        videoEditer= TXVideoEditer(this).apply {
            setVideoPath(videoPath)
            initWithPreview(TXVideoEditConstants.TXPreviewParam().apply {
                videoView = flPlayVideo
                renderMode = TXVideoEditConstants.PREVIEW_RENDER_MODE_FILL_EDGE
            })
        }
    }


    private fun loadThumbList(){
        videoEditer?.setCutFromTime(0, videoDuration)
        videoEditer?.getThumbnail(
            thumbCount,
            IVideoCutLayout.DEFAULT_THUMBNAIL_WIDTH,
            IVideoCutLayout.DEFAULT_THUMBNAIL_HEIGHT,
            true
        ) { index, timeMs, bitmap ->
            runOnUiThread {
                videoThumb.addVideoThumb(bitmap)
            }
        }
    }

    private fun setListeners() {
        videoEditer?.setTXVideoPreviewListener(object : TXVideoEditer.TXVideoPreviewListener {
            override fun onPreviewProgress(time: Int) {
                currentTimeMs = time / 1000  //videoProgress:ms
                tvTime.text = DateTimeUtil.formattedTime((currentTimeMs * 1F / 1000).roundToInt().toLong())
                if (currentPlayState == PlayState.STATE_PLAY) {
                    val distance =
                        (currentTimeMs * 1F / videoDuration) * allThumbWidth - currentScrolledX
                    videoThumb.scrollThumb(distance)
                }
            }

            override fun onPreviewFinished() {
                startPLay()
            }
        })
        videoThumb.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                currentScrolledX += dx
                if (currentPlayState != PlayState.STATE_PLAY) {
                    val progress = currentScrolledX.toFloat() / allThumbWidth
                    val currentTimeMs = (videoDuration * progress).toInt()
                    videoEditer?.previewAtTime(currentTimeMs.toLong())
                    currentPlayState = PlayState.STATE_PREVIEW_AT_TIME
                }
            }

            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                when (newState) {
                    RecyclerView.SCROLL_STATE_DRAGGING -> {
                        if (currentPlayState == PlayState.STATE_PLAY) {
                            videoEditer?.pausePlay()
                            currentPlayState = PlayState.STATE_PAUSE
                            ivPlay.setImageResource(R.drawable.ic_ugc_play)
                        }
                    }
                }
            }
        })
        ivPlay.setOnClickListener {
            if(currentPlayState==PlayState.STATE_PLAY){
                videoEditer?.pausePlay()
                currentPlayState=PlayState.STATE_PAUSE
                ivPlay.setImageResource(R.drawable.ic_ugc_play)
            }else if(currentPlayState==PlayState.STATE_PAUSE){
                videoEditer?.resumePlay()
                currentPlayState=PlayState.STATE_PLAY
                ivPlay.setImageResource(R.drawable.ic_ugc_pause)
            }else if(currentPlayState==PlayState.STATE_PREVIEW_AT_TIME){
                videoEditer?.startPlayFromTime(currentTimeMs.toLong(),videoDuration)
                currentPlayState=PlayState.STATE_PLAY
                ivPlay.setImageResource(R.drawable.ic_ugc_pause)
            }

        }
    }

    private fun startPLay(){
        videoEditer?.startPlayFromTime(0,videoDuration)
        currentPlayState=PlayState.STATE_PLAY
        ivPlay.setImageResource(R.drawable.ic_ugc_pause)
    }

    private var loadingDialog:UGCLoadingDialog?=null


    private fun showLoading(){
        hideLoading()
        loadingDialog=UGCLoadingDialog.newInstance("正在截取封面")
        loadingDialog?.show(supportFragmentManager,"正在截取封面")
    }

    private fun hideLoading(){
        loadingDialog?.dismissAllowingStateLoss()
    }




    override fun onDestroy() {
        super.onDestroy()
        videoEditer?.setTXVideoPreviewListener(null)
        videoEditer?.stopPlay()
        videoEditer?.release()
    }


}