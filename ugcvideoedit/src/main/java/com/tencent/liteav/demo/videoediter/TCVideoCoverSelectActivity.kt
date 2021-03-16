package com.tencent.liteav.demo.videoediter

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.tencent.qcloud.ugckit.UGCKit
import com.tencent.qcloud.ugckit.UGCKitConstants
import com.tencent.qcloud.ugckit.module.cut.IVideoCutLayout
import com.tencent.qcloud.ugckit.module.effect.utils.PlayState
import com.tencent.qcloud.ugckit.utils.DateTimeUtil
import com.tencent.qcloud.ugckit.utils.ScreenUtils
import com.tencent.ugc.TXVideoEditConstants
import com.tencent.ugc.TXVideoEditer
import com.tencent.ugc.TXVideoInfoReader
import kotlinx.android.synthetic.main.ugcedit_activity_video_cover_selecet.*
import kotlin.concurrent.thread

class TCVideoCoverSelectActivity:AppCompatActivity() {

    private var mVideoResolution:Int=-1
    private var videoPath:String?=null

    private var videoEditer:TXVideoEditer?=null

    private var lastTimeMs=0

    private var currentTimeMs=0

    private lateinit var videoInfo:TXVideoEditConstants.TXVideoInfo

    private var singleThumbWidth=0

    private var thumbCount=0

    private var allThumbWidth=0

    private var currentScrolledX=0

    private var currentPlayState=PlayState.STATE_NONE


    companion object{

        fun open(context: Context, videoPath: String, videoUri: String) {
            context.startActivity(
                Intent(context, TCVideoCoverSelectActivity::class.java)
                    .putExtra(UGCKitConstants.VIDEO_PATH, videoPath)
                    .putExtra(UGCKitConstants.VIDEO_URI, videoUri)
            )
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.ugcedit_activity_video_cover_selecet)
        setTitle()
        parseIntent()
        init()
        loadThumbList()
    }

    private fun setTitle(){
        ScreenUtils.setMarginTop(titleLayout)
        findViewById<View>(R.id.page_title_left_icon).setOnClickListener { finish() }
        val btn_next=findViewById<TextView>(R.id.btn_next)
        btn_next.text="完成"
        btn_next.setOnClickListener {
            thread {
                val coverBitmap: Bitmap?=TXVideoInfoReader.getInstance(this).getSampleImage(0L,intent.getStringExtra(UGCKitConstants.VIDEO_PATH))
                Log.i("sck220", "setTitle: ${coverBitmap?.width}  ${coverBitmap?.height}")
            }
        }
    }

    private fun parseIntent(){
        mVideoResolution = intent.getIntExtra(
            UGCKitConstants.VIDEO_RECORD_RESOLUTION,
            TXVideoEditConstants.VIDEO_COMPRESSED_720P
        )
        val mVideoPath = intent.getStringExtra(UGCKitConstants.VIDEO_PATH)
        val mVideoUri = intent.getStringExtra(UGCKitConstants.VIDEO_URI)
        videoPath=  if(Build.VERSION.SDK_INT >= 29) mVideoUri else mVideoPath

        videoInfo = TXVideoInfoReader.getInstance(UGCKit.getAppContext()).getVideoFileInfo(videoPath)
        thumbCount = (videoInfo.duration/3000).toInt()
        singleThumbWidth= ScreenUtils.dp2px(this,40F).toInt()
        allThumbWidth=thumbCount*singleThumbWidth
    }


    private fun init(){
        videoEditer= TXVideoEditer(this)
        videoEditer?.setVideoPath(videoPath)
        videoEditer?.initWithPreview(TXVideoEditConstants.TXPreviewParam().apply {
            videoView = flPlayVideo
            renderMode = TXVideoEditConstants.PREVIEW_RENDER_MODE_FILL_EDGE
        })
        videoEditer?.setTXVideoPreviewListener(object : TXVideoEditer.TXVideoPreviewListener {
            override fun onPreviewProgress(time: Int) {
                currentTimeMs = time / 1000  //videoProgress:ms
                tvTime.text = DateTimeUtil.formattedTime((currentTimeMs / 1000).toLong())

                val distance=(currentTimeMs*1F/videoInfo.duration)*allThumbWidth-currentScrolledX
                videoThumb.scrollThumb(distance)
                lastTimeMs=currentTimeMs
            }

            override fun onPreviewFinished() {
                videoEditer?.startPlayFromTime(0, videoInfo.duration)
            }
        })
        videoThumb.addOnScrollListener(object:RecyclerView.OnScrollListener(){
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                currentScrolledX+=dx
                if(currentPlayState==PlayState.STATE_PAUSE){
//                    val progress=currentScrolledX*1F/allThumbWidth
//                    val startTime=videoInfo.duration*progress
//                    videoEditer?.previewAtTime(startTime.toLong())
                }
            }

            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                when(newState){
                    RecyclerView.SCROLL_STATE_DRAGGING ->{
                        if(currentPlayState==PlayState.STATE_PLAY){
                            videoEditer?.pausePlay()
                            currentPlayState=PlayState.STATE_PAUSE
                        }
                        ivPlay.setImageResource(if(currentPlayState==PlayState.STATE_PLAY) R.drawable.ic_ugc_pause else R.drawable.ic_ugc_play)
                    }
                    RecyclerView.SCROLL_STATE_IDLE->{
                        val progress=currentScrolledX*1F/allThumbWidth
                        val startTime=videoInfo.duration*progress
                        videoEditer?.startPlayFromTime(startTime.toLong(), videoInfo.duration)
                        currentPlayState=PlayState.STATE_PLAY
                        ivPlay.setImageResource(R.drawable.ic_ugc_pause)
                    }
                }
            }
        })
        ivPlay.setOnClickListener {
            if(currentPlayState==PlayState.STATE_PLAY){
                videoEditer?.pausePlay()
                currentPlayState=PlayState.STATE_PAUSE
            }else if(currentPlayState==PlayState.STATE_PAUSE){
                videoEditer?.resumePlay()
                currentPlayState=PlayState.STATE_PLAY
            }
            ivPlay.setImageResource(if(currentPlayState==PlayState.STATE_PLAY) R.drawable.ic_ugc_pause else R.drawable.ic_ugc_play)
        }
        videoEditer?.startPlayFromTime(0,videoInfo.duration)
        currentPlayState=PlayState.STATE_PLAY
    }


    private fun loadThumbList(){
        videoEditer?.setCutFromTime(0, videoInfo.duration)
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


    override fun onDestroy() {
        super.onDestroy()
        videoEditer?.setTXVideoPreviewListener(null)
        videoEditer?.stopPlay()
        videoEditer?.release()
    }


}