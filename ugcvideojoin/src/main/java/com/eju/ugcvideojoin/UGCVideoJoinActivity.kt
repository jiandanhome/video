package com.eju.ugcvideojoin

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.tencent.liteav.demo.videoediter.TCVideoCutActivity
import com.tencent.qcloud.ugckit.UGCKitConstants
import com.tencent.qcloud.ugckit.component.dialogfragment.VideoWorkProgressFragment
import com.tencent.qcloud.ugckit.module.picker.data.TCVideoFileInfo
import com.tencent.qcloud.ugckit.utils.ScreenUtils
import com.tencent.qcloud.ugckit.utils.VideoPathUtil
import com.tencent.ugc.TXVideoEditConstants
import com.tencent.ugc.TXVideoEditConstants.TXEffectType_DARK_DRAEM
import com.tencent.ugc.TXVideoEditConstants.TXPreviewParam
import com.tencent.ugc.TXVideoJoiner
import kotlinx.android.synthetic.main.activity_ugc_video_join.*
import java.io.FileInputStream


class UGCVideoJoinActivity :AppCompatActivity(){

    private var mTXVideoJoiner:TXVideoJoiner?=null

    private var videoWorkProgressFragment:VideoWorkProgressFragment?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ugc_video_join)
        clContent.setPadding(0, ScreenUtils.getStatusBarHeight(this), 0, 0)
        initData()
    }

    private fun initData(){
        val videoSourceList=intent.getParcelableArrayListExtra<TCVideoFileInfo>(UGCKitConstants.INTENT_KEY_MULTI_CHOOSE)
        if(videoSourceList.isNullOrEmpty()){
            finish()
            return
        }
        val param = TXPreviewParam()
        param.videoView = flVideo
        param.renderMode = TXVideoEditConstants.PREVIEW_RENDER_MODE_FILL_EDGE

        mTXVideoJoiner = TXVideoJoiner(this)
        mTXVideoJoiner?.setTXVideoPreviewListener(object : TXVideoJoiner.TXVideoPreviewListener {
            override fun onPreviewProgress(p0: Int) {
            }
            override fun onPreviewFinished() {
                try {
                    mTXVideoJoiner?.startPlay()
                } catch (e: Exception) {
                }
            }
        })
        mTXVideoJoiner?.initWithPreview(param)
        val result=mTXVideoJoiner?.setVideoPathList(videoSourceList.map {
            if (Build.VERSION.SDK_INT >= 29) {
                it.fileUri.toString()
            } else {
                it.filePath
            }
        })
        if(result== TXVideoEditConstants.ERR_UNSUPPORT_VIDEO_FORMAT){
            Toast.makeText(this, "视频合成失败 本机型暂不支持此视频格式", Toast.LENGTH_SHORT).show()
            finish()
            return
        }else if(result== TXVideoEditConstants.ERR_UNSUPPORT_AUDIO_FORMAT){
            Toast.makeText(this, "视频合成失败 暂不支持非单双声道的视频格式", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        mTXVideoJoiner?.startPlay()
        showProgress()
        // 生成视频输出路径
        val videoOutputPath = VideoPathUtil.generateVideoPath()
        mTXVideoJoiner?.setVideoJoinerListener(object : TXVideoJoiner.TXVideoJoinerListener {
            override fun onJoinProgress(p0: Float) {
                updateProgress((p0 * 100).toInt())
            }

            override fun onJoinComplete(p0: TXVideoEditConstants.TXJoinerResult?) {
                hideProgress()
                if (p0?.retCode == TXVideoEditConstants.JOIN_RESULT_OK) {
                    startActivity(Intent(this@UGCVideoJoinActivity,TCVideoCutActivity::class.java)
                        .putExtra(UGCKitConstants.VIDEO_PATH,videoOutputPath)
                        .putExtra(UGCKitConstants.VIDEO_URI, Uri.parse(videoOutputPath).toString())
                    )
                } else {
                    Toast.makeText(this@UGCVideoJoinActivity, "视频合成失败 ${p0?.descMsg}", Toast.LENGTH_SHORT).show()
                }
                finish()
            }
        })
        mTXVideoJoiner?.joinVideo(TXVideoEditConstants.VIDEO_COMPRESSED_540P, videoOutputPath)

    }

    private fun showProgress(){
        hideProgress()
        videoWorkProgressFragment=VideoWorkProgressFragment.newInstance("视频拼接中")
        videoWorkProgressFragment?.setOnClickStopListener {
            hideProgress()
            finish()
        }
        videoWorkProgressFragment?.showAllowingStateLoss(supportFragmentManager)
    }

    private fun updateProgress(progress:Int){
        videoWorkProgressFragment?.setProgress(progress)
    }

    private fun hideProgress(){
        videoWorkProgressFragment?.dismissAllowingStateLoss()
        videoWorkProgressFragment=null
    }

    override fun onDestroy() {
        try {
            hideProgress()
            mTXVideoJoiner?.stopPlay()
            mTXVideoJoiner?.cancel()
            mTXVideoJoiner?.setTXVideoPreviewListener(null)
            mTXVideoJoiner?.setVideoJoinerListener(null)
        } catch (e: Exception) {
        }
        super.onDestroy()
    }

}