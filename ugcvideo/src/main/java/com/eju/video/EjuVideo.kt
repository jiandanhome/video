package com.eju.video

import android.app.Activity
import androidx.fragment.app.Fragment
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import com.eju.ugcvideojoin.UGCSelectVideoActivity
import com.tencent.liteav.demo.superplayer.TXVideoPlayerActivity
import com.tencent.liteav.demo.videoediter.TCVideoCoverSelectActivity
import com.tencent.liteav.demo.videorecord.TCVideoRecordActivity
import com.tencent.qcloud.ugckit.UGCKit
import com.tencent.qcloud.ugckit.UGCKitConstants
import com.tencent.qcloud.ugckit.custom.MusicListProvider
import com.tencent.qcloud.ugckit.module.effect.bgm.TCMusicInfo
import com.tencent.rtmp.downloader.ITXVodDownloadListener
import com.tencent.rtmp.downloader.TXVodDownloadManager
import com.tencent.rtmp.downloader.TXVodDownloadMediaInfo
import com.tencent.ugc.TXUGCBase
import com.tencent.ugc.TXVideoEditConstants
import com.tencent.ugc.TXVideoInfoReader
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

object EjuVideo {

    fun init(context: Context,licenceUrl:String,licenceKey:String) {
        UGCKit.init(context.applicationContext)
        TXUGCBase.getInstance().setLicence(context.applicationContext, licenceUrl, licenceKey)
    }


    suspend fun getVideoFileInfo(context:Context,localVideoPath:String): TXVideoEditConstants.TXVideoInfo {
        return withContext(Dispatchers.IO){
            TXVideoInfoReader.getInstance(context).getVideoFileInfo(localVideoPath)
        }
    }

    suspend fun getVideoFrameAt(context: Context,localVideoPath:String,timeInMs:Int):Bitmap?{
        return withContext(Dispatchers.IO){
            TXVideoInfoReader.getInstance(context).getSampleImage(timeInMs.toLong(),localVideoPath)
        }
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

    //m3u8视频下载
    @Deprecated("只能下载m3u8视频")
    suspend fun downloadVideo(outputPath:String,videoUrl:String,progressCallback:((Float)->Unit)?=null):TXVodDownloadMediaInfo{
        return suspendCancellableCoroutine { continuation->
            TXVodDownloadManager.getInstance().setDownloadPath(outputPath)
            TXVodDownloadManager.getInstance().setListener(
                object:ITXVodDownloadListener{
                    override fun onDownloadStart(p0: TXVodDownloadMediaInfo?) {
                    }
                    override fun onDownloadProgress(p0: TXVodDownloadMediaInfo?) {
                        p0?.let {
                            progressCallback?.invoke(it.progress)
                        }
                    }
                    override fun onDownloadStop(p0: TXVodDownloadMediaInfo?) {
                    }
                    override fun onDownloadFinish(p0: TXVodDownloadMediaInfo) {
                        continuation.resume(p0)
                    }
                    override fun onDownloadError(p0: TXVodDownloadMediaInfo?, p1: Int, p2: String?) {
                        continuation.resumeWithException(IllegalStateException(p2))
                    }
                    override fun hlsKeyVerify(
                        p0: TXVodDownloadMediaInfo?,
                        p1: String?,
                        p2: ByteArray?
                    ): Int {
                        return 0
                    }

                })
            val request=TXVodDownloadManager.getInstance().startDownloadUrl(videoUrl)
            continuation.invokeOnCancellation {
                TXVodDownloadManager.getInstance().stopDownload(request)
            }
        }
    }
    fun deleteDownloadFile(path:String){
        TXVodDownloadManager.getInstance().deleteDownloadFile(path)
    }
}
