package com.eju.ejuvideo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.eju.video.EjuVideo

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        EjuVideo.startVideoRecord(this)
    }
}