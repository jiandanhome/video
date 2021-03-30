package com.eju.ejuvideo

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import com.eju.video.EjuVideo
import com.tencent.qcloud.ugckit.utils.ToastUtil
import java.util.*

class UgcVideoDemoActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ugc_video_demo)

    }

    fun record(view: View){
        EjuVideo.startVideoRecord(this,20)
    }


    fun play(view: View){
        startActivity(Intent(this,SimplePlayVideo::class.java))
    }

    fun play1(view: View){
        val localVideo=Random().nextBoolean()
        if(localVideo){
            ToastUtil.toastShortMessage("播放本地视频")
            EjuVideo.playVideo(this,"/storage/emulated/0/DCIM/Camera/VID_20201224_100659.mp4")
        }else{
            ToastUtil.toastShortMessage("播放网络视频")
            EjuVideo.playVideo(this,"https://img-test.jiandanhome.com/relations/963/2021-03-09/source/1615255594522.mp4")
        }


    }


    fun selectVideoCover(view: View){
        EjuVideo.selectVideoCover(this,"/storage/emulated/0/DCIM/Camera/VID_20201224_100659.mp4",20)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.i("sck220", "使用页面: ${requestCode} ${resultCode} ${EjuVideo.getVideoPathFromIntent(data)}")
    }
}