package com.tencent.qcloud.ugckit.custom

import android.content.Context
import android.graphics.Bitmap
import android.os.Environment
import java.io.File
import java.io.FileOutputStream

object UGCImageUtils {

    fun saveBitmap(context: Context,bitmap: Bitmap?):String?{
        return bitmap?.let { bitmap ->
            val pictureOutputDir=EjuVideoConfig.pictureOutputDirPath?:context.getExternalFilesDir(
                Environment.DIRECTORY_PICTURES
            ).absolutePath
            val imageFilePath=File(pictureOutputDir,"${System.currentTimeMillis()}.jpg")
            FileOutputStream(imageFilePath).use {
                bitmap.compress(Bitmap.CompressFormat.JPEG,100,it)
            }
            bitmap.recycle()
            imageFilePath.absolutePath
        }?:null
    }
}