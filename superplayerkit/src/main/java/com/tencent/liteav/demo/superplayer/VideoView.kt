package com.tencent.liteav.demo.superplayer

import android.content.Context
import android.os.Bundle
import android.util.AttributeSet
import android.util.Log
import android.view.GestureDetector
import android.view.GestureDetector.SimpleOnGestureListener
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.widget.SeekBar
import androidx.constraintlayout.widget.ConstraintLayout
import com.tencent.qcloud.ugckit.module.effect.utils.PlayState
import com.tencent.qcloud.ugckit.utils.DateTimeUtil
import com.tencent.rtmp.ITXVodPlayListener
import com.tencent.rtmp.TXLiveConstants
import com.tencent.rtmp.TXVodPlayConfig
import com.tencent.rtmp.TXVodPlayer
import kotlinx.android.synthetic.main.view_ugc_video_view.view.*

class VideoView(context: Context, attributeSet: AttributeSet) :ConstraintLayout(
    context,
    attributeSet
),
    ITXVodPlayListener {

    val TAG="EJuVideoView"

    private var gestureDetector:GestureDetector?=null


    private lateinit var videoPlayer:TXVodPlayer

    private var currentPlayState=PlayState.STATE_NONE

    init {
        LayoutInflater.from(context).inflate(R.layout.view_ugc_video_view, this)
        initVideoPlayer()
        setListeners()
    }

    private fun initVideoPlayer(){
        videoPlayer= TXVodPlayer(context).apply {
            setPlayerView(cloudVideoView)
            enableHardwareDecode(true)
            setRenderRotation(TXLiveConstants.RENDER_ROTATION_PORTRAIT)
            setRenderMode(TXLiveConstants.RENDER_MODE_ADJUST_RESOLUTION)
            setConfig(TXVodPlayConfig())
            setVodListener(this@VideoView)
            isLoop=true
        }
    }

    private fun setListeners() {
        ibPlayPause.setOnClickListener {
            if(currentPlayState==PlayState.STATE_PLAY){
                pause()
            }else if(currentPlayState==PlayState.STATE_PAUSE){
                resume()
            }
        }
        sbProgress.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    Log.i("sck220", "setListeners: ${progress}")
                    videoPlayer.seek(progress)
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }
        })

        gestureDetector = GestureDetector(context, object : SimpleOnGestureListener() {
            override fun onSingleTapConfirmed(e: MotionEvent): Boolean {
                ibPlayPause.performClick()
                return true
            }
        })
        gestureDetector?.setIsLongpressEnabled(false)
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        gestureDetector?.onTouchEvent(event)
        return true
    }

    fun start(videoPath: String){
        videoPlayer.startPlay(videoPath)
    }

    fun pause(){
        if(currentPlayState==PlayState.STATE_PLAY){
            videoPlayer.pause()
            currentPlayState=PlayState.STATE_PAUSE
            ibPlayPause.setImageResource(R.drawable.ic_ugc_play)
            ivPlay.visibility=View.VISIBLE
        }
    }

    fun resume(){
        if(currentPlayState==PlayState.STATE_PAUSE){
            videoPlayer.resume()
            currentPlayState=PlayState.STATE_PLAY
            ibPlayPause.setImageResource(R.drawable.ic_ugc_pause)
            ivPlay.visibility=View.GONE
        }
    }


    override fun onPlayEvent(p0: TXVodPlayer?, event: Int, param: Bundle) {
        when(event){
            TXLiveConstants.PLAY_EVT_VOD_PLAY_PREPARED, TXLiveConstants.PLAY_EVT_PLAY_BEGIN -> {
                currentPlayState = PlayState.STATE_PLAY
            }
            TXLiveConstants.PLAY_EVT_PLAY_LOADING -> {
                pbLoading.visibility = View.VISIBLE
            }
            TXLiveConstants.PLAY_EVT_RCV_FIRST_I_FRAME, TXLiveConstants.PLAY_EVT_VOD_LOADING_END -> {
                pbLoading.visibility = View.GONE
            }
            TXLiveConstants.PLAY_EVT_PLAY_PROGRESS -> {
                //播放时间s
                val progress = param.getInt(TXLiveConstants.EVT_PLAY_PROGRESS)
                //加载时间s
                val loadedDuration = param.getInt(TXLiveConstants.EVT_PLAYABLE_DURATION_MS) / 1000
                //总时间s
                val totalDuration = param.getInt(TXLiveConstants.EVT_PLAY_DURATION)

                tvCurrentTime.text = DateTimeUtil.formattedTime(progress.toLong())
                tvTotalTime.text = DateTimeUtil.formattedTime(totalDuration.toLong())
                sbProgress.max = totalDuration
                sbProgress.progress = progress
                sbProgress.secondaryProgress = loadedDuration
            }
        }
    }

    override fun onNetStatus(p0: TXVodPlayer?, p1: Bundle?) {
    }

    override fun onDetachedFromWindow() {
        videoPlayer.stopPlay(true)
        Log.i(TAG, "onDetachedFromWindow: ")
        cloudVideoView.onDestroy()
        super.onDetachedFromWindow()
    }
}