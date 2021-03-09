package com.tencent.liteav.demo.videoediter.custom

object VideoOutProvider {

    var videoProvider:((String)->Unit)?=null

    fun outputVideoPath(videoPath:String){
        videoProvider?.invoke(videoPath)
    }

}