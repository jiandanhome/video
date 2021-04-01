package com.tencent.liteav.demo.videoediter

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.tencent.qcloud.ugckit.UGCKit
import com.tencent.qcloud.ugckit.UGCKitConstants
import com.tencent.qcloud.ugckit.component.dialogfragment.VideoWorkProgressFragment
import com.tencent.qcloud.ugckit.component.timeline.ViewTouchProcess
import com.tencent.qcloud.ugckit.module.cut.IVideoCutLayout
import com.tencent.qcloud.ugckit.module.effect.utils.PlayState
import com.tencent.qcloud.ugckit.utils.ScreenUtils
import com.tencent.qcloud.ugckit.utils.ToastUtil
import com.tencent.qcloud.ugckit.utils.VideoPathUtil
import com.tencent.ugc.TXVideoEditConstants
import com.tencent.ugc.TXVideoEditer
import com.tencent.ugc.TXVideoInfoReader
import kotlinx.android.synthetic.main.ugcedit_activity_select_video_cover.*
import kotlinx.android.synthetic.main.ugcedit_activity_select_video_cover.flPlayVideo
import kotlinx.android.synthetic.main.ugcedit_activity_select_video_cover.ivPlay
import kotlinx.android.synthetic.main.ugcedit_activity_select_video_cover.titleLayout
import kotlinx.android.synthetic.main.ugcedit_activity_select_video_cover.tvTime
import kotlinx.android.synthetic.main.ugcedit_activity_select_video_cover.videoThumb
import kotlinx.android.synthetic.main.ugcedit_activity_video_cut_new.*
import java.io.File
import kotlin.concurrent.thread

class TCVideoCutNewActivity:AppCompatActivity() {

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

    private var startTimeMs=0L

    private var endTimeMs=0L

    private var maxTimeMs=60000L

    private var initMarginLeft=0

    private var videoOutputPath:String?=null

    private var rotation=0




