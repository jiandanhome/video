package com.eju.video

import android.app.Activity
import androidx.fragment.app.Fragment
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import com.eju.ugcvideojoin.UGCSelectVideoActivity
import com.tencent.liteav.demo.superplayer.TXVideoPlayerActivity
import com.tencent.liteav.demo.videoediter.TCSelectVideoCoverActivity
import com.tencent.liteav.demo.videoediter.TCVideoCutNewActivity
import com.tencent.liteav.demo.videorecord.TCVideoRecordActivity
import com.tencent.qcloud.ugckit.UGCKit
import com.tencent.qcloud.ugckit.UGCKitConstants
import com.tencent.qcloud.ugckit.custom.MusicListProvider
import com.tencent.qcloud.ugckit.module.effect.bgm.TCMusicInfo
import com.tencent.ugc.TXUGCBase
import com.tencent.ugc.TXVideoEditConstants
import com.tencent.ugc.TXVideoInfoReader
import kotlin.math.max

object EjuVideo {

    fun init(context: Context,licenceUrl:String,licenceKey:String) {
        UGCKit.init(context.applicationContext)
        TXUGCBase.getInstance().setLicence(context.applicationContext, licenceUrl, licenceKey)
    }


     fun getVideoFileInfo(context:Context,localVideoPath:String): TXVideoEditConstants.TXVideoInfo=TXVideoInfoReader.getInstance(context).getVideoFileInfo(localVideoPath)

     fun getVideoFrameAt(context: Context,localVideoPath:String,timeInMs:Int):Bitmap?=TXVideoInfoReader.getInstance(context).getSampleImage(timeInMs.toLong(),localVideoPath)

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



    //本地视频封面选择
    fun selectVideoCover(activity: Activity, localVideoPath:String,requestCode:Int){
        TCSelectVideoCoverActivity.open(activity,localVideoPath,requestCode)
    }
    fun selectVideoCover(fragment: Fragment, localVideoPath:String,requestCode:Int){
        TCSelectVideoCoverActivity.open(fragment,localVideoPath,requestCode)
    }
    fun getVideoCoverFromIntent(data:Intent?):String?{
        return data?.getStringExtra(UGCKitConstants.COVER_PIC)
    }
    fun getVideoCoverTimeFromIntent(data:Intent?):Int?{
        return data?.getIntExtra(UGCKitConstants.COVER_PIC_TIME_IS_MS,0)
    }

    //本地视频拼接
    fun joinVideo(activity: Activity,requestCode:Int){
        UGCSelectVideoActivity.openForResult(activity,requestCode)
    }
    fun joinVideo(fragment: Fragment,requestCode:Int){
        UGCSelectVideoActivity.openForResult(fragment,requestCode)
    }


    //本地视频裁剪
    fun cutVideo(activity: Activity,localVideoPath:String,maxDuration:Long,requestCode:Int){
        TCVideoCutNewActivity.open(activity,localVideoPath, maxDuration,requestCode)
    }
    fun cutVideo(fragment: Fragment,localVideoPath:String,maxDuration:Long,requestCode:Int){
        TCVideoCutNewActivity.open(fragment,localVideoPath, maxDuration,requestCode)
    }


}
