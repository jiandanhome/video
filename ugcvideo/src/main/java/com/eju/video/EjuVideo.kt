package com.eju.video

import android.app.Activity
import androidx.fragment.app.Fragment
import android.content.Context
import android.content.Intent
import com.eju.ugcvideojoin.UGCSelectVideoActivity
import com.tencent.liteav.demo.superplayer.TXVideoPlayerActivity
import com.tencent.liteav.demo.videoediter.TCVideoCoverSelectActivity
import com.tencent.liteav.demo.videorecord.TCVideoRecordActivity
import com.tencent.qcloud.ugckit.UGCKit
import com.tencent.qcloud.ugckit.UGCKitConstants
import com.tencent.qcloud.ugckit.custom.MusicListProvider
import com.tencent.qcloud.ugckit.module.effect.bgm.TCMusicInfo
import com.tencent.ugc.TXUGCBase

object EjuVideo {

    fun init(context: Context,licenceUrl:String,licenceKey:String) {
        UGCKit.init(context.applicationContext)
        TXUGCBase.getInstance().setLicence(context.applicationContext, licenceUrl, licenceKey)
    }

    //视频录制和编辑
    //提供选择音乐的列表
    fun provideMusicList(list:List<TCMusicInfo>){
        MusicListProvider.musicList.clear()
        MusicListProvider.musicList.addAll(list)
    }
    fun startVideoRecord(activity: Activity,requestCode:Int,enableChangeVideoName:Boolean=false){
        activity.startActivityForResult(Intent(activity, TCVideoRecordActivity::class.java)
            .putExtra(UGCKitConstants.ENABLE_CHANGE_VIDEO_NAME,enableChangeVideoName)
            ,requestCode)
    }
    fun startVideoRecord(fragment: Fragment,requestCode:Int,enableChangeVideoName:Boolean=false){
        fragment.activity?.let {
            fragment.startActivityForResult(Intent(it, TCVideoRecordActivity::class.java)
                .putExtra(UGCKitConstants.ENABLE_CHANGE_VIDEO_NAME,enableChangeVideoName)
                ,requestCode)
        }
    }
    fun getVideoPathFromIntent(data:Intent?):String?{
        return data?.getStringExtra(UGCKitConstants.VIDEO_PATH)
    }


    //视频播放
    fun playVideo(context: Context,videoUrl:String?){
        videoUrl?.let {
            TXVideoPlayerActivity.open(context,it)
        }
    }

    //视频拼接
    fun joinVideo(context: Context){
        context.startActivity(Intent(context,UGCSelectVideoActivity::class.java))
    }



    //本地视频封面选择
    fun selectVideoCover(activity: Activity, localVideoPath:String,requestCode:Int){
        TCVideoCoverSelectActivity.open(activity,localVideoPath,requestCode)
    }
    fun selectVideoCover(fragment: Fragment, localVideoPath:String,requestCode:Int){
        TCVideoCoverSelectActivity.open(fragment,localVideoPath,requestCode)
    }
    fun getVideoCoverFromIntent(data:Intent?):String?{
        return data?.getStringExtra(UGCKitConstants.COVER_PIC)
    }
    fun getVideoCoverTimeFromIntent(data:Intent?):Int?{
        return data?.getIntExtra(UGCKitConstants.COVER_PIC_TIME_IS_MS,0)
    }
}
