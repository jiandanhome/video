package com.eju.ejuvideo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.eju.video.EjuVideo

class UgcVideoDemoActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ugc_video_demo)
    }

    fun record(view: View){
        EjuVideo.startVideoRecord(this)
    }


    fun play(view: View){
        val urlList= listOf("https://img-test.jiandanhome.com/relations/963/2021-03-09/source/1615255594522.mp4",
        "")
        EjuVideo.playVideo(this,urlList[0])
    }

    fun select(view: View){
        EjuVideo.joinVideo(this)
    }
}