    companion object{
        fun open(activity: Activity, videoPath: String, maxDuration: Long, requestCode: Int) {
            activity.startActivityForResult(
                Intent(activity, TCVideoCutNewActivity::class.java)
                    .putExtra(UGCKitConstants.VIDEO_PATH, videoPath)
                    .putExtra(UGCKitConstants.VIDEO_CUT_MAX_DURATION, maxDuration)
                    .putExtra(
                        UGCKitConstants.VIDEO_URI, Uri.parse(videoPath).toString()
                    ), requestCode
            )
        }

        fun open(fragment: Fragment, videoPath: String, maxDuration: Long, requestCode: Int) {
            fragment.activity?.let { activity->
                fragment.startActivityForResult(
                    Intent(activity, TCVideoCutNewActivity::class.java)
                        .putExtra(UGCKitConstants.VIDEO_PATH, videoPath)
                        .putExtra(UGCKitConstants.VIDEO_CUT_MAX_DURATION, maxDuration)
                        .putExtra(
                            UGCKitConstants.VIDEO_URI, Uri.parse(videoPath).toString()
                        ), requestCode
                )
            }
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.ugcedit_activity_video_cut_new)
        setTitle()
        parseIntent()
        readVideoPathInfo {
            init()
            loadThumbList()
            setListeners()
            startPlay()
        }
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

            cutVideo()
        }
    }

    private fun cutVideo(){
        showProgress()
        thread {
            videoEditer?.setCutFromTime(startTimeMs, endTimeMs)
            videoOutputPath = VideoPathUtil.generateVideoPath(null)
            videoEditer?.setVideoGenerateListener(object : TXVideoEditer.TXVideoGenerateListener {
                override fun onGenerateProgress(p0: Float) {
                    runOnUiThread {
                        updateProgress((p0 * 100).toInt())
                    }
                }

                override fun onGenerateComplete(result: TXVideoEditConstants.TXGenerateResult) {
                    runOnUiThread {
                        hideProgress()
                        if (result.retCode == TXVideoEditConstants.GENERATE_RESULT_OK) {
                            setResult(
                                RESULT_OK, Intent().putExtra(
                                    UGCKitConstants.VIDEO_PATH,
                                    videoOutputPath
                                )
                            )
                            finish()
                        } else {
                            ToastUtil.toastShortMessage("裁剪失败,${result.descMsg}")
                        }
                    }
                }
            })
            videoEditer?.generateVideo(TXVideoEditConstants.VIDEO_COMPRESSED_720P, videoOutputPath)
        }
    }

    private fun parseIntent(){
        val videoPath = intent.getStringExtra(UGCKitConstants.VIDEO_PATH)
        val videoUri = intent.getStringExtra(UGCKitConstants.VIDEO_URI)
        this.videoPath=  if(Build.VERSION.SDK_INT >= 29) videoUri else videoPath
        maxTimeMs=intent.getLongExtra(UGCKitConstants.VIDEO_CUT_MAX_DURATION, 30000L)
    }


    private fun readVideoPathInfo(successCallBack: () -> Unit){
        thread {
            try {
                val videoInfo = TXVideoInfoReader.getInstance(UGCKit.getAppContext()).getVideoFileInfo(
                    videoPath
                )
                videoDuration=videoInfo.duration
                runOnUiThread {
                    successCallBack.invoke()
                }

            } catch (e: Exception) {
                runOnUiThread{
                    Toast.makeText(this, "本地视频文件读取失败", Toast.LENGTH_SHORT).show()
                    finish()
                }
            }
        }

    }

    private fun init(){
        thumbCount = (videoDuration/3000).toInt()
        singleThumbWidth= ScreenUtils.dp2px(this, 40F).toInt()
        allThumbWidth=thumbCount*singleThumbWidth

        videoEditer= TXVideoEditer(this).apply {
            setVideoPath(videoPath)
            initWithPreview(TXVideoEditConstants.TXPreviewParam().apply {
                videoView = flPlayVideo
                renderMode = TXVideoEditConstants.PREVIEW_RENDER_MODE_FILL_EDGE
            })
        }

        startTimeMs=0L
        maxTimeMs= maxTimeMs.coerceAtMost(videoDuration)
        endTimeMs=maxTimeMs
        initMarginLeft=(ScreenUtils.getScreenWidth(this)/2f-ScreenUtils.dp2px(this, 12F)).toInt()

        llIndicator.visibility=View.VISIBLE
        val leftLayoutParams=ivLeft.layoutParams as LinearLayout.LayoutParams
        leftLayoutParams.leftMargin= initMarginLeft
        ivLeft.layoutParams=leftLayoutParams

        val middleLayoutParams=vMiddle.layoutParams as LinearLayout.LayoutParams
        middleLayoutParams.width= (allThumbWidth*(maxTimeMs*1F/videoDuration)).toInt()
        vMiddle.layoutParams=middleLayoutParams

        setSelectedDurationText()
    }


    private fun loadThumbList(){
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

    private val videoPreviewListener=object : TXVideoEditer.TXVideoPreviewListener {
        override fun onPreviewProgress(time: Int) {
            currentTimeMs = time / 1000  //videoProgress:ms
            if (currentPlayState == PlayState.STATE_PLAY) {
                val distance = (currentTimeMs * 1F / videoDuration) * allThumbWidth - currentScrolledX
                videoThumb.scrollThumb(distance)
            }
        }

        override fun onPreviewFinished() {
            startPlay()
            videoThumb.scrollThumb(-allThumbWidth.toFloat())
        }
    }

    private fun setListeners() {
        videoEditer?.setTXVideoPreviewListener(videoPreviewListener)
        videoThumb.addOnScrollListener(object : RecyclerView.OnScrollListener() {

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                currentScrolledX += dx

                val leftLayoutParams = ivLeft.layoutParams as LinearLayout.LayoutParams
                leftLayoutParams.leftMargin -= dx
                ivLeft.layoutParams = leftLayoutParams

                if (currentPlayState != PlayState.STATE_PLAY) {
                    val progress = currentScrolledX.toFloat() / allThumbWidth
                    val currentTimeMs = (videoDuration * progress).toInt()
//                    Log.i("sck220", "previewAtTime: ${currentTimeMs}")
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
                videoEditer?.startPlayFromTime(startTimeMs, endTimeMs)
                currentPlayState=PlayState.STATE_PLAY
                ivPlay.setImageResource(R.drawable.ic_ugc_pause)
            }

        }


        ViewTouchProcess(ivLeft).setOnPositionChangedListener(object :
            ViewTouchProcess.OnPositionChangedListener {
            override fun onPostionChanged(distance: Float) {

                if (currentPlayState == PlayState.STATE_PLAY) {
                    videoEditer?.pausePlay()
                    currentPlayState = PlayState.STATE_PAUSE
                    ivPlay.setImageResource(R.drawable.ic_ugc_play)
                }

                val maxWidth = maxTimeMs * 1F / videoDuration * allThumbWidth
                val leftLayoutParams = ivLeft.layoutParams as LinearLayout.LayoutParams
                val middleLayoutParams = vMiddle.layoutParams as LinearLayout.LayoutParams

                if (leftLayoutParams.leftMargin + distance.toInt() >= initMarginLeft - currentScrolledX &&
                    middleLayoutParams.width - distance.toInt() <= maxWidth
                ) {
                    leftLayoutParams.leftMargin += distance.toInt()
                    ivLeft.layoutParams = leftLayoutParams

                    val middleLayoutParams = vMiddle.layoutParams as LinearLayout.LayoutParams
                    middleLayoutParams.width -= distance.toInt()
                    vMiddle.layoutParams = middleLayoutParams

                    startTimeMs =
                        ((leftLayoutParams.leftMargin - (initMarginLeft - currentScrolledX)) * 1F / allThumbWidth * videoDuration).toLong()

                    setSelectedDurationText()
                }
            }

            override fun onChangeComplete() {

            }
        })

        ViewTouchProcess(ivRight).setOnPositionChangedListener(object :
            ViewTouchProcess.OnPositionChangedListener {
            override fun onPostionChanged(distance: Float) {
                if (currentPlayState == PlayState.STATE_PLAY) {
                    videoEditer?.pausePlay()
                    currentPlayState = PlayState.STATE_PAUSE
                    ivPlay.setImageResource(R.drawable.ic_ugc_play)
                }
                val maxWidth = maxTimeMs * 1F / videoDuration * allThumbWidth
                val middleLayoutParams = vMiddle.layoutParams as LinearLayout.LayoutParams

                val leftLayoutParams = ivLeft.layoutParams as LinearLayout.LayoutParams
                val space = allThumbWidth - (leftLayoutParams.leftMargin - (initMarginLeft - currentScrolledX))

                if (middleLayoutParams.width + distance <= (Math.min(space.toFloat(),maxWidth))) {
                    middleLayoutParams.width += distance.toInt()
                    vMiddle.layoutParams = middleLayoutParams
                    endTimeMs =
                        (startTimeMs + (middleLayoutParams.width * 1F / allThumbWidth) * videoDuration).toLong()
                    setSelectedDurationText()
                }
            }

            override fun onChangeComplete() {

            }
        })

        iv_rotate.setOnClickListener {
            rotation = if (rotation < 270) rotation + 90 else 0
            videoEditer?.setRenderRotation(rotation)
        }
    }

    private fun setSelectedDurationText(){
        tvTime.text="已选择${((endTimeMs-startTimeMs)/1000)}s"
    }

    private fun startPlay(){
        videoEditer?.startPlayFromTime(startTimeMs, endTimeMs)
        currentPlayState=PlayState.STATE_PLAY
        ivPlay.setImageResource(R.drawable.ic_ugc_pause)
    }


    private var videoWorkProgressFragment:VideoWorkProgressFragment?=null


    private fun showProgress(){
        hideProgress()
        videoWorkProgressFragment= VideoWorkProgressFragment.newInstance("视频裁剪中")
        videoWorkProgressFragment?.setOnClickStopListener {
            hideProgress()
            videoEditer?.cancel()
            videoEditer?.setVideoGenerateListener(null)
            videoOutputPath?.let { File(it).delete() }
            ToastUtil.toastShortMessage("裁剪已取消")
        }
        videoWorkProgressFragment?.showAllowingStateLoss(supportFragmentManager)
    }

    private fun updateProgress(progress: Int){
        videoWorkProgressFragment?.setProgress(progress)
    }

    private fun hideProgress(){
        videoWorkProgressFragment?.dismissAllowingStateLoss()
        videoWorkProgressFragment=null
    }



    override fun onDestroy() {
        super.onDestroy()
        videoEditer?.setTXVideoPreviewListener(null)
        videoEditer?.stopPlay()
        videoEditer?.release()
    }


}