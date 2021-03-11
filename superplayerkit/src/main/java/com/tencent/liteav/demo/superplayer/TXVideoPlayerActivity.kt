package com.tencent.liteav.demo.superplayer

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_tc_video_player.*

class TXVideoPlayerActivity:AppCompatActivity() {

    companion object{
        fun open(context: Context,videoUrl:String) {
            context.startActivity(Intent(context, TXVideoPlayerActivity::class.java)
                .putExtra("videoUrl",videoUrl)
            )
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tc_video_player)
        setListeners()
        play()
    }

    private fun setListeners() {
        superPlayerView.setPlayerViewCallback(object: SuperPlayerView.OnSuperPlayerViewCallback {
            override fun onStartPlayFirstFrame() {
                pbLoading.visibility=View.GONE
            }

            override fun onStartFullScreenPlay() {
            }
            override fun onStopFullScreenPlay() {
            }
            override fun onClickFloatCloseBtn() {
            }
            override fun onClickSmallReturnBtn() {
                onBackPressed()
            }
            override fun onStartFloatWindowPlay() {
            }

        })


    }

    override fun onBackPressed() {
        if(superPlayerView.playerMode==SuperPlayerDef.PlayerMode.FULLSCREEN){
            superPlayerView.switchPlayMode(SuperPlayerDef.PlayerMode.WINDOW)
        }else{
            super.onBackPressed()
        }

    }

    private fun play(){
        superPlayerView.playWithModel(SuperPlayerModel().apply { url=intent.getStringExtra("videoUrl") })
    }

    override fun onResume() {
        super.onResume()
        superPlayerView.onResume()
    }

    override fun onPause() {
        super.onPause()
        superPlayerView.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        superPlayerView.release()
        superPlayerView.resetPlayer()
    }
}