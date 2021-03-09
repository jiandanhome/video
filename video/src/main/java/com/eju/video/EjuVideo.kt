package com.eju.video

import androidx.fragment.app.Fragment
import android.content.Context
import android.content.Intent
import com.tencent.liteav.demo.videoediter.custom.VideoOutProvider
import com.tencent.liteav.demo.videorecord.TCVideoRecordActivity
import com.tencent.qcloud.ugckit.UGCKit
import com.tencent.ugc.TXUGCBase

object EjuVideo {

    const val licenceKey="b95bf0cead8efcf49867daef712e942a"
    const val licenceUrl="http://license.vod2.myqcloud.com/license/v1/bc3914089ac22ecb967478125efabd44/TXUgcSDK.licence"


    fun init(context: Context, videoProvider:((videoPath:String)->Unit)) {
        UGCKit.init(context.applicationContext)
        TXUGCBase.getInstance().setLicence(context.applicationContext, licenceUrl, licenceKey)
        VideoOutProvider.videoProvider={
            videoProvider?.invoke(it)
        }
    }

    fun startVideoRecord(context: Context){
        context.startActivity(Intent(context, TCVideoRecordActivity::class.java))
    }

    fun startVideoRecord(fragment: Fragment){
        fragment?.activity?.let {
            fragment.startActivity(Intent(it,TCVideoRecordActivity::class.java))
        }
    }

}