package com.eju.ugcvideojoin

import android.content.Intent
import android.net.Uri
import android.os.*
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.tencent.liteav.demo.videoediter.TCVideoCutActivity
import com.tencent.liteav.demo.videoediter.TCVideoCutNewActivity
import com.tencent.qcloud.ugckit.UGCKitConstants
import com.tencent.qcloud.ugckit.component.dialogfragment.VideoWorkProgressFragment
import com.tencent.qcloud.ugckit.module.picker.data.TCVideoFileInfo
import com.tencent.qcloud.ugckit.utils.ScreenUtils
import com.tencent.qcloud.ugckit.utils.ToastUtil
import com.tencent.qcloud.ugckit.utils.VideoPathUtil
import com.tencent.ugc.TXVideoEditConstants
import com.tencent.ugc.TXVideoEditConstants.TXEffectType_DARK_DRAEM
import com.tencent.ugc.TXVideoEditConstants.TXPreviewParam
import com.tencent.ugc.TXVideoJoiner
import kotlinx.android.synthetic.main.activity_ugc_video_join.*
import java.io.File
import java.io.FileInputStream
import kotlin.concurrent.thread


class UGCVideoJoinActivity :AppCompatActivity(){

    private var mTXVideoJoiner:TXVideoJoiner?=null


    private val videoSourceList= mutableListOf<TCVideoFileInfo>()

    private var videoOutputPath:String?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ugc_video_join)
        clContent.setPadding(0, ScreenUtils.getStatusBarHeight(this), 0, 0)
        parseIntent()
        readResult {
            startJoin()
        }
        tvBack.setOnClickListener {
            videoOutputPath?.let { File(it).delete() }
            finish()
        }
    }

    private fun parseIntent(){
        val videoSourceList=intent.getParcelableArrayListExtra<TCVideoFileInfo>(UGCKitConstants.INTENT_KEY_MULTI_CHOOSE)
        if(videoSourceList.isNullOrEmpty()){
            finish()
            return
        }
        this.videoSourceList.clear()
        this.videoSourceList.addAll(videoSourceList)
    }


    private fun readResult(callback:()->Unit){
        thread {
            mTXVideoJoiner = TXVideoJoiner(this)
            val result=mTXVideoJoiner?.setVideoPathList(videoSourceList.map {
                if (Build.VERSION.SDK_INT >= 29) {
                    it.fileUri.toString()
                } else {
                    it.filePath
                }
            })
            runOnUiThread {
                when (result) {
                    TXVideoEditConstants.ERR_UNSUPPORT_VIDEO_FORMAT -> {
                        Toast.makeText(this, "视频合成失败 本机型暂不支持此视频格式", Toast.LENGTH_SHORT).show()
                        finish()
                    }
                    TXVideoEditConstants.ERR_UNSUPPORT_AUDIO_FORMAT -> {
                        Toast.makeText(this, "视频合成失败 暂不支持非单双声道的视频格式", Toast.LENGTH_SHORT).show()
                        finish()
                    }
                    else -> callback.invoke()
                }
            }

        }
    }



    private fun startJoin(){
        // 生成视频输出路径
        videoOutputPath = VideoPathUtil.generateVideoPath()
        mTXVideoJoiner?.setVideoJoinerListener(object : TXVideoJoiner.TXVideoJoinerListener {
            override fun onJoinProgress(p0: Float) {
                cProgress.progress=p0
            }
            override fun onJoinComplete(p0: TXVideoEditConstants.TXJoinerResult?) {
                if (p0?.retCode == TXVideoEditConstants.JOIN_RESULT_OK) {
                    setResult(RESULT_OK,Intent().putExtra(UGCKitConstants.VIDEO_PATH,videoOutputPath))
                } else {
                    Toast.makeText(this@UGCVideoJoinActivity, "视频合成失败 ${p0?.descMsg}", Toast.LENGTH_SHORT).show()
                }
                finish()
            }
        })
        thread {
            mTXVideoJoiner?.joinVideo(TXVideoEditConstants.VIDEO_COMPRESSED_540P, videoOutputPath)
        }
    }


    override fun onDestroy() {
        try {
            mTXVideoJoiner?.stopPlay()
            mTXVideoJoiner?.cancel()
            mTXVideoJoiner?.setTXVideoPreviewListener(null)
            mTXVideoJoiner?.setVideoJoinerListener(null)
        } catch (e: Exception) {
        }
        super.onDestroy()
    }

}