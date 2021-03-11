package com.eju.video

import androidx.fragment.app.Fragment
import android.content.Context
import android.content.Intent
import com.eju.ugcvideojoin.UGCSelectVideoActivity
import com.tencent.liteav.demo.superplayer.TXVideoPlayerActivity
import com.tencent.liteav.demo.videoediter.custom.VideoOutProvider
import com.tencent.liteav.demo.videorecord.TCVideoRecordActivity
import com.tencent.qcloud.ugckit.UGCKit
import com.tencent.qcloud.ugckit.custom.MusicListProvider
import com.tencent.qcloud.ugckit.module.effect.bgm.TCMusicInfo
import com.tencent.ugc.TXUGCBase

object EjuVideo {

    fun init(context: Context,licenceUrl:String,licenceKey:String) {
        UGCKit.init(context.applicationContext)
        TXUGCBase.getInstance().setLicence(context.applicationContext, licenceUrl, licenceKey)

    }

    fun setVideoProvider(videoProvider:(String)->Unit){
        VideoOutProvider.videoProvider=videoProvider
    }

    fun startVideoRecord(context: Context){
        context.startActivity(Intent(context, TCVideoRecordActivity::class.java))
    }

    fun startVideoRecord(fragment: Fragment){
        fragment?.activity?.let {
            fragment.startActivity(Intent(it,TCVideoRecordActivity::class.java))
        }
    }

    fun playVideo(context: Context,videoUrl:String?){
        videoUrl?.let {
            TXVideoPlayerActivity.open(context,it)
        }
    }

    fun joinVideo(context: Context){
        context.startActivity(Intent(context,UGCSelectVideoActivity::class.java))
    }

    fun provideMusicList(list:List<TCMusicInfo>){
        MusicListProvider.musicList.clear()
        MusicListProvider.musicList.addAll(list)
    }

}