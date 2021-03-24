package com.eju.ejuvideo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_ugc_video_view_demo.*

class UgcVideoViewDemo : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ugc_video_view_demo)
        val localVideoPath="/storage/emulated/0/DCIM/Camera/VID_20201224_100659.mp4"
        val videoUrl="https://img-test.jiandanhome.com/relations/963/2021-03-09/source/1615255594522.mp4"
        video_view.start(localVideoPath,true)
    }

    override fun onPause() {
        super.onPause()
        video_view.pause()
    }

    override fun onResume() {
        super.onResume()
        video_view.resume()
    }
